package practice.spring.tobyVer2.chapter1;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.aspectj.lang.annotation.AdviceName;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.StaticApplicationContext;


public class HelloTest {
	
	StaticApplicationContext ac;
	
	@Before
	public void setUp() {
		ac = new StaticApplicationContext();
		
		// Hello 클래스를 hello1이라는 이름의 싱글톤 빈으로 컨테이너에 등록
		ac.registerSingleton("hello1", Hello.class);
	}
	
	@Test
	@AdviceName("컨테이너 빈 참조")
	public void ApplicationContextTest() {
		
		Hello hello1 = ac.getBean("hello1", Hello.class);
		assertNotNull(hello1);
	}
	
	@Test
	@AdviceName("오브젝트 생성 후 DI작업 진행하여 빈등록")
	public void BeanDefinitionTest() {
		BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
		helloDef.getPropertyValues().addPropertyValue("name", "Spring");
		
		ac.registerBeanDefinition("hello2", helloDef);
		
		Hello hello2 = ac.getBean("hello2", Hello.class);
		assertThat(hello2.sayHello(), is("Hello Spring"));
		
		assertThat(ac.getBeanFactory().getBeanDefinitionCount(), is(2));
	}
	
	@Test
	@AdviceName("POJO테스트")
	public void registerBeanWithDependencyTest() {
		// StringPrinter 클래스 타입이며 printer라는 이름을 가진 빈 등록
		ac.registerBeanDefinition("printer", new RootBeanDefinition(StringPrinter.class));
		
		BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
		helloDef.getPropertyValues().addPropertyValue("name", "Spring"); // 단순 값을 갖는 프로퍼티 등록
		
		// 아이디가 printer인 빈에 대한 레퍼런스를 프로퍼티로 등록
		helloDef.getPropertyValues().addPropertyValue("printer", new RuntimeBeanReference("printer"));
		
		ac.registerBeanDefinition("hello", helloDef);
		
		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();
		
		assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
	}
	
	@Test
	@AdviceName("XML을 사용한 메타정보")
	public void GenericApplicationContextTest() {
		GenericApplicationContext ac = new GenericApplicationContext();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(ac);
		
		reader.loadBeanDefinitions("/ver2Chapter1/StringPrinter.xml");
		
		ac.refresh(); // 모든 메타정보가 등록이 완료됐으니 애플리케이션 컨테이너를 초기화하라는 명령어
		
		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();
		
		assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
	}
}
