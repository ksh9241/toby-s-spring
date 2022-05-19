package practice.spring.tobyVer2.chapter1;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.aspectj.lang.annotation.AdviceName;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
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
	
	@Test
	@AdviceName("XML을 사용한 메타정보2")
	public void GenericXmlApplicationContextTest() {
		GenericXmlApplicationContext ac = 
				new GenericXmlApplicationContext("/ver2Chapter1/StringPrinter.xml");
		
		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();
		
		assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
	}
	
	@Test
	@AdviceName("트리구조 컨텍스트")
	/**
	 * 트리구조 컨텍스트는 오브젝트 상속과 매우 유사하다.
	 * 생성하는 객체의 빈을 먼저 참조하고 자신의 컨텍스트에 빈이 존재하지 않으면 부모 컨텍스트의 빈을 참조하여 존재 할 경우 반환한다.
	 * */
	public void TreeContextTest() {
		String basePath = "/ver2Chapter1/";
		ApplicationContext parent = new GenericXmlApplicationContext(basePath + "parentContext.xml");
		
		GenericApplicationContext child = new GenericApplicationContext(parent);
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(child);
		reader.loadBeanDefinitions(basePath + "childContext.xml");
		
		child.refresh();
		
		Printer printer = child.getBean("printer", Printer.class); // 자칙 XML에 존재하지 않는 printer 빈을 부모 XML에서 가져옴
		assertNotNull(printer);

		Hello hello = child.getBean("hello", Hello.class); // 자식 XML에 존재하는 hello빈을 가져와서 name은 Child가 됨.
		hello.print();
		assertThat(printer.toString(), is("Hello Child"));
	}
	
	@Test
	@AdviceName("스테레오타입 빈 생성")
	public void AnnotationConfigApplicationContextTest() {
		ApplicationContext ctx = new AnnotationConfigApplicationContext("practice.spring.tobyVer2.chapter1"); // 스테레오타입 어노테이션이 붙은 클래스를 스캔할 패키지 위치
		AnnotatedHello hello = ctx.getBean("myAnnotatedHello", AnnotatedHello.class); // 빈 이름은 클래스명 첫글자만 소문자로 변환됨 (Default) 
		
		assertNotNull(hello);
	}
	
	@Test
	@AdviceName("Configuration 클래스를 이용한 테스트")
	public void ConfigurationAnnotationTest() {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(AnnotatedHelloConfig.class);
		AnnotatedHelloConfig hello = ctx.getBean("annotatedHelloConfig", AnnotatedHelloConfig.class);
		
		AnnotatedHello childHello = hello.annotatedHello();
		if (childHello instanceof AnnotatedHello) {
			System.out.println("맞음");
		}
		assertNotNull(hello);
	}
	
	@Test
	@AdviceName("빈 싱글톤 테스트")
	public void singletonTest() {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(HelloConfig.class);
		HelloConfig config = ctx.getBean(HelloConfig.class);
		Hello hello1 = config.hello();
		Hello hello2 = config.hello2();
		
		assertEquals(hello1.printer, hello2.printer); // 싱글톤 객체이기 때문에 New생성을 통한 printer 오브젝트가 동일하다.
	}
}
