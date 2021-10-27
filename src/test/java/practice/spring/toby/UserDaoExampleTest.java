package practice.spring.toby;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import practice.spring.toby.chapter3.JdbcContext;
import practice.spring.toby.chapter3.User;
import practice.spring.toby.chapter3.UserDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/chapter3/applicationContext.xml")
public class UserDaoExampleTest {
	
	@Autowired
	UserDao dao;
	
	User user;
	User user1;
	@Before
	public void setUp() {
		user = new User("1234", "1234", "1234");
		user1 = new User("아이디", "이름", "비밀번호");
	}
	
	@Test
	public void deleteAll() throws ClassNotFoundException, SQLException {
		dao.deleteAll();
		dao.add(user);
		//dao.anonymousClassAdd(user1);
	}
}
