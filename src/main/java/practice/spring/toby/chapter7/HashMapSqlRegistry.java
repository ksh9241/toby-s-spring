package practice.spring.toby.chapter7;

import java.util.HashMap;
import java.util.Map;

// HashMap을 이용하는 SqlRegistry 클래스
public class HashMapSqlRegistry implements SqlRegistry {
	Map<String, String> sqlMap = new HashMap<>();
	
	@Override
	public void registerSql(String key, String sql) {
		sqlMap.put(key, sql);
	}

	@Override
	public String findSql(String key) throws SqlNotFoundException {
		String sql = sqlMap.get(key);
		if (sql == null) {
			throw new SqlNotFoundException(key + " 를 통한 SQL을 찾을 수 없습니다.");
		}
		return sql;
	}
}
