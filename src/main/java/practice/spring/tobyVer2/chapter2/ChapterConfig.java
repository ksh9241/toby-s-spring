package practice.spring.tobyVer2.chapter2;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import oracle.jdbc.pool.OracleDataSource;

@Configuration
@PropertySource("database.properties")
public class ChapterConfig {

	@Bean
	public DataSource dataSource() {
		try {
			OracleDataSource dataSource = new OracleDataSource();
			dataSource.setURL("${db.url}");
			dataSource.setUser("${db.username}");
			dataSource.setPassword("${db.password}");
			
			return dataSource;
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}
}
