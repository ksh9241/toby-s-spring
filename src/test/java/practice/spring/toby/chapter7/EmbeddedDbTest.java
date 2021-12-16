package practice.spring.toby.chapter7;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

public class EmbeddedDbTest {

	EmbeddedDatabase db;
	SimpleJdbcTemplate template;
	
	@Before
	public void setUp() {
		db = new EmbeddedDatabaseBuilder()
				.setType(HSQL)
				.addScript("/chapter7/schema.sql")
				.addScript("/chapter7/data.sql")
				.build();
	
		template = new SimpleJdbcTemplate(db); // EmbeddedDatabase는 DataSource의 서브 인터페이스이므로 DataSource를 필요로 하는 SimpleJdbcInsert를 만들 때 사용할 수 있다.
	}
	
	@After
	public void tearDown() {
		db.shutdown();
	}
	
	@Test
	public void initData() {
		assertThat(template.queryForInt("SELECT COUNT(1) FROM SQLMAP"), is(2));
		
		List<Map<String, Object>> list = template.queryForList("SELECT * FROM SQLMAP ORDER BY KEY_");
		
		assertThat((String)list.get(0).get("KEY_"), is("KEY1"));
		assertThat((String)list.get(0).get("SQL_"), is("SQL1"));
		assertThat((String)list.get(1).get("KEY_"), is("KEY2"));
		assertThat((String)list.get(1).get("SQL_"), is("SQL2"));
	}
}
