package practice.spring.toby.chapter3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

public class UserDao{
	@Autowired
	private static DataSource dataSource;
	
	Connection conn;
	PreparedStatement ps;
	ResultSet rs;
	
	
	public UserDao() {}
	
	// 메서드 내의 로컬 클래스로 재정의하여 전략패턴 사용 (장점 : 메서드마다 생성되는 외부클래스를 안만들고 전략패턴 사용 가능)
	public void add (final User user) throws SQLException, ClassNotFoundException {
		// add의 파라미터인 User를 내부클래스에서 사용하기 위해 final로 선언해줘야 한다.
		class AddStatementStrategy implements StatementStrategy{ // 중첩클래스 (nested class)
			
			@Override
			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
				PreparedStatement ps = c.prepareStatement("INSERT INTO users(id, name, password) VALUES (?, ?, ?)");
				ps.setString(1, user.getId());
				ps.setString(2, user.getName());
				ps.setString(3, user.getPassword());
				
				return ps;
			}
		}
		StatementStrategy st = new AddStatementStrategy();
		jdbcContextWithStatementStrategy(st);
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
		jdbcContextWithStatementStrategy(st);
	}
	
	public User get(String id) throws ClassNotFoundException, SQLException {
		
		User user = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
			ps.setString(1, id);
			
			rs = ps.executeQuery();
			
			if(rs.next()) {
				user = new User();
				user.setId(rs.getString("id"));
				user.setName(rs.getString("name"));
				user.setPassword(rs.getString("password"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		if (user == null) throw new EmptyResultDataAccessException(1);
		
		return user;
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
		// 전략 오브젝트를 익명 내부 클래스로 생성
		StatementStrategy st = new StatementStrategy() {
			
			@Override
			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
				PreparedStatement ps = c.prepareStatement("DELETE FROM users");
				return ps;
			}
		};
		
		jdbcContextWithStatementStrategy(st); // 컨텍스트
	}
	
	public void close () throws SQLException {
		if (rs != null) rs.close();
		if (ps != null) ps.close();
		if (conn != null) conn.close();
	}

	// applicationContext 
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
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
	
	// 메서드로 분리한 try/catch/finally 컨텍스트 코드
	public static void jdbcContextWithStatementStrategy (StatementStrategy stmt) throws SQLException, ClassNotFoundException {
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = dataSource.getConnection();
			ps = stmt.makePreparedStatement(conn);
			ps.executeUpdate();
		} catch (Exception e) {
			System.out.println("들어옴");
			e.printStackTrace();
			throw e;
		} finally {
			if (ps != null) ps.close();
			if (conn != null) conn.close();
		}
	}
	
}


