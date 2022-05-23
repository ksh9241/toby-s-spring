package practice.spring.tobyVer2.chapter2;

import javax.naming.NamingException;

import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

public class JNDIExample {
	public static void main (String [] args) throws IllegalStateException, NamingException {
		SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
		SimpleDriverDataSource ds = new SimpleDriverDataSource();	// DB정보 매개변수 입력
		builder.bind("jdbc/DefaultDS", ds);		// JNDI <jee:jndi-lookup id="dataSource" name="jdbc/DefaultDS"> 으로 생성한 빈 name입력
		builder.activate();
	}
}
