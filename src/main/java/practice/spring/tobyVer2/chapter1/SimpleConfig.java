package practice.spring.tobyVer2.chapter1;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericXmlApplicationContext;

@Configuration
public class SimpleConfig {

	@Autowired
	Hello hello;
	
	@Bean
	Hello hello() {
		return new Hello();
	}
	
	public class Hello {
		@PostConstruct
		public void init() {
			System.out.println("Init");
		}
	}
	
	public void sayHello() {
		System.out.println("Init 이후에 나옴");
	}
}
