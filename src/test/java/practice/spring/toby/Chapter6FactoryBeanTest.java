package practice.spring.toby;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static practice.spring.toby.chapter5.UserServiceImple.MIN_LOGCOUNT_FOR_SILVER;
import static practice.spring.toby.chapter5.UserServiceImple.MIN_RECCOMEND_FOR_GOLD;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import practice.spring.toby.chapter6.Hello;
import practice.spring.toby.chapter6.HelloTarget;
import practice.spring.toby.chapter6.Level;
import practice.spring.toby.chapter6.Message;
import practice.spring.toby.chapter6.MessageFactoryBean;
import practice.spring.toby.chapter6.MockMailSender;
import practice.spring.toby.chapter6.TxProxyFactoryBean;
import practice.spring.toby.chapter6.UppercaseHandler;
import practice.spring.toby.chapter6.User;
import practice.spring.toby.chapter6.UserDao;
import practice.spring.toby.chapter6.UserService;
import practice.spring.toby.chapter6.UserServiceImple;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/chapter6/applicationContext.xml")
public class Chapter6FactoryBeanTest {

	@Autowired
	ApplicationContext context;
	
	@Autowired
	UserDao userDao;
	
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
	}
	
	@Test
	public void pointcutAdvisor () {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget()); 				
		
		// 메서드 이름을 비교해서 대상을 선정하는 알고리즘을 제공하는 포인트컷
		NameMatchMethodPointcut pointCut = new NameMatchMethodPointcut(); 
		pointCut.setMappedName("sayH*");
		
		// 포인트컷과 어드바이스를 Advisor로 묶어서 한번에 추가
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointCut, new UppercaseAdvice()));
		
		Hello proxiedHello = (Hello) pfBean.getObject();	
		
		assertThat(proxiedHello.sayHello("Kim"), is("HELLO KIM"));
		assertThat(proxiedHello.sayHi("Kim"), is("HI KIM"));
		assertThat(proxiedHello.sayThankYou("Kim"), is("Thank You Kim")); // 메서드 이름이 포인트컷의 선정조건에 맞지 않으므로 부가기능이 적용되지 않는다.
	}
	
	@Test
	public void simpleProxy() {
		Hello proxiedHello = (Hello) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {Hello.class}, new UppercaseHandler(new HelloTarget()));
		
		assertThat(proxiedHello.sayHello("Kim"), is("HELLO KIM"));
		assertThat(proxiedHello.sayHi("Kim"), is("HI KIM"));
		assertThat(proxiedHello.sayThankYou("Kim"), is("THANK YOU KIM"));
	}
	
	@Test
	public void proxyFactoryBean() {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget()); 				// 타깃 설정
		pfBean.addAdvice(new UppercaseAdvice());			// 부가기능 설정
		
		Hello proxiedHello = (Hello) pfBean.getObject();	// FactoryBean이므로 getObject()로 생성된 프록시를 가져온다.
		
		assertThat(proxiedHello.sayHello("Kim"), is("HELLO KIM"));
		assertThat(proxiedHello.sayHi("Kim"), is("HI KIM"));
		assertThat(proxiedHello.sayThankYou("Kim"), is("THANK YOU KIM"));
	}
	
	static class UppercaseAdvice implements MethodInterceptor {
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			// 리플렉션의 Method와 달리 메서드가 실행 시 타깃 오브젝트를 전달할 필요가 없다. MethodInvocation은 메서드 정보와 함께 타깃 오브젝트를 알고 있기 때문이다.
			String ret = (String) invocation.proceed();
			return ret.toUpperCase();
		}
	}
	
	@Test
	@DirtiesContext // 다이나믹 프록시 팩토리 빈을 직접 만들어 사용할 때는 없앴다가 다시 등장한 컨텍스트 무효화 어노테이션
	public void upgradeAllOrNothing() throws Exception {
		MockMailSender mailSender = new MockMailSender();
		
		UserServiceImple testUserService = new UserServiceImple(users.get(3).getId());
		testUserService.setUserDao(userDao);
		testUserService.setMailSender(mailSender);
		
		TxProxyFactoryBean txProxyFactoryBean = context.getBean("&userService", TxProxyFactoryBean.class);
		txProxyFactoryBean.setTarget(testUserService);
		UserService txUserService = (UserService) txProxyFactoryBean.getObject(); // 변경된 타깃 설정을 이용해서 트랜잭션 다이나믹 프록시 오브젝트를 다시 생성한다.
		
		userDao.deleteAll();
		for (User user : users) userDao.add(user);
		
		try {
			txUserService.upgradeLevels();
			fail("TestUserServiceException expected");
		} catch (Exception e) {
		}
		
		checkLevel(users.get(1), false);
	}
	
	//@Test
	public void getMessageFromFactoryBean() {
		Object message = context.getBean("message"); 
		System.out.println("message == "+ (message instanceof Message));
		assertThat(message, is(Message.class));
		assertThat(((Message)message).getMessage(), is("Factory Bean"));
	}
	
	//@Test
	public void getFactoryBean() throws Exception {
		Object factory = context.getBean("&message"); // &가 붙고 안 붙고에 따라 getBean() 메서드가 돌려주는 오브젝트가 달라진다.
		assertThat(factory, is(MessageFactoryBean.class));
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
