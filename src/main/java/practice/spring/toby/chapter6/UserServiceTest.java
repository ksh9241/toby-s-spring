package practice.spring.toby.chapter6;

public class UserServiceTest {

	static class TestUserServiceImple extends UserServiceImple {
		private String id = "4"; // 테스트 픽스처의 users(3)의 id값을 고정시켰다.
		
		protected void upgradeLevel (User user) {
			if (user.getId().equals(this.id)) throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
	}
}
