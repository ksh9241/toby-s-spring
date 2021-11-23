# AOP
AOP는 IoC/DI, 서비스 추상화와 더불어 스프링의 3대 기반기술의 하나다. AOP를 바르게 이용하려면 OOP를 대체하려고 하는 것처럼 보이는 AOP라는 이름 뒤에 감춰진, 그 필연적인 등장배경과 스프링이 그것을 도입한 이유, 그 적용을 통해 얻을 수 있는 장점이 무엇인지에 대한 충분한 이해가 필요하다. 스프링에 적용된 가장 인기 있는 AOP의 적용대상은 선언적 트랜잭션 기능이다.

### 트랜잭션 코드의 분리
스프링이 제공하는 깔끔한 트랜잭션 인터페이스를 사용하였음에도 불구하고 비즈니스 로직의 주인이어야할 메서드 안에 트랜잭션 코드가 더 많은 자리를 차지하고 있다. 그렇다고해서 논리적으로 트랜잭션 코드를 다른 로직에서 처리하는 것은 맞지않다.

##### 메서드 분리
```JAVA
public void upgradeLevels() {
		// 트랜잭션 추상화 API를 적용
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition()); 
		
		// 비즈니스 로직 시작.
		try {
			List<User> users = userDao.getAll();
			for (User user : users) {
				if (changeUpgradeLevel(user)) {
					upgradeLevel(user);
				}
			}
		// 비즈니스 로직 끝.
		
			transactionManager.commit(status);
		} catch (Exception e) {
			transactionManager.rollback(status);
			throw e;
		} 
	}
```

비즈니스 로직 코드를 사이에 두고 트랜잭션 시작과 종료를 담당하는 코드가 앞뒤에 위치하고 있다. 또 이 코드의 특징은 트랜잭션 경계설정의 코드와 비즈니스로직 코드 간에 서로에게 요청하는 정보가 없다는 것이다. 비즈니스 로직에서 직접 DB를 사용하지 않기 때문에 트랜잭션 준비 과정에서 만들어진 DB 커넥션 정보 등을 직접 참조할 필요가 없다. 따라서 이 두 가지 코드는 성격이 다를 뿐 아니라 서로 주고받는 것도 없는 완벽하게 독립적인 코드다.

##### DI 적용을 이용한 트랜잭션 분리
구체적인 구현 클래스를 직접 참조하는 경우의 전형적인 단점이다. 직접 사용하는 것이 문제가 된다면 간접적으로 사용하면된다. DI의 기본 아이디어는 실제 사용할 오브젝트의 클래스 정체는 감춘 채 인터페이스를 통해 간접적으로 접근하는 것이다. 그 덕분에 구현 클래스는 얼마든지 외부에서 변결할 수 있다. 바로 이런 개념을 가진 DI가 지금 필요하다. 
(예제 practice.spring.toby.chapter6 : UserServiceTx, UserServiceImple )

##### 트랜잭션 경계설정 코드 분리의 장점
1. 비즈니스 로직을 담당하고 있는 UserServiceImple의 코드를 작성할 때는 트랜잭션과 같은 기술적인 내용에는 전혀 신경쓰지 않아도 된다. 스프링의 JDBC나 JTA 같은 로우레벨의 트랜잭션 API는 물론이고 스프링의 트랜잭션 추상화 API조차 필요 없다. 트랜잭션은 DI를 이용해 UserServiceTx와 같은 트랜잭션 기능을 가진 오브젝트가 먼저 실행되도록 만들기만 하면 된다.
2. 비즈니스 로직에 대한 테스트를 손쉽게 만들어낼 수 있다는 것이다.

##### 복잡한 의존관계 속 테스트
복잡한 의존관계를 갖는 오브젝트들이 테스트를 진행되는 동안에 같이 실행된다. 더 큰 문제는 그 세 가지 의존 오브젝트도 자신의 코드만 실행하고 마는 것이 아니라는 점이다. 의존관계 오브젝트 역시 다른 오브젝트의 의존관계를 갖고 있다면 꼬리물기 식으로 계속 오브젝트가 실행된다. 그 어느 것이라도 바르게 셋업되어 있지 않거나, 코드에 문제가 있다면 그 때문에 우리가 테스트하려는 오브젝트에 대한 테스트가 실패해버린다. 이런 경우의 테스트는 준비하기 힘들고, 환경이 조금이라도 달라지면 동일한 테스트 결과를 내지 못할 수도 있으며, 수행 속도는 느리고 그에 따라 테스트를 작성하고 실행하는 빈도가 점차로 떨어질 것이 분명하다.

##### 테스트 대상 오브젝트 고립시키기
그래서 테스트 대상이 환경이나 외부 서버, 다른 클래스의 코드에 종속되고 영향을 받지 않도록 고립시킬 필요가 있다. 예시로 MailSender인터페이스를 고립시키는 DummyMailSender 같은 클래스가 있다. 또는 MockMailSender라는 목 오브젝트도 사용했다.

##### 고립된 단위 테스트 활용
예제 Chapter6UserServiceTest

다섯 단계의 테스트
1. 테스트 실행에 필요한 유저 DB 데이터 주입.
2. 메일 발송 여부를 확인하기 위해 MailSender 목 오브젝트 DI
3. 실제 테스트 대상인 UserServiceImple 의 메서드 실행
4. 결과가 DB에 반영됐는지 확인하기 위해 UserDao를 통한 데이터 가져오기 (UserServiceImple의 upgradeLevels())
5. 목 오브젝트를 통한 UserServiceImple에 의한 메일 발송이 있었는지 확인.

##### 단위 테스트와 통합 테스트
단위테스트 : 테스트 대상 클래스를 목 오브젝트 등의 테스트 대역을 이용해 의존 오브젝트나 외부의 리소스를 사용하지 않도록 고립시켜서 테스트 하는 것
통합테스트 : 두 개 이상의 성격이나 계층이 다른 오브젝트가 연동하도록 만들어 테스트하거나 또는 외부의 DB나 파일, 서비스 등의 리소스가 참여하는 테스트

- 하나의 클래스나 성격과 목적이 같은 긴밀한 클래스 몇 개를 모아서 외부와의 의존관계를 모두 차단하고 필요에 따라 스텁이나 목 오브젝트 등의 테스트 대역을 이용하도록 테스트를 만든다.
- 외부 리소스를 사용해야만 가능한 테스트는 통합 테스트로 만든다.
- DB와 연동하는 오브젝트의 경우 분리해서 단위테스트로 만들기 힘들기 때문에 DB까지 하나로 묶어서 단위테스트로 진행한다.
- 각각의 단위테스트에서 성공하였더라도 단위테스트를 연결하여 통합테스트를 진행했을 때 오류가 발생할 수도 있다. 하지만 단위테스트에서 오류가 없었기 때문에 오류를 발견하기가 훨씬 수월하다.
- 단위테스트를 만들기가 너무 복잡하다고 판단되면 통합테스트를 고려한다.
- 스프링 테스트 컨텍스트 프레임워크를 이용하는 테스트는 통합테스트이다. (JUnit)

테스트 코드를 만들려는 노력을 게을리하면 테스트 작성이 불편해지고, 텟트ㅡ를 잘 만들지 않게 될 가능성이 높아진다. 테스트가 없으니 과감하게 리팩토링 할 엄두를 내지 못할 것이고 코드의 품질은 점점 떨어지고 유연성과 확장성을 잃어갈지 모른다.

##### 목 프레임워크
목 오브젝트를 만들 때 테스트에서는 사용하지 않는 인터페이스도 모두 일일이 구현해줘야한다. 검증 기능이 있는 목 오브젝트를 만들려면, 메서드의 호출 내용을 저장했다가 이를 다시 불러오는 것도 매번 귀찮은 일이 아닐 수 없다. (JavaMail, MockUserDao ... ) 다행이도 이런 번거로운 목 오브젝트를 편리하게 작성하도록 도와주는 다양한 목 오브젝트 지원 프레임워크가 있다.

##### Mockito 프레임워크
Mockito 프레임워크는 사용하기도 편리하고, 코드도 직관적이라 최근 많은 인기를 끌고 있다.
목 오브젝트 사용 네가지 단계 * 두 번째와 네 번째는 각각 필요할 경우에만 사용할 수 있다.
1. 인터페이스를 이용해 목 오브젝트를 만든다.
2. 목 오브젝트가 리턴할 값이 있으면 이를 지정해준다. 메서드가 호출되면 예외를 강제로 던지게 만들 수도 있다.
3. 테스트 대상 오브젝트에 DI 해서 목 오브젝트가 테스트 중에 사용되도록 만든다.
4. 테스트 대상 오브젝틀ㄹ 사용한 후에 목 오브젝트의 특정 메서드가 호출됐는지, 어떤 값을 가지고 몇 번 호출됐는지를 검증한다.

- times() : 메서드의 호출 횟수를 검증해준다.
- any()    : 파라미터의 내용은 무시하고 호출 횟수만 확인할 수 있다.
- verify().update(users.get(1)) : users.get(1)을 파라미터로 update() 호출된 적 있는지 확인해준다. update()가 호출된적이 없거나 파라미터가 users(1) 이 아니라면 테스트는 실패한다.

MailSender의 경우 ArgumentCaptor을 사용하여 실제 MailSender 목 오브젝트에 전달된 파라미터를 가져와 내용을 검증하는 방법을 사용했다. 파라미터를 직접 비교하기보다 파라미터의 내부 정보를 확인해야 하는 경우에 유용하다.

### 다이나믹 프록시와 팩토리 빈
핵심 기능과 부가기능을 인터페이스를 통해서 구현한다. 부가기능을 담은 클래스는 중요한 특징이 있다. 부가기능 외의 나머지 모든 기능은 원래 핵심기능을 가진 클래스로 위임해줘야 한다. 핵심기능은 부가기능을 가진 클래스의 존재 자체를 모른다. 따라서 부가기능은 핵심기능을 사용하는 구조가 되는 것이다. 부가기능 코드에서 핵심기능으로 요청을 위임해주는 과정에서 자신이 가진 부가적인 기능을 적용해줄 수 있다.비즈니스 로직 코드에 트랜잭션 기능을 부여해주는 것이 바로 그런 대표적인 경우다. 이렇게 마치 자신이 클라이언트가 사용하려고 하는 실제 대상인 것처럼 위장해서 클라이언트의 요청을 받아주는 것을 대리자, 대리인과 같은 역할을 한다고 해서 프록시라고 부른다.
프록시의 특징은 타깃과 같은 인터페이스를 구현했다는 것과 프록시가 타깃을 제어할 수 있는 위치에 있다는 것이다.

##### 프록시의 사용목적
1. 클라이언트가 타깃에 접근하는 방법을 제어하기 위해서다.
2. 타깃에 부가적인 기능을 부여해주기 위해서다.

두 가지 모두 대리 오브젝트라는 개념의 프록시를 두고 사용한다는 점은 동일하지만, 목적에 따라서 디자인 패턴에서는 다른 패턴으로 구분된다.

##### 데코레이터 패턴
데코레이터 패턴은 타깃에 부가적인 기능을 런타임 시 다이나믹하게 부여해주기 위해 프록시를 사용하는 패턴을 말한다. 다이나믹하게 기능을 부가한다는 의미는 컴파일 시점, 즉 코드상에서는 어떤 방법과 순서로 프록시와 타깃이 연결되어 사용되는지 정해져 있지 않다는 뜻이다. 이 패턴의 이름이 데코레이터라고 불리는 이유는 마치 제품이나 케익 등을 여러 겹으로 포장하고 그 위에 장식을 붙이는 것처럼 실제 내용물은 동일하지만 부가적인 효과를 부여해줄 수 있기 때문이다. 또한 프록시가 직접 타깃을 고정시킬 필요가 없기 때문에 같은 인터페이스를 구현한 타켓과 여러개의 프록시를 사용할 수 있다.

ex) 클라이언트 -> 라인넘버 데코레이터 -> 컬러 데코레이터 -> 페이징 데코레이터 -> 소스코드 출력 기능 (타깃)

##### 프록시 패턴
데코레이션 패턴은 클라이언트와 사용 대상 사이에 대리 역할을 맡은 오브젝트를 두는 방법이었다면 프록시패턴은 프록시를 사용하는 방법 중에서 타깃에 대한 접근 방법을 제어하려는 목적을 가진 경우를 가리킨다. 프록시 패턴의 프록시는 타깃의 기능을 확장하거나 추가하지 않는다. 대신 클라이언트가 타깃에 접근하는 방식을 변경해준다. 
프록시와 데코레이터는 유사하지만 프록시는 코드에서 자신이 만들거나 접근할 타깃 클래스 정보를 알고 있는 경우가 많다. 물론 인터페이스를 통해 위임하도록 만들 수도 있다.

ex) 클라이언트 -> 접근제어 프록시 -> 컬러 데코레이터 -> 페이징 데코레이터 -> 소스코드 출력기능

##### 다이나믹 프록시
프록시 팩토리에 의해 런타임 시 다이나믹하게 만들어지는 오브젝트다. 다이나믹 프록시 오브젝트는 타깃의 인터페이스와 같은 타입으로 만들어진다. 클라이언트는 다이나믹 프록시 오브젝트를 타깃 인터페이스를 통해 사용할 수 있다. 이 덕분에 프록시를 만들 때 인터페이스를 모두 구현해가면서 클래스를 정의하는 수고를 덜 수 있다. 하지만 프록시로서 필요한 부가기능 제공 코드는 직접 작성해야 한다. 부가기능은 프록시 오브젝트와 독립적으로 InvocationHandler를 구현한 오브젝트에 담는다.


프록시 기능
- 타깃과 같은 메서드를 구현하고 있다가 메서드가 호출되면 타깃 오브젝트로 위임한다.
- 지정된 요청에 대해서는 부가기능을 수행한다.

프록시 작성의 문제점
- 타깃의 인터페이스를 구현하고 위임하는 코드를 작성하기가 번거롭다. 부가기능이 필요 없는 메서드도 구현해서 타깃으로 위임하는 코드를 일일이 만들어줘야한다. 복잡하진 않지만 인터페이스의 메서드가 많아지고 다양해지면 상당히 부담스러운 작업이 될 것이다. 또 타깃 인터페이스가 변경되면 함께 수정해줘야 한다는 부담도 있다.
- 부가기능 코드가 중복될 가능성이 많다는 점이다.

##### 리플렉션
리플렉션이란 자바의 코드 자체를 추상화해서 접근하도록 만든 것이다.  ( 객체를 통해 클래스의 정보를 분석해 내는 프로그램 기법 )
BeanFactory는 어플리케이션이 실행한 후 객체가 호출 될 당시 객체의 인스턴스를 생성하게 되는데 이 때 필요한 기술이 Reflection이다.
실행중인 자바프로그램 내부를 검사하고 내부의 속성을 수정할 수 있도록 한다.
```JAVA
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
```

##### 다이나믹 프록시
다이나믹 프록시는 팩토리에 의해 런타임 시 동적으로 만들어지는 오브젝트이다. 클라이언트는 다이나믹 프록시 오브젝트를 타깃 인터페이스를 통해 사용할 수 있다. 이 덕분에 프록시를 만들 때 인터페이스를 모두 구현해가면서 클래스를 정의하는 수고를 덜 수 있다. 다이나믹 프록시가 인터페이스 구현 클래스의 오브젝트는 만들어주지만, 프록시로서 필요한 부가기능 제공 코드는 직접 작성해야 한다. 부가기능은 프록시 오브젝트와 독립적으로 InvocationHandler를 구현한 오브젝트에 담는다. 다이나믹 프록시 오브젝트는 클라이언트의 모든 요청을 리플렉션 정보로 변환해서 InvocationHandler 구현 오브젝트의 invoke() 메서드로 넘긴다. 타깃 인터페이스의 모든 메서드의 요청이 하나의 메서드로 집중되기 때문에 중복되는 기능을 효과적으로 제공할 수 있다.

```JAVA
public class UppercaseHandler implements InvocationHandler {

	Hello target;
	
	public UppercaseHandler (Hello target) {
		this.target = target;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String ret = (String) method.invoke(target, args); // 타깃으로 위임. 인터페이스의 메서드 호출에 모두 적용된다.
		return ret.toUpperCase();
	}
}
```

##### 다이나믹 프록시를 위한 팩토리 빈
DI의 대상이 되는 다이나믹 프록시 오브젝트는 일반적인 스프링의 빈으로는 등록할 방법이 없다는 점이다. 스프링의 빈은 기본적으로 클래스 이름과 프로퍼티로 정의된다. 스프링은 지정된 클래스 이름을 가지고 리플렉션을 이용해서 해당 클래스의 오브젝트를 만든다. Class의 newInstance() 메서드는 해당 클래스의 파라미터가 없는 생성자를 호출하고, 그 결과 생성되는 오브젝트를 돌려주는 리플렉션  API다.
ex ) Date now = (Date) Class.forName("java.util.Date").newInstance();

스프링은 내부적으로 리플렉션 API를 이용해서 빈 정의에 나오는 클래스 이름을 가지고 빈 오브젝트를 생성한다. 문제는 다이나믹 프록시 오브젝트는 이런 식으로 프록시 오브젝트가 생성되지 않는다는 점이다. 다이나믹 프록시는 Proxy 클래스의 newProxyInstance()라는 스태틱 팩토리 메서드를 통해서만 만들 수 있다.

##### 팩토리 빈
스프링은 클래스 정보를 가지고 디폴트 생성자를 통해 오브젝트를 만드는 방법 외에도 빈을 만들 수 있는 여러 가지 방법을 제공한다. 대표적으로 팩토리빈을 이용한 빈 생성 방법을 들 수 있다. 팩토리 빈을 만드는 방법은 여러가지가 있는데 가장 간단한 방법은 FactoryBean 이라는 인터페이스를 구현하는 것이다.

```JAVA

public interface FactoryBean<T> {
	T getObject() throws Exception;	// 빈 오브젝트를 생성해서 돌려준다.
	Class<?> getObjectType();		// 생성되는 오브젝트의 타입을 알려준다.
	boolean isSingleton();		// getObject()가 돌려주는 오브젝트가 항상 같은 싱글톤 오브젝트인지 알려준다.
}

// 생성자를 제공하지 않는 클래스
public class Message {

	String text;
	
	// 생성자가 private으로 선언되어 있어서 외부에서 생성자를 통한 오브젝트를 만들 수 없다.
	private Message (String text) {
		this.text = text;
	}
	public String getMessage() {
		return this.text;
	}
	
	// 생성자 메서드 대신 사용할 수 있는 스태틱 팩토리 메서드를 제공한다.
	public static Message newMessage (String text) {
		return new Message(text);
	}
}
```

사실 스프링은 private 생성자를 가진 클래스도 빈으로 등록해주면 리플렉션을 이용해 오브젝트를 만들어준다. 리플렉션은 private으로 선언된 접근 규약을 위반할 수 있는 강력한 기능이 있기 때문이다. 하지만 생성자를 private으로 만들었다는 것은 스태틱 메서드를 통해 오브젝트를 만들어져야 하는 중요한 이유가 있기 때문이므로 이를 무시하고 오브젝트를 강제로 생성하면 위험하다.

##### 다이나믹 프록시를 만들어주는 팩토리 빈
Proxy의 newProxyInstance() 메서드를 통해서만 생성이 가능한 다이나믹 프록시 오브젝트는 일반적인 방법으로는 스프링의 빈으로 등록할 수 없다. 대신 팩토리 빈을 사용하면 다이나믹 프록시 오브젝트를 스프링의 빈으로 만들어줄 수가 있다. 스프링 빈 설정에는 팩토리 빈과 타깃 클래스만 빈으로 등록한다. 팩토리 빈은 다이나믹 프록시가 위임할 타깃 오브젝트에 대한 레퍼런스를 프로퍼티를 통해 DI 받아둬야 한다. 그 외에도 다이나믹 프록시나 TransactionHandler를 만들 때 필요한 정보는 팩토리 빈의 프로퍼티로 설정해뒀다가 다이나믹 프록시를 만들면서 전달해줘야 한다. (1권 454p 이미지 참조)

##### 트랜잭션 프록시 팩토리 빈 테스트
TransactionHandler와 다이나믹 프록시 오브젝트를 직접 만들어서 테스트했을 때는 타깃 오브젝트를 바꾸기가 쉬웠는데 이제는 스프링 빈에서 생성되는 프록시 오브젝트에 대해 테스트를 해야 하기 때문에 간단하지 않다. 가장 문제는 타깃 오브젝트에 대한 레퍼런스는 TransactionHandler 오브젝트가 갖고 있는데, TxProxyFactoryBean 내부에서 만들어져 다이나믹 프록시 생성에 사용될 뿐 별도로 참조할 방법이 없다는 점이다. 방법은 빈으로 등록된 TxProxyFactoryBean을 직접 가져와서 프록시를 만들어 보면 된다.

```JAVA
@Test
@DirtiesContext // 다이나믹 프록시 팩토리 빈을 직접 만들어 사용할 때는 없앴다가 다시 등장한 컨텍스트 무효화 어노테이션
public void upgradeAllOrNothing() throws Exception {
	MockMailSender mailSender = new MockMailSender();
		
	UserServiceImple testUserService = new UserServiceImple(users.get(3).getId());
	testUserService.setUserDao(userDao);
	testUserService.setMailSender(mailSender);
		
	TxProxyFactoryBean txProxyFactoryBean = context.getBean("&userService", TxProxyFactoryBean.class);
	txProxyFactoryBean.setTarget(testUserService);
	UserService txUserService = (UserService) txProxyFactoryBean.getObject(); // 변경된 타깃 설정을 이용해서 트랜잭션 다이나믹 프록시 오브젝트를 다시 생성한다.
		
	userDao.deleteAll();
	for (User user : users) userDao.add(user);
		
	try {
		txUserService.upgradeLevels();
		fail("TestUserServiceException expected");
	} catch (Exception e) {
	}
		
	checkLevel(users.get(1), false);
}
```