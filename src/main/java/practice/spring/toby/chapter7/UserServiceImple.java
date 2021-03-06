package practice.spring.toby.chapter7;
import static practice.spring.toby.chapter5.UserServiceImple.MIN_LOGCOUNT_FOR_SILVER;
import static practice.spring.toby.chapter5.UserServiceImple.MIN_RECCOMEND_FOR_GOLD;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImple implements UserService{
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	MailSender mailSender;
	
	private String id;
	public UserServiceImple() {}
	
	public UserServiceImple (String id) {
		this.id = id;
	}
	
	
	public void setUserDao (UserDao userDao) {
		this.userDao = userDao;
	}
	
	public void setMailSender (MailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	@Override
	public void add(User user) {
		if (user.getLevel() == null) user.setLevel(Level.BASIC);
		userDao.add(user);
	}
	
	public void upgradeLevels() {
		List<User> users = userDao.getAll();
		for (User user : users) {
			if (changeUpgradeLevel(user)) {
				upgradeLevel(user);
			}
		}
	}
	
	protected void upgradeLevel(User u) {
		if (u.getId().equals(this.id)) throw new TestUserServiceException();
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
	public User get(String id) {
		return userDao.get(id);
	}

	@Override
	public List<User> getAll() {
		return userDao.getAll();
	}

	@Override
	public void deleteAll() {
		userDao.deleteAll();
	}

	@Override
	public void update(User user) {
		userDao.update(user);
	}
}


