package practice.spring.toby.chapter5;
import static practice.spring.toby.chapter5.UserServiceImple.MIN_LOGCOUNT_FOR_SILVER;
import static practice.spring.toby.chapter5.UserServiceImple.MIN_RECCOMEND_FOR_GOLD;

import java.util.List;

import org.springframework.stereotype.Service;;

@Service
public class UserTransactionExceptionService implements UserService {
	
	UserDao userDao;
	
	private String id;
	
	public UserTransactionExceptionService() {}
	
	public UserTransactionExceptionService (String id) {
		this.id = id;
	}
	
	@Override
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	public void upgradeLevels() {
		List<User> list = userDao.getAll();
		for (User u : list) {
			if (changeUpgradeLevel (u)) {
				upgradeLevel (u);
			}
		}
	}
	
	private void upgradeLevel(User u) {
		if (u.getId().equals(this.id)) throw new TestUserServiceException();
		
		u.upgradeLevel();
		userDao.update(u);
	}
	
	private boolean changeUpgradeLevel (User user) {
		Level currentLevel = user.getLevel();
		switch (currentLevel) {
			case BASIC : return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
			case SILVER : return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
			case GOLD : return false;
			default : throw new IllegalArgumentException("Unknown Level : " + currentLevel);
		}
	}

	@Override
	public void add(User user) {
		if (user.getLevel() == null) user.setLevel(Level.BASIC);
		userDao.add(user);
	}

}
