package practice.spring.toby.chapter7;

import java.util.List;

import org.springframework.mail.MailSender;

public class UserServiceTest extends UserServiceImple{
	
	UserDao userDao;
	MailSender mailSender;
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	@Override
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender; 
	}

	public static class TestUserServiceImple extends UserServiceImple {
		private String id = "4"; // 테스트 픽스처의 users(3)의 id값을 고정시켰다.
		
		protected void upgradeLevel (User user) {
			if (user.getId().equals(this.id)) throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
		
		public List<User> getAll() {
			for (User user : super.getAll()) {
				super.update(user);
			}
			return null;
		}
	}
}
