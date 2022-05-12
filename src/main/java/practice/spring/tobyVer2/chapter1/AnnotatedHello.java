package practice.spring.tobyVer2.chapter1;

import org.springframework.stereotype.Component;

/**
 * @Component
 * @Controller
 * @Service
 * @Repository
 * @Test
 * 등의 어노테이션을 스테레오 타입의 빈 생성이라고 한다.
 * */
@Component("myAnnotatedHello") // 빈 이름을 클래스명과 다르게 설정하고 싶을 때
public class AnnotatedHello {
	
}
