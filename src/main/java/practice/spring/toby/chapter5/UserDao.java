package practice.spring.toby.chapter5;

import java.util.List;

import javax.sql.DataSource;

public interface UserDao {

	public void setDataSource (DataSource dataSource);
	
	public void add (User user);
	
	public User get (String id);
	
	public void deleteAll ();
	
	public int getCount();
	
	public void update (User user);
	
	public List<User> getAll();
}
