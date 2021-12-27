package practice.spring.toby.chapter7.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import practice.spring.toby.chapter7.DummyMailSender;
import practice.spring.toby.chapter7.UserDao;
import practice.spring.toby.chapter7.UserService;
import practice.spring.toby.chapter7.UserServiceTest;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "practice.spring.toby.chapter7")	// BeanFactoryPostProcessor구현체를 적용하여 @Componenet 를 포함한 하위 어노테이션 (클래스 생성 빈)을 찾을 패키지 범위 
@PropertySource("/chapter7/database.properties")
@EnableSqlService		// 어노테이션 인터페이스를 만들어서 사용한다.
public class applicationContextConfig implements SqlMapConfig{
	
	// 클래스를 빈으로 만든 뒤 Autowired로 의존성 주입하였다. 기존 Bean으로 만들었던 부분은 제거하였다.
	@Autowired
	UserDao userDao;
	
	@Autowired
	Environment env;
	
	@Value("${db.driverClass}")
	Class<? extends java.sql.Driver> driverClass;
	
	@Value("${db.url}")
	String url;
	
	@Value("${db.username}")
	String username;
	
	@Value("${db.password}")
	String password;
	
	/**
	 * DB 연결과 트랜잭션
	 */
	
	@Override
	public Resource getSqlMapResource() {
		return new ClassPathResource("/chapter7/sqlmap.xml");
	}
	
	@Bean
	public DataSource dataSource () {
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		
		dataSource.setDriverClass(driverClass);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		
		return dataSource;
	}
	
	// @Value 어노테이션을 통한 프로퍼티 값을 필드에 주입할 때 필요한 빈
	@Bean
	public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource()); 
	}
	
	/**
	 * 프로파일 중첩 클래스 
	 */
	
	@Configuration
	@Profile("production")
	public static class ProductionAppContextConfig {
		@Bean
		public MailSender mailSender() {
			JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			mailSender.setHost("localhost");
			return mailSender;
		}
	}
	
	@Configuration
	@Profile("test")
	public static class TestAppContextConfig {
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
}
