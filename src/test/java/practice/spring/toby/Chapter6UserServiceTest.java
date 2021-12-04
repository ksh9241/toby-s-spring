package practice.spring.toby;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static practice.spring.toby.chapter5.UserServiceImple.MIN_LOGCOUNT_FOR_SILVER;
import static practice.spring.toby.chapter5.UserServiceImple.MIN_RECCOMEND_FOR_GOLD;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import practice.spring.toby.chapter6.Level;
import practice.spring.toby.chapter6.MockMailSender;
import practice.spring.toby.chapter6.MockUserDao;
import practice.spring.toby.chapter6.Target;
import practice.spring.toby.chapter6.TestUserServiceException;
import practice.spring.toby.chapter6.TransactionHandler;
import practice.spring.toby.chapter6.User;
import practice.spring.toby.chapter6.UserDao;
import practice.spring.toby.chapter6.UserService;
import practice.spring.toby.chapter6.UserServiceImple;
import practice.spring.toby.chapter6.UserServiceTx;

@RunWith(SpringJUnit4ClassRunner.class)
//@WebAppConfiguration
@ContextConfiguration("/chapter6/applicationContext.xml")
public class Chapter6UserServiceTest {

	@Autowired
	UserDao userDao;
	
	@Autowired
	PlatformTransactionManager transactionManager;
	
	@Autowired
	UserService testUserService;
	
	MockMailSender mailSender;
	
	// 사용안함 (DefaultAdvisorAutoProxy 중)
	UserServiceTx service;
	UserServiceImple imple;
	
	
	List<User> users;
	
	@Before
	public void setUp() {
		users = Arrays.asList(
				new User("1","1","1",Level.BASIC,MIN_LOGCOUNT_FOR_SILVER - 1, 0, "NO"),	
				new User("2","2","2",Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "YES"),	
				new User("3","3","3",Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD - 1, "NO"),	
				new User("4","4","4",Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD, "YES"),	
				new User("5","5","5",Level.GOLD, 100, 100, "NO")	
			);
		
		//service = new UserServiceTx();
	}
	
	//@Test
	public void transactionSync() {
		DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition(); // 트랜잭션 정의는 기본 값을 사용한다.
		TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);	// 트랜잭션 매니저에게 트랜잭션을 요청한다.
		
		try {
			testUserService.add(users.get(0));
			testUserService.add(users.get(1));
			assertThat(userDao.getCount(), is(2));
		}finally {
			transactionManager.rollback(txStatus);
		}
	}
	
	@Test
	@Transactional
	@Rollback(false)
	public void transactionSync_Annotation() {
		testUserService.deleteAll();
		testUserService.add(users.get(0));
		testUserService.add(users.get(1));
	}
	
	@Test
	public void readOnlyTransactionAttribute() {
		testUserService.getAll();
	}
	
	//@Test
	public void upgradeAllOrNothing() throws Exception{
		userDao.deleteAll();
		users.forEach(user -> userDao.add(user));
		
		try {
			testUserService.upgradeLevels();
			fail("TestUserServiceException expected");
		} catch (TestUserServiceException e) {
		}
		
		checkLevel(users.get(1), false);
	}
	
	@Test
	public void methodSignaturePointcut() throws SecurityException, NoSuchMethodException, ClassNotFoundException {
		AspectJExpressionPointcut pointcut  = new AspectJExpressionPointcut();
		
		Method[] methods = Class.forName("practice.spring.toby.chapter6.Target").getDeclaredMethods();
		for (Method m : methods) {
			System.out.println(m.getName() +" "+ m.getReturnType());
		}
		
		// Target Class minus() 메서드의 시그니처
		pointcut.setExpression("execution(* minus(..))");
		
		// Target.minus()
		assertThat(pointcut.getClassFilter().matches(Target.class) && pointcut.getMethodMatcher().matches(Target.class.getMethod("minus", int.class, int.class), null), is(true));
		
		// Target.plus()
		assertThat(pointcut.getClassFilter().matches(Target.class) && pointcut.getMethodMatcher().matches(Target.class.getMethod("plus", int.class, int.class), null), is(false));
		
		// Bean.method()
		assertThat(pointcut.getClassFilter().matches(Bean.class) && pointcut.getMethodMatcher().matches(Target.class.getMethod("method"), null), is(false));
	}
	
	//@Test
	public void upgradeAllOrNothing_transactionHandler() throws Exception{
		imple = new UserServiceImple(users.get(3).getId());
		imple.setUserDao(userDao);
		
		mailSender = new MockMailSender();
		imple.setMailSender(mailSender);
		
		TransactionHandler txHandler = new TransactionHandler();
		txHandler.setTarget(imple);
		//txHandler.setTransactionManager(transactionManager);
		txHandler.setPattern("upgradeLevels");
		UserService txUserService = (UserService) Proxy.newProxyInstance(getClass().getClassLoader(),new Class[] {UserService.class} , txHandler);
		
		userDao.deleteAll();
			
		for (int i = 0; i < users.size(); i++) {
			userDao.add(users.get(i));
		}
		
		try {
			service.upgradeLevels();
			fail("TestUserServiceException expected");
		} catch (Exception e) {
			
		}
		checkLevel(users.get(1), false);
	}
	
	//@Test
	public void upgradeLevels () throws Exception {
		MockMailSender mailSender = new MockMailSender();
		imple = new UserServiceImple();
		
		MockUserDao mockUserDao = new MockUserDao(this.users);
		
		
		imple.setUserDao(mockUserDao);
		imple.setMailSender(mailSender);
		
		imple.upgradeLevels();
		
		List<User> updated = mockUserDao.getUpdate();
		assertThat(updated.size(), is(2));
		checkUserAndLevel(updated.get(0), "2", Level.SILVER);
		checkUserAndLevel(updated.get(1), "4", Level.GOLD);
		
		List<String> request = mailSender.getRequests();
		assertThat(request.size(), is(2));
		assertThat(request.get(0), is(users.get(1).getEmail()));
		assertThat(request.get(1), is(users.get(3).getEmail()));
	}
	
	//@Test
	public void upgradeLevels_Mockito () throws Exception {
		imple = new UserServiceImple();
		UserDao mockUserDao = Mockito.mock(UserDao.class);
		Mockito.when(mockUserDao.getAll()).thenReturn(this.users);
		imple.setUserDao(mockUserDao);
		
		MailSender mockMailSender = Mockito.mock(MailSender.class);
		imple.setMailSender(mockMailSender);
		
		imple.upgradeLevels();
		
		Mockito.verify(mockUserDao, Mockito.times(2)).update(any(User.class));
		Mockito.verify(mockUserDao, Mockito.times(2)).update(any(User.class));
		Mockito.verify(mockUserDao, Mockito.times(1)).update(users.get(1));
		assertThat(users.get(1).getLevel(), is(Level.SILVER));
		Mockito.verify(mockUserDao).update(users.get(3));
		assertThat(users.get(3).getLevel(), is(Level.GOLD));
		
		ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
		Mockito.verify(mockMailSender, Mockito.times(2)).send(mailMessageArg.capture());
		List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
		assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
		assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));
	}
	
	// id와 level을 확인하는 간단한 헬퍼 메서드
	private void checkUserAndLevel (User updated, String expectedId, Level expectedLevel) {
		assertThat(updated.getId(), is(expectedId));
		assertThat(updated.getLevel(), is(expectedLevel));
	}
	
	private void checkLevel (User user, boolean upgraded) {
		User userUpdate = userDao.get(user.getId());
		if (upgraded) {
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel())); // 업그레이드가 일어났는지 확인
		} else {
			assertThat(userUpdate.getLevel(), is(user.getLevel())); 			// 업그레이드가 일어나지 않았는지 확인
		}
		
	}
}
