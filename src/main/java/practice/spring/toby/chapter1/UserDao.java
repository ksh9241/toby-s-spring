package practice.spring.toby.chapter1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao{
	Connection conn;
	PreparedStatement ps;
	ResultSet rs;
	ConnectionMaker connectionMaker;
	
	public UserDao (ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker;
	}
	
	public void add(User user) throws ClassNotFoundException, SQLException {
		conn = connectionMaker.getConnection();
		
		ps = conn.prepareStatement("insert into users(id, name, password) values(?, ?, ?)");
		
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());
		
		ps.execute();
		
		close();
	}
	
	public User get(String id) throws ClassNotFoundException, SQLException {
		conn = connectionMaker.getConnection();
		ps = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
		ps.setString(1, id);
		
		rs = ps.executeQuery();
		rs.next();
		User user = new User();
		user.setId(rs.getString("id"));
		user.setName(rs.getString("name"));
		user.setPassword(rs.getString("password"));
		
		close();
		
		return user;
	}
	
	public void close () throws SQLException {
		if (rs != null) rs.close();
		if (ps != null) ps.close();
		if (conn != null) conn.close();
	}
}
