package practice.spring.toby.chapter1;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import java.sql.SQLException;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

public class UserDaoTest {
	ApplicationContext context;
	
	@Test
	public void addAndGet () throws ClassNotFoundException, SQLException {
		// UserDao 와 SimpleConnectionMaker 사이의 의존관계 설정 효과
//		ApplicationContext context2 = new AnnotationConfigApplicationContext(DaoFactory.class);
		context = new GenericXmlApplicationContext("chapter1/applicationContext.xml");
		//ApplicationContext context = new ClassPathXmlApplicationContext("chapter1/applicationContext.xml");
		// 의존관계 주입을 통한 방법.
		UserDao dao = context.getBean("userDao", UserDao.class);
		
		// 의존관계 검색을 이용한 방법
		//UserDao dao = new UserDao();
		
		User user = new User("whiteship", "백기선", "married");
		
		dao.add(user);
		
		System.out.println(user.getId() + " 등록 성공");
		
		User user2 = dao.get(user.getId());
		// 수동확인
		System.out.println(user2.getName());
		System.out.println(user.getPassword());
		
		// 자동확인
//		if (!user.getName().equals(user2.getName())) {
//			System.out.println("테스트 실패 (name) ");
//		} else if (!user.getPassword().equals(user2.getPassword() + "1")) {
//			System.out.println("테스트 실패 (password) ");
//		} else {
//			System.out.println("조회 테스트 성공");
//		}
		
		dao.deleteAll();
		// JUnit 사용
		assertThat(user.getName(), is(user2.getName()));
		assertThat(user.getPassword(), is(user2.getPassword()));
		
		System.out.println(user.getId() + " 조회 성공");
		
	}
	
	@Test
	public void getCount () throws Exception{
		context = new GenericXmlApplicationContext("chapter1/applicationContext.xml");
		UserDao dao = context.getBean("userDao", UserDao.class);
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(new User("testId1", "testName", "testPw"));
		assertThat(dao.getCount(), is(1));
		
		dao.add(new User("testId2", "testName", "testPw"));
		assertThat(dao.getCount(), is(2));
		
		dao.add(new User("testId3", "testName", "testPw"));
		assertThat(dao.getCount(), is(3));
		
	}
	
	// JUnit Test 실행 ( RunAs -> JUnit을 실행하면 JUnitCore.main없이 테스트 가능하다. )
	public static void main(String[] args) {
		JUnitCore.main("practice.spring.toby.chapter1.UserDaoTest");
	}
	
	
}
