package practice.spring.tobyVer2.chapter1;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * description : ApplicationContextInitializer는 컨텍스트가 생성된 후에 초기화 작업을 진행하는 오브젝트를 만들 때 사용한다. 
 * 대부분의 빈 메타정보는 XML 이나 @Configuration 클래스로 작성할 수 있기 때문에 별도의 초기화 과정이 필요없다.
 * 하지만 오브젝트나 그에 포함되는 프로퍼티 소스는 빈이 아니고 컨텍스트가 생성하는 오브젝트이기 때문에 설정이 필요하다.
 * */
public class MyContextInitializer implements ApplicationContextInitializer<AnnotationConfigWebApplicationContext>{

	@Override
	public void initialize(AnnotationConfigWebApplicationContext applicationContext) {
		ConfigurableEnvironment ce = applicationContext.getEnvironment();
		
		Map<String, Object> m = new HashMap<>();
		m.put("db.username", "Spring");
		
		ce.getPropertySources().addFirst(new MapPropertySource("myPs", m));
	}
}
