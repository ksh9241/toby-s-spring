package practice.spring.toby.chapter5;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImple implements UserService {
	
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMEND_FOR_GOLD = 30;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	MailSender mailSender;
	
	
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
	
	protected void upgradeLevel(User u) {
		u.upgradeLevel();
		userDao.update(u);
		sendUpgradeEmail(u);
	}
	
	private void sendUpgradeEmail(User u) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(u.getEmail());
		mailMessage.setFrom("useradmin@ksug.org");
		mailMessage.setSubject("Upgrade 안내");
		mailMessage.setText("사용자님의 등급이 " + u.getLevel().name());
		
		mailSender.send(mailMessage);
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


	@Override
	public void setMailSender(MockMailSender mockMailSender) {
		mailSender = mockMailSender;
	}
	
}
