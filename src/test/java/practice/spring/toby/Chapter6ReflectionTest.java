package practice.spring.toby;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import practice.spring.toby.chapter6.Hello;
import practice.spring.toby.chapter6.HelloTarget;
import practice.spring.toby.chapter6.HelloUppercase;

public class Chapter6ReflectionTest {
	
	@Test
	public void classNamePointcutAdvisor() {
		NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
			public ClassFilter getClassFilter () { // 익명 내부 클래스 방식으로 클래스를 정의한다.
				return new ClassFilter() {
					public boolean matches(Class<?> clazz) {
						return clazz.getSimpleName().startsWith("HelloT");
					}
				};
			}
		};
		classMethodPointcut.setMappedName("sayH*");
		
		checkAdviced(new HelloTarget(), classMethodPointcut, true); // 적용 클래스
		
		class HelloWorld extends HelloTarget {};
		checkAdviced(new HelloWorld(), classMethodPointcut, false); // 적용 클래스 아님
		
		class HelloToby extends HelloTarget {};
		checkAdviced(new HelloToby(), classMethodPointcut, true); // 적용 클래스
	}

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
	
	private void checkAdviced (Object target, Pointcut pointcut, boolean adviced) {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(target);
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
		Hello proxiedHello = (Hello) pfBean.getObject();
		
		if (adviced) {
			assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
			assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
			assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
		}
		else {
			assertThat(proxiedHello.sayHello("Toby"), is("Hello Toby"));
			assertThat(proxiedHello.sayHi("Toby"), is("Hi Toby"));
			assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
		}
	}
	
	static class UppercaseAdvice implements MethodInterceptor {
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			// 리플렉션의 Method와 달리 메서드가 실행 시 타깃 오브젝트를 전달할 필요가 없다. MethodInvocation은 메서드 정보와 함께 타깃 오브젝트를 알고 있기 때문이다.
			String ret = (String) invocation.proceed();
			return ret.toUpperCase();
		}
	}
}
