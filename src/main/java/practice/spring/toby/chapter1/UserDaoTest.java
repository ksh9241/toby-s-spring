package practice.spring.toby.chapter1;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import java.sql.SQLException;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class) // 스프링의 테스트 컨텍스트 프레임워크의 JUnit 확장기능 지정 ( ApplicationContext를 만들고 관리하는 작업 )
@ContextConfiguration(locations = "/chapter1/applicationContext.xml") // 테스트컨텍스트가 자동으로 만들어 줄 애플리케이션 컨텍스트의 위치 지정
public class UserDaoTest {
	User user1;
	User user2;
	User user3;
	
	@Autowired
	ApplicationContext context;
	
	@Inject
	UserDao dao;
	// static method를 지원하여 UserDaoTest 클래스를 실행할 때 단 한번만 실행된다.
	/**
	static ApplicationContext context;
	@BeforeClass
	public void newApplicationContext() {
		context = new GenericXmlApplicationContext("chapter1/applicationContext.xml");
	}
	*/
	
	@Before
	public void setUp() {
		//dao = context.getBean("userDao", UserDao.class); ApplicationContext에 있는 빈을 찾아서 의존성 주입을 해주는데 @Autowired를 사용하면 메서드 내에서가 아닌 인스턴스 변수에 직접 맞는 타입의 빈을 주입해준다.
		
		user1 = new User("gyumee", "박성철", "springno1");
		user2 = new User("leegw700", "이길원", "springno2");
		user3 = new User("bumjin", "박범진", "springno3");
		
		System.out.println("context : " + this.context);
		System.out.println("this : " + this);
	}
	
	@Test
	public void addAndGet () throws ClassNotFoundException, SQLException {
		// UserDao 와 SimpleConnectionMaker 사이의 의존관계 설정 효과
//		ApplicationContext context2 = new AnnotationConfigApplicationContext(DaoFactory.class);
		//ApplicationContext context = new ClassPathXmlApplicationContext("chapter1/applicationContext.xml");
		// 의존관계 주입을 통한 방법.
		
		// 의존관계 검색을 이용한 방법
		//UserDao dao = new UserDao();
		
		
		dao.add(user1);
		dao.add(user2);
		dao.add(user3);
		
		User copyUser = dao.get(user1.getId());
		
		dao.deleteAll();
		// JUnit 사용
		assertThat(user1.getName(), is(copyUser.getName()));
		assertThat(user1.getPassword(), is(copyUser.getPassword()));
		
		System.out.println(user1.getId() + " 조회 성공");
		
	}
	
	@Test
	public void getCount () throws Exception{
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		assertThat(dao.getCount(), is(1));
		
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		dao.add(user3);
		assertThat(dao.getCount(), is(3));
	}
	
	@Test(expected = EmptyResultDataAccessException.class) // 테스트 중에 발생할 것으로 기대하는 예외 클래스를 지정해준다.
	public void getUserFailure() throws SQLException, ClassNotFoundException {
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.get("unknown_id"); // 이 메서드 실행 중에 예외가 발생해야 한다.
		
	}
	
	// JUnit Test 실행 ( RunAs -> JUnit을 실행하면 JUnitCore.main없이 테스트 가능하다. )
	public static void main(String[] args) {
		JUnitCore.main("practice.spring.toby.chapter1.UserDaoTest");
	}
	
	
}
