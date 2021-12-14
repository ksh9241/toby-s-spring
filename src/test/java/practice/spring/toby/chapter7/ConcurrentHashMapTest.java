package practice.spring.toby.chapter7;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class ConcurrentHashMapTest {

	UpdatableSqlRegistry sqlRegistry;
	
	@Before
	public void setUp() {
		sqlRegistry = new ConcurrentHashMapSqlRegistry();
		sqlRegistry.registerSql("KEY1", "SQL1");
		sqlRegistry.registerSql("KEY2", "SQL2");
		sqlRegistry.registerSql("KEY3", "SQL3");
	}
	
	@Test
	public void find() {
		checkFindResult("SQL1", "SQL2", "SQL3");
	}
	
	// 반복적으로 검증하는 부분은 별도의 메서드로 분리해두면 테스트 코드가 깔끔해진다.
	private void checkFindResult(String expected1, String expected2, String expected3) {
		assertThat(sqlRegistry.findSql("KEY1"), is(expected1));
		assertThat(sqlRegistry.findSql("KEY2"), is(expected2));
		assertThat(sqlRegistry.findSql("KEY3"), is(expected3));
	}
	
	@Test(expected = SqlNotFoundException.class)
	public void unknownKey() {
		// 주어진 키에 해당하는 SQL을 찾을 수 없을 때 예외가 발생하는지 확인한다.
		sqlRegistry.findSql("SQL9999!@#$");
	}
	
	@Test
	public void updateSingle() {
		// 하나의 SQL을 변경하는 기능에 대한 테스트다.
		sqlRegistry.updateSql("KEY2", "Modified2");
		checkFindResult("SQL1", "Modified2", "SQL3");
	}
	
	@Test
	public void updateMulti() {
		Map<String, String> sqlmap = new HashMap<>();
		sqlmap.put("KEY1", "Modified1");
		sqlmap.put("KEY3", "Modified3");
		
		sqlRegistry.updateSql(sqlmap);
		checkFindResult("Modified1", "SQL2", "Modified3");
	}
	
	@Test(expected = SqlUpdateFailureException.class)
	public void updateWithNotExistingKey() {
		sqlRegistry.updateSql("SQL9999!@#$", "Modified2");
	}
}
