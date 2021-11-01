package practice.spring.toby;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import practice.spring.toby.chapter4.User;
import practice.spring.toby.chapter4.UserDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/chapter4/applicationContext.xml")
public class ExceptionExampleTest {

	@Autowired
	UserDao dao = new UserDao();
	
	@Test
	public void addException() throws ClassNotFoundException, SQLException {
		dao.deleteAll();
		dao.add(new User("123","123","123"));
		dao.add(new User("123","123","123"));
		
		//assertThat(dao.getUserList().size(), is(0));
	}
}
