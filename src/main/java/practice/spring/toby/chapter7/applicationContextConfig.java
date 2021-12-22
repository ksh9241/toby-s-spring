package practice.spring.toby.chapter7;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mail.MailSender;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import oracle.jdbc.driver.OracleDriver;
import practice.spring.toby.chapter7.UserServiceTest.TestUserServiceImple;

@Configuration
@EnableTransactionManagement
public class applicationContextConfig {
	
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
	
	
	/**
	 * 애플리케이션 로직 & 테스트
	 */
	
	@Bean
	public UserDao userDao () {
		UserDaoJdbc dao = new UserDaoJdbc();
		dao.setDataSource(dataSource());
		dao.setSqlService(sqlService());
		
		return dao;
	}
	
	@Bean
	public UserService userService() {
		UserServiceImple service = new UserServiceImple();
		service.setUserDao(userDao());
		service.setMailSender(mailSender());
		return service;
	}
	
	@Bean
	public TestUserServiceImple testUserService () {
		TestUserServiceImple testService = new TestUserServiceImple();
		testService.setUserDao(userDao());
		testService.setMailSender(mailSender());
		
		return testService;
	}
	
	@Bean
	public MailSender mailSender() {
		return new DummyMailSender();
	}
	
	
	/**
	 * SQL 서비스 
	 */
	
	@Bean
	public SqlService sqlService () {
		OxmSqlService sqlService = new OxmSqlService();
		sqlService.setUnmarshaller(unmarshaller());
		sqlService.setSqlRegistry(sqlRegistry());
		
		return sqlService;
	}
	
	@Bean
	public SqlRegistry sqlRegistry () {
		EmbeddedDbSqlRegistry sqlRegistry = new EmbeddedDbSqlRegistry();
		sqlRegistry.setDataSource(embeddedDatabase());
//		sqlRegistry.setDataSource(dataSource());
		
		return sqlRegistry;
	}
	
	@Bean
	public Unmarshaller unmarshaller () {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("practice.spring.toby.chapter7.jaxb");
		return marshaller;
	}
	
	@Bean
	public DataSource embeddedDatabase() {
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		EmbeddedDatabase db = builder
						.setType(EmbeddedDatabaseType.HSQL)
						.addScript("/chapter7/schema.sql")
						.build();
		return db;
	}
}
