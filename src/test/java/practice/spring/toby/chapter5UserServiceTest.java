package practice.spring.toby;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static practice.spring.toby.chapter5.UserServiceImple.MIN_LOGCOUNT_FOR_SILVER;
import static practice.spring.toby.chapter5.UserServiceImple.MIN_RECCOMEND_FOR_GOLD;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import practice.spring.toby.chapter5.Level;
import practice.spring.toby.chapter5.TestUserServiceException;
import practice.spring.toby.chapter5.User;
import practice.spring.toby.chapter5.UserDao;
import practice.spring.toby.chapter5.UserService;
import practice.spring.toby.chapter5.UserTransactionExceptionService;;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/chapter5/applicationContext.xml")
public class chapter5UserServiceTest {
	
	@Autowired
	UserDao dao;
	
	@Autowired
	UserService service;
	
	List<User> users;
	
	@Autowired
	PlatformTransactionManager transactionManager;
	
	@Autowired
	MailSender mailSender;
	
	@Before
	public void setUp() {
		
		// 경계값 테스트 값
		users = Arrays.asList(
			new User("1","1","1",Level.BASIC,MIN_LOGCOUNT_FOR_SILVER - 1, 0, null),	
			new User("2","2","2",Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, null),	
			new User("3","3","3",Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD - 1, null),	
			new User("4","4","4",Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD, null),	
			new User("5","5","5",Level.GOLD, 100, 100, null)	
		);
	}
	
	@Test
	public void upgradeAllOrNothing() throws Exception{
		UserTransactionExceptionService service = new UserTransactionExceptionService(users.get(3).getId());
		service.setUserDao(dao);
		service.setTransactionManager(transactionManager);
		service.setMailSender(mailSender);
		dao.deleteAll();
		
		for (User u : users) dao.add(u);
		
		try {
			service.upgradeLevels();
			fail("TestUserServiceException expected");
		} catch (TestUserServiceException e) {
		}
		
		checkLevel(users.get(1), false);
	}
	
	@Test
	public void upgradeLevels () {
		UserTransactionExceptionService service = new UserTransactionExceptionService();
		
		dao.deleteAll();
		for (User u : users) dao.add(u);
		
		service.setUserDao(dao);
		service.setTransactionManager(transactionManager);
		service.setMailSender(mailSender);
		service.upgradeLevels();
		checkLevel(users.get(0), false);
		checkLevel(users.get(1), true);
		checkLevel(users.get(2), false);
		checkLevel(users.get(3), true);
		checkLevel(users.get(4), false);
	}
	
	//@Test
	public void newUserLevel () {
		dao.deleteAll();
		
		User user1 = new User();
		user1.setId("1");
		user1.setName("1");
		user1.setPassword("1");
		
		User user2 = new User("2", "2", "2", Level.SILVER, 50, 0, null);
		
		service.add(user1); 
		service.add(user2); 
		
		User resultUser1 = dao.get(user1.getId());
		User resultUser2 = dao.get(user2.getId());
		assertThat(resultUser1.getLevel(), is(Level.SILVER));
		//checkLevel(resultUser1, Level.BASIC);
		//checkLevel(resultUser2, Level.SILVER);
	}
	
	private void checkLevel (User user, boolean upgraded) {
		User userUpdate = dao.get(user.getId());
		if (upgraded) {
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel())); // 업그레이드가 일어났는지 확인
		} else {
			assertThat(userUpdate.getLevel(), is(user.getLevel())); 			// 업그레이드가 일어나지 않았는지 확인
		}
		
	}
}
