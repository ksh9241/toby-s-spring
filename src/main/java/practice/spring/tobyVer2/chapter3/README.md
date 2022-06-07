# 3장 스프링 웹 기술과 스프링 MVC
엔터프라이즈 애플리케이션의 가장 앞단에서 사용자 또는 클라이언트 시스템과 연동하는 책임을 맡고 있는 것이 바로 웹 프레젠테이션 계층이다.

스프링은 기본적으로 이렇게 기술의 변화가 잦은 웹 계층과 여타 계층을 깔끔하게 분리해서 개발하는 아키텍처 모델을 지지한다.

3장에서는 스프링의 웹 계층 설계와 기술의 선정에 관한 기본 원칙을 알아보고, 스프링 웹 기술의 다양한 전략을 살펴볼 것이다.

## 3.1 스프링의 웹 프레젠테이션 계층 기술
스프링 애플리케이션 입장에서는 두 가지로 구분할 수 있다. 스프링 웹 기술을 사용하는 프레젠테이션 계층과 스프링 외의 웹 기술을 사용하는 프레젠테이션 게층이다.

### 3.1.1 스프링에서 사용되는 웹 프레임워크의 종류
#### 스프링 웹 프레임워크
##### - 스프링 서블릿/스프링 MVC
스프링이 직접 제공하는 서블릿 기반의 MVC 프레임워크다. 프론트 컨트롤러 역할을 하는 DispatcherServlet을 핵심 엔진으로 사용한다.

스프링 서블릿의 모든 컴포넌트는 스프링의 서블릿 애플리케이션 컨텍스트의 빈으로 등록되어 동작한다. 따라서 루트 컨텍스트에 존재하는 서비스 계층의 빈을 사용할 수 있다.

##### - 스프링 포틀릿
스프링이 제공하는 포틀릿 MVC 프레임워크다. 서블릿과 유사한 포틀릿 컨테이너에서 동작한다. 스프링 서블릿과 마찬가지로 스프링 포틀릿도 전용 포틀릿 애플리케이션 컨텍스트를 갖는다.

#### 스프링 포트폴리오 웹 프레임워크
##### - Spring Web Flow
SWF라고 불리기도 한다. 스프링 웹 플로우는 스프링 서블릿을 기반으로 해서 상태유지 스타일의 웹 애플리케이션을 작성하게 해주는 프레임워크다.

##### - Spring JavaScript
자바스크립트 툴킷인 Dojo를 추상화한 것으로 스프링 서블릿과 스프링 웹 플로우에 연동해서 손쉽게 Ajax 기능을 구축할 수 있도록 만들어져 있다.

##### - Spring Faces
JSF를 스프링 MVC와 스프링 SWF의 뷰로 손쉽게 사용할 수 있게 해주는 프레임워크다.

##### - Spring Web Service
스프링 MVC와 유사한 방식으로 SOAP 기반의 웹 서비스 개발을 가능하게 해주는 프레임워크다. 강력한 오브젝트 매핑과 XML 마샬링 기능을 제공하고 있다. 스프링 Security를 비롯한 각종 스프링의 기능을 활용할 수 있다.

##### - Spring BlazeDS Integration
어도비 플렉스의 BlazeDS와 스프링을 통합해서 빠르고 쉽게 플렉스를 지원하는 스프링 애플리케이션을 개발할 수 있도록 해주는 프레임워크다. 플렉스와 함께 스프링을 사용한다면 적극적으로 도입을 고려해볼 만한 프레임워크다.

#### 스프링을 기반으로 두지 않는 웹 프레임워크
##### - JSP/Servlet
##### - Struts1
##### - Struts2
##### - Tapestry 3, 4
##### - JSF/Seam

### 3.1.2 스프링 MVC와 DispatcherServlet 전략
스프링은 유연성과 확장성, 다양성에 무게를 두고 있는 프레임워크다. 스프링이 제공하는 주요 기능은 세부 전략을 변경해서 사용할 수 있도록 확장 포인트가 제공된다. 스프링을 잘 사용하는 비결은 스프링이 제공하는 유연하고 확장성이 뛰어난 구조를 이용해서 각 프로젝트에 맞는 최적화된 구조를 만들어내고, 관례를 따라 빠르게 개발 가능한 스프링 기반의 프레임워크를 만들어서 사용해야 한다.

#### DispatcherServlet과 MVC 아키텍처
스프링의 웹 기술은 MVC 아키텍처를 근간으로 하고 있다. MVC 아키텍처는 보통 프론트 컨트롤러 패턴과 함께 사용된다. 프론트 컨트롤러 패턴은 중앙집중형 컨트롤러를 프레젠테이션 계층의 제일 앞에 둬서 서버로 들어오는 모든 요청을 먼저 받아서 처리하게 만든다. 스프링이 제공하는 스프링 서블릿/MVC의 핵심은 DispatcherServlet 이라는 프론트 컨트롤러다.

서버가 브라우저나 여타 HTTP 클라이언트로부터 HTTP 요청을 받기 시작해서 다시 HTTP로 결과를 응답해주기까지의 과정을 살펴본다.

##### (1) DispatcherServlet의 HTTP 요청 접수
서블릿 컨테이너는 HTTP 프로토콜을 통해 들어오는 요청이 스프링의 DispatcherServlet에 할당된 것이라면 HTTP 요청정보를 DispatcherServlet에 전달해준다.

HTTP 요청은 GET 과 POST로 구분된다.

##### (2) DispatcherServlet에서 컨트롤러로 HTTP 요청 위임
DispatcherServle은 URL이나 파라미터 정보, HTTP 명령 등을 참고로 해서 어떤 컨트롤러에게 작업을 위임할지 결정한다. 컨트롤러를 선정하는 것을 DispatcherServlet의 핸들러 매핑 전략을 이용한다.

DispatcherServlet은 그 자체로 스프링 컨텍스트에 등록되는 빈이 아니므로 DI가 일어나는 것은 아니다. 하지만 마치 DI가 적용되는 것처럼 서블릿 애플리케이션 컨텍스트의 빈을 가져와 사용한다.

DispatcherServlet은 어떤 종류의 오브젝트라도 컨트롤러로 사용할 수 있다. 이것이 DispatcherServlet이 갖는 무한한 확장의 비결이다. 스프링 서블릿/MVC 확장구조의 기본은 바로 어댑터를 통한 컨트롤러 호출 방식이다.

##### (3) 컨트롤러의 모델 생성과 정보 등록
컨트롤러 작업 순서
- 사용자의 요청을 해석
- 서비스 계층 오브젝트에게 작업 위임
- 결과를 받아서 모델을 생성
- 어떤 뷰를 사용할지 결정

모델을 생성하고 모델에 정보를 넣어주는 게 컨트롤러가 해야 할 마지막 중요한 두 가지 작업 중 하나다. 컨트롤러가 어떤식으로든 다시 DispatcherServlet에 돌려줘야 할 두가지 정보가 모델과 뷰다.
모델은 모통 맵에 담긴 정보라고 생각하면 된다.

##### (4) 컨트롤러의 결과 리턴 : 모델과 뷰
MVC의 모든 요소가 그렇듯이 뷰도 하나의 오브젝트다. 보통은 뷰의 논리적인 이름을 리턴해주면 DispatcherServlet의 전략인 뷰 리졸버가 이를 이용해 뷰 오브젝트를 생성해준다.

ModelAndView 라는 오브젝트가 있는데, 이 ModelAndView가 DispatcherServlet이 최종적으로 어댑터를 통해 컨트롤러로부터 돌려받는 오브젝트다.

##### (5) DispatcherServlet의 뷰 호출과 (6) 모델 참조
DispatcherServlet이 컨트롤러로부터 전달받은 모델과 뷰를 받은 뒤 뷰 오브젝트에게 모델을 전달해주고 클라이언트에게 돌려줄 최종 결과물을 생성해달라고 요청하는 것이다.

##### (7) HTTP 응답 돌려주기
DispatcherServlet 은 등록된 후처리기가 있는지 확인하고, 있다면 후처리기에서 후속 작업을 진행한 뒤에 뷰가 만들어준 HttpServletResponse에 담긴 최종 결과를 서블릿 컨테이너에게 돌려준다.

#### DispatcherServlet의 DI 가능한 전략
DispatcherServlet에는 DI로 확장 가능한 전략이 있다고 언급했다. 스프링 MVC는 자주 사용되는 전략을 디폴트로 설정해주고 있다.

##### - HnadlerMapping
핸들러 매핑은 URL과 요청 정보를 기준으로 어떤 핸들러 오브젝트, 즉 컨트롤러를 사용할 것인지를 결정하는 로직을 담당한다.
DispatcherServlet은 하나 이상의 핸들러 매핑을 가질 수 있다.

##### - HandlerAdapter
핸들러 어댑터는 핸들러 매핑으로 선택한 컨트롤러/핸들러를 DispatcherServlet이 호출할 때 사용하는 어댑터다. 컨트롤러를 결정했다고 해도 호출방법을 DispatcherServlet이 알 길이 없다.
그래서 컨트롤러 타입을 지원하는 HandlerAdapter가 필요하다.

디폴트로 등록되어있는 어댑터
- HttpRequestHandlerAdapter
- SimpleControllerHandlerAdapter
- AnnotationMethodHandlerAdapter

##### - HandlerExceptionResolver
HandlerExceptionResolver 전략은 예외가 발생했을 때 이를 처리하는 로직을 갖고 있다. 예외가 발생했을 때 개발 컨트롤러가 아니라 프론트 컨트롤러인 DispatcherServlet을 통해 처리돼야 한다. DispatcherServlet은 등록된 HandlerExceptionResolver 중에서 발생한 예외에 적합한 것을 찾아서 예외처리를 위임한다.


##### - ViewResolver
뷰 리졸버는 컨트롤러가 리턴한 뷰 이름을 참고해서 적절한 뷰 오브젝트를 찾아주는 로직을 가진 전략 오브젝트다.

##### - LocaleResolver
지역정보를 결정해주는 전략이다. HTTP 헤더의 정보를 보고 지역정보를 설정해준다. (다국어 처리에 사용되는 것 같음.)

##### - ThemeResolver
테마를 가지고 이를 변경해서 사이트를 구성할 경우 쓸 수 있는 테마 정보를 결정해주는 전략이다.

##### - RequestToViewNameTranslator
컨트롤러에서 뷰 이름이나 뷰 오브젝트를 제공해주지 않았을 경우 URL과 같은 요청정보를 참고해서 자동으로 뷰 이름을 생성해주는 전략이다.

지금까지 살펴본 전략은 모두 DispatcherServlet의 동작방식을 확장할 수 있도록 만들어진 확장 포인트라고 볼 수 있다. DispatcherServlet은 서블릿 컨테이너가 생성하고 관리하는 오브젝트이지, 스프링의 컨텍스트에서 관리하는 빈 오브젝트가 아니다.


## 3.2 스프링 웹 애플리케이션 환경 구성

### 3.2.2 스프링 웹 학습 테스트
DispatcherServlet과 MVC 아키텍처가 적용된 웹 프레젠테이션 계층 기술에 대한 학습 테스트를 작성하는 일은 쉽지 않다. 서버에 배치하지 않은 채로 스프링 MVC의 기능을 테스트할 수 있는 방법을 알아본다.

서블릿이 서버 밖에서 테스트하기 쉽지 않은 대표적인 이유는 서블릿 컨테이너와 유사한 환경정보를 담은 오브젝트들을 구성해야 하고, 컨테이너가 브라우저에서 받은 사용자의 요청을 해석해서 만들어주는 HttpServletRequest와 결과를 저장할 HttpServletResponse도 필요하기 때문이다. HttpSession, ServletContext, HttpCookie, HttpHeader 등의 정보도 오브젝트 형태로 구성해서 전달해줘야 서블릿을 동작시킬 수 있기 때문이다.

스프링 테스트 모듈에 포함된 목 오브젝트인 MockHttpServletRequest, Response 등을 활용하면 웬만한 서블릿의 기능은 서버에 굳이 배치하지 않고도 테스트가 가능하다.

#### 서블릿 테스트용 목 오브젝트

##### - MockHttpServletRequest
##### - MockHttpServletResponse
##### - MockHttpSession
##### - McokServletConfig, MockServletContext
- 드물지만 ServletConfig나 ServletContext를 따로 만들어서 서블릿을 테스트해야 할 경우가 있다. 서블릿 컨텍스트 레벨에 저장해둔 오브젝트를 사용하는 경우가 대표적이다.


#### 테스트를 위한 DispatcherServlet 확장
DispatcherServlet을 테스트하려면 DispatcherServlet의 디폴트 설정을 최대한 사용한다고 해도 준비할 게 제법 있다. WEB-INF 밑에 '서블릿명-servlet.xml' 이름을 가진 설정파일도 준비해줘야 한다. 다양한 전략의 DispatcherServlet를 테스트하려면 설정파일도 다수 만들어야 되고 설정파일 위치도 변경해줘야 한다.

이러한 귀찮음을 해결하기 위한 테스트방법으로는 DispatcherServlet을 확장해서 사용하면 편리하다.

```java
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
```

#### ConfigurableDispatcherServlet을 이용한 스프링 MVC 테스트
ConfigurableDispatcherServlet의 장점은 컨트롤러의 종류나 DispatcherServlet의 전략 구성에 상관없이 적절한 웹 요청만 만들어주면, 스프링 MVC 애플리케이션이 동작하게 만들 수 있다는 점이다.

그런데 DispatcherServlet 전략과 상관없이 컨트롤러 클래스의 로직을 테스트하는게 목적이라면 DispatcherServlet을 거치지 않고 컨트롤러에 바로 요청을 보내서 검증하는 방법이 낫다.

이때는 순수하게 코드가 바르게 작성됐는지만 검증하려는 것이므로 DispatcherServlet에서 핸들러매핑 등이 제대로 됐는지의 테스트는 관심사항이 아니다.

## 3.3 컨트롤러
컨트롤러의 역할은 서비스 계층의 메서드를 선정하는 것과 메서드가 필요로 하는 파라미터 타입에 맞게 요청 정보를 변환해주는 것이다. 이후 서비스 계층의 작업이 끝난 후에 컨트롤러의 역할은 서비스 계층의 메서드가 돌려준 결과를 보고 어떤 뷰를 보여줘야 하는지 결정해야 한다. 때로는 페이지가 바뀌도록 리다이렉트 해줘야 한다. 뷰 선택이 끝나면 뷰에 출력할 내용을 모델에 적절한 이름으로 넣어줘야 한다.

상태를 세션에 저장하는 경우도 있다. DB나 URL 파라미터, 쿠키에 저장하기 어려운 경우 세션에 정보를 저장하는 것도 컨트롤러의 책임이다. 때로는 더 이상 필요 없어진 세션의 오브젝트를 제거해주는 작업도 필요하다.

애플리케이션 특성상 컨트롤러의 역할이 크다면 책임의 성격과 특징, 변경 사유 등을 기준으로 세분화해줄 필요가 있다. 스프링 MVC가 컨트롤러 모델을 미리 제한하지 않고 어댑터 패턴을 사용해서라도 컨트롤러의 종류를 필요에 따라 확장할 수 있도록 만든 이유가 바로 이 때문이다.

### 3.3.1 컨트롤러의 종류와 핸들러 어댑터
스프링 MVC가 지원하는 컨트롤러의 종류는 네 가지다. 각 컨트롤러를 DispatcherServlet에 연결해주는 핸들러 어댑터가 하나씩 있어야 하므로 핸들러 또한 네 개다. 이 중에서 SimpleServletHandlerAdapter를 제외한 세 개의 핸들러 어댑터는 DispatcherServlet에 디폴트 전략으로 설정되어 있다.

#### Servlet과 SimpleServletHandlerAdapter
첫 번째 컨트롤러 타입은 표준 서블릿이다. 기존에 서블릿으로 개발된 코드를 스프링 애플리케이션에 가져와 사용하려면 일단 서블릿을 web.xml에 별도로 등록하지 말고 스프링 MVC 컨트롤러로 등록해서 사용하는 게 좋다.
장점으로는 서블릿 클래스를 그대로 사용하면서 스프링 빈으로 등록된다는 점이다.

단 서블릿이 컨트롤러 빈으로 등록된 경우에는 자동으로 init(), destroy() 와 같은 생명주기 메서드가 호출되지 않는 점을 주의하자.

#### HttpRequestHandler와 HttpRequestHandlerAdapter
HttpRequestHandler는 인터페이스로 정의된 컨트롤러 타입이다. 서블릿 인터페이스와 비슷하다. 실제로 HttpRequestHandler는 서블릿처럼 동작하는 컨트롤러를 만들기 위해 사용한다. 전형적인 서블릿 스펙을 준수할 필요 없이 HTTP 프로토콜을 기반으로 한 전용 서비스를 만들려고 할 때 사용할 수 있다. HttpRequestHandler는 모델과 뷰 개념이 없는 HTTP 기반의 RMI (Remote Method Invocation)와 같은 로우레벨 서비스를 개발할 때 이용할 수 있다는 사실만 기억하자.

#### Controller와 SimpleControllerHandlerAdapter
SimpleControllerHandlerAdapter에 의해 실행되는 Controller 타입 컨트롤러는 인터페이스를 구현해서 만든다. 

Controller 타입의 컨트롤러는 다른 컨트롤러보다 유연하게 클래스를 설계할 수 있지만, 권장하진 않는다. 이유는 웹 브라우저를 클라이언트로 갖는 컨트롤러로서의 필수 기능이 구현된 AbstractController를 상속해서 컨트롤러를 만드는 게 편리하기 때문이다.

AbstractController 프로퍼티 목록

##### - synchronizeOnSession
HTTP 세션에 대한 동기화 여부를 결정하는 프로퍼티다. 

##### - supportedMethods
컨트롤러가 허용하는 HTTP 메서드 (GET, POST...) 를 지정할 수 있다.

##### - useExpiresHeader, useCacheControlHeader, useCacheControlNoStore, cacheSeconds
이 네 가지 프로퍼티는 Expires, Cache-control HTTP 헤더를 이용해서 브라우저의 캐시 설정정보를 보내줄 것인지를 결정한다.

#### AnnotationMethodHandlerAdapter
AnnotationMethodHandlerAdapter 는 여타 핸들러 어댑터와는 다른 독특한 특징이 있다. 가장 큰 특징은 지원하는 컨트롤러의 타입이 정해져 있지 않다는 점이다. 대신 클래스와 메서드에 붙은 몇 가지 어노테이션의 정보와 메서드 이름, 파라미터, 리턴 타입에 대한 규칙 등을 종합적으로 분석해서 컨트롤러를 선별하고 호출 방식을 결정한다. 그 덕분에 상당히 유연한 방식으로 컨트롤러를 작성할 수 있다.

또 다른 특징은 컨트롤러 하나가 하나 이상의 URL과 매핑된다. AnnotationMethodHandlerAdapter은 URL 의 매핑을 컨트롤러 단위가 아니라 메서드 단위로 가능하게 했다.

추가로 DefaultAnnotationHandlerMapping 핸들러 매핑과 함께 사용해야 한다.

### 3.3.2 핸들러 매핑
핸들러 매핑은 HTTP 요청정보를 이용해서 이를 처리할 핸들러 오브젝트, 즉 컨트롤러를 찾아주는 기능을 가진 DispatcherSerlvet의 전략이다. 하나의 핸들러 매핑 전략이 여러 가지 타입의 컨트롤러를 선택할 수 있다.

핸들러 매핑 전략 목록

#### BeanNameUrlHandlerMapping
디폴트 핸들러 매핑의 하나다. 빈의 이름에 들어있는 URL을 HTTP 요청의 URL과 비교해서 일치하는 빈을 찾아준다. URL에는 ANT 패턴이라고 불리는 *, **나  ? 와 같은 와일드카드를 사용하는 패턴을 넣을 수 있다.

#### ControllerBeanNameHandlerMapping
ControllerBeanNameHandlerMapping은 빈의 아이디나 빈 이름을 이용해 매핑해주는 핸들러 매핑 전략이다.

ControllerBeanNameHandlerMapping은 디폴트 매핑이 아니므로 사용하려면 빈으로 등록해줘야 한다. 특정 클래스를 빈으로 등록한 경우에는 디폴트 전략은 모두 무시된다.

#### ControllerClassNameHandlerMapping
ControllerClassNameHandlerMapping 은 빈 이름 대신 클래스 이름을 URL에 매핑해주는 핸들러 매핑 클래스다.

디폴트 전략이 아니므로 사용하려면 빈으로 등록해줘야 한다.

#### SimpleUrlHandlerMapping
SimpleUrlHandlerMapping은 URL과 컨트롤러의 매핑정보를 한 곳에 모아놓을 수 있는 핸들러 매핑 전략이다.

프로퍼티에 매핑정보를 직접 넣어줘야 하므로 SimpleUrlHandlerMapping 빈을 등록해야 사용할 수 있다.

SimpleUrlHandlerMapping의 장점은 매핑정보가 한 곳에 모여 있기 때문에 URL을 관리하기가 편리하다는 것이다. 그래서 컨트롤러의 개수가 많은 대규모 프로젝트에서 선호한다.

#### DefaultAnnotationHandlerMapping
@RequestMapping이라는 어노테이션을 컨트롤러 클래스나 메서드에 직접 부여하고 이를 이용해 매핑하는 전략이다.

#### 기타공통 설정정보
핸들러 매핑 설정에서 공통적으로 사용되는 주요 프로퍼티만 간단히 설명 한다.

##### - order
핸들러 매핑은 한 개 이상을 동시에 사용할 수 있다. 기본적으로 이미 두 개의 매핑이 등록되어 있다. 물론 한 가지 매핑 방식으로 통일하면 가장 이상적이다.

두 개의 핸들러 매핑을 사용할 때 동일한 URL 매핑정보를 주의해야 한다. 이런 경우를 위해 핸들러 매핑의 우선순위를 지정할 수 있다.

##### - defaultHandler
핸들러 매핑 빈의 defaultHandler 프로퍼티를 지정해두면 URL을 찾지 못했을 때 자동으로 디폴트 핸들러를 선택해준다. URL을 찾지못해 404에러를 던질 때 안내문구를 사용하는 URL로 설정해두면 편리하다.

##### - alwaysUseFullPath
URL 매핑은 기본적으로 웹 애플리케이션의 컨텍스트 패스와 서블릿 패스 두 가지를 제외한 나머지만 가지고 비교한다. URL 기준을 상대경로만 사용하는 이유는 웹 애플리케이션의 배치 경로와 서블릿 매핑을 변경하더라도 URL 매핑정보가 영향받지 않도록 하기 위해서다.

HTML의 링크라면 상대경로를 사용하면 되지만 URL은 절대경로를 사용하므로 바뀌지 않는 부분만 매핑에 이용하는 것이 바람직하다.

하지만 위의 옵션은 URL의 전체경로로 매핑해주길 원할 때 사용한다.

##### - detectHandlerslnAncestorContexts
일반적으로 자식 컨텍스트는 루트 컨텍스트를 참조할 수 있다. 하지만 핸들러 매핑의 경우는 다르다. 핸들러 매핑 클래스는 기본적으로 현재 컨텍스트 (서블릿 컨텍스트) 안에서만 매핑할 컨트롤러를 찾는다. 웹 환경에 종속적인 컨트롤러 빈은 서블릿 컨텍스트에만 두는 것이 바람직하기 때문이다.

위 옵션을 true로 변경하면 부모 컨텍스트까지 뒤져서 매핑 대상 컨트롤러를 찾게 할 수는 있다. 하지만 절대 사용하지 말자.

### 3.3.3 핸들러 인터셉터
핸들러 인터셉터는 DispatcherServlet이 컨트롤러를 호출하기 전, 후처리 할수 있는 일종의 필터다.

핸들러 매핑의 역할은 URL로부터 컨트롤러만 찾아주는 것이 아니다.
핸들러 매핑은 DispatcherServlet이 매핑요청을 하면 핸들러매핑은 HandlerExecutionChain을 돌려준다. 핸들러 인터셉터를 설정하지 않았다면 바로 컨트롤러가 실행된다. 반면에 하나 이상의 핸들러 인터셉터를 지정했다면 순서에 따라 인터셉터를 거친 후에 컨트롤러가 호출된다.

#### HandlerInterceptor
핸들러 인터셉터는 HandlerInterceptor 인터페이스를 구현해서 만든다.

##### - boolean preHandler()
컨트롤러 호출 전에 실행된다. 반환값이 true면 핸들러 체인 다음 단계로 진행하고, false면 작업을 중단하고 리턴한다.

##### - void postHandler()
컨트롤러가 실행된 이후에 실행되는 메서드

##### - void afterCompletion()
메서드 이름 그대로 모든 뷰에서 최종 결과를 생성하는 일을 포함한 모든 작업을 완료된 후에 실행된다.

#### 핸들러 인터셉터 적용
핸들러 매핑 빈의 interceptors 프로퍼티를 이용해 핸들러 인터셉터 빈의 레퍼런스를 넣어주면 된다.

핸들러 인터셉터는 기본적으로 핸들러 매핑 단위로 등록된다.

### 3.3.4 컨트롤러 확장
```java
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @Interface ViewName {
	String value();
}

@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @Interface RequiredParams {
	String[] value();
}

public class HelloController implements SimpleController {

	@ViewName("/WEB-INF/view/hello.jsp")
	@RequiredParams({"name"})
	public void control(Map<String, String> params, Map<String, Object> model) {
		model.put("message", "Hello " + params.get("name"));
	}
}

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
		
		Map<String, String> params = new HashMap<>();
		for (String param : requiredParams.value()) {
			String value = request.getParameter(param);
			if (value == null) throw new IllegalStateException();
			params.put(param, value);
		}
		
		Map<String, Object> model = new HashMap<>();
		
		((SimpleController)handler).control(params, model);
		
		return new ModelAndView(viewName.value(), model); 
	}

	// getLastModified()는 컨트롤러의 getLastModified() 메서드를 다시 호출해서 컨트롤러가 결정하도록 만든다. 캐싱을 하지않으려면 0보다 작은 값을 리턴하면 된다. 
	@Override
	public long getLastModified(HttpServletRequest request, Object handler) {
		return -1;
	}
}

```