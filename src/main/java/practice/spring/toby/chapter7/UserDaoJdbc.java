package practice.spring.toby.chapter7;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import practice.spring.toby.chapter4.DuplicateUserIdException;

public class UserDaoJdbc implements UserDao{

	DataSource dataSource;
	JdbcTemplate jdbcTemplate;
	SqlService sqlService;
	
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
	
	public void setSqlService (SqlService sqlService) {
		this.sqlService = sqlService;
	}
	
	public void add (User user) {
		try {
			jdbcTemplate.update(sqlService.getSql("userAdd"), user.getId(), user.getName(), user.getPassword(), user.getEmail(), user.getLevel().intValue(), user.getLogin(), user.getRecommend());
		} catch (DuplicateKeyException e) {
			throw new DuplicateUserIdException(e);
		}
	}
	
	public User get (String id) {
		return jdbcTemplate.queryForObject(sqlService.getSql("userGet"), new Object[] {id}, userMapper);
	}
	
	public void deleteAll () {
		jdbcTemplate.update(sqlService.getSql("userDeleteAll"));
	}
	
	public int getCount () {
		return jdbcTemplate.queryForObject(sqlService.getSql("userGetCount"), Integer.class);
	}

	public void update(User user1) {
		try {
			jdbcTemplate.update(sqlService.getSql("userUpdate"), user1.getName(), user1.getPassword(), user1.getLogin(), user1.getRecommend(), user1.getLevel().intValue(), user1.getId());	
		} catch (DuplicateKeyException e) {
			throw e;
		}
	}

	@Override
	public List<User> getAll() {
		List<User> list = new ArrayList<>();
		list = jdbcTemplate.query(sqlService.getSql("userGetAll"), userMapper); 
		return list;
	}
}
