package practice.spring.toby.chapter5;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImple implements UserService {
	
	@Autowired
	UserDao userDao;
	
	
	public void setUserDao (UserDao userDao) {
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
		u.upgradeLevel();
		userDao.update(u);
	}


	private boolean changeUpgradeLevel (User user) {
		Level currentLevel = user.getLevel();
		switch (currentLevel) {
			case BASIC : return (user.getLogin() >= 50);
			case SILVER : return (user.getRecommend() >= 30);
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
