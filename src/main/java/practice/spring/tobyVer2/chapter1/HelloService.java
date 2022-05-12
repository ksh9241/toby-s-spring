package practice.spring.tobyVer2.chapter1;

import org.springframework.context.annotation.Bean;

public class HelloService {
	Printer printer;
	
	public void setPrinter(Printer printer) {
		this.printer = printer;
	}

	@Bean
	private Hello hello() {
		Hello hello = new Hello();
		hello.setPrinter(this.printer);
		return hello;
	}
	
	@Bean
	private Hello hello2() {
		Hello hello = new Hello();
		hello.setPrinter(this.printer);
		return hello;
	}
	
	@Bean
	private Printer printer() {
		return new StringPrinter();
	}
}
