package practice.spring.toby;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;

import org.junit.Test;

import practice.spring.toby.chapter6.Hello;
import practice.spring.toby.chapter6.HelloTarget;
import practice.spring.toby.chapter6.HelloUppercase;

public class Chapter6ReflectionTest {

	@Test
	public void invokeMethod() throws Exception {
		String name = "String";
		
		// length()
		assertThat(name.length(), is(6));
		
		Method lengthMethod = String.class.getMethod("length");
		assertThat((Integer) lengthMethod.invoke(name), is(6));
		
		// charAt()
		assertThat(name.charAt(0), is('S'));
		
		Method charAtMethod = String.class.getMethod("charAt", int.class);
		assertThat((Character) charAtMethod.invoke(name, 0), is('S'));
	}
	
	@Test
	public void simpleProxy () {
		Hello hello = new HelloTarget();
		assertThat(hello.sayHello("Toby"), is("Hello Toby"));
		assertThat(hello.sayHi("Toby"), is("Hi Toby"));
		assertThat(hello.sayThankYou("Toby"), is("Thank You Toby"));
	}
	
	@Test
	public void simpleProxy_uppercase () {
		Hello hello = new HelloUppercase(new HelloTarget());
		assertThat(hello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(hello.sayHi("Toby"), is("HI TOBY"));
		assertThat(hello.sayThankYou("Toby"), is("THANK YOU TOBY"));
	}
}
