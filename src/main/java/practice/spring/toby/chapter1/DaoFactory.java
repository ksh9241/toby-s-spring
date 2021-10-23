package practice.spring.toby.chapter1;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@Configuration
public class DaoFactory {
	
	@Inject
	DataSource dataSource;
	
	// 의존관계 주입을 통한 ConnectionMaker 오브젝트 가져오기
	@Bean
	public UserDao userDao() {
		UserDao userDao = new UserDao();
		//userDao.setConnectionMaker(connectionMaker());
		//userDao.setDataSource(dataSource);
		return userDao;
	}
	
//	public AccountDao accountDao () {
//		AccountDao dao = new AccountDao(addConnectionMaker());
//		return dao;
//	}
	
	// Dao를 사용 할 때 공통으로 사용할 DB커넥션 생성 함수.
	@Bean
	public ConnectionMaker connectionMaker () {
		return new DSimpleConnectionMaker();
	}
	
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		
		dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
		dataSource.setUrl("jdbc:oracle:thin:@localhost:1521:xe");
		dataSource.setUsername("practice");
		dataSource.setPassword("1234");
		
		return dataSource;
	}
}
