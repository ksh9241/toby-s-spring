package practice.spring.toby.chapter7;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailSender;

@Configuration
@Profile("test")
public class TestAppContextConfig {
	
	/**
	 * 애플리케이션 로직 & 테스트
	 */
	
	@Autowired
	UserDao userDao;
	
	@Bean
	public UserService userServiceTest() {
		UserServiceTest ust = new UserServiceTest();
		ust.setUserDao(this.userDao);
		ust.setMailSender(mailSender());
		
		return ust;
	}
	
	@Bean
	public MailSender mailSender() {
		return new DummyMailSender();
	}
}
