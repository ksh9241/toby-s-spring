package practice.spring.toby.chapter1;

import java.sql.SQLException;

public class UserDaoTest {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// UserDao 와 SimpleConnectionMaker 사이의 의존관계 설정 효과
		UserDao dao = new NUserDao(new DSimpleConnectionMaker());
		
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
