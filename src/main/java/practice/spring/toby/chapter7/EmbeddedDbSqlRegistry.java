package practice.spring.toby.chapter7;

import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class EmbeddedDbSqlRegistry implements UpdatableSqlRegistry {
	SimpleJdbcTemplate jdbc;
	TransactionTemplate transactionTemplate;
	
	public void setDataSource (DataSource dataSource) {
		// DataSource를 DI 받아서 SimpleJdbcTemplate 형태로 저장해두고 사용한다.
		jdbc = new SimpleJdbcTemplate(dataSource);
		
		// dataSource로 TransactionManager를 만들고 이를 이용해 TransactionTemplate을 생성한다.
		transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
		
	}

	@Override
	public void registerSql(String key, String sql) {
		jdbc.update("INSERT INTO SQLMAP(KEY_, SQL_) VALUES (?, ?)", key, sql);
	}

	@Override
	public String findSql(String key) throws SqlNotFoundException {
		try {
			return jdbc.queryForObject("SELECT SQL_ FROM SQLMAP WHERE KEY_ = ?", String.class, key);
		} catch (EmptyResultDataAccessException e) {
			throw new SqlNotFoundException(key + " 에 해당하는 SQL을 찾을 수 없습니다.");
		}
	}

	@Override
	public void updateSql(String key, String sql) throws SqlUpdateFailureException {
		int result = jdbc.update("UPDATE SQLMAP SET SQL_ = ? WHERE KEY_ = ?", sql, key);
		
		if (result == 0) {
			throw new SqlUpdateFailureException(key + " 에 해당하는 SQL을 찾을 수 없습니다.");
		}
	}

	@Override
	public void updateSql(final Map<String, String> sqlmap) throws SqlUpdateFailureException {
		// 익명 내부 클래스로 사용하기 위해 프로퍼티를 final로 받는다.
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				for (Map.Entry<String, String> entry : sqlmap.entrySet()) {
					updateSql(entry.getKey(), entry.getValue());
				}
			}
		});
	}
}
