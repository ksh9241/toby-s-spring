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
JTATransactionManager로 는 주요 자바 서버에서 제공하는 JTA 정보를 JNDI를 통해 자동으로 인식하는 기능을 갖고 있다. 따라서 별다른 설정 없이 JTATransactionManager를 사용하기만 해도 서버의 트랜잭션 매니저/서비스와 연동해서 동작하낟.