package practice.spring.toby.chapter1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
	
	private Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "practice", "1234");
		return conn;
	}
	
	public void add(User user) throws ClassNotFoundException, SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = conn.prepareStatement("insert into users(id, name, password) values(?, ?, ?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());
		
		ps.execute();
		
		close(null, ps);
	}
	
	public User get(String id) throws ClassNotFoundException, SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
		ps.setString(1, id);
		
		ResultSet rs = ps.executeQuery();
		rs.next();
		User user = new User();
		user.setId(rs.getString("id"));
		user.setName(rs.getString("name"));
		user.setPassword(rs.getString("password"));
		
		close(rs, ps);
		
		return user;
	}
	
	public void close (ResultSet rs, PreparedStatement ps) throws SQLException {
		if (rs != null) rs.close();
		if (ps != null) ps.close();
		if (conn != null) conn.close();
	}
}
