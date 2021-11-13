package practice.spring.toby.chapter6;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import practice.spring.toby.chapter4.DuplicateUserIdException;

public class UserDaoJdbc implements UserDao{

	DataSource dataSource;
	JdbcTemplate jdbcTemplate;
	
	public UserDaoJdbc() {}
	
	private RowMapper<User> userMapper = new RowMapper<User>() {
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getString(1));
			user.setName(rs.getString(2));
			user.setPassword(rs.getString(3));
			user.setLogin(rs.getInt(4));
			user.setRecommend(rs.getInt(5));
			user.setLevel(Level.valueOf(Integer.parseInt(rs.getString(6))));
			user.setEmail(rs.getString(7));
			return user;
		}
	};
	
	public void setDataSource (DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
		
	}
	
	public void add (User user) {
		try {
			jdbcTemplate.update("INSERT INTO users VALUES(?, ?, ?, ?, ?, ?, ?)", user.getId(), user.getName(), user.getPassword(), user.getLogin(), user.getRecommend(), user.getLevel().intValue(), user.getEmail());
		} catch (DuplicateKeyException e) {
			throw new DuplicateUserIdException(e);
		}
	}
	
	public User get (String id) {
		return jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?", new Object[] {id}, userMapper);
	}
	
	public void deleteAll () {
		jdbcTemplate.update("DELETE FROM users");
	}
	
	public int getCount () {
		return jdbcTemplate.queryForInt("SELECT COUNT(*) FROM users");
	}

	public void update(User user1) {
		try {
			String sql = "UPDATE users SET name = ?, password = ?, login = ?, recommend = ?, user_level = ? WHERE id = ?";
			jdbcTemplate.update(sql, user1.getName(), user1.getPassword(), user1.getLogin(), user1.getRecommend(), user1.getLevel().intValue(), user1.getId());	
		} catch (DuplicateKeyException e) {
			throw e;
		}
	}

	@Override
	public List<User> getAll() {
		String sql = "SELECT * FROM users";
		List<User> list = new ArrayList<>();
		list = jdbcTemplate.query(sql, userMapper); 
		return list;
	}
}
