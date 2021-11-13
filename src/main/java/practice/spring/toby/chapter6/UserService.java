package practice.spring.toby.chapter6;

import org.springframework.transaction.PlatformTransactionManager;

public interface UserService {
	public void upgradeLevels ();

	public void add(User user1);
}
