package practice.spring.toby.chapter7;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

// 테스트, 운영 모두 사용되는 Bean의 경우 따로 관리하여 구성하는 오브젝트를 만들었다. (의존성 주입 시 같은 타입의 빈이 다중이기 때문에 에러발생함.)
@Configuration
@Profile("production")
public class ProductionAppContextConfig {

	@Bean
	public MailSender mailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("localhost");
		return mailSender;
	}
}
