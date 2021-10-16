package practice.spring.toby.chapter1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoFactory {
	
	@Bean
	public UserDao userDao () {
		UserDao dao = new UserDao(addConnectionMaker());
		return dao;
	}
	
//	public AccountDao accountDao () {
//		AccountDao dao = new AccountDao(addConnectionMaker());
//		return dao;
//	}
	
	// Dao를 사용 할 때 공통으로 사용할 DB커넥션 생성 함수.
	@Bean
	public ConnectionMaker addConnectionMaker () {
		return new DSimpleConnectionMaker();
	}
}
