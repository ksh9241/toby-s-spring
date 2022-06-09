package practice.spring.tobyVer2.chapter3;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.oxm.castor.CastorMarshaller;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.xml.MarshallingView;

/**
 * Castor를 이용한 
 * Marshaller Example
 * */
@Component
@ComponentScan(basePackages = "practice.spring.tobyVer2")
public class Info {

	String message;
	
	public Info(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	@Bean
	public CastorMarshaller castorMarsharller() {
		return new CastorMarshaller();
	}
	
	@Bean
	public MarshallingView helloMarshallingView() {
		return new MarshallingView(castorMarsharller());
	}
}
