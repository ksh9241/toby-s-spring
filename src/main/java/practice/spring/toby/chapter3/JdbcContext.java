package practice.spring.toby.chapter3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

public class JdbcContext {
	private DataSource dataSource;
	
	// DataSource 타입 빈을 DI 받을 수 있게 준비해둔다.
	public void setDataSource (DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void WorkWithStatementStrategy(StatementStrategy stmt) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = dataSource.getConnection();
			ps = stmt.makePreparedStatement(conn);
			
			ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			if (ps != null) ps.close();
			if (conn != null) conn.close();
		}
	}
}
