package practice.spring.tobyVer2.chapter1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;


/**
 * basePackageClasses : 스캔할 패키지 명을 클래스나 인터페이스로 찾도록 마커 방식으로 처리
 * excludeFilters : 스캔 중 빈으로 생성하지 않을 스테레오타입 처리 (클래스, 어노테이션 둘 다 가능)
 * */
@Configuration
//@ComponentScan(basePackageClasses = Printer.class, excludeFilters = @Filter(Controller.class)) // 어노테이션 제외 방식	
@ComponentScan(basePackageClasses = Printer.class, excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value=HelloConfig.class))	// 클래스 제외 방식
public class HelloConfig {

	@Bean
	public Hello hello() {
		Hello hello = new Hello();
		hello.setName("Spring");
		hello.setPrinter(printer());
		return hello;
	}
	
	@Bean
	public Hello hello2() {
		Hello hello = new Hello();
		hello.setName("Spring2");
		hello.setPrinter(printer());
		return hello;
	}

	@Bean
	public Printer printer() {
		return new StringPrinter();
	}
}
