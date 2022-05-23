package practice.spring.tobyVer2.chapter2;

import static org.junit.Assert.assertNotNull;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ChapterConfig.class})
public class DBConnectionTest {

	@Autowired
	DataSource dataSource;
	SimpleJdbcTemplate simpleJdbcTemplate;
	
	@Before
	void init () {
		simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}

	@Test
	void Test() {
		assertNotNull(new Object());
	}
	
}
