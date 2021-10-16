package practice.spring.toby.chapter1;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class UserDaoTest {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// UserDao 와 SimpleConnectionMaker 사이의 의존관계 설정 효과
		ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
		UserDao dao = context.getBean("userDao", UserDao.class);
		UserDao dao2 = context.getBean("userDao", UserDao.class);
		
		System.out.println(dao.hashCode());
		System.out.println(dao2.hashCode());
		
//		User user = new User();
//		user.setId("whiteship");
//		user.setName("백기선");
//		user.setPassword("married");
//		
//		dao.add(user);
//		
//		System.out.println(user.getId() + " 등록 성공");
//		User user2 = dao.get(user.getId());
//		System.out.println(user2.getName());
//		
//		System.out.println(user.getPassword());
//		
//		System.out.println(user.getId() + " 조회 성공");
	}
}
