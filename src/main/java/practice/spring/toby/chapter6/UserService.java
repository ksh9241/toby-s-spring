package practice.spring.toby.chapter6;

import java.util.List;

public interface UserService {
	public void upgradeLevels ();
	public User get(String id);
	public List<User> getAll();
	public void deleteAll();
	public void add(User user1);
	public void update(User user);
}
