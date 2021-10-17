package practice.spring.toby.chapter1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CountingDaoFactory {

	@Bean
	public UserDao userDao() {
		return new UserDao(connectionMaker());
	}

	@Bean
	public ConnectionMaker connectionMaker() {
		return new CountingConnectionMaker(realCountingMaker());
	}

	@Bean
	public ConnectionMaker realCountingMaker() {
		return new DSimpleConnectionMaker();
	}
}
