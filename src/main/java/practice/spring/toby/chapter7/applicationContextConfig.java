package practice.spring.toby.chapter7;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import oracle.jdbc.driver.OracleDriver;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "practice.spring.toby.chapter7")	// BeanFactoryPostProcessor구현체를 적용하여 @Componenet 를 포함한 하위 어노테이션 (클래스 생성 빈)을 찾을 패키지 범위 
@Import({SqlServiceContextConfig.class, ProductionAppContextConfig.class, TestAppContextConfig.class})	// 해당 클래스를 가져온다.
public class applicationContextConfig {
	
	// 클래스를 빈으로 만든 뒤 Autowired로 의존성 주입하였다. 기존 Bean으로 만들었던 부분은 제거하였다.
	@Autowired
	UserDao userDao;
	
	/**
	 * DB 연결과 트랜잭션
	 */
	
	@Bean
	public DataSource dataSource () {
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		dataSource.setDriverClass(OracleDriver.class);
		dataSource.setUrl("jdbc:oracle:thin:@localhost:1521:xe");
		dataSource.setUsername("practice");
		dataSource.setPassword("1234");
		
		return dataSource;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource()); 
	}
}
