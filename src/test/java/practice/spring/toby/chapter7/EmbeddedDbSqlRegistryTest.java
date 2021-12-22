package practice.spring.toby.chapter7;

import static org.junit.Assert.fail;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

public class EmbeddedDbSqlRegistryTest extends AbstractUpdatableSqlRegistryTest{

	EmbeddedDatabase db;
	
	@Autowired
	UserDao dao;
	
	@Override
	protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
		db = new EmbeddedDatabaseBuilder()
				.setType(HSQL)
				.addScript("/chapter7/schema.sql")
				.build();
		EmbeddedDbSqlRegistry embeddedDbSqlRegistry = new EmbeddedDbSqlRegistry();
		embeddedDbSqlRegistry.setDataSource(db);
		
		return embeddedDbSqlRegistry;
	}
	
	@After
	public void tearDown() {
		db.shutdown();
	}
	
	@Test
	public void transactionalUpdate() {
		// 초기 상태 확인
		checkFind("SQL1", "SQL2", "SQL3");
		
		Map<String, String> sqlmap = new HashMap<>();
		sqlmap.put("KEY1", "Modified1");
		sqlmap.put("KEY9999!@#$", "Modified9999");
		
		try {
			sqlRegistry.updateSql(sqlmap);
			fail(); // 테스트를 강제로 실패하게 만들고 기대와 다르게 동작한 원인을 찾도록 해야한다.
		} catch (SqlUpdateFailureException e) {
			// 첫 번째  SQL은 정상적으로 수정했지만 트랜잭션이 롤백되기 때문에 다시 원래대로 돌아와야 한다.
			checkFind("SQL1", "SQL2", "SQL3"); 
		}
	}
	
}
