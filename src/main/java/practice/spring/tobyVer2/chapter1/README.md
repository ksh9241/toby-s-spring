# 1장. IoC 컨테이너와 DI

### 1.1 IoC 컨테이너 : 빈 팩토리와 애플리케이션 컨텍스트
스프링에선 IoC를 담당하는 컨테이너를 빈 팩토리 또는 애플리케이션 컨텍스트라고 부르기도 한다. 오브젝트 생성과 오브젝트 사이의 런타임 관계를 설정하는 DI 관점으로 볼 떄는 컨테이너를 빈 팩토리라고 한다.

#### 1.1.1 IoC 컨테이너를 이용해 애플리케이션 만들기

##### POJO 클래스
각각의 POJO는 특정 기술과 스펙에서 독립적일뿐더러 의존관계에 있는 다른 POJO와 느슨한 결합을 갖도록 만들어야 한다.

##### 설정 메타정보
스프링의 설정 메타정보는 BeanDefinition 인터페이스로 표현되는 순수한 추상 정보다. 즉 애플리케이션 컨텍스트는 바로 이 BeanDefinition으로 만들어진 메타정보를 담은 오브젝트를 사용해 IoC와 DI 작업을 수행한다.
원본의 포맷과 구조, 자료의 특성에 맞게 읽어와 BeanDefinition 오브젝트로 변환해주는 BeanDefinitionReader가 있으면 된다.

- 빈 메타정보
	- 빈 아이디, 이름, 별칭 : 빈 오브젝트를 구분할 수 있는 식별자
	- 클래스 또는 클래스 이름 : 빈으로 만들 POJO 클래스 또는 서비스 클래스 정보
	- 스코프 : 싱글톤, 프로토타입과 같은 빈의 생성 방식과 존재 범위
	- 프로퍼티 값 또는 참조 : DI에 사용할 프로퍼티 이름과 값 또는 참조하는 빈의 이름
	- 생성자 파라미터 값 또는 참조 : DI에 사용할 생성자 파라미터 이름과 값 또는 참조할 빈의 이름
	- 지연로딩 여부, 우선 빈 여부, 자동와이어링 여부, 부모 빈 정보, 빈팩토리 이름 등

결국 스프링 애플리케이션이란 POJO 클래스와 설정 메타정보를 이용해 IoC컨테이너가 만들어주는 오브젝트 조합이다.


#### 1.1.2 IoC컨테이너의 종류와 사용방법
이미 스프링에는 다양한 용도로 쓸 수 있는 십여 개의 ApplicationContext 구현 클래스가 존재한다. 직접 코드를 통해 ApplicationContext 오브젝트를 생성하는 경우는 거의 없다. 스프링에서 제공하는 ApplicationContext 구현 클래스 종류에 대해 살펴보자.

##### StaticApplicationContext
BeanDefinition 오브젝트를 직접 만들고, 코드를 통해 IoC 컨테이너에 등록하는 방법을 사용해봤다. 이 때 사용하는 컨테이너는 StaticApplicationContext다. StaticApplicationContext는 코드를 통해 빈 메타정보를 등록하기 위해 사용한다.
스프링의 기능에 대한 학습테스트를 제외하면 실제로 사용하지 않는다. 스태틱 애플리케이션 컨텍스트는 실전에서는 사용하면 안 된다. ( 이유에 대해 찾아봤지만 안나와서 코드를 봐보니 기본생성자가 Null로 되어있어서 그런가?? 추후 더 찾아볼 예정 )

##### GenericApplicationContext
GenericApplicationContext는 가장 일반적인 애플리케이션 구현 클래스이다. 실전에서 사용될 수 있는 모든 기능을 갖추고 있으며 컨테이너의 주요 기능을 DI를 통해 확장할 수 있도록 설계되었다. StaticApplicationContext 와는 달리 XML 파일과 같은 외부의 리소스에 있는 빈 설정 메타정보를 리더를 통해 읽어들여서 메타정보로 전환해서 사용한다. 스프링은 XML 말고도 프로퍼티 파일에서 빈 설정 메타정보를 가져오는 PropertiesBeanDefinitionReader도 제공한다.
JUnit 테스트 내에서 사용할 수 있는 애플리케이션 컨텍스트를 자동으로 만들어 주는데 이 컨텍스트가 바로 GenericApplicationContext다.

##### GenericXmlApplicationContext
GenericXmlApplicationContext 는 XmlBeanDefinitionReader를 내장하고 있기 떄문에 따로 만들지 않고 XML파일을 읽어서 refresh를 통해 초기화하는 것 까지 한 줄로 끝낼 수 있다.

##### WebApplicationContext
웹 환경에서 main() 메서드 대신 서블릿 컨테이너가 브라우저로의 오는 HTTP 요청을 받아서 해당 요청에 매핑되어 있는 서블릿을 실행해주는 방식으로 동작한다. 서블릿이 일종의 main() 메서드와 같은 역할을 하는 셈이다.
서블릿 컨테이너는 브라우저와 같은 클라이언트로부터 들어오는 요청을 받아서 서블릿을 동작시켜주는 일을 맡는다. 다행히도 스프링은 웹 환경에서 애플리케이션 컨텍스트를 생성하고 설정 메타 정보로 초기화해주고, 클라이언트로부터 들어오는 요청마다 적절한 빈을 찾아서 이를 실행해주는 기능을 가진 DispatcherServlet이라는 이름의 서블릿을 제공한다. 일단 스프링 IoC 컨테이너는 WebApplicationContext 인터페이스를 구현한 것임을 기억하자.

#### 1.1.3 IoC 컨테이너 계층구조
IoC 컨테이너는 애플리케이션마다 하나씩이면 충분하다. 빈의 개수가 많아져서 설정파일이 커지는게 문제라면 쪼개서 만들고 하나의 애플리케이션 컨텍스트가 어려 개의 설정파일을 사용하게 하면 그만이다.
하지만 한 개 이상의 IoC 컨테이너를 만들어두고 사용해야 할 때가 있는데 바로 트리 모양의 계층 구조이다.

##### 부모 컨텍스트를 이용한 계층구조 효과
계층구조 안에 모든 컨텍스트는 각자 독립적인 설정정보를 가지고 빈 오브젝트를 만들고 관리한다. 하지만 DI를 위해 빈을 찾을 때는 자신의 빈부터 부모 컨텍스트의 빈까지 모두 검색한다. 다만 하위 컨텍스트에서는 빈을 검색하지 않는다. 그런 의미에서 같은 레벨에 있는 형제 컨텍스트의 빈도 찾을 수 없다. 때문에 자신이 만든 스프링 애플리케이션이 어떻게 컨텍스트가 만들어지고 어느 것이 루트 컨텍스트이고, 어느 것이 그 자식 컨텍스트인지는 분명하게 알아야 한다.

#### 1.1.4 웹 애플리케이션의 IoC 컨테이너 구성
애플리케이션에서 IoC 컨테이너를 사용하는 방법은 크게 세 가지로 구분된다. 두 가지는 웹 모듈 안에 컨테이너를 두는 것이고, 하나는 엔터프라이즈 애플리케이션 레벨에 두는 방법이다.

많은 웹 요청을 한 번에 받을 수 있는 대표 서블릿을 등록해두고, 공통적인 선행 작업을 수행하게 한 후에 각 요청의 기능을 담당하는 핸들러라고 불리는 클래스를 호출하는 방식으로 개발한다.

##### 웹 애플리케이션의 컨텍스트 계층구조
웹 애플리케이션 컨텍스트에 등록되는 컨테이너는 루트 웹 애플리케이션 컨텍스트라고 불린다. 일반적으로 전체 계층구조 내에서 가장 최상단에 위치한 루트 컨텍스트가 되기 떄문이다.
그런데 여러 개의 자식 컨텍스트를 두고 공통적인 빈을 부모 컨텍스트로 뽑아내서 공유하려는게 아니라면 왜 이렇게 계층구조로 만들까?? 이유는 전체 애플리케이션에서 웹 기술에 의존적인 부분과 그렇지 않은 부분을 구분하기 위해서다.
데이터 엑세스 계층, 서비스 계층은 스프링 기술을 사용하고 스프링 빈으로 만들지만 웹을 담당하는 프레젠테이션 계층은 스프링 외의 기술을 사용하는 경우도 종종 있기 때문이다.

스프링은 웹 애플리케이션마다 하나씩 존재하는 서블릿 컨테이너를 통해 루트 애플리케이션 컨텍스트에 접근할 수 있는 방법을 제공한다.

- WebApplicationContextUtils.getWebApplicationContext(ServletContext sc) : DispatcherServlet이나 WebApplicationContext에 접근할 수 있는 static 메서드를 제공한다.

ServletContext는 웹 애플리케이션마다 하나씩 만들어지는 것으로, 서블릿의 런타임 환경정보를 담고있다.

##### 웹 애플리케이션 컨텍스트 구성 방법
1. 컨텍스트 계층구조 만들기
2. 컨텍스트를 하나만 사용하기

- 서블릿 컨텍스트와 루트 애플리케이션 컨텍스트 계층구조
	- 가장 많이 사용되는 기본적인 구성방법이다. 스프링 웹 기술을 사용하는 경우 웹 관련 빈들은 서블릿의 컨텍스트에 두고 나머지는 루트 애플리케이션 컨텍스트에 등록한다.

- 루트 애플리케이션 컨텍스트 단일구조
	- 스프링 웹 기술을 사용하지 않고 서드파티 웹 프레임워크나 서비스 엔진만을 사용해서 프레젠테이션 계층을 만든다면 스프링 서블릿을 둘 이유가 없다.

- 서블릿 컨텍스트 단일 구조
	- 스프링 웹 기술을 사용하면서 스프링 외의 프레임워크나 서비스 엔진에서 스프링 빈을 이용할 생각이 아니라면 루트 애플리케이션 컨텍스트를 생략할 수도 있다.


##### 루트 애플리케이션 컨텍스트 등록
웹 애플리케이션 레벨에 만들어지는 루트 웹 애플리케이션 컨텍스트를 등록하는 가장 간단한 방법은 서블릿의 이벤트 리스너를 이용하는 것이다. 웹 애플리케이션 전체에 적용 가능한 DB 연결 기능이나 로깅 같은 서비스를 만드는 데 유용하게 쓰인다. 스프링은 이러한 기능을 가진 리스너인 ContextLoaderListener를 제공한다.

```JAVA
/** 
ContextLoaderListener 등록 방법 : web.xml에 리스너 선언
기능 : 웹 애플리케이션이 시작할 때 자동으로 루트 애플리케이션 컨텍스트 생성 후 초기화

Default
애플리케이션 컨텍스트 클래스 : XmlWebApplicationContext
XML 설정파일 위치 : /WEB-INF/applicationContext.xml
*/

<listener>
	<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
```

- contextConfigLocation : 디폴트 XML 설정파일 위치를 바꿀 수 있다.
- contextClass : 디폴트 애플리케이션컨텍스트 클래스를 변경할 수 있다.

##### 서블릿 애플리케이션 컨텍스트 등록
스프링의 웹 기능을 지원하는 프론트 컨트롤러 서블릿은 DispatcherServlet이다. 이름에서 알 수 있듯이 web.xml에 등록해서 사용할 수 있는 평범한 서블릿이다. 각 DispatcherServlet은 서블릿이 초기화 될 때 자신만의 컨텍스트를 생성하고 초기화한다.

### 1.2 IoC/DI를 위한 빈 설정 메타정보 작성
IoC 컨테이너의 가장 기본적인 역할은 코드를 대신해서 애플리케이션을 구성하는 오브젝트를 생성하고 관리하는 것이다. POJO로 만들어진 애플리케이션 클래스와 서비스 오브젝트들이 그 대상이다.
빈을 만들기 위한 설정 메타정보는 파일이나 어노테이션 같은 리소스로부터 전용 리더를 통해 얽혀서 BeanDefinition 타입의 오브젝트로 변환된다. BeanDefinition에 어떠한 방식으로든 빈 생성 정보만 담겨있다면 IoC 컨테이너가 빈을 읽어서 처리할 수 있다.

#### 1.2.1 빈 설정 메타정보
BeanDefinition은 여러 개의 빈을 만드는 데 재사용될 수 있다. 설정 메타정보가 같지만 이름이 다른 여러 개의 빈 오브젝트를 만들 수 있기 때문이다.
따라서 BeanDefinition 에는 빈의 이름이나 아이디를 나타내는 정보를 포함하지 않는다. 대신 IoC 컨테이너에 이 BeanDefinition 정보가 등록될 때 이름을 부여해줄 수 있다.

#### 1.2.2 빈 등록 방법
빈 등록 방법은 메타정보를 작성해서 컨테이너에게 건네주면 된다. 보통 XML문서, 프로퍼티 파일, 소스코드 어노테이션과 같은 외부 리소스로 빈 메타정보를 작성하고 이를 적절한 리더나 변환기를 통해 애플리케이션 컨텍스트가 사용할 수 있는 정보로 변환해주는 방법을 사용한다.

스프링에서 자주사용되는 빈의 등록방법 5가지

- XML :<Bean>태그
- XML : 네임스페이스와 전용 태그
- 자동인식을 이용한 빈 등록 : 스테레오타입 어노테이션과 빈 스캐너
- XML을 이용한 빈 스캐너 등록
- 빈 스캐너를 내장한 애플리케이션 컨텍스트 사용


##### 자바 코드에 의한 빈 등록 : @Configuration 클래스의 @Bean 메서드
자바코드를 이용한 빈 등록에 사용되는 클래스는 그저 평범한 자바 코드처럼 동작하지 않는다는 사실을 알아야 한다. @Bean이 붙은 메서드는 new를 통한 여러번 생성을 시켜도 싱글톤이 디폴트 값이기 때문에 최초 한번만 빈으로 생성된다.  DI를 통해 다른 여러 빈에 참조되든, getBean() 메서드에 의해 가져오든 상관없이 한 개의 오브젝트만 생성이 되고 더 이상 새로운 오브젝트가 만들어지지 않도록 특별한 방법으로 @Bean 메서드를 조작해둔다.
@Configuration 과 @Bean을 사용하는 클래스는 순수한 오브젝트 팩토리 클래스라기보다는 자바 코드로 표현하는 메타정보라고 이해하는 것이 좋다.

자바코드에 의한 설정이 XML과 같은 외부 설정 파일을 이용하는 것보다 유용한 점

- 컴파일러나 IDE를 통한 타입 검증이 가능하다.
	- XML은 런타임 시 예외가 발생해야 오류 검증이 가능한데 자바코드는 컴파일 시 오류를 확인할 수 있다.

- 자동완성과 같은 IDE 지원 기능을 최대한 이용할 수 있다.
	- XML과 다르게 자동완성의 기능을 이용하여 오타도 줄이면서 빠르게 만들 수 있다.

- 이해하기 쉽다.
	- 평범한 자바코드가 <Bean>태그보다 더 친숙하다.

- 복잡한 빈 설정이나 초기화 작업을 손쉽게 적용할 수 있다.


##### 자바 코드에 의한 빈 등록 : 일반 빈 클래스의 @Bean 메서드
@Configuration 어노테이션이 붙은 클래스가 아닌 일반 POJO클래스에서도 @Bean을 사용할 수 있다. 다만 이때는 POJO 클래스에서 빈을 생성하기 때문에 싱글톤 빈으로 사용되지 않는다. 그렇기 때문에 일반 클래스에서 @Bean 사용 할 때는 DI 코드를 주의해서 작성해야 한다.

@Bean 메서드가 정의된 클래스 밖에서 사용할 수 없게 scope를 private으로 선언한 뒤 클래스 내부에서 DI를 통해서 참조하도록 한다.


##### 빈 등록 메타정보 구성 전략

- XML 단독 사용 : 모든 빈을 명시적으로 XML에 등록하는 방법
	- 모든 빈을 XML에서 확인할 수 있는 장점
	- 빈이 많아지면 XML관리가 번거로울 수 있다는 단점

- XML과 빈 스캐닝의 혼용 : XML과 빈 스캐너에 의한 자동인식 방법을 함께 사용하는 방법
	- 애플리케이션 3계층의 핵심로직을 빈 스캐닝에 의한 자동인식 대상으로 처리
	- 불편한 기술서비스, 기반 서비스, 컨테이너 설정 등의 빈은 XML을 사용하여 처리
	- 스캔 대상이 되는 클래스의 패키지를 지정해줘야 한다. (context:component-scan)

- XML 없이 빈 스캐닝 단독 사용 : 모든 빈의 등록을 XML 없이 자동스캔만으로 가져가는 방법
	- 스프링 3.0에서 처음 가능하다.
	- 루트컨텍스트와 서블릿 컨텍스트 모두 contextClass 파라미터를 추가해 AnnotationConfigWebApplicationContext로 컨텍스트 클래스를 변경해줘야한다.
	- contextLocations 파라미터에는 스캔 대상 패키지를 넣어줘야 한다.
	- 장점으로는 빈의 모든 정보가 자바코드에 담겨 있으므로 타입에 안전한 방식으로 작성 가능
	- 단점은 스키마에 정의된 전용 태그를 사용할 수 없다. (aop, tx 등)


#### 1.2.3 빈 의존관계 설정 방법
총 8가지의 빈 의존관계 주입 방법이 존재한다.

##### XML: <property>, <constructor-arg>
<bean>을 이용해 빈을 등록했다면 프로퍼티와 생성자 두 가지 방식으로 DI를 지정할 수 있다. 프로퍼티는 수정자 메서드를 사용하고, 생성자는 빈 클래스의 생성자를 이용하는 방법이다.

- <property>: 수정자 주입

수정자를 통한 의존관계의 빈을 주입하려면 <property> 태그를 사용할 수 있다. DI의 가장 대표적인 방법이다.
XML의 <property> 에는 해당 프로퍼티의 타입정보가 나타나지 않는다. 따라서 주입 대상 프로퍼티와 주입될 빈 또는 값의 타입이 호환되는지 주의를 기울여서 작성해야 한다.

```JAVA
<property name="name" value="Spring" />
<property name="age" value="30" />
<property name="myClass" value="java.lang.String" />
```

- <constructor-arg>: 생성자 주입

constructor-arg는 생성자를 통한 빈 또는 값의 주입에 사용된다. 생성자의 파라미터를 이용하기 때문에 한 번에 여러 개의 오브젝트를 주입할 수 있다.
수정자 메서드처럼 간단히 이름을 이용하는 대신 파라미터의 순서나 타입을 명시하는 방법이 필요하다.

```JAVA
<constructor-arg index="0" value="Spring" />
<constructor-arg type="package.depth1.Printer" ref="printer" />
```

##### XML : 자동와이어링
자동와이어링은 명시적으로 프로퍼티나 생성자 파라미터를 지정하지 않고 미리 정해진 규칙을 이용해 자동으로 DI 설정을 컨테이너가 추가하도록 만드는 것이다.

- byName: 빈 이름 자동와이어링

autowire="byName" 옵션으로 해당 클래스의 프로퍼티 이름과 동일한 빈을 찾아서 자동으로 프로퍼티로 등록해준다.

```JAVA
<bean id="hello" class="...Hello" autowire="byName">
	<property name="name" value="Spring" />
	<!-- autowire="byName" 옵션을 주어 printer빈의 id와 Hello 빈의 printer인스턴스명을 보고 DI를 하여 생략하였다. -->
</bean>

<bean id="printer" class="...StringPrinter" />
```

- byType: 타입에 의한 자동와이어링

이전에 만들어진 클래스를 재사용하거나 규모가 큰 프로젝트라 모든 개발자가 명명규칙을 정확하게 부여하기가 어렵다면 이름 대신 타입에 의한 자동와이어링을 사용할 수 있다.
autowire="byType"을 옵션에 <bean>에 넣어주거나 default-autowire=byType"을 <beans>에 넣어주면 된다.
단점으로는 타입이 같은 빈이 두 개 이상 존재하는 경우에는 적용되지 못한다.

##### XML : 네임스페이스와 전용 태그
스키마를 정의해서 사용하는 전용 태그의 의존관계 지정은 단순하지 않다. 태그 하나당 몇 개의 빈이 만들어지는 지 각 빈의 이름은 무엇인지가 명확하지 않기 때문이다.
규칙은 아니지만 관례적으로 전용 태그에 의해 만들어지는 빈을 다른 빈이 참조할 경우에는 id 어트리뷰트를 사용해 빈의 아이디를 지정한다. 그래서 다른 빈의 DI할 때 ref값으로 넣어줄 수 있다.
상당수의 전용 태그는 ID조차 선언하지 않는 경우가 많다. 대부분 컨테이너가 참조하는 설정정보로만 사용되기 때문이다.

##### 어노테이션 : @Resource
- 수정자 메서드

수정자 setter는 가장 대표적인 DI 방법이다.
@Resource와 같은 어노테이션으로 된 의존관계 정보를 이용해 DI가 이뤄지게 하려면 다음 세 가지 방법 중 하나를 선택해야 한다.

1. XML의 <context:annotation-config />

@Resource와 같은 어노테이션 의존관계를 읽어서 메타정보를 추가해주는 기능을 가진 빈 후처리기를 등록해주는 전용 태그다.


2. XML의 <context:component-scan />

빈 스캐닝을 통한 빈 등록 방법을 지정하는 것


3. AnnotationConfigApplicationContext 또는 AnnotationConfigWebApplicationContext

빈 스캐너와 어노테이션 의존관계 정보를 읽는 후처리기를 내장한 애플리케이션컨텍스트를 사용하는 것이다.


- 필드

@Resource는 필드에도 붙을 수 있다. 수정자가 없어도 상관없다. 필드의 접근자는 public이 아니어도 상관없다. 프로퍼티에 대한 수정자가 없다면 코드가 깔끔해지긴하지만 컨테이너 밖에서 수동으로 DI할 경우 불편하다. 단위테스트가 필요한 클래스라면 수정자 없는 필드 주입을 사용하는 건 별로 바람직하지 않다.
반면 컨테이너를 이용하는 통합 테스트를 주로 하는 DAO에서는 수정자 없이 필드 주입만을 사용해도 별 문제가 되지 않는다.

XML의 자동와이어링은 각 프로퍼티에 주입할 만한 후보 빈이 없을 경우에 무시하고 넘어간다. 하지만 @Resource같은 경우 반드시 참조할 빈이 존재해야 한다. 만약 DI할 빈을 찾을 수 없다면 예외가 발생한다.
참조할 빈의 이름을 이용하여 빈을 찾는다. 하지만 이름을 통한 빈을 찾을 수 없을 경우 타입을 통해 한번 더 빈을 찾는다.


##### 어노테이션 :@Autowired/@Inject
어노테이션을 이용한 의존관계 설정 방법의 두 번째는 @Autowired와 @Inject를 이용하는 것이다.
스프링으로 개발한 POJO를 앞으로 다른 환경에서도 사용할 가능성이 있다면 @Inject와 DIJ에서 정의한 어노테이션을 사용하는 게 좋다.

- 수정자 메서드와 필드

@Resource와 비슷하지만 다른점은 이름이 아닌 타입을 기준으로 먼저 DI할 빈을 찾는다.


- 생성자

@Autowired는 @Resource와 다르게 생성자에도 부여할 수 있다. @Autowired는 단 하나의 생성자에만 사용할 수 있다는 제한이 있다.

- 일반 메서드

@Autowired는 수정자, 생성자 외의 일반 메서드에도 적용할 수 있다. 생성자 주입은 모든 프로퍼티를 DI해야 하고, 수정자 주입은 수정자메서드의 관리가 번거롭기 때문에 각각의 장단점이 존재한다.
그래서 등장한 것이 일반 메서드를 사용한 DI 방법이다. 생성자 주입과 딜리 오브젝트 생성 후에 차례로 호출이 가능하므로 여러 개를 만들어도 된다.

- 컬렉션과 배열

@Autowired를 이용하면 같은 타입의 빈이 하나 이상 존재할 때 그 빈들을 모두 DI받도록 할 수 있다. @Autowired의 대상이 되는 필드나 프로퍼티, 메서드의 파라미터를 컬렉션이나 배열로 선언하면 된다.
컬렉션과 배열을 단지 같은 타입의 빈이 여러 개 등록되는 경우에 충돌을 피하려는 목적으로 사용해서는 안 된다. 의도적인 목적을 가지고 사용해야 한다.

```JAVA
@Autowired
Collection<Printer> printers;

@Autowired
Printer[] printers;

@Autowired
Map<String, Printer> printerMap;
```

- @Qualifier

Qualifier는 타입 외의 정보를 추가해서 자동와이어링을 세밀하게 제어할 수 있는 보조적인 방법이다.
@Qualifier를 사용했을 때 한 가지 기능이 더 있다. @Qualifier("mainDB")로 설정했을 때 Qualifier 속성 값의 mainDB가 없다면 mainDB로 된 빈을 한번 더 확인한다. 이 방법은 혼란을 초래할 수 있어서 권장하진 않지만 예외를 발생시키지 않을 수도 있다.


##### @Autowired와 getBean(), 스프링 테스트
getBean("빈 이름") 은 기본적으로 Object 타입으로 리턴한다. 따라서 원하는 타입으로 캐스팅해야 한다. 특정 타입 빈이 하나만 존재한다면 @Autowired 처럼 이름 대신 타입을 이용해서 빈을 찾을 수 있다.


##### 자바코드에 의한 의존관계 설정
@Configuration 과 @Bean을 이용하여 자바코드로 빈을 등록하는 경우

- @Bean 메서드 호출

@Configuration 이 붙지 않은 클래스의 @Bean 메서드에서는 싱글톤으로 오브젝트가 관리되지 않기 때문에 사용하면 안된다는 사실을 주의해야 한다.

#### 1.2.4 프로퍼티 값 설정 방법
보통 싱글톤은 동시성 문제 때문에 필드 값을 함부로 수정하지 않는다. 대개는 상태가 없는 방식으로 만들기 때문에 필드에 있는 값은 읽기전용인 경우가 대부분이다.

##### 메타정보 종류에 따른 값 설정 방법
값을 넣는 방법도 빈 등록 방법과 마찬가지로 네 가지로 구분해서 볼 수 있다.

- XML : <property>와 전용 태그

ref 어트리뷰트를 이용해 다른 빈의 아이디를 지정한다. 만약 value 어트리뷰트를 사용한다면 런타임 시 주입할 값으로 인식한다.


- 어노테이션 : @Value

빈이 사용해야 할 단순한 값이나 오브젝트를 코드에 담지 않고 설정을 통해 런타임시 주입해주는 이유

빈 의존관계는 아니지만 어떤 값을 외부에서 주입해야 하는 용도는 두 가지가 있다.

1. 환경에 따라 매번 달라질 수 있는 값으로 대표적으로DataSource 타입의 빈에 제공하는 DriverClass, userName, password, URL이 있다.

2. 클래스의 필드에 초기값을 설정해두고 대개는 그 값을 사용하지만 특별한 경우 초기값 대신 다른 값을 지정하고 싶을 경우가 있다.

```JAVA
public class Test {
/**
 인스턴스 값 초기화와 @Value의 차이점
 @Value 어노테이션은 스프링 컨테이너가 참조하는 정보이지 그 자체로 클래스의 필드에 값을 넣어주는 기능이 있는 것은 아니다.
 따라서 테스트 코드와 같이 컨테이너 밖에서 사용된다면 @Value 어노테이션은 무시된다.
 @Value로 값을 설정해준다는 것은 자바 코드와 컨테이너가 런타임 시에 주입하는 정보를 분리하겠다는 의미이고, 외부로부터의 주입을 통한 초기화가 반드시 필요하다고 이해할 수 있다.
*/
	String a = "초기화";
	
	@Value("초기화")
	String b;

	// username속성이 정의된 database.properties 파일의 XML에서 지정해둬야 한다.
	@Value("${database.username}")
	String userName;
}
```

- 자바코드 : @Value

클래스 자체가 메타정보이기 때문에, 설정을 변경해야 할 때마다 코드를 수정하고 재컴파일하는 게 문제가 되지 않는다. 하지만 환경에 종속적인 정보는 역시 환경정보나 프로퍼티 파일에서 가져오는 것이 바람직하다.


##### PropertyEditor와 ConversionService
XML의 value 어트리뷰트나 @Value의 엘리먼트는 모두 텍스트 문자로 작성된다. 타입이 String이라면 상관없지만 그 외의 타입인 경우라면 타입을 변경하는 과정이 필요하다.

스프링은 두 가지 타입 변환 서비스를 제공한다. 디폴트로 사용되는 타입 변환기는 PropertyEditor라는 java.beans의 인터페이스를 구현한 것이다.

- 기본타입

스프링의 내장 프로퍼티 에디터가 변환을 지원하는 기본 타입이다.

[boolean, Boolean, byte, Byte, short, Short, int, Integer, long, Long, float, Float, double, Double, BigDecimal, BigInteger, char, Character, String]

int 같은 기본타입은 물론이고 Integer같은 오브젝트 타입도 함께  지원한다.


- 배열

값을 콤마로 구분해서 넣어주면 배열 형태로 변환한다.

@Value("1,2,3,4") int[] arr;

- 기타
	- charset : UTF-8, ISO-8895-1 과 같은 값을 Charset 타입으로 만들어준다.
	- Class : JDBC 드라이버 클래스를 선언할 때 자주 사용했던 타입이다.
	- Currency : ISO 4217 코드를 따르는 Currency타입으로 변환해준다.
	- File : File 타입으로 변환해준다. file:, classpath: 와 같은 접두어를 사용할 수 있다.
	- InputStream : InputStream 타입으로 변환해준다.
	- Pattern : Pattern 타입으로 변환해준다.
	- Resource : 스프링의 리소스 타입으로 변환해준다. 배열도 지원
	- Timezone : Timezone으로 변환해준다.
	- URI, URL : URI 또는 URL로 변환해준다.

각 타입의 프로퍼티 에디터를 찾는 방법은 타입 이름 뒤에 Editor를 붙여주면 된다.

스프링 3.0 부터는 PropertyEditor 대신 사용할 수 있는 ConversionService를 지원하기 시작했다.
ConversionService 느 스프링이 직접 제공하는 타입 변환 API다. 멀티스레드 환경에서 공유해 사용될 수 있다.


##### Null과 빈 문자열
스트링 타입에서 Null과 빈문자열은 비슷한 용도로 사용되기는 하지만 동일하지 않기 때문에 구분해서 사용해야 한다.
일반적인 경우에는 null을 명시적으로 선언할 필요는 없다.

```JAVA
<property name="name" value="" />
<property name="name"><null /><property/> <!-- 널 태그를 사용한다. -->
```

##### 프로퍼티 파일을 이용한 값 설정
XML에서 일부 설정정보를 별도의 파일로 분리해두면 유용할 때가 있다. 서버환경에 종속적인 정보가 있다면, 이를 애플리케이션의 구성정보에서 분리하기 위해서다. 변경되는 이유와 시점이 다르다면 분리하는 것이 객체지향 설계의 기본 원칙이다. 환경에 따라 자주 변경될 수 있는 내용은 프로퍼티 파일로 분리하는 것이 가장 깔끔하다.

또 한가지 장점은 @Value를 효과적으로 사용할 수 있다. @Value는 소스코드 안에 포함되는 어노테이션이어서 값이 변경되면 매번 새로 컴파일해야 한다. 하지만 프로퍼티 파일의 내용을 참조하게 하면 소스코드 수정 없이 @Value를 통해 프로퍼티에 값을 변경할 수 있다.

```JAVA
<!-- before -->
<bean id="test" class="...DataSource">
	<property name="driverClass" value="com.mysql.jdbc.Driver" />
</bean>

<!-- after -->
<bean id="test" class="...DataSource">
	<property name="driverClass" value="${db.driverClass}" />
</bean>

<!-- database.properties 파일 -->
db.driverClass=com.mysql.jdbc.Driver

/**
사용하기 위해서는 아래 태그를 추가해줘야 한다.
<context:property-placeholder location="classpath:database.properties" />
*/
```

이런 동작원리는 <context:property-placeholder> 태그에 의해 자동으로 등록되는 PropertyPlaceHolderConfigurer 빈이 담당한다. 이 빈은 빈 팩토리 후처리기다. 빈 팩토리 후처리기는 빈 설정 메타정보가 모두 준비됐을 때 빈 메타정보 자체를 조작하기 위해 사용된다.

- 능동변환 :SpEL

프로퍼티 대체위치를 설정해두고 빈 팩토리 후처리기에서 바꿔주기를 기다리는 수동적인 위의 방법과 달리 다른 빈 오브젝트에 직접 접근할 수 있는 표현식을 이용해 원하는 프로퍼티 값을 능동적으로 가져오는 방법이다.

```JAVA
<!-- before -->
<bean id="test" class="...DataSource">
	<property name="driverClass" value="com.mysql.jdbc.Driver" />
</bean>

<!-- after -->

<!-- 
빈 팩토리 후처리기 처럼 동작해서 처리하는 것이 아닌 단순 프로퍼티 파일의 내용을 담은 properties 타입 빈을 만들어 줄 뿐이다. 
properties 는 Map 인터페이스를 구현한 클래스이다.
-->
<util:properties id="dbprops" location="classpath:database.properties" /> 

<bean id="test" class="...DataSource">
	<property name="driverClass" value="#{dbprops['db.driverClass']}" />
</bean>

<!-- database.properties 파일 -->
db.driverClass=com.mysql.jdbc.Driver
```

#### 1.2.5 컨테이너가 자동등록하는 빈

##### ApplicationContext, BeanFactory
스프링에서는 컨테이너 자신을 빈으로 등록해두고 필요하면 일반 빈에서 DI 받아서 사용할 수 있다.
어노테이션을 이용한 의존관계 설정을 사용하지 않는다면 @Autowired를 사용할 수 없다. 이때는 ApplicationContextAware라는 특별한 인터페이스를 구현해주면된다.
ApplicationContextAware의 setApplicationContext() 메서드가 있어서 스프링이 애플리케이션 컨텍스트 오브젝트를 DI 해줄 수 있다.


컨텍스트 내부에 만들어진 빈 팩토리 오브젝트를 직접 사용하고 싶다면 BeanFactory 타입으로 DI 해줄 필요가 있다. 이때는 DefaultListableBeanFactory 오브젝트로 캐스팅해서 사용한다. 빈 팩토리는 ApplicationContext 구현 클래스 안에 내부적으로 따로 생성해두기 때문에 BeanFactory로 DI 받는 오브젝트는 ApplicationContext로 가져오는 오브젝트와 다르다는 점을 기억해두자.
BeanFactory를 어노테이션 없이 가져오려면 BeanFactoryAware 인터페이스를 구현하면 된다.

##### ResourceLoader, ApplicationEventPublisher
스프링 컨테이너는 ResourceLoader 이기도 하다. 따라서 서버환경에서 다양한 Resource를 로딩할 수 있는 기능을 제공한다.

만약 코드를 통해 서블릿 컨텍스트의 리소스를 읽어오고 싶다면 컨테이너를 ResourceLoader 타입으로 DI 받아서 활용하면 된다.

웹 애플리케이션으로 배포된 스프링은 기본적으로 서블릿 컨텍스트의 리소스를 이용할 수 있도록 ResourceLoader가 구성된다.

ApplicationContext 인터페이스는 이미 ResourceLoader를 상속하고 있다.


##### systemProperties, systemEnvironment
스프링 컨테이너가 직접 등록하는 빈 중에서 타입이 아니라 이름을 통해 접근할 수 있는 두 가지 빈이 있다. systemProperties, systemEnvironment 빈이다. 각각 Properties 타입과 Map 타입이기 때문에 타입에 의한 접근 방법은 적절치 않다.

systemProperties 빈은 System.getProperties() 메서드가 돌려주는 Properties타입의 오브젝트를 읽기전용으로 접근할 수 있게 만든 빈 오브젝트다. JVM이 생성해주는 시스템 프로퍼티 값을 읽을 수 있게 해준다.

그런데 systemProperties, systemEnvironment 라는 이름의 빈을 직접 정의해두면 스프링이 이 빈들을 자동으로 추가해주지 못하기 때문에 주의해야 하며, 사용금지 목록에 올려두자.


### 1.3 프로토 타입과 스코프
기본적으로 스프링의 빈은 싱글톤으로 만들어진다. 하나의 빈 오브젝트에 여러 스레드가 접근하기 때문에 상태 값을 인스턴스 변수에 저장해두고 사용할 수 없다. 따라서 싱글톤의 필드에는 의존관계에 있는 빈에 대한 레퍼런스나 읽기전용 값만 저장해두고 오브젝트의 변하는 상태를 저장하는 인스턴스 변수는 두지 않는다.

그런데 때로는 빈을 싱글톤이 아닌 다른 방법으로 만들어 사용해야 할 때가 있다. 하나의 빈 설정으로 어려 개의 오브젝트를 만들어서 사용하는 경우다. 이때는 프로토타입 빈과 스코프 빈을 생각하자.

#### 1.3.1 프로토타입 스코프

##### 프로토타입 빈의 생명주기와 종속성
프로토타입 빈은 독특하게 이 IoC의 기본원칙을 따르지 않는다. 프로토타입 스코프를 갖는 빈은 요청이 있을 때마다 컨테이너가 생성하고 초기화하고 DI까지 해주기도 하지만 일단 빈을 제공하고 나면 컨테이너는 더 이상 빈 오브젝트를 관리하지 않는다. 한번 만들어진 프로토타입 빈 오브젝트는 다시 컨테이너를 통해 가져올 방법이 없고, 빈이 제거되기 전에 빈이 사용한 리소스를 정리하기 위해 호출하는 메서드도 이용할 수 없다. 프로토타입 빈은 이 빈을 주입받는 오브젝트에 종속적일 수 밖에 없다.


##### 프로토타입 빈의 용도
프로토타입 빈은 코드에서 new로 오브젝트를 생성하는 것을 대신하기 위해 사용된다. 보통은 컨테이너에게 new 요청을 하지않고 코드 안에서 직접 처리하지만 간혹 DI를 위해 컨테이너가 오브젝트를 생성하기도 한다. 매번 새로운 오브젝트가 필요하면서 DI를 통해 다른 빈을 사용할 수 있어야 한다면 프로토타입 빈이 가장 적절한 선택이다.

고급 AOP 기능을 사용하면 ServiceRequest를 프로토타입 빈으로 만들어 getBean()으로 가져오지 않고, 단순히 new 키워드로 생성해도 DI가 된다. 이 방법이 좀 더 깔끔하지만 JVM이나 클래스 로더 설명과 같은 부가적인 작업이 필요하다.

```JAVA
@Component
@Scope("prototype")	// 컨텍스트를 통해 오브젝트를 호출할 때마다 새로운 오브젝트를 반환
public class ServiceRequest {

	// 전형적인 데이터 중심 아텍처
//	String customerNo; 
	
	/**
	 * 오브젝트 중심으로 설계하여 변화에 유연하게 대응
	 * 인풋값을 고정으로 처리하게 되면 변화에 취약하다.
	 */
	Customer customer; 
	String productNo;
	String description;	
}
```

##### DI와 DL
프로토타입 빈은DI될 대상이 여러 군데라면 각기 다른 오브젝트를 생성한다. 하지만 같은 컨트롤러에서 매번 요청이 있을 때마다 새롭게 오브젝트가 만들어져야 하는 경우에는 적합하지 않다. new 키워드를 대신하기 위해 사용되는 것이 프로토타입의 용도라고 본다면 DI는 프로토타입 빈을 사용하기에 적합한 방법이 아니다. 따라서 코드 내에서 필요할 때마다 컨테이너에게 요청해서 새로운 오브젝트를 만들어야 한다. DL방식으로 사용해야 한다는 뜻이다.


##### 프로토타입 빈의 DL 전략
- ApplicationContext, BeanFactory

@Autowired나 @Resource를 이용해 DI받은 뒤 getBean() 메서드를 직접 호출해서 빈을 가져오는 방법

- ObjectFactory, ObjectFactoryCreatingFactoryBean

직접 애플리케이션 컨텍스트를 사용하지 않으려면 중간에 컨텍스트에 getBean()을 호출해주는 역할을 맡을 오브젝트를 두면 된다. 가장 쉽게 생각 해볼 수 있는 것은 팩토리다.

적어도 ApplicationContext와 getBean()처럼 너무 로우레벨의 API를 사용하지 않기 때문에 코드가 깔끔하고 테스트가 편리하다.

인터페이스와 팩토리 클래스를 직접 구현하기 귀찮다면 스프링이 제공하는 ObjectFactory 인터페이스와 그걸 구현한 빈 클래스를 사용하자.

```JAVA
@Resource
ObjectFactory<ClassName> instanceName;
```

- 메서드 주입

스프링이 제공해주는 또 다른 DL 전략은 메서드 주입이다. 메서드 주입은 @Autowired를 메서드에 붙여서 메서드 파라미터에 의해 DI 되게하는 메서드를 이용한 주입 방식과 혼동하면 안 된다. 메서드 주입은 메서드를 통한 주입이 아니라 메서드 코드 자체를 주입하는 것을 말한다.

컨트롤러 클래스에 추상 메서드를 선언해둔다. 팩토리 역할을 하는 메서드라고 보면 된다.

<lookup-method> 라는 태그의 name이 스프링이 구현해줄 추상 메서드의 이름이고 빈 어트리뷰트는 메서드에서 getBean()으로 가져올 빈의 이름이다.

메서드 주입 방식은 그 자체로 스프링 API에 의존적이 아니므로 스프링 외의 환경에 가져다 사용할 수도 있고 컨테이너의 도움 없이 단위 테스트를 할 수도 있다.

클래스 자체가 추상 클래스이기때문에 사용 시 오버라이딩해야하는 번거로운 점도 있다.

```JAVA
abstract public  ServiceRequest getServiceRequest();

<bean id="serviceRequestController" class="...serviceRequestController"> 
	<lookup-method name="getServiceRequest" bean="serviceRequest" />
</bean>
```


- provider<T>

기본 개념과 사용방법은 ObjectFactory와 유사하지만 ObjectFactoryCreatingFactoryBean을 이용해 빈을 등록해주지 않아도 되기 떄문에 편리하다. Provider 인터페이스를 @Autowired, @Inject, @Resource 중 하나를 사용해 DI 되도록 지정해주기만 하면 스프링이 자동으로 Provider를 구현한 오브젝트를 생성해서 주입해준다.


#### 1.3.2 스코프
스프링은 싱글톤, 프로토타입 외에 요청, 세션, 글로벌세션, 애플리케이션이라는 네 가지 스코프를 기본적으로 제공한다. 이 스코프는 모두 웹 환경에서만 의미 있다.

애플리케이션을 제외한 나머지 세 가지 스코프는 싱글톤과 다르게 독립적인 상태를 저장해두고 사용하는 데 필요하다.

- 요청 스코프 

요청 스코프 빈은 하나의 웹 요청 안에서 만들어지고 해당 요청이 끝날 때 제거된다. 각 요청별로 독립적인 빈이 만들어지기 때문에 빈 오브젝트 내에 상태 값을 저장해 둬도 안전하다. 하나의 웹 요청을 처리하는 동안에 참조하는 요청 스코프 빈은 항상 동일한 오브젝트임이 보장된다. 요청 스코프 빈의 주요 용도는 애플리케이션 코드에서 생성한 정보를 프레임워크 레벨의 서비스나 인터셉터 등에 전달하는 것이다.
예를 들어 보안 프레임워크에서 현재 요청에 관련된 권한 정보를 요청 스코프 빈에 저장해뒀다가 필요한 빈에서 참조하게 할 수 있다.


- 세션 스코프, 글로벌세션 스코프

HTTP 세션은 사용자별로 만들어지고 브라우저를 닫거나 세션 타임이 종료될 때까지 유지되기 때문에 로그인 정보나 사용자별 선택옵션 등을 저장해두기에 유용하다. 웹 환경 정보에 접근할 수 있는 계층에서만 가능한 작업이다. 서비스 계층이나 데이터 액세스 계층에서 HTTP 세션에 접근하려 한다면 문제가 된다. 그렇다고 웹 환경에 종속적인 HttpSession 오브젝트를 다른 계층으로 넘겨서 사용하게 하는건 매우 나쁜 방법이다. 글로벌세션 스코프는 포틀릿에만 존재하는 글로벌 세션에 저장되는 빈이다.


- 애플리케이션 스코프

애플리케이션 스코프는 서블릿 컨텍스트에 저장되는 빈 오브젝트다. 서블릿 컨텍스트는 웹 애플리케이션마다 만들어진다. 따라서 애플리케이션 스코프는 컨텍스트가 존재하는 동안 유지되는 싱글톤 스코프와 비슷한 존재 범위를 갖는다. 그럼 싱글톤 스코프를 사용하면 될 텐데 애플리케이션 스코프를 사용하는 이유는 드물지만 웹 애플리케이션과 애플리케이션 컨텍스트의 존재 범위가 다른 경우가 있기 때문이다.
더 오래 혹은 더 짧게 존재하는 서블릿 레벨의 컨텍스트가 있기 때문이다.

##### 스코프 빈의 사용 방법
애플리케이션 스코프를 제외한 나머지 세 가지 스코프는 프로토타입 빈과 마찬가지로 한 개 이상의 빈 오브젝트가 생성된다. 하지만 프로토타입 빈과는 다르게 생성부터 초기화, DI, DL 그리고 제거까지 전 과정을 다 관리한다. 컨테이너가 정확하게 언제 새로운 빈이 만들어지고 사용되는 지 파악할 수 있기 때문이다. 

그러나 프로토타입과 마찬가지로 하나 이상의 오브젝트가 만들어져야 하기 때문에 싱글톤에 DI해주는 방법으로 사용할 수 없다. 특히나 요청 스코프 같은 경우는 컨텍스트 초기화 시점에는 요청이 들어올 수 없기 떄문에 생성초자 되지 않는다. 컨텍스트 초기화 시점에 요청스코프를 DI하게 되면 오류가 발생한다. 결국 프로토타입 빈과 동일하게 Provider 혹은 ObjectFactoryt같은 DL방식으로 사용해야 한다.


스코프 프록시는 프록시 패턴을 활용한 것이다. 다만 지연로딩이나 원격 오브젝트 접속 등과 같은 보편적인 응용 방법 대신 스코프에 따라 다른 오브젝트를 사용하게 해주는 독특한 목적을 위해 프록시를 사용한다. 프록시 빈이 인터페이스를 구현하고 있고, 클라이언트에서 인터페이스를 DI 받는다면 proxyMode를 ScopedProxyMode.INTERFACES로 지정해주고, 프록시 빈 클래스를 직접 DI한다면 ScopedProxyMode.TARGET_CLASS로 지정하면 된다.

XML로 스코프 빈을 등록하고 DI에서 사용할 것이라면 <aop:scoped-proxy>를 넣어주면 된다.


##### 커스텀 스코프와 상태를 저장하는 빈 사용하기
스프링이 기본적으로 제공하는 스코프 외에도 임의의 스코프를 만들어 사용할 수 있다. 싱글톤 외에 스코프를 사용한다는 건 기본적으로 빈에 상태를 저장해두고 사용한다는 의미다. 커스텀 스코프를 만들기 위한 프레임워크로는 웹 플로우나 제이보스 씸이 있다. 그리고 더 이상 유지할 필요가 없는 상태정보는 가능한 한 빨리 제거해야 한다.

### 1.4 기타 빈 설정 메타정보

#### 1.4.1 빈 이름
ID 혹은 NAME 모두 빈의 식별자로 사용된다. 빈의 식별자는 XML이라면 ID 와 NAME 두 가지 어트리뷰트를 이용해 정의할 수 있다.
기존 빈의 속성값에 변화를 주고 싶지 않다면 <alias> 태그를 사용하여 별칭을 줄 수도 있다.

- ID
- NAME
	- name의 경우 id와 다르게 구분자를 통한 여러개의 빈의 이름을 줄 수 있다. 이렇게 주는 이유는 상황에 따라 각기 다른 이름으로 빈을 참조할 때 편리하다.

##### 어노테이션에서의 빈 이름
- @Component와 같은 스테레오타입의 어노테이션을 통해 빈을 만들 때는 보통 클래스의 이름의 첫 글자만 소문자로 바꿔서 빈으로 생성된다. 빈 이름을 사용자가 설정하고 싶을 땐 ()안에 빈 이름을 작성해주거나 @Named 어노테이션을 사용하여 변경할 수 있다.

- @Bean 어노테이션을 사용하는 경우에는 작성한 명칭 그대로를 빈 이름으로 사용된다.

#### 1.4.2 빈 생명주기 메서드

##### 초기화 메서드
빈 오브젝트가 생성되고 DI 작업까지 마친 다음에 실행되는 메서드를 말한다. 하지만 DI를 통해 모든 프로퍼티가 주입된 후에야 가능한 초기화 작업도 있다.
초기화 메서드를 지정하는 방법은 네 가지가 있다.

- 초기화 콜백 인터페이스

InitializingBean 인터페이스를 구현해서 빈을 작성하는 방법이다. InitializingBean의 afterPropertiesSet() 메서드를 사용하면 프로퍼티 설정까지 마친 뒤에 호출한다. 애플리케이션 빈 코드에 스프링 인터페이스가 노출되기 때문에 별로 권장하진 않지만 동작방식을 이해하기 쉽다.

- init-method 지정

빈 태그의 init-method 속성을 넣어서 초기화 작업을 수행할 메서드 이름을 지정할 수 있다. 초기화 콜백 인터페이스와 달리 빈 클래스에 스프링 API가 노출되지 않기 때문에 깔끔하다는 장점이 있는 반면 코드만 보고 초기화 메서드가 호출될 지 알 수 없기 때문에 이해하는데 불편할 수 있다.

- @PostConstruct

초기화를 담당할 메서드에 @PostConstruct 어노테이션을 부여해주기만 하면 된다. 위의 두 가지 방법보다 부담도 적으면서 직관적이다. 가장 사용이 권장되는 방식이다.

- @Bean(init-method)

빈 생성 시 어노테이션 옵션에 init-method를 사용하여 프로퍼티 설정 이후 호출할 메서드를 설정한다. xml보다는 직관적이다.


##### 제거 메서드
제거 메서드는 컨테이너가 종료될 때 호출되서 빈이 사용한 리소스를 반환하거나 종료 전에 처리해야 할 작업을 수행한다.
제거 메서드는 세 가지 방법으로 지정할 수 있다.

- 제거 콜백 인터페이스

DisposableBean 인터페이스를 구현해서 destroy()를 구현하는 방법이다. 스프링 API에 종속되는 코드를 만드는 단점이 있다.

- destroy-method

빈 태그에 destroy-method 속성을 넣어서 제거 메서드 지정

- @PreDestroy

컨테이너가 종료될 때 실행될 메서드에 @PreDestroy를 붙여주면 된다.

- @Bean(destroyMethod)

@Bean 어노테이션 옵션에 destroyMethod 사용하여 제거 메서드 지정


#### 1.4.3 팩토리 빈과 팩토리 메서드
생성자 대신 오브젝트를 생성해주는 코드의 도움을 받아서 빈 오브젝트를 생성하는 것을 팩토리 빈이라고 부른다. 빈 팩토리와 비슷하지만 전혀 다른 것이니 혼동하지 않도록 주의하자.
팩토리 빈 자신은 빈 오브젝트로 사용되지 않는다. 대신 빈 오브젝트를 만들어주는 기능만 있을 뿐이다.

- FactoryBean 인터페이스

new 키워드나 리플렉션 API를 이용해 생성자를 호출하는 방식으로 만들 수 없는 JDK 다이나믹 프록시를 빈으로 등록하기 위해 FactoryBean 인터페이스를 구현해서 다이나믹 프록시를 생성하는 getObject() 메서드를 구현하고 팩토리 빈으로 등록해서 사용한다. 스프링 인터페이스를 구현하는 것이 불편하지 않다면 사용하기 적당하다.

- 스태틱 팩토리 메서드

스태틱 팩토리 메서드는 클래스의 스태틱 메서드를 호출해서 인스턴스를 생성하는 방식이다. JDK를 비롯해서 다양한 기술 API에서 자주 사용된다. 스태틱 팩토리 메서드를 통해 빈 오브젝트를 생성해야 한다면 
<bean> 태그에 factory-method 어트리뷰트를 이용하는 것이 편리하다. 

오브젝트 생성과 함께 초기화 작업이 필요한 경우라면 스태틱 팩토리 메서드를 이용해야 한다.

- 인스턴스 팩토리 메서드

스태틱 메서드 대신 오브젝트의 인스턴스 메서드를 이용해 빈 오브젝트를 생성할 수도 있다. 하지만 FactoryBean이라는 스프링의 인터페이스에 종속적이라는 단점도 있다.

- @Bean 메서드

자바 코드에 의한 빈 등록 방식에서 사용하는 @Bean 메서드도 일종의 팩토리 메서드다. 스프링 컨테이너가 @Bean 메서드를 실행해 빈 오브젝트를 가져오는 방식이기 때문이다.

자바 코드에 빈의 설정과 DI를 대폭 적용한다면 @Configuration이 붙은 설정 전용 클래스를 사용하는 것이 편리하다.

### 1.5 스프링 3.1의 IoC 컨테이너와 DI
스프링 3.1에 새롭게 도입된 IoC/DI 기술은 다음 두 가지다.

1. 강화된 자바 코드 빈 설정 : 자바 코드를 이용한 설정 방식을 빈 설정 메타정보 작성에 본격적으로 사용할 수 있도록 기능을 대폭 확장한 것이다.

2. 런타임 환경 추상화 : 개발과 테스트, 운영 단계에서 IoC/DI 구성이 달라질 때 이를 효과적으로 관리할 수 있게 해주는 런타임 환경정보 관리 기능이다.

스프링 3.1의 가장 큰 특징은 자바 코드를 이용한 설정 메타정보 작성이 쉽다는 점이다.

#### 1.5.1 빈의 역할과 구분

##### 빈의 종류

- 애플리케이션 로직 빈

일반적으로 애플리케이션의 로직을 담고 있는 주요 클래스의 오브젝트가 빈으로 지정된다. (DAO, Service, Controller 등)

- 애플리케이션 인프라 빈

DataSource나 DataSourceTransactionManager는 DAO나 Service처럼 스프링 컨테이너에 등록되는 빈이기는 하지만 성격이 다르다. 외부 리소스와의 연결을 관리하거나 서비스 빈이 특정 트랜잭션 기술에 종속되지 않도록 부가기능을 제공해주는 빈들을 애플리케이션 기반 빈 또는 애플리케이션 인프라스트럭처빈 이라고 부르는 게 좋겠다.

- 컨테이너 인프라 빈

AOP에서 사용하던 DefaultAdvisorAutoProxyCreator는 Advisor 타입 빈의 포인트컷 정보를 이용해서 타깃 빈을 선정하고, 선정된 빈을 프록시로 바꿔주는 기능을 담당한다. 이런 빈은 스프링 컨테이너의 기능에 관여한다. 이렇게 스프링 컨테이너의 기능을 확장해서 빈의 등록과 생성, 관계설정, 초기화 등의 작업에 참여하는 빈을 컨테이너 인프라스트럭처 빈이라고 부른다.

##### 컨테이너 인프라 빈과 전용 태그
컨테이너 인프라 빈은 애플리케이션 로직 빈이나 애플리케이션 인프라 빈과는 성격이 크게 다르다. 스프링은 빈을 개발자가 XML에 직접 등록하는 대신 전용 태그를 사용해 간접적으로 등록하는 방법을 권장한다.

<context:annotation-config> 태그는 context 네임스페이스의 태그를 처리하는 핸들러를 통해 특정 빈이 등록되게 해줄 뿐이다. 이 과정에서 등록되는 빈이 스프링 컨테이너를 확장해서 빈의 등록과 관계 설정, 후처리 등에 새로운 기능을 부여하는 컨테이너 인프라다. 컨테이너 인프라 빈은 일반 애플리케이션 개발자가 직접 개발해서 추가할 일이 거의 없고, 일정한 설정 패턴이 있기 때문에 전용 태그로 등록하고 어트리뷰트를 통해 필요한 속성만 부여하도록 하는 것이 일반적이다.


##### 빈의 역할
스프링의 빈을 역할에 따라 구분한다면 세 가지로 나눌 수 있다.

- ROLE_APPLICATION
	- 애플리케이션 로직 빈과 애플리케이션 인프라 빈처럼 애플리케이션이 동작하는 중에 사용되는 빈을 말한다. 애플리케이션 구성 빈이라고 볼 수 있다.

- ROLE_SUPPORT
	- 복합 구조의 빈을 정의할 때 보조적으로 사용되는 빈의 역할을 지정하려고 정의된 것이다. 실제로는 거의 사용하지 않으니 무시해도 좋다.

- ROLE_INFRASTRUCTURE
	- <context:annotation-config> 같은 전용 태그에 의해 등록되는 컨테이너 인프라 빈들이 바로 이 ROLE_INFRASTRUCTURE 값을 갖고 있다.

스프링 3.1부터는 개발자가 빈을 정의할 때 이 역할 값을 직접 지정할 수 있도록 @Role이라는 어노테이션이 도입됐다.


#### 1.5.2 컨테이너 인프라 빈을 위한 자바 코드 메타정보

##### IoC/DI 설정 방법의 발전
|버전|애플리케이션 로직 빈|애플리케이션 인프라 빈|컨테이너 인프라 빈|
|-----|-------|-------|-------|
|스프링 1.x|bean 태그|bean 태그|bean 태그|
|스프링 2.0|bean 태그|bean 태그|전용태그|
|스프링 2.5|bean 태그, 빈 스캔|bean 태그|전용태그|
|스프링 3.0|bean 태그, 빈 스캔, 자바 코드|bean 태그, 자바 코드|전용태그|
|스프링 3.1|bean 태그, 빈 스캔, 자바 코드|bean 태그, 자바 코드|전용태그, 자바 코드|

##### 자바코드를 이용한 컨테이너 인프라 빈 등록
- @ComponentScan
	- @Configuration 이 붙은 클래스에 @ComponentScan 어노테이션을 추가하면 XML에서 <context:component-scan>을 사용한 것처럼 스테레오타입 어노테이션이 붙은 빈을 자동으로 스캔해서 등록해준다.

```JAVA
@Configuration
@ComponentScan("패키지경로")	// 스캐너 패키지와 하위 클래스 중에서 @Component 같은 스테레오 타입 어노테이션이 붙은 클래스를 모두 찾아서 빈으로 등록한다.
public class AppConfig {
}


/** 
패키지 이름 대신 마커 클래스나 인터페이스 사용 방법

마커를 통한 패키지 지정 방식 장점
1. 패키지 이름을 텍스트로 넣으면 오타 발생 가능성 있음.
2. 리팩토링으로 패키지를 옮기거나 패키지 이름이 바뀌는 경우 텍스트일 때는 일일이 수정해야 함.
3. 패키지를 여러 개 사용한다면 읽기도 불편
*/
public interface ServiceMarker {}

@Configuration
//@ComponentScan(basePackageClasses = Printer.class, excludeFilters = @Filter(Controller.class)) // 어노테이션 제외 방식	
@ComponentScan(basePackageClasses = Printer.class, excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value=HelloConfig.class))	// 클래스 제외 방식
public class AppConfig {
}

```


- @Import
	- @Import 는 다른 @Configuration 클래스를 빈 메타정보에 추가할 때 사용한다.

```JAVA
@Configuration
@Import(DataSourceConfig.class)	// 다른 빈 메타정보를 추가할 때 사용
public class AppConfig {
}
```

- @ImplortResource

자주 사용되지 않는 일부 전용 태그는 아직 자바 코드와 어노테이션을 이용하는 방식이 지원되지 않는다. 또한 스프링 시큐리티와 같은 서브 프로젝트나 스프링 외의 오픈소스 라이브러리와 상용 제품에서 제공하는 스프링용 전용 태그는 XML 전용 태그만 지원될 수도 있다.

꼭 필요한 XML 빈 설정만 별도의 파일로 작성한 뒤 @Configuration 클래스에서 @ImportResource를 사용해 XML 파일 위치를 지정해준다.

```JAVA
@Configuration
@ImportResource("xml경로")
public class AppConfig {
}
```

- @EnableTransactionManagement

@Configuration 클래스에서 사용하는 어노테이션이다. XML의 <tx:annotation-driver/> 태그와 동일한 기능을 수행한다. @Transactional로 트랜잭션 속성을 지정할 수 있게 해주는 AOP 관련 빈을 등록해주는 것이다.


#### 1.5.3 웹 애플리케이션의 새로운 IoC 컨테이너 구성
웹 환경에서는 보통 루트 애플리케이션 컨텍스트와 서블릿 애플리케이션 컨텍스트의 두 단계로 분리해 사용하는 경우가 일반적이라고 설명했다. 각각 web.xml의 <listener>와 <servlet>에 컨텍스트 설정 정보를 넣어 웹 애플리케이션이 시작될 때 자동으로 생성되게 만든다. 기본 메타정보는 XML이다. 그래서 contextConfigLocation파라미터를 이용해 XML 파일 위치를 지정해주거나 디폴트 XML파일인 /WEB-INF/applicationContext.xml을 사용한다.

- 루트 애플리케이션 컨텍스트 등록

```JAVA
/**
아래 listener 태그를 이용하여 디폴트 컨텍스트 클래스인 XmlWebApplicationContext 를 이용해 애플리케이션 컨텍스트를 만들고 /WEB-INF/applicationContext.xml을 설정파일로 사용한다.
*/
<listener>
	<listener-class>org.springframework.web.context.ContextLoaderListener<listener-class>
</listener>
```

@Configuration 어노테이션을 통한 루트컨텍스트 설정 방법은 아래와 같다.

```JAVA
<context-param>
	<param-name>contextConfigLocation</param-name>
	<param-value>패키지위치</param-value>
</context-param>
```

- 서블릿 컨텍스트 등록

서블릿 컨텍스트는 DispatcherServlet 을 등록하면 만들어진다. 루트 컨텍스트와 동일하게 XmlWebApplicationContext가 디폴트 컨텍스트 클래스다.

앞에서 사용했던 @Configuration 같은 어노테이션은 <context:annotation-config /> 전용 태그에 의해 등록되는 컨테이너 인프라 빈이 스프링 컨테이너의 기능을 확장해줬기 때문에 사용할 수 있다고 설명했다.
그렇다면 AnnotationConfigWebApplicationContext를 사용했을 때는 이런 컨테이너 인프라 빈을 어디에 등록해야 할까?

답은 아무것도 안해도 된다. AnnotationConfigWebApplicationContext는 <context:annotation-config /> 이 등록해주는 빈을 기본적으로 추가해주기 때문이다.


#### 1.5.4 런타임 환경 추상화와 프로파일
스프링의 빈 설정 메타정보는 빈의 클래스와 값 속성, 다른 빈과의 관계로 이루어져 있다. 애플리케이션 기능이 바뀌지 않는다면 메타정보는 대부분 바뀌지 않지만 애플리케이션 동작 환경에 따라서 바뀌어야 하는 것도 있다. 특히 외부 리소스나 서버환경과 관련이 깊은 애플리케이션 인프라 빈의 클래스와 속성은 같은 애플리케이션이라고 하더라도 실행환경에 따라서 달라질 수 있다.

##### 환경에 따른 빈 설정정보 변경 전략과 한계
스프링으로 만든 애플리케이션은 성격이 다른 여러 환경에서 동작하게 된다. 개발환경, 테스트환경, 운영환경 정도로 구분할 수 있다.

- 빈 설정파일의 변경

가장 쉬운 방법은 여러 개의 설정정보 XML을 만들어서 환경에 따라 사용하는 XML을 나누는 것이다. 하지만 개발이나 유지보수가 진행되면서 설정정보 환경이 지속적으로 달라지는 경우라면 메타정보를 관리하는 것은 번거롭고 위험하다.

- 프로퍼티 파일 활용

환경에 따라 달라지는 정보를 담은 프로퍼티 파일을 활용하는 방법도 있다. 빈 설정 메타정보를 담은 XML이나 @Configuration 클래스는 애플리케이션 로직이 바뀌지 않는 한 건드리지 않고 환경에 따라 달라지는 외부 정보만 프로퍼티 파일 등에 두고 XML에서 읽어서 사용하는 방법이다. 각 환경에 따라 달라지는 정보만 프로퍼티 파일 등으로 준비하면 된다.

주의사항으로는 소스코드를 배포할 때 프로퍼티 파일을 포함하지 않도록 주의해야 한다. 소스코드 버전 관리 툴이 서브 버전이라면 svn:ignore 프로퍼티를, git이라면 gitignore 등을 이용해 개발환경을 위한 프로퍼티 파일은 소스코드 리포지토리로 전달되지 않게 한다.

```JAVA
/**
DataSource로 예시
프로퍼티 치환자를 사용해 빈에 주입할 속성 정보를 외부 파일로 빼는 방법이다.
*/

<bean id="dataSource" class="...SimpleDriverDataSource">
	<property name="driverClass" value="${db.driverclass}"/>
	<property name="url" value="${db.url}"/>
	<property name="username" value="${db.username}"/>
	<property name="password" value="${db.password}"/>
</bean>
```

##### 런타임 환경과 프로파일
스프링 3.0까지는 XML을 통한 환경설정에 대한 뾰족한 해결책이 없었지만 3.1부터는 깔끔하게 해결할 수 있다. 런타임 환경 추상화를 이용하는 것이다.
런타임 환경은 애플리케이션 컨텍스트에 새롭게 도입된 개념이다. 컨텍스트 내부에 Environment 인터페이스를 구현한 런타임 환경 오브젝트가 만들어져서 빈을 생성하거나 의존관계를 주입할 때 사용된다.

런타임 환경은 프로파일과 프로퍼티 소스로 구성된다. 환경에 따라 프로파일과 프로퍼티 소스가 다르게 구성된 Environment 오브젝트가 사용되는 식이다.
프로파일의 개념은 간단하다. 환경에 따라 다르게 구성되는 빈들을 다른 이름을 가진 프로파일 안에 정의한다. 애플리케이션 컨텍스트가 시작될 때 지정된 프로파일에 속한 빈들만 생성하게 되는 것이다.

##### 활성 프로파일 지정 방법
사용할 프로파일은 Environment 오브젝트에 setActiveProfiles()메서드가 있는데 여기에 사용할 프로파일 이름을 넣어주면 된다. 프로파일이 XML이 로딩되거나 @Configuration 클래스가 적용되는 refresh() 메서드가 컨텍스트에서 실행되기 전에 지정해줘야 한다.

```JAVA
GenericXmlApplicationContext ac = new GenericXmlApplicationContext();
ac.getEnvironment().setActiveProfiles("dev");
ac.load(getClass(), "applicationContext.xml");
ac.refresh();
```

애플리케이션 컨텍스트는 서버에서 web.xml을 통해 간접적으로 생성하는 경우가 대부분이다. 이때는 활성 프로파일을 시스템 프로퍼티나 환경변수를 통해 지정할 수 있다.
WAS 시동 스크립트에서 환경변수 spring.profiles.active에 dev를 넣고 WAS를 시작하면 dev 활성 프로파일이 적용되면서 컨텍스트가 만들어질 것이다.

환경변수를 설정하는 대신 JVM의 커맨드라인 파라미터를 이용해 시스템 프로퍼티를 지정할 수 있다. 아래와 같이 -D 옵션을 사용하면 된다.

-Dspring.profiles.active


##### 프로파일 활용 전략
프로파일은 한 번에 두 가지 이상을 활성화할 수도 있다. 예를 들어 DB 연결과 관련된 빈은 dsDev, dsTest, dsProduction 의 세 가지 프로파일로 구분하고, 메일 서버 접속과 관련된 빈은 실제 동작하는 메일 서버와 연결하는 빈을 가진 mailServer와 테스트용으로 사용할 목 오브젝트를 가진 mockMailServer 프로파일로 구분해놓을 수 있다.

```JAVA
<context-param>
	<param-name>spring.profiles.active</param-name>
	<param-value>dsDev, mockMailServer</param-value>
</context-param>
```

이렇게 프로파일을 다양한 방식으로 나눠서 사용하는 경우에는 활성 프로파일을 지정할 때 애플리케이션이 바르게 동작하는 데 필요한 빈이 모두 포함되는지 유의해야 한다.
프로파일을 적용하고 나면 애플리케이션에 적용된 활성 프로파일이 무엇인지, 그로 인해 등록된 빈은 어떤 것이 있는지 확인해보는 것이 좋다.
컨텍스트 오브젝트의 getEnvironment() 메서드로 런타임 환경 오브젝트를 가져와서 getActiveProfiles() 메서드를 실행하면 활성 프로파일 목록을 가져올 수 있다.

@Configuration 붙은 클래스를 이용해 빈을 정의하는 경우에도 프로파일을 지정할 수 있다. @Profile("지정된 프로파일명")

#### 1.5.5 프로퍼티 소스

##### 프로퍼티
자바에서 말하는 프로퍼티는 기본적으로 키와 그에 대응되는 값의 쌍을 말한다. 스프링의 XML에서 <property>의 name과 value 어트리뷰트를 이용해 프로퍼티 정보를 표현한다.

```JAVA
// database.properties
db.username=spring
db.password=book

// 프로퍼티 값 가져오기.
Properties p = new Properties();
p.load(new FileInputStream("database.properties"));
```

스프링에서는 <util:properties id="newProperties" location="database.properties" /> util 전용 태그를 이용해서 프로퍼티 파일의 내용을 읽어 초기화된 Properties 타입의 빈을 정의할 수 있다.

또는 database.properties 파일을 읽어서 그 프로퍼티 키에 대응되는 치환자를 찾아 빈의 프로퍼티 값을 업데이트 해주는 기능이 있는 <context:property-placeholder> 를 사용하기도 한다.
그런데 Properties 가 기본적으로 지원하는 프로퍼티 파일은 ISO-8859-1 인코딩만 지원하기 때문에 영문만 사용할 수 있다. 인코딩이 불가능한 문자는 유니코드 값을 대신 사용해야 한다.

##### 스프링에서 사용되는 프로퍼티의 종류
- 환경변수
	- 스프링 애플리케이션이 구동되는 OS의 환경변수도 키와 값으로 표현되는 대표적인 프로퍼티다. 환경변수 프로퍼티는 같은 시스템에서 여러 개의 WAS를 구동하더라도 모두 동일하게 적용되는 매우 넓은 범위에 적용되는 프로퍼티다.

- 시스템 프로퍼티
	- 시스템 프로퍼티는 JVM 레벨에 정의된 프로퍼티를 말한다. JVM이 시작될 때 시스템 관련 정보 (os, name, user, home 등) 부터 자바 관련 정보 (java.home, java.version, java.class, path 등), 기타 JVM 관련 정보 등이 시스템 프로퍼티에 등록된다.

- JNDI
	- WAS 전체에 적용돼야 할 프로퍼티라면 시스템 프로퍼티가 좋겠지만, WAS에 여러 개의 웹 애플리케이션이 올라가고 그중 하나의 애플리케이션에만 프로퍼티를 지정하고 싶다면 JNDI 프로퍼티 또는 JNDI 환경 값을 사용하는 방법도 고려해볼 만 하다. 스프링에선 다음과 같은 전용 태그 한줄이면 충분하다. <jee:jndi-lookup id="db.username" jndi-name="db.username" />

- 서블릿 컨텍스트 파라미터
	- 웹 애플리케이션 레벨의 프로퍼티를 지정하고 싶긴 한데 서버에서 웹 애플리케이션 범위의 JNDI 값을 설정하기 번거롭다면 web.xml에 서블릿 컨텍스트 초기 파라미터를 프로퍼티로 사용할 수 있다.
	- 스프링 애플리케이션에서 사용하는 방법
	- ServletContext 오브젝트를 직접 빈에서 주입받은 뒤, ServletContext를 통해 컨텍스트 파라미터를 가져오는 방법
	- ServletContextPropertyPlaceholderConfigurer를 사용하는 것.

- 서블릿 컨픽 파라미터
	- ServletContext 와 ServletConfig는 혼동하기 쉬운데, 전자는 서블릿이 소속된 웹 애플리케이션의 컨텍스트이고 후자는 개별 서블릿을 위한 설정이다.
	- ServletContext는 특정 서블릿에 소속되지 않은 루트 컨텍스트에도 영향을 주지만, ServletConfig는 해당 서블릿의 서블릿 컨텍스트에만 영향을 준다.
	- 접근방법은 ServletConfigAware 인터페이스를 구현하거나 @Autowired로 주입받아서 getInitParameter() 메서드를 사용한다.


##### 프로파일의 통합과 추상화
스프링 3.0까지는 프로퍼티 종류를 저장해두는 방식이 달라지면 이를 사용하는 방법도 달라져야 했다. 스프링3.1에서는 프로퍼티 소스라는 개념으로 추상화하고, 프로퍼티의 저장 위치에 상관없이 동일한 API를 가져올 수 있게 해준다. 프로퍼티 소스는 프로파일과 함께 런타임 환경정보를 구성하는 핵심 정보다. Environment 타입의 런타임 오브젝트를 이용하면 일관된 방식으로 프로퍼티 정보를 가져올 수 있다.

StandardEnvironment는 기본적으로 다음 두 가지 종류의 프로퍼티 소스를 제공한다.

- 시스템 프로퍼티 소스
- 환경변수 프로퍼티 소스

런타임 환경 오브젝트의 getProperty()를 호출하기만 하면 현재 런타임 환경에 등록된 모든 프로퍼티 소스를 뒤져서 프로퍼티 값을 찾아온다.
따라서 프로퍼티 저장방식이 바뀌어도 이를 사용하는 코드는 수정할 필요가 없다.

addFirst(), addLast(), addBefore(), addAfter()를 이용하여 프로퍼티를 등록할 수도 있다.


##### 프로퍼티 소스의 사용

- Environment.getProperty()

가장 간단한 방법은 Environment 오브젝트를 빈에 주입바다서 직접 프로퍼티 값을 가져오는 것이다.
필요할 때만 사용하는 프로퍼티라면 이렇게 코드에서 가져와서 사용해도 되겠지만, 해당 빈에서 반복적으로 사용해야 한다면 @PostConstruct 메서드를 이용해 클래스 멤버 필드에 미리 프로퍼티 값을 저장해두는 편이 낫다.

- PropertySourceConfigurerPlaceholder와 <context:property-placeholder>

@PostConstruct 어노테이션을 통한 메서드 생성 후 프로퍼티 값을 넣어주는 게 번거롭게 느껴진다면 @Value와 프로퍼티 ${} 치환자를 사용할 수도 있다.

@Value에 치환자를 사용하려면 컨텍스트에 PropertySourcePlaceholderConfigurer 빈이 등록되어 있어야 한다. PropertySourcePlaceholderConfigurer 는 특정 프로퍼티 파일이 아니라 환경 오브젝트에 통합된 프로퍼티 소스로부터 프로퍼티 값을 가져와 컨텍스트의 @Value 또는 XML에 있는 ${} 치환자의 값을 바꿔주는 것이다.

```JAVA
/**
 PropertySourcePlaceholderConfigurer 빈 등록
반드시 static 메서드로 등록해야 한다. 이유는 PropertySourcePlaceholderConfigurer 가 BeanFactoryPostProcessor 후처리기로 되어있는데 @Bean 메서드를 처리하는 기능도 같은 BeanFactoryPostProcessor로 되어 있어서 @Bean 메서드에서 다른 후처리기를 만들어서 다시 @Bean 메서드가 있는 클래스의 빈 설정을 가공하도록 만들 수 없기 때문이다.
*/

@Bean
public static PropertySourcePlaceholderConfigurer pspc() {
	return new PropertySourcePlaceholderConfigurer();
}
```

@Configuration 클래스를 함께 사용하는 경우라면 @Value의 치환자에도 적용되므로 PropertySourcePlaceholderConfigurer 빈을 중복해서 등록해줄 필요는 없다. 반대의 경우도 마찬가지다.


##### @PropertySource와 프로퍼티 파일
대표적인 프로퍼티 정보 저장 방식인 프로퍼티 파일도 프로퍼티 소스로 등록하고 사용할 수 있다. @PropertySource를 이용하면 된다.

```JAVA
@Configuration
@ProperySource(name="myPropertySource", value={"database.properties", "settings.xml"})
public class AppConfig {
}
```


##### 웹 환경에서 사용되는 프로퍼티 소스와 프로퍼티 소스 초기화 오브젝트
애플리케이션 컨텍스트를 코드에서 직접 생성하는 독립형 애플리케이션과 달리 웹 환경에서는 리스너나 서블릿에서 컨텍스트가 자동으로 생성된다. 이렇게 생성되는 애플리케이션 컨텍스트에 프로퍼티 소스를 추가하려면 애플리케이션 컨텍스트 초기화 오브젝트를 사용하면 된다.

```JAVA
/**
ApplicationContextInitalizer는 컨텍스트가 생성된 후에 초기화 작업을 진행하는 오브젝트를 만들 때 사용한다.
*/
public interface ApplicationContextInitalizer<C extends ConfigurableApplicationContext> {
	void initialize(C applicationContext);
}

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
```

위 방법은 꼭 필요한 경우에만 사용하는 것을 권장한다.

### 정리
1장에서는 스프링의 컨테이너인 애플리케이션 컨텍스트를 이용해 빈을 정의하고 사용하는 방법을 알아봤다. 

- 스프링 애플리케이션은 POJO 클래스와 빈 설정 메타정보로 구성된다.
- 빈 설정 메타정보는 특정 포맷의 파일이나 리소스에 종속되지 않는다. 필요하다면 새로운 설정정보 작성 방법을 얼마든지 만들어 사용할 수 있다.
- 스프링의 빈 등록 방법에는 크게 XML과 빈 자동인식, 자바코드 세 가지로 구분할 수 있다.
- 프로퍼티 값은 빈에 주입되는 빈 오브젝트가 아닌 정보다.
- 프로퍼티 값 중에서 환경에 따라 자주 바뀌는 것은 프로퍼티 파일과 같은 별도의 리소스 형태로 분리해놓는 것이 좋다.
- 빈의 존재 범위인 스코프는 싱글톤과 프로토타입 그리고 기타 스코프로 구분할 수 있다.
- 프로토타입과 싱글톤이 아닌 스코프 빈은 DL 방식을 이용하거나, 스코프 프록시 빈을 DI 받는 방법을 사용해야 한다.
- 스프링 3.1은 어노테이션과 자바 코드를 이용한 빈 메타정보 작성 기능을 발전시켜서 자바 코드만으로도 스프링 애플리케이션의 모든 빈 설정이 가능하게 해준다.
- 스프링 3.1의 프로파일과 프로퍼티 소스로 이뤄진 런타임 환경 추상화 기능을 이용하면 환경에 따라 달라지는 빈 구성과 속성 지정 문제를 쉽게 다룰 수 있다.