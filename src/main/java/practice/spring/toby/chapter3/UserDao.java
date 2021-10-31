package practice.spring.toby.chapter3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

public class UserDao{
	//private JdbcContext context;
	
	// 스프링이 제공하는 JDBC 기본 템플릿
	private JdbcTemplate template;
	Connection conn;
	PreparedStatement ps;
	ResultSet rs;
	
	// 조회 시 공통으로 사용되는 RowMapper를 인스턴스 변수로 생성하여 중복 제거.
	private RowMapper<User> userMapper = new RowMapper<User>() {
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new User(rs.getString(1), rs.getString(2), rs.getString(3));
		}
		
	};
	
	public UserDao() {}
	
	
	
	// JdbcContext를 DI받도록 만든다. (수정자 메서드를 이용한 JdbcContext 초기화를 하기때문에 사용하지 않는 메서드)
//	public void setJdbcContext(JdbcContext context) { 
//		this.context = context;
//	}
	
	// applicationContext (의존성 주입을 위한 setter)
	public void setDataSource(DataSource dataSource) { // 수정자 메서드이면서 JdbcContext에 대한 생성, DI 작업을 동시에 수행한다.
		template = new JdbcTemplate(dataSource);
	}
	
	// 메서드 내의 로컬 클래스로 재정의하여 전략패턴 사용 (장점 : 메서드마다 생성되는 외부클래스를 안만들고 전략패턴 사용 가능)
//	public void add (final User user) throws SQLException, ClassNotFoundException {
//		// add의 파라미터인 User를 내부클래스에서 사용하기 위해 final로 선언해줘야 한다.
//		class AddStatementStrategy implements StatementStrategy{ // 중첩클래스 (nested class)
//			
//			@Override
//			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//				PreparedStatement ps = c.prepareStatement("INSERT INTO users(id, name, password) VALUES (?, ?, ?)");
//				ps.setString(1, user.getId());
//				ps.setString(2, user.getName());
//				ps.setString(3, user.getPassword());
//				
//				return ps;
//			}
//		}
//		StatementStrategy st = new AddStatementStrategy();
//	}
	
	public void add (final User user) throws SQLException, ClassNotFoundException {
		
		// add의 파라미터인 User를 내부클래스에서 사용하기 위해 final로 선언해줘야 한다.
//		this.context.WorkWithStatementStrategy(new StatementStrategy() {
//			
//			@Override
//			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//				ps = c.prepareStatement("INSERT INTO users(id, name, password) VALUES (?, ?, ?)");
//				ps.setString(1, user.getId());
//				ps.setString(2, user.getName());
//				ps.setString(3, user.getPassword());
//				
//				return ps;
//			}
//		});
		
		template.update("INSERT INTO users(id, name, password) VALUES (?, ?, ?)", user.getId(), user.getName(), user.getPassword());
	}
	
	// 익명 내부 클래스를 통한 전략패턴
	public void anonymousClassAdd (final User user) throws ClassNotFoundException, SQLException {
		// 인터페이스를 생성자처럼 이용해서 오브젝트 생성
		StatementStrategy st = new StatementStrategy() {
			
			@Override
			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
				ps = c.prepareStatement("INSERT INTO users(id, name, password) VALUES (?, ?, ?)");
				ps.setString(1, user.getId());
				ps.setString(2, user.getName());
				ps.setString(3, user.getPassword());
				
				return ps;
			}
		};
		//jdbcContextWithStatementStrategy(st);
	}
	
	public User get(String id) throws ClassNotFoundException, SQLException {
//		User user = null;
//		try {
//			conn = dataSource.getConnection();
//			ps = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
//			ps.setString(1, id);
//			
//			rs = ps.executeQuery();
//			
//			if(rs.next()) {
//				user = new User();
//				user.setId(rs.getString("id"));
//				user.setName(rs.getString("name"));
//				user.setPassword(rs.getString("password"));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			close();
//		}
//		
//		if (user == null) throw new EmptyResultDataAccessException(1);
//		
//		return user;
		return template.queryForObject("SELECT * FROM users WHERE id = ?", new Object[] {id}, userMapper);
	}
	
	public List<User> getUserList () throws SQLException, ClassNotFoundException {
		 List<User> list = template.query("SELECT * FROM users ORDER BY id", userMapper);
		return list;
	}
	
	public int getCount () throws ClassNotFoundException, SQLException {
//		return template.query(new PreparedStatementCreator() /*첫 번째 콜백. Statement 생성*/ {
//			
//			@Override
//			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//				return con.prepareStatement("SELECT COUNT(*) FROM users");
//			}
//		}, new ResultSetExtractor<Integer>() {
//			@Override
//			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
//				rs.next();
//				return rs.getInt(1);
//			} // 두 번째 콜백 ResultSet으로 부터 값 추출
//			
//		});
		try {
			return template.queryForInt("SELECT COUNT(*) FROM users");
		} catch (Exception e) {
			throw e;
		}
	}
	
//	public void deleteAll() throws SQLException, ClassNotFoundException {
//		try {
//			System.out.println("connection ==" + dataSource.getConnection());
//			conn = dataSource.getConnection(); // 변히자 읺는 부분  ↑ 
//			
//			//ps = conn.prepareStatement("DELETE FROM users");  // 변하는 부분
//			
//			//ps = new UserDaoDeleteAll().makeStatement(conn);			// 템플릿 메서드 패턴
//			ps = new DeleteAllStatement().makePreparedStatement(conn);	// 전략 패턴
//			
//			ps.execute();						// 변히자 읺는 부분  ↓
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			close();	
//		}
//	}

	// 전략패턴의 모습
	// 컨텍스트 (DBConnection부터 SQL까지) 부분을 분리하여 deleteAll 메서드가 클라이언트가 된다.
	public void deleteAll() throws SQLException, ClassNotFoundException {
		//context.executeSql("DELETE FROM users");
		
		// JdbcTemplate의 콜백은 PreparedStatementCreator 인터페이스의 createPreparedStatement 메서드이다.
		// 템플릿으로부터 커넥션을 제공받아 PreparedStatement 만들어 돌려준다는 면에서 구조는 동일하다.
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
	
	// 템플릿 메서드 패턴을 이용한 동적 기능 확장
	// IllegalStateException: Failed to load ApplicationContext 예외 발생함
	//abstract protected PreparedStatement makeStatement (Connection c) throws SQLException;


	// 전략 패턴을 이용한 동적 기능 확장
//	@Override
//	public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//		ps = c.prepareStatement("DELETE FROM users");
//		return ps;
//	}
}


