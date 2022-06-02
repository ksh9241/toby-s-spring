package practice.spring.tobyVer2.chapter3;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Description : 테스트를 위해 DispatcherServlet을 확장한 클래스
 * */
public class ConfigurableDispatcherServlet extends DispatcherServlet{
	
	private Class<?>[] classes;
	private String[] locations;
	
	private ModelAndView mav;
	
	public ConfigurableDispatcherServlet(String[] locations) {
		this.locations = locations;
	}
	
	public ConfigurableDispatcherServlet(Class<?>[] classes) {
		this.classes = classes;
	}
	
	public void setLocations(String ...locations) {
		this.locations = locations;
	}
	
	// 주어진 클래스로부터 상대적인 위치의 클래스패스에 있는 설정파일을 지정할 수 있게 해준다.
	public void setRelativeLocations(Class clazz, String ...relativeLocations) {
		String[] locations = new String[relativeLocations.length];
		String currentPath = ClassUtils.classPackageAsResourcePath(clazz) + "/";
		
		for (int i = 0; i < relativeLocations.length; i++) {
			locations[i] = currentPath + relativeLocations[i];
		}
		
		this.setLocations(locations);
	}
	
	public void setclasses(Class<?> ...classes) {
		this.classes = classes;
	}
	
	// DispatcherServlet의 서블릿 컨텍스트를 생성하는 메서드를오버라이드해서 테스트용 메타정보를 이용해서 서블릿 컨텍스트를 생성한다.
	protected WebApplicationContext createWebApplicationContext (ApplicationContext parent) {
		AbstractRefreshableWebApplicationContext wac = new AbstractRefreshableWebApplicationContext() {
			
			@Override
			protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
				if (locations != null) {
					XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(beanFactory);
					xmlReader.loadBeanDefinitions(locations);
				}
				
				if (classes != null) {
					AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(beanFactory);
					reader.register(classes);
				}
			}
		};
		
		wac.setServletContext(getServletContext());
		wac.setServletConfig(getServletConfig());
		wac.refresh();
		
		return wac;
	}
	
	// 뷰를 실행하는 과정을 가로채서 컨트롤러가 돌려준 ModelAndView 정보를 따로 저장해둔다.
	@Override
	protected void render(ModelAndView mv, HttpServletRequest req, HttpServletResponse res) throws Exception {
		this.mav = mv;
		super.render(mv, req, res);
	}
}
