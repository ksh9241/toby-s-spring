package practice.spring.toby.chapter4;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import practice.spring.toby.chapter3.JdbcContext;
import practice.spring.toby.chapter3.StatementStrategy;

public class UserDaoJdbc implements UserDao{
	private JdbcContext context;
	
	// 스프링이 제공하는 JDBC 기본 템플릿
	private JdbcTemplate template;
	Connection conn;
	PreparedStatement ps;
	ResultSet rs;
	
	DataSource dataSource;
	
	// 조회 시 공통으로 사용되는 RowMapper를 인스턴스 변수로 생성하여 중복 제거.
	private RowMapper<User> userMapper = new RowMapper<User>() {
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new User(rs.getString(1), rs.getString(2), rs.getString(3));
		}
		
	};
	
	public UserDaoJdbc() {}
	
	// applicationContext (의존성 주입을 위한 setter)
	public void setDataSource(DataSource dataSource) { // 수정자 메서드이면서 JdbcContext에 대한 생성, DI 작업을 동시에 수행한다.
		template = new JdbcTemplate(dataSource);
		context = new JdbcContext();
		context.setDataSource(dataSource);
		this.dataSource = dataSource;
	}
	
	
	public void add (final User user) throws DuplicateUserIdException{
		template.update("INSERT INTO users(id, name, password) VALUES (?, ?, ?)", user.getId(), user.getName(), user.getPassword());
		
		// JdbcTemplate의 예외를 다른 예외로 던져주고 싶을 때
		try {
			
		} catch (DuplicateKeyException e) {
			throw new DuplicateUserIdException(e); // JdbcTemplate의 DuplicateKeyException을 사용하지 못하거나 사용하고 싶지않을 때 캐치에서 잡아서 다른 예외로 던질 수도 있다.
		}
		
//		try {
//			this.context.WorkWithStatementStrategy(new StatementStrategy() {
//				@Override
//				public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//					ps = c.prepareStatement("INSERT INTO users(id, name, password) VALUES (?, ?, ?)");
//					ps.setString(1, user.getId());
//					ps.setString(2, user.getName());
//					ps.setString(3, user.getPassword());
//					
//					return ps;
//				}
//			});
//		} catch (SQLException e) {
//			//if (e.getErrorCode() == 1) { // SQLIntegrityConstraintViolationException 의 에러코드가 1임. (pk값 중복)
//			if ("23000".equals(e.getSQLState())) { // SQLIntegrityConstraintViolationException 의 경우 "23000"의 값을 반환함.
//				// 예외 전환 (중첩 예외)
//				throw new DuplicateUserIdException(e);
//			} else {
//				throw new RuntimeException(e); // 언체크 예외로 예외 포장 (다른 메서드로 throws하지 않기 위함)
//			}
//			
//		}		
	}
	
	
	public User get(String id) {
		return template.queryForObject("SELECT * FROM users WHERE id = ?", new Object[] {id}, userMapper);
	}
	
	public List<User> getUserList () {
		 List<User> list = template.query("SELECT * FROM users ORDER BY id", userMapper);
		return list;
	}
	
	public int getCount () {
		try {
			return template.queryForInt("SELECT COUNT(*) FROM users");
		} catch (Exception e) {
			throw e;
		}
	}

	// 전략패턴의 모습
	// 컨텍스트 (DBConnection부터 SQL까지) 부분을 분리하여 deleteAll 메서드가 클라이언트가 된다.
	public void deleteAll() {
		template.update(
			new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					return conn.prepareStatement("DELETE FROM users");
				}
			}
		);
		
		// JdbcTemplate 내장 콜백을 사용하였다. 위와 동일하다.
		template.update("DELETE FROM users");
	}
	
	public void close () throws SQLException {
		if (rs != null) rs.close();
		if (ps != null) ps.close();
		if (conn != null) conn.close();
	}
}


