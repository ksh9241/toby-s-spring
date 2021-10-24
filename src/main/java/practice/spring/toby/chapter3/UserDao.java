package practice.spring.toby.chapter3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

public abstract class UserDao{
	@Autowired
	private DataSource dataSource;
	
	Connection conn;
	PreparedStatement ps;
	ResultSet rs;
	
	
	public UserDao() {}
	
	
	public void add(User user) throws ClassNotFoundException, SQLException {
		try {
			conn = dataSource.getConnection();
			
			ps = conn.prepareStatement("insert into users(id, name, password) values(?, ?, ?)");
			
			ps.setString(1, user.getId());
			ps.setString(2, user.getName());
			ps.setString(3, user.getPassword());
			
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
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
	
	public void deleteAll() throws SQLException, ClassNotFoundException {
		try {
			conn = dataSource.getConnection(); // 변히자 읺는 부분  ↑ 
			
			//ps = conn.prepareStatement("DELETE FROM users");  // 변하는 부분
			ps = makeStatement(conn);
			
			ps.execute();						// 변히자 읺는 부분  ↓
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();	
		}
		
		
	}
	
//	private PreparedStatement makeStatement(Connection conn) throws SQLException {
//		ps = conn.prepareStatement("DELETE FROM users");
//		return ps;
//	}


	public int getCount() throws SQLException, ClassNotFoundException {
		conn = dataSource.getConnection();
		
		ps = conn.prepareStatement("SELECT COUNT(*) as count FROM users");
		
		rs = ps.executeQuery();
		rs.next();
		int result = rs.getInt("count");
		return result;
	}
	
	public void close () throws SQLException {
		if (rs != null) rs.close();
		if (ps != null) ps.close();
		if (conn != null) conn.close();
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	// 템플릿 메서드 패턴을 이용한 동적 기능 확장
	abstract protected PreparedStatement makeStatement (Connection c) throws SQLException;
}
