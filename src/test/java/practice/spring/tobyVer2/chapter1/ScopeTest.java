package practice.spring.tobyVer2.chapter1;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.aspectj.lang.annotation.AdviceName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class ScopeTest {

	/**
	 * 싱글톤 스코프의 빈은 하나의 오브젝트를 재사용하기 때문에 테스트 결과 한 개의 오브젝트만 Set에 들어간 것을 확인할 수 있다.
	 * */
	@Test
	@AdviceName("싱글톤 스코프")
	public void singletonScopeTest() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(SingletonBean.class);
		Set<SingletonBean> beans = new HashSet<>();
		
		beans.add(ac.getBean(SingletonBean.class));
		beans.add(ac.getBean(SingletonBean.class));
		assertThat(beans.size(), is(1));
		
		beans.add(ac.getBean(SingletonClientBean.class).bean1);
		beans.add(ac.getBean(SingletonClientBean.class).bean2);
		assertThat(beans.size(), is(1));
	}
	
	static class SingletonBean {}

	static class SingletonClientBean {
		// 한 번 이상 DI가 일어날 수 있도록 두 개의 DI용 프로퍼티 선언
		@Autowired
		SingletonBean bean1;
		
		@Autowired
		SingletonBean bean2;
	}
	
	
	/**
	 * 프로토타입 스코프는 컨테이너에게 빈을 요청할 때마다 새로운 오브젝트를 생성해준다.
	 * 때문에 Set으로 중복을 허용하지 않더라도 오브젝트가 다르기 때문에 ADD시 Size가 증가한다.
	 * */
	@Test
	@AdviceName("프로토타입 스코프")
	public void prototypeScopeTest() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class);
		Set<PrototypeBean> beans = new HashSet<>();
		
		beans.add(ac.getBean(PrototypeBean.class));
		assertThat(beans.size(), is(1));
		
		beans.add(ac.getBean(PrototypeBean.class));
		assertThat(beans.size(), is(2));
		
		beans.add(ac.getBean(PrototypeClientBean.class).bean1);
		assertThat(beans.size(), is(3));
		
		beans.add(ac.getBean(PrototypeClientBean.class).bean2);
		assertThat(beans.size(), is(4));
	}
	
	
	@Scope("prototype")	// 어노테이션을 이용해 프로토타입 빈을 만들려면 @Scope의 기본값을 prototype으로 지정한다.
	static class PrototypeBean {}
	
	static class PrototypeClientBean {
		@Autowired
		PrototypeBean bean1;
		
		@Autowired
		PrototypeBean bean2;
	}
}
