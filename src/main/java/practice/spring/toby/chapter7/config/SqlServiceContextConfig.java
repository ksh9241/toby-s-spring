package practice.spring.toby.chapter7.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import practice.spring.toby.chapter7.EmbeddedDbSqlRegistry;
import practice.spring.toby.chapter7.OxmSqlService;
import practice.spring.toby.chapter7.SqlRegistry;
import practice.spring.toby.chapter7.SqlService;

@Configuration
public class SqlServiceContextConfig {
	
	@Autowired
	SqlMapConfig sqlConfig;
	
	/**
	 * SQL 서비스 
	 */
	@Bean
	public SqlService sqlService () {
		OxmSqlService sqlService = new OxmSqlService();
		sqlService.setUnmarshaller(unmarshaller());
		sqlService.setSqlRegistry(sqlRegistry());
		sqlService.setSqlmap(sqlConfig.getSqlMapResource());
		
		return sqlService;
	}
	
	@Bean
	public SqlRegistry sqlRegistry () {
		EmbeddedDbSqlRegistry sqlRegistry = new EmbeddedDbSqlRegistry();
		sqlRegistry.setDataSource(embeddedDatabase());
		
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
