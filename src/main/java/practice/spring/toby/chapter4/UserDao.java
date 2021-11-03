package practice.spring.toby.chapter4;

import java.util.List;

public interface UserDao {
	void add(User user);
	User get(String id);
	List<User> getUserList();
	int getCount();
	void deleteAll();
}
