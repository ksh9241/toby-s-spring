package practice.spring.toby.chapter5;

public interface UserService {
	public void setUserDao (UserDao userDao);
	
	public void upgradeLevels ();

	public void add(User user1);
}
