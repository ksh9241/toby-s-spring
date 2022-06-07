package practice.spring.tobyVer2.chapter3;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.mapping.Map;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;

public class SimpleHandlerAdapter implements HandlerAdapter{

	// 이 핸들러 어댑터가 지원하는 타입을 확인해준다.
	@Override
	public boolean supports(Object handler) {
		return (handler instanceof SimpleController);
	}

	
	@Override
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		Method m = ReflectionUtils.findMethod(handler.getClass(), "control", Map.class, Map.class);
		
		// 컨트롤러 메서드의 어노테이션에서 필요한 정보를 가져온다.
		ViewName viewName = AnnotationUtils.getAnnotation(m, ViewName.class);
		RequiredParams requiredParams = AnnotationUtils.getAnnotation(m, RequiredParams.class);
		
		return null;
	}

	// getLastModified()는 컨트롤러의 getLastModified() 메서드를 다시 호출해서 컨트롤러가 결정하도록 만든다. 캐싱을 하지않으려면 0보다 작은 값을 리턴하면 된다. 
	@Override
	public long getLastModified(HttpServletRequest request, Object handler) {
		return -1;
	}

}
