# 서비스 추상화

##### IDE의 자동수정 기능과 테스트 코드 작성
대부분의 자바 IDE에는 컴파일 에러에 대한 자동수정 기능이 있다. 이클립스에는 빠름고침이라는 기능이 있어서 컴파일 에러가 발생하면 Ctrl + 1 키를 눌러서 IDE가 제안하는 자동수정 내용 중 하나를 선택할 수 있다.

##### 객체지향 장점
각 오브젝트와 메서드가 각각 자기 몫의 책임을 맡아 일을 하는 구조로 만들어졌다. 필요가 생기면 이런 작업을 수행해달라고 서로 요청하는 구조다. 각자 자기 책임에 충실한 작업만 하고 있으니 코드를 이해하기도 쉽다. 또 변경이 필요할 때 어디를 수정해야 할지도 쉽게 알 수 있다. 각각을 독립적으로 테스트하도록 만들면 테스트 코드도 단순해진다. 객체지향적인 코드는 다른 오브젝트의 데이터를 가져와서 작업하는 대신 데이터를 갖고 있는 다른 오브젝트에게 작업을 해달라고 요청한다. 오브젝트에게 데이터를 요구하지 말고 작업을 요청하라는 것이 객체지향 프로그래밍의 가장 기본이 되는 원리이기도 하다.

##### 상수 활용
기존 비즈니스 로직에서 경계값이나 조건처리를 숫자로 넣어두면 코드를 다 읽기전에 어떤 의미로 저 숫자의 값을 넣었는지 이해하기 어렵다. 그래서 불변 객체의 상수를 사용하여 숫자값을 상수로 변경하였다. 변경 전과 비교했을 때 코드를 다 읽지 않아도 상수 네이밍으로도 충분한 의미전달이 가능하다. 또한 중복으로 사용하는 부분에서 일일이 하드코딩하지않고 상수로 사용한다면 나중에 조건이 변경되었을 때 상수값 하나만 변경하면 모든 수정처리가 완료된다.

### 트랜잭션 서비스 추상화
비즈니스 로직을 실행 중 예외가 발생하면 앞에 수행했던 데이터는 초기화를 시키는 게 맞다. 데이터가 원치 않는 값으로 변경되었을 가능성도 있고, 정상적으로 반영되었다고 해도 일부분의 데이터만 변경시키면 변경대상이지만 변경되지 않은 데이터의 사용자가 반발이 심할 것이기 때문이다.

##### 테스트용 Service 대역
트랜잭션 테스트를 하기 위해서는 중간에 예외를 강제로 만들어서 중단시켜야 한다. 하지만 실제 사용하는 코드를 강제로 예외를 발생시키게 수정하는 방법은 좋은 방법이 아니다. 그래서 이런 경우에는 테스트용 Service를 만들어 사용하는 것이 좋다.

##### 트랜잭션 경계설정
DB는 그 자체로 완벽한 트랜잭션을 지원한다. 하나의 SQL 명령을 처리하는 경우는 DB가 트랜잭션을 보장해준다고 믿을 수 있다. 하지만 두번에 걸쳐서 SQL명령을 처리할 때에도 트랜잭션은 활성화되어야 한다. 가령 입금의 경우도 입금계좌에 이체금액만큼 증가되고, 출금계좌의 잔고를 수정하는 작업을 실행한다. 만약 여기서 도중에 문제가 발생했다면 앞에 성공한 SQL까지도 롤백해버리는 트랜잭션 롤백을 하고 모든 작업이 성공적으로 이루어졌으면 트랜잭션 커밋을 한다. 
트랜잭션 경계란 Connection을 가져와 사용하다가 닫는 사이에 범위를 말한다. JDBC에서 트랜잭션 경계설정을 해주는 방법은 자동커밋 옵션 [ setAutoCommit(false) ] 을 false로 만들어주면 된다.

##### 트랜잭션 동기화
트랜잭션을 수동으로 경계를 설정해주기 위해선 Connection 오브젝트가 생성하고부터 관련된 SQL명령을 모두 마치고 Connection을 반납해야 한다. 기존의 java에서는 그걸 메서드의 파라미터에 Connection 오브젝트를 넘겨서 지역변수로 사용했다. 그러면 결합도가 높아져서 코드가 좋지 않다. 스프링은 이러한 문제를 해결할 수 있는 방법을 제공한다.
1. 동립적인 트랜잭션 동기화 방식이다. 트랜잭션 동기화란 트랜잭션을 시작하기 위해 만든 Connection 오브젝트를 특별한 저장소에 보관해두고, 이후에 호출되는 DAO의 메서드에서 저장된 Connection을 가져다가 사용하게 하는 것이다. 정확히는 DAO가 사용하는 JdbcTemplate이 트랜잭션 동기화 방식을 이용하도록 하는 것이다.

동작순서
- UserService는 Connection을 생성.
- 이를 트랜잭션 동기화 저장소에 저장해두고 Connection의 SetAutoCommit(false)를 호출해 트랜잭션을 시작시킨 후에 본격적으로 DAO의 기능을 이용.
- SQL 명령 메서드가 호출되고 메서드 내부에서 이용하는 JdbcTemplate 메서드에서 가장먼저 트랜잭션 동기화 저장소에 현재 시작된 트랜잭션을 가진 Connection 오브젝트가 존재하는지 확인.
- 2번에서 메서드 시작할때 저장해둔 Connection을 가져온다.
- Connection을 이용해 PreparedStatement를 만들고 SQL을 실행한다. 결과를 트랜잭션 동기화 저장소에 저장.
- 모든 SQL 명령이 완료되었으면 Connection의 commit()을 호출 후 Connection을 반환한다.
- 도중에 예외가 발생하였을 경우 Connection의 Rollback()를 호출 후 Connection을 반환한다.

트랜잭션 동기화 저장소는 작업 스레드마다 독립적으로 Connection 오브젝트를 저장하고 관리하기 때문에 다중 사용자를 처리하는 서버의 멀티스레드 환경에서도 충돌이 날 염려는 없다.

##### JdbcTemplate과 트랜잭션 동기화
JdbcTemplate의 동작원리는 미리 생성돼서 트랜잭션 동기화 저장소에 등록된 DB커넥션이나 트랜잭션이 없는 경우에는 직접 DB 커넥션을 만들고 트랜잭션을 시작해서 JDBC 작업을 진행한다. 만약 메서드에서 미리 동기화를 시작해놓았다면 그때부터 실행되는 JdbcTemplate의 메서드에서는 직접 DB 커넥션을 만드는 대신 트랜잭션 동기화 저장소에 들어있는 DB 커넥션을 가져와서 사용한다. 이를 통해 이미 시작된 트랜잭션에 참여하는 것이다.

##### 트랜잭션 서비스 추상화
기술과 환경에 종속되는 트랜잭션 경계설정 코드 : 한 개 이상의 DB로의 작업을 하나의 트랜잭션으로 만드는건 JDBC의 커넥션을 이용한 트랜잭션 방식인 로컬 트랜잭션으로는 불가능하다. 왜냐하면 로컬 트랜잭션은 하나의 DB 커넥션에 종속되기 때문이다. 따라서 각 DB와 독립적으로 만들어지는 커넥션을 통해서가 아니라 별도의 트랜잭션 관리자를 통해 트랜잭션을 관리하는 글로벌 트랜잭션 방식을 사용해야 한다. 
자바는 JDBC 외에 이런 글로벌 트랜잭션을 지원하는 트랜잭션 매니저를 지원하기 위한 API인 JTA (Java Transaction API) 를 제공하고 있다.

##### 스프링의 트랜잭션 서비스 추상화
스프링이 제공하는 트랜잭션 경계설정을 위한 추상 인터페이스는 PlatformTransactionManager다. JDBC의 로컬 트랜잭션을 이용한다면 PlatformTransactionManager를 구현한 DataSourceTransactionManager를 사용하면 된다. 사용할 DB의 DataSource를 생성자 파라미터로 넣으면서 DataSourceTransactionManager의 오브젝트를 만든다.

```JAVA
PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
// DefaultTransactionDefinition 오브젝트는 트랜잭션에 대한 속성을 담고 있다.
// TransactionStatus는 트랜잭션에 대한 조작이 필요할 때 PlatformTransactionManager 메서드의 파라미터로 전달해주면 된다.
		
		try {
			List<User> users = userDao.getAll();
			for (User user : users) {
				if (changeUpgradeLevel(user)) {
					upgradeLevel(user);
				}
			}
			transactionManager.commit(status);
		} catch (Exception e) {
			transactionManager.rollback(status);
			throw e;
		} 
```

##### 트랜잭션 기술 설정의 분리
트랜잭션 추상화 API를 적용한 UserService 코드를 JTA를 이용하는 글로벌 트랜잭션으로 변경하려면 구현 클래스를 DataSourceTransactionManager에서 JTATransactionManager로 바꿔주기만 하면 된다.
JTATransactionManager로 는 주요 자바 서버에서 제공하는 JTA 정보를 JNDI를 통해 자동으로 인식하는 기능을 갖고 있다. 따라서 별다른 설정 없이 JTATransactionManager를 사용하기만 해도 서버의 트랜잭션 매니저/서비스와 연동해서 동작한다.

### 서비스 추상화와 단일 책임 원칙

##### 수직, 수평 계층구조와 의존관계
기술과 서비스에 대한 추상화 기법을 이용하면 특정 기술환경에 종속되지 않는 포터블한 코드를 만들 수 있다. Dao와 Service는 각각 담당하는 코드의 기능적인 관심에 따라 분리되고, 서로 불필요한 영향을 주지 않으면서 독자적으로 확장이 가능하도록 만든 것이다. 같은 애플리케이션 로직을 담는 코드지만 내용에 따라 분리했다. 같은 계층에서 수평적인 분리라고 볼 수 있다. 
트랜잭션 추상화는 이와는 좀 다르다. 애플리케이션의 비즈니스 로직과 그 하위에서 동작하는 로우레벨의 트랜잭션 기술이라는 아예 다른 계층의 특성을 갖는 코드를 분리한 것이다.

##### 계층과 책임의 분리
UserService	    ---> UserDao : 애플리케이션계층 

        ↓		    	↓

TransactionManager ---> DataSource : 서비스 추상화 계층 

        ↓		    	↓

JDBC, JTA, Connection Pooling, JNDI, WAS, Database... : 기술 서비스 계층 

##### 단일 책임 원칙
적절한 분리가 가져오는 특징은 객체지향 설계의 원칙 중의 하나인 단일 책임 원칙으로 설명할 수 있다. 단일 책임 원칙은 하나의 모듈은 한 가지 책임을 가져야 한다는 의미다. 하나의 모듈이 바뀌는 이유는 한 가지여야 한다고 설명할 수도 있다.

##### 단일 책임 원칙의 장점
단일 책임 원칙을 잘 지키고 있다면, 어떤 변경이 필요할 때 수정 대상이 명확해진다. 기술이 바뀌면 기술 계층과의 연동을 담당하는 기술 추상화 계층의 설정만 바꿔주면 된다. 데이터를 가져오는 테이블의 이름이 바뀌었다면 데이터 액세스 로직을 담고 있는 Dao를 변경하면 된다. 비즈니스 로직도 마찬가지이다. 만약 애플리케이션 계층의 코드가 특정 기술에 종속돼서 기술이 바뀔 때마다 코드의 수정이 필요하다면 어떨지 상상해보자. (서비스 클래스 수) x (수정되는 기능을 가진 메서드 수) 만큼의 엄청난 코드를 수정해야 할 것이다. 이런경우 DI를 통해 설정을 수정하는 방법과 비교해보면 엄청난 차이가 생긴다. 단지 작업량의 문제뿐 아니라 실수가 일어날 확률이 높고 그 말은 치명적인 버그가 도입될 가능성도 있다.
객체지향 설계와 프로그래밍의 원칙은 서로 긴밀하게 관련이 있다. 단일 책임 원칙을 잘 지키는 코드를 만들려면 인터페이스를 도입하고 이를 DI로 연결해야 하며, 그 결과로 단일 책임 원칙 뿐 아니라 개방 폐쇄 원칙도 잘 지키고, 모듈 간 결합도가 낮아서 서로의 변경이 영향을 주지 않고, 같은 이유로 변경이 단일 책임에 집중되는 응집도 높은 코드가 나오니까 말이다. 이런 과정에서 전략패턴, 어댑터 패턴, 브리지 패턴, 미디에이터 패턴 등 많은 디자인 패턴이 자연스럽게 적용되기도 한다.

##### JavaMail을 이용한 메일 발송 기능
자바에서 메일을 발송할 때는 표준 기술인 JavaMail을 사용하면 된다.
추가할 라이브러리
- spring-context-support
- javax.mail
- javax.activation

##### JavaMail이 포함된 코드 테스트
만약 메일 서버가 준비되어 있지 않다면 다음과 같은 예외가 발생할 것이다.
java.lang.RuntimeException : javax.mail.MessagingException :Could not connect to SMTP host
테스트 실패의 원인은 분명하다 메일을 발송하려는데 메일 서버가 현재 연결 가능하도록 준비되어 있지 않기 때문이다. 테스트가 수행될 때 메일이 전송될 수 있으니 실제로 사용할 메일 서버를 제대로 준비해두고 테스트를 실행할 수 있다. 그리고 메일도 실제로 발송될 것이다. 과연 테스트마다 매번 메일이 발송되는 것이 바람직한 것인가? 대개는 바람직하지 못하다. 메일 발송이란 매우 부하가 큰 작업이다. 메일 서버는 충분히 테스트된 시스템이다. SMTP로 메일 전송 요청을 받으면 별문제 없이 메일이 잘 전송됐다고 믿어도 충분하다. 따라서 JavaMail을 통해 메일 서버까지만 메일이 전달됐으면, 결국 사용자에게도 메일이 잘 보내졌을 것이라고 생각할 수 있다.

##### JavaMail을 이용한 테스트의 문제점
JavaMail의 핵심 API는 DataSource 처럼 인터페이스로 만들어져서 구현을 바꿀 수 있는 게 없다. 메일 발송을 위해 가장 먼저 생성해야 하는 javax.mail.Session 클래스의 사용방법을 살펴보자.

```JAVA
Session s = Session.getInstance(props, null);
```

JavaMail에서는 Session 오브젝트를 만들어야만 메일 메시지를 생성할 수 있고, 메일을 전송할 수 있다. 그런데 이 Session은 인터페이스가 아니고 클래스다. 게다가 생성자가 모두 private으로 되어 있어서 직접 생성도 불가능하다. 스태틱 팩토리 메서드를 이용한 오브젝트를 만드는 방법밖에 없다. 게다가 Session 클래스는 더 이상 상속이 불가능한 final 클래스다. 도무지 구현을 바꿔치기할 만한 인터페이스의 존재가 보이지 않는다. 메일 메시지를 작성하는 MailMessage도 전송 기능을 맡고 있는 Transport도 마찬가지다. 그렇다면 테스트용 JavaMail로 대체하여 테스트하고 사용하는 것을 포기해야하는 것은 아니다. 스프링은 JavaMail을 사용해 만든 코드를 테스트하기 힘든 문제를 해결하기 위해서 JavaMail에 대한 추상화 기능을 제공하고 있다.

```JAVA
// JavaMail의 서비스 추상화 인터페이스
public interface MailSender {
	void send(SimpleMailMessage simpleMessage) throws MailException;
	void send (SimpleMailMessage[] simpleMessages) throws MailException;
}
```

스프링이 제공하는 JavaMailSender 인터페이스와 관련 클래스 등으로 수정하고 나니 따로 try/catch 등으로 예외를 잡아줄 필요가 없다. 스프링의 예외 처리 원칙에 따라서 JavaMail을 처리하는 중에 발생한 각종 예외를 MailException이라는 런타임예외로 포장해서 던져주기 때문이다.

##### 테스트와 서비스 추상화
스프링이 직접 제공하는 MailSender를 구현한 추상화 클래스는 JavaMailServiceImpl 하나뿐이다. 다양한 트랜잭션 기술에 대해 추상화 클래스를 제공하는 것과는 분명 대비된다. 그럼에도 불구하고 이 추상화된 메일 전송 기능을 사용해 애플리케이션을 작성함으로써 얻을 수 있는 장점은 크다.
- JavaMail이 아닌 다른 메시징 서버의 API를 이용해 메일을 전송해야 하는 경우 해당 기술의 API를 이용하는 MailSender 구현 클래스를 만들어 DI해주면 된다.
- 메일을 바로 전송하지 않고 작업 큐에 담아뒀다가 정해진 시간에 메일을 발송하는 기능을 만드는 일도 어렵지 않다. 메일 발송 큐의 구현을 하나 만들어두고 다시 DI를 통해 JavaMailServiceImpl 같은 실제 메일 발송용 오브젝트를 연결해서 사용할 수 있다.

##### 메일발송작업 트랜잭션
우리가 구현한 코드의 한가지 문제점은 메일발송 작업에 트랜잭션이 빠져있다는 사실이다. 레벨 업그레이드 중 DB에 반영했던 업그레이드를 모두 롤백했다고 해도 이미 보내진 메일을 롤백할 순 없다. 이 문제를 해결하기위한 두 가지 방법 중 첫 번째는 메일을 업그레이드 할 사용자를 발견했을 때마다 발송하지 않고 발송 대상을 별도의 목록에 저장해두는 것이다. 그리고 업그레이드가 모두 성공적으로 끝났을 때 한번에 메일을 전송하면 된다. 이 방식의 단점은 메일 저장용 리스트 등을 파라미터로 계속 갖고 다녀야 한다는 점이다.
다른 방법은 MailSender를 확장해서 메일 전송에 트랜잭션 개념을 적용하는 것이다.
전자가 사용자 관리 비즈니스 로직과 메일 발송에 트랜잭션 개념을 적용하는 기술적인 부분이 한데 섞이게 한다면, 후자의 MailSender의 구현 클래스를 이용하는 방법은 서로 다른 종류의 작업을 분리해 처리한다는 면에서 장점이 있다.

##### 테스트 대역
DummyMailSender 클래스는 아무것도 하는 일이 없다. MailSender 인터페이스를 구현해놨을뿐 메서드는 비어있다. 기존의 클래스의 경우라면 하는 일이 없는 클래스는 메모리 낭비만하는 가치가 없는 클래스지만 이 DummyMailSender 클래스의 가치는 매우 크다. 이 클래스를 통해 실제 JavaMail 클래스를 사용하지 않고 실제 메일도 보내지 않으면서 테스트를 진행할 수 있기 떄문이다. 스프링의 XML 설정파일을 테스트용으로 따로 만드는 이유는 개발자 환경에서 손쉽게 이용할 수 있는 테스트용 DB를 사용하도록 만들기 위해서다. 대부분 테스트할 대상이 의존하고 있는 오브젝트를 DI를 통해 바꿔치기하는 것이다.

##### 의존오브젝트의 변경을 통한 테스트 방법
테스트 대상이 되는 오브젝트가 또 다른 오브젝트에 의존하는 일은 매우 흔하다. 의존한다는 말은 종속되거나 기능을 사용한다는 의미다. 작은 기능이라도 다른 오브젝트의 기능을 사용하면, 사용하는 오브젝트의 기능이 바뀌었을 때 자신이 영향을 받을 수 있기 때문에 의존하고 있다고 말하는 것이다. 의존 오브젝트는 협력오브젝트라고도 한다. 협력해서 일을 처리하기 때문이다. 실제로 의존관계가 있는 오브젝트를 테스트하기에는 연결된 많은 작업을 함께 신경써야 한다. 테스트 코드에서 이러한 문제를 해결하기 위해서는 의존오브젝트를 기능이 없거나 매우 간단한 오브젝트로 교체하여 테스트하고자 하는 오브젝트에만 집중할 때 DI를 통해 쉽게 교체가 가능하다. 예시로 JavaMail 의 메일송신 기능은 반드시 테스트가 필요하지만 매번 테스트를 위해 실제 메일을 전송하게 된다면 서버에도 부담이 많이가고, 받는 사용자도 불편할 것이다. 이럴 때 앞에서 만든 MailSender 인터페이스를 구현한 DummyMailSender 같은 클래스로 의존오브젝트를 교체하여 Service 클래스의 메서드를 쉽게 테스트할 수 있다.

##### 테스트 대역의 종류와 특징
테스트 대상이 되는 오브젝트의 기능에만 충실하게 수행하면서 빠르게, 자주 테스트를 실행할 수 있도록 사용하는 이런 오브젝트를 통틀어서 테스트 대역(test double) 이라고 부른다.
대표적인 테스트 대역은 테스트 스텁(test stub)이다. 테스트 스텁은 테스트 대상 오브젝트의 의존 객체로서 존재하면서 테스트 동안에 코드가 정상적으로 수행할 수 있도록 돕는 것을 말한다.
일반적으로 테스트 스텁은 메서드를 통해 전달하는 파라미터와 달리, 테스트 코드 내부에서 간접적으로 사용된다. DummyMailSender 같은 클래스가 심플한 테스트 스텁의 예다.

테스트 대상 오브젝트의 메서드가 돌려주는 결과뿐 아니라 테스트 오브젝트가 간접적으로 의존 오브젝트에 넘기는 값과 그 행위 자체에 대해서도 검증하고 싶다면 목 오브젝트 (mock object)를 사용해야 한다.
목 오브젝트는 스텁처럼 테스트 오브젝트가 정상적으로 실행되도록 도와주면서, 테스트 오브젝트와 자신의 사이에서 일어나는 커뮤니케이션 내용을 저장해뒀다가 테스트 결과를 검증하는 데 활용할 수 있게 해준다.

# 정리
비즈니스 로직을 담은 UserService 클래스를 만들고 트랜잭션을 적용하면서 스프링의 서비스 추상화에 대해 알아보았다.
- 비즈니스 로직을 담은 코드는 데이터 액세스 로직을 담은 코드와 깔끔하게 분리되는 것이 바람직하다. 비즈니스 로직 코드 또한 내부적으로 책임과 역할에 따라서 깔끔하게 메서드로 정리돼야 한다.
- 이를 위해서는 DAO의 기술 변화에 서비스 계층의 코드가 영향을 받지 않도록 인터페이스와 DI를 잘 활용해서 결합도를 낮춰줘야 한다.
- DAO를 사용하는 비즈니스 로직에는 단위 작업을 보장해주는 트랜잭션이 필요하다.
- 트랜잭션의 시작과 종료를 지정하는 일을 트랜잭션 경계설정이라고 한다. 트랜잭션 경계설정은 주로 비즈니스 로직 안에서 일어나는 경우가 많다.
- 시작된 트랜잭션 정보를 담은 오브젝트를 파라미터로 DAO에 전달하는 방법은 매우 비효율적이기 때문에 스프링이 제공하는 트랜잭션 동기화 기법을 활용하는 것이 편리하다. ( PlatformTransactionManager )
- 자바에서 사용되는 트랜잭션 API의 종류와 방법은 다양하다. 환경과 서버에 따라서 트랜잭션 방법이 변경되면 경계설정 코드도 함께 변경돼야 한다.
- 트랜잭션 방법에 따라 비즈니스 로직을 담은 코드가 함께 변경되면 단일 책임 원칙에 위배 되며, DAO가 사용하는 특정 기술에 대해 강한 결합을 만들어낸다.
- 서비스 추상화는 로우레벨의 트랜잭션 기술과 API의 변화에 상관없이 일관된 API를 가진 추상화 계층을 도입한다.
- 서비스 추상화는 테스트하기 어려운 JavaMail 같은 기술에도 적용할 수 있다. 테스트를 편리하게 작성하도록 도와주는 것만으로도 서비스 추상화는 가치가 있다.
- 테스트 대상이 사용하는 의존 오브젝트를 대체할 수 있도록 만든 오브젝트를 테스트 대역이라고 한다.
- 테스트 대역은 테스트 대상 오브젝트가 원활하게 동작할 수 있도록 도우면서 테스트를 위해 간접적인 정보를 제공해주기도 한다.
- 테스트 대역 중에서 테스트 대상으로부터 전달받은 정보를 검증할 수 있도록 설계된 것을 목 오브젝트라고 한다.