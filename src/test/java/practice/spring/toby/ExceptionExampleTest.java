package practice.spring.toby;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import practice.spring.toby.chapter4.User;
import practice.spring.toby.chapter4.UserDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/chapter4/applicationContext.xml")
public class ExceptionExampleTest {

	@Autowired
	UserDao dao;
	
	@Autowired
	DataSource dataSource;
	
	@Test(expected = DuplicateKeyException.class)
	public void addException() {
		dao.deleteAll();
		dao.add(new User("123","123","123"));
		dao.add(new User("123","123","123"));
		
	}
	
	@Test
	public void userCountTest() {
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
	}
	
	// DataSource를 사용해 SQLException에서 직접 DuplicateKeyException으로 전환하는 기능
	@Test
	public void sqlExceptionTranslate() {
		dao.deleteAll();
		
		try {
			dao.add(new User("123","123","123"));
			dao.add(new User("123","123","123"));
		} catch (DuplicateKeyException e) {
			SQLException sqEx = (SQLException) e.getRootCause(); // getRootCause() : 중첩되어 있는 SQLException을 가져올 수 있다.
			SQLExceptionTranslator set = // 코드를 이용한 SQLException 전환 
					new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
			
			assertThat(set.translate(null, null, sqEx), is(DuplicateKeyException.class));
		}
	}
}
