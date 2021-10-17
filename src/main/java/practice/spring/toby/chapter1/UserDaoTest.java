package practice.spring.toby.chapter1;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

public class UserDaoTest {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// UserDao 와 SimpleConnectionMaker 사이의 의존관계 설정 효과
//		ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
		ApplicationContext context = new GenericXmlApplicationContext("chapter1/applicationContext.xml");
		//ApplicationContext context = new ClassPathXmlApplicationContext("chapter1/applicationContext.xml");
		// 의존관계 주입을 통한 방법.
		UserDao dao = context.getBean("userDao", UserDao.class);
		
		// 의존관계 검색을 이용한 방법
		//UserDao dao = new UserDao();
		
		User user = new User();
		user.setId("whiteship");
		user.setName("백기선");
		user.setPassword("married");
		
		dao.add(user);
		
		System.out.println(user.getId() + " 등록 성공");
		User user2 = dao.get(user.getId());
		System.out.println(user2.getName());
		
		System.out.println(user.getPassword());
		
		System.out.println(user.getId() + " 조회 성공");
	}
}
