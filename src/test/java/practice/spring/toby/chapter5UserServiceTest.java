package practice.spring.toby;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import practice.spring.toby.chapter5.Level;
import practice.spring.toby.chapter5.User;
import practice.spring.toby.chapter5.UserDao;
import practice.spring.toby.chapter5.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/chapter5/applicationContext.xml")
public class chapter5UserServiceTest {

	@Autowired
	UserDao dao;
	
	@Autowired
	UserService service;
	
	List<User> users;
	
	@Before
	public void setUp() {
		// 경계값 테스트 값
		users = Arrays.asList(
			new User("1","1","1",Level.BASIC, 49, 0),	
			new User("2","2","2",Level.BASIC, 50, 0),	
			new User("3","3","3",Level.SILVER, 60, 29),	
			new User("4","4","4",Level.SILVER, 60, 30),	
			new User("5","5","5",Level.GOLD, 100, 100)	
		);
	}
	
	@Test
	public void upgradeLevels () {
		dao.deleteAll();
		
		for (User u : users) dao.add(u);
		
		service.upgradeLevels();
		checkLevel(users.get(0), Level.BASIC);
		checkLevel(users.get(1), Level.SILVER);
		checkLevel(users.get(2), Level.SILVER);
		checkLevel(users.get(3), Level.GOLD);
		checkLevel(users.get(4), Level.GOLD);
	}
	
	@Test
	public void newUserLevel () {
		dao.deleteAll();
		
		User user1 = new User();
		user1.setId("1");
		user1.setName("1");
		user1.setPassword("1");
		
		User user2 = new User("2", "2", "2", Level.SILVER, 50, 0);
		
		service.add(user1); 
		service.add(user2); 
		
		User resultUser1 = dao.get(user1.getId());
		User resultUser2 = dao.get(user2.getId());
		checkLevel(resultUser1, Level.BASIC);
		checkLevel(resultUser2, Level.SILVER);
	}
	
	
	private void checkLevel (User user, Level expectedLevel) {
		User userUpdate = dao.get(user.getId());
		assertThat(userUpdate.getLevel(), is(expectedLevel));
	}
}
