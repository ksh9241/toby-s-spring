package practice.spring.toby.chapter1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class UserDao{
	// 멀티스레드 환경에서 싱글톤 사용 시 주의사항
	private  ConnectionMaker connectionMaker; // ConnectionMaker 인터페이스는 DB 연결 관련 읽기 전용 인터페이스이므로 인스턴스 변수로 사용해도 문제가 없다.
	private DataSource dataSource;
	
	// 아래의 인스턴스 변수 같은 경우 멀티스레드 환경에서 호출하여 데이터를 수정, 반환 등을 처리하기 때문에 인스턴스변수로 사용 시 문제가 된다. 따라서 로컬변수(지역변수) 로 사용해야 한다.
	Connection conn;
	PreparedStatement ps;
	ResultSet rs;
	
	// 의존관계 주입을 통한 외부에서 오브젝트 가져오기
	public UserDao (ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker;
	}
	
	// 의존관계 검색 방식으로 오브젝트 가져오기
	public UserDao() {
//		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
//		this.dataSource = context.getBean("dataSource", DataSource.class);
	}
	
	
	public void add(User user) throws ClassNotFoundException, SQLException {
		//conn = connectionMaker.getConnection();
		conn = dataSource.getConnection();
		
		ps = conn.prepareStatement("insert into users(id, name, password) values(?, ?, ?)");
		
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());
		
		ps.execute();
		
		close();
	}
	
	public User get(String id) throws ClassNotFoundException, SQLException {
		conn = dataSource.getConnection();
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
	
	public void deleteAll() throws SQLException, ClassNotFoundException {
		conn = dataSource.getConnection();
		ps = conn.prepareStatement("DELETE FROM users");
		
		ps.execute();
		
		close();
	}
	
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

	public void setConnectionMaker(ConnectionMaker addConnectionMaker) {
		this.connectionMaker = addConnectionMaker;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
