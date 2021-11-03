# 예외
잘못된 예외처리 코드 때문에 찾기 힘든 버그를 낳을 수도 있고, 생각하지 않았던 예외상황이 발생했을 때 상상 이상으로 난처해질 수도 있다.

### 초 난감 예외처리

#####예외 블랙홀 

```JAVA
try {
  ...
} catch (Exception e) { // 예외를 잡고 아무것도 하지 않는다.
   System.out.println(e);
   e.printStackTrace();  // 콘솔에 예외를 출력하는 두 개의 코드 역시 문제가 많다.
}
```
예외가 발생하면 try/catch를 사용하여 잡아내는 것까지는 좋은데 아무것도 하지않고 별 문제 없는 것처럼 넘어가 버리는 건 정말 위험한 일이다. 원치 않는 예외가 발생한 것보다 훨씬 더 나쁜 일이다. 왜냐하면 저렇게 처리하면 예외는 나오지않는데 프로그램은 계속 실행되기 때문이다. 예외를 처리할 때 반드시 지켜야 할 핵심 원칙은 한 가지다. 모든 예외는 적절하게 복구되든지 아니면 작업을 중단시키고 운영자 또는 개발자에게 분명하게 통보돼야 한다. 조취를 취할 방법을 모르겠다면, 메서드에 throws Exception을 선언해서 메서드 밖으로 던지고 자신을 호출한 코드에 예외처리 책임을 전가해버려라.

##### 무책임한 throws
가끔 보면 호출하는 모든 메서드에 throws Exception을 통해 계속 예외처리를 던져버리는 경우가 있다.  이러한 소스에도 심각한 문제점이 있다. 결과적으로 적절한 처리를 통해 복구될 수 있는 예외상황도 제대로 다룰 수 없는 기회를 박탈당한다. 이러한 코드도 매우 안 좋은 예외 처리 방법이다. 위 두 가지 나쁜 습관은 어떤 경우에도 용납하지 않아야 한다.

##### 예외의 종류와 특징
자바에서 throw를 통해 발생시킬 수 있는 예외는 크게 세 가지가 있다.
- Error
첫째는 java.lang.Error 클래스의 서브 클래스들이다. 주로 자바 VM에서 발생시키는 것이고 애플리케이션 코드에서 잡으려고하면 안된다. OutOfMemoryError이나 ThreadDeath 같은 에러는 catch 블록에서 잡아봤자 아무런 대응 방법이 없기 때문이다. 따라서 애플리케이션에서는 이러한 에러에 대한 처리는 신경쓰지 않아도 된다.

- Exception과 체크 예외
java.lang.Exception 클래스와 그 서브 클래스로 정의되는 예외들은 에러와 달리 개발자들이 만든 애플리케이션 코드의 작업 중에 예외상황이 발생했을 경우에 사용된다. Exception 클래스는 체크 예외와 언체크 예외로 구분된다. 체크예외의 경우 Exception 클래스의 서브클래스이면서 RuntimeException 클래스를 상속하지 않은 것들이고 언체크 예외는 RuntimeException 클래스를 상속한 예외이다.
- Exception
	- checked Exception
		- Exception 클래스의 서브클래스 && RuntimeException 클래스 상속 X
	- unchecked Exception
		- RuntimeException 클래스의 서브클래스

체크 예외가 발생할 수 있는 메서드를 사용할 경우 반드시 예외를 처리하는 코드를 함께 작성해야 한다. 그렇지 않으면 컴파일 에러가 발생한다.

- RuntimeException과 언체크/런타임 예외
RuntimeException을 상속한 예외들은 명시적인 예외처리를 강제하지 않기 때문에 언체크 예외라고 불린다. 런타임 예외는 주로 프로그램의 오류가 있을 때 발생하도록 의도된 것들이다. 대표적으로 오브젝트를 할당하지 않은 레퍼런스 변수를 사용하려고 시도했을 때 발생하는 NPE이나, 허용되지 않는 값을 사용해서 메서드를 호출할 때 발생하는 IllegalArgumentException 등이 있다. 코드에서 미리 조건을 체크하도록 주의 깊게 만든다면 피할 수 있지만 개발자가 부주의해서 발생할 수 있는 경우에 발생하도록 만든 것이 런타임 예외다. 예상하지 못했던 예외상황에서 발생하기 때문에 굳이 try/catch나 throws를 사용하지 않아도 되도록 만든 것이다.

##### 예외 처리방법
- 예외 복구
예외 상황을 파악하고 문제를 해결해서 정상 상태로 돌려놓는 것이다. 가령 네트워크가 불안해서 원격 DB 서버에 접속하다 실패해서 SQLException이 발생할 수도 있다. 이런경우 일정시간 기다린다던가, 다른 네트워크를 잡는 등 예외상황으로부터 복구를 시도할 수 있다.

- 예외 처리 회피
예외 처리를 자신이 담당하지 않고 호출한 메서드로 던져버리는 것이다. throws를 통한 예외 발생 시 던지거나 catch 블록 내 throw를 통한 발생된 예외를 던지는 것이다. JdbcContext나 JdbcTemplate를 사용하다가 ResultSet 혹은 PreparedStatement를 사용하다 SQLException이 발생하면 자신이 처리하지 않고 템플릿으로 던져버린다. 콜백 오브젝트의 메서드는 모두 throws SQLException이 붙어있다. 예외를 처리하는 일은 콜백 오브젝트의 역할이 아니라고 보기 때문이다. 하지만 콜백과 템플릿처럼 긴밀한 관계가 아닌 상황에서 자신의 코드에서 발생한 예외를 그냥 던지는 것은 무책임한 책임회피링 수 있다. 예외를 회피하는 것은 예외를 복구하는 것처럼 의도가 분명해야 한다.

- 예외전환
예외 회피랑 비슷하게 예외를 메서드 밖으로 던지지만 예외를 그대로 넘기는 게 아니라 적절한 예외로 전환해서 던진다는 특징이 있다. 예외전환은 보통 두 가지 목적으로 사용된다.
1. 내부에서 발생한 예외를 그대로 던지는 것이 그 예외상황에 대한 적절한 의미를 부여해주지 못하는 경우, 그 의미를 분명하게 해줄 수 있는 예외로 바꿔주기 위해서다.
예시로 새로운 사용자를 등록하려 시도했을 때 동일한 데이터가 있어서 SQLException을 그대로 밖으로 던져버리면 DAO를 이용해 사용자를 추가하려고 한 서비스 계층 등에서는 왜 SQLException이 발생했는지 쉽게 알 방법이 없다. 이럴 땐 DAO에서 예외를 해석해서 DuplicateUserIdException 같은 예외로 바꿔서 던져주는 게 좋다.
```JAVA
catch (SQLException e) {
	if (e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY) throw DuplicateUserIdException(e); // 중첩예외
	else throw e;
	
	// 예외 처리 두가지 방법
	throw DuplicateUserldException(e); // 중첩예외
	    or
            throw DuplicateUserldException().initCause(e); // 예외 전환
}
```

일반적으로 체크 예외를 계속 throws를 사용해 넘기는 건 무의미하다. 메서드 선언이 지저분해지고 아무런 장점이 없다. 이런경우 해결할 수 없다면 빠르게 RuntimeException으로 포장해 던지게 해서 다른 계층의 메서드를 작성할 때 불필요한 throws 선언이 들어가지 않도록 처리해야 한다.

##### 런타임 예외의 보편화
자바 초기부터 있었던 JDK의 API와 달리 최근에 등장하는 표준 스펙 또는 오픈소스 프레임워크에서는 API가 발생시키는 예외를 체크 예외 대신 언체크 예외로 정의하는 것이 일반화되고 있다. 예전에는 복구 가능성이 조금이라도 있다면 체크예외로 만들었지만 지금은 항상 복구할 수 있는 예외가 아니라면 언체크 예외로 만드는 경향이 있다. 단 런타임 예외를 사용할 경우 사용에 더 주의를 기울일 필요도 있다. 컴파일러가 강제하지 않으므로 신경 쓰지 않으면 예외 상황을 충분히 고려하지 않을 수도 있기 때문이다.

##### 애플리케이션 예외
시스템 또는 외부의 예외상황이 원인이 아니라 애플리케이션 자체의 로직에 의해 의도적으로 발생시키고, 반드시 catch해서 무엇인가 조치를 취하도록 요구하는 예외를 애플리케이션 예외라고 한다.
만약 출금 서비스를 만든다고 가정해보자면 잔고를 확인하지 않고 요청하는 금액을 무차별적으로 출금해주는 서비스는 없을 것이다. 이럴 때 DB에 출금요청 금액만큼 출금 후 잔액 부족같은 상황을 예외로 잡아서 요청한 메서드에 throw하게되면 금전적인 문제의 위험이 줄어든다. 이러한 상황은 런타임 예외로 만들어두는 것보다 필수로 체크해야하는 체크 예외로 만들어두는 것이 안전하다.

##### SQLException
체크예외인 SQLException은 99%의 경우로 코드레벨에서는 복구할 방법이 없는 예외이다. 프로그램의 오류 또는 개발자의 부주의에 의해 발생하는 경우이거나, 통제할 수 없는 외부상황에 의해 발생되는 예외이기 때문이다. 그럼 왜 체크예외인가? 그 이유는 개발자가 쿼리문을 잘못 작성했던 외부상황 (네트워크환경 등.)에 문제가 생겼던 빠르게 개발자에게 문제점을 알려서 문제를 인지하게 해야하기 때문이다. 그래서 SQLException이 JdbcTemplate에서 throw하지않고 RuntimeException인 DataAccessException 으로 포장해서 처리하는 이유도 그 때문이다. 예외상황을 알리지만 해결방안이 없는경우가 대다수이기 때문에 throws를 통한 다른 메서드에 예외를 전가하지 않는 것이다.

##### JDBC의 한계
JDBC는 자바를 이용해 DB에 접근하는 방법을 추상화된 API형태로 정의해놓고, 각 DB 업체가 JDBC 표준을 따라 만들어진 드라이버를 제공하게 해준다. 내부 구현은 다르겠지만 JDBC의 Connection, Statement, ResultSet 등의 표준 인터페이스를 통해 그 기능을 제공해주기 때문에 JDBC만 숙지한다면 일관된 방법으로 프로그램을 개발할 수 있다. 이러한 장점으로 다양한 DB 프로그램 사용의 장벽을 낮추지만 DB를 자유롭게 변경해서 사용할 수 있는 유연한 코드를 보장해주지는 못한다. 그 이유에는 두 가지가 있다.
1. 비표준 SQL
DB마다 비표준 내장함수가 존재하기 때문이다. 가령 mySQL과 Oracle을 봐도 그렇다. 어디부터 어디까지의 결과를 출력하고 싶을 때 Oracle의 경우 between을 통해 시작값과 종료값을 적는다. 하지만 mySQL의 경우 limit을 통해서 범위를 지정한다. 이런식의 비표준 SQL 때문에 DAO에 개발자가 작성한 sql도 특정 DB에 종속적인 코드가 된다.

2. 호환성 없는 SQLException의 DB 에러정보
두 번째 문제는 예외문제이다. DB를 사용하다보면 발생할 수 있는 예외가 존재하는데 SQL 문법오류도 있고, DB커넥션도 있으며, 테이블 혹은 컬럼이 존재하지 않거나, 키가 중복되거나, 다양한 제약조건을 위배하는 시도를 한 경우, 데드락에 걸렸거나 락을 얻지 못했을 경우 등 수백여 가지에 이른다. JDBC API는 이러한 많은 예외상황을 SQLException 하나만을 던지도록 설계되어 있다. 결국 호환성 없는 에러 코드와 표준을 잘 따르지 않는 상태 코드를 가진 SQLException만으로 DB에 독립적인 유연한 코드를 작성하는건 불가능에 가깝다.

##### DB 에러코드 매핑을 통한 전환

```JAVA
	<bean id="Oracle" class="org.springframework.jdbc.support.SQLErrorCodes">
		<property name="badSqlGrammarCodes"> <!-- 예외 클래스 종류 -->
			<value>900,903,917,936,942,17006</value> <!-- 매핑되는 DB에러코드. 에러코드가 세분화된 경우 여러개가 들어가기도 한다. -->
		</property>
		
		<property name="invalidResultSetAccessCodes">
			<value>17003</value>
		</property>
		
		<property name="duplicateKeyCodes">
			<value>1</value>
		</property>
		
		<property name="dataIntegrityViolationCodes">
			<value>1400,1722,2291,2292</value>
		</property>
		
		<property name="dataAccessResourceFailureCodes">
			<value>17002,17447</value>
		</property>
	</bean>
```

DB전용 에러코드를 미리 빈으로 생성 후 전환시켜둔 뒤 JdbcTemplate를 통해 DB와 통신하게 되면 예외가 발생했을 때 value값에 있는 코드에 맞는 예외가 반환된다. 이렇게 사용하는 DB별 전용코드를 만들어서 JdbcTemplate의 SQLException만 던져주는 문제를 조금은 해결할 수 있다.

##### DAO 인터페이스와 구현의 분리
DAO를 굳이 따로 만들어서 사용하는 이유는 무엇일까? 가장 중요한 이유는 데이터 엑세스 로직을 담은 코드를 성격이 다른 코드에서 분리해놓기 위해서다. 또한 분리된 DAO는 전략 패턴을 적용해 구현 방법을 변경해서 사용할 수 있게 만들기 위해서이기도 하다. DAO를 사용하는 쪽에선 DAO가 내부에서 어떤 데이터 액세스 기술을 사용하는지 신경 쓰지 않아도 된다. 그런 면에서 DAO는 인터페이스를 사용해 구체적은 클래스 정보와 구현 방법을 감추고 DI를 통해 제공되도록 만드는 것이 바람직하다.

```JAVA
public interface UserDao {
	public void add(User user) throws PersistentException;	// JAP
	public void add(User user) throws HibernateException;	// Hibernate
	public void add(User user) throws JdoException;		// JDO
}
```
위 소스처럼 DAO 메서드를 추상화했지만 구현 기술마다 던지는 예외가 다르기 때문에 문제가 발생한다.

```JAVA
public interface UserDao {
	public void add(User user) throws Exception;	// JAP, Hibernate, JDO
}
```
위 소스는 한번에 처리할 수 있지만 무책임하다.
그러나 걱정할 것 없다. JDBC보다 늦게 등장한 JDO, Hibernate, JPA등의 기술은 SQLException 같은 체크 예외 대신 런타임 예외를 사용하기 때문에 throws선언해 주지 않아도 된다.

```JAVA
public interface UserDao {
	public void add(User user) ;	// JAP, Hibernate, JDO, JDBC
}
```
결론은 그냥 throws를 하지않아도 다양한 기술을 사용할 수 있지만 data Access관련 예외가 모두 무시할 수 있는 것은 아니다. 예를 들면 PK중복 같은 경우는 JDBC, JPA, Hibernate 모두 다른 예외를 던지기 때문에 어쩔수 없이 클라이언트가 DAO의 기술에 의존적이 될 수밖에 없다.

##### DataAccessException 사용 시 주의사항
DuplicateKeyException의 경우 JPA, Hibernate 등은 해당되지 않고, JDBC를 이용했을 때만 발생한다. 이유는 SQLException에 담긴 DB의 에러코드를 바로 해석하는 JDBC의 경우와 달리 JPA나 하이버네이트 등에서는 각 기술이 재정의한 예외를 가져와 스프링이 최종적으로 DataAccessException으로 변환하는데 DB의 에러코드와 달리 이런 예외들은 세분화되어 있지 않기 때문이다. DataAccessException이 기술에 상관없이 어느정도 추상화된 공통 예외로 변환해주긴 하지만 근본적인 한계 때문에 완벽하다고 기대할 수는 없다. 따라서 사용에 주의를 기울여야 한다. 테스트를 통해 미리 학습 테스트를 만들어서 실제로 전환되는 예외의 종류를 확인해둘 필요가 있다.

### 정리
이 장에서는 엔터프라이즈 애플리케이션에서 사용할 수 있는 바람직한 예외처리 방법은 무엇인지 살펴봤다.
- 예외를 잡아서 아무런 조치를 취하지 않거나 의미없는 throws 선언을 남발하는 것은 위험하다.
- 예외는 복구하거나 예외처리 오브젝트로 의도적으로 전달하거나 적절한 예외로 전환해야 한다.
- 좀 더 의미 있는 예외로 변경하거나, 불필요한 catch/throws를 피하기 위해 런타임 예외로 포장하는 두 가지 방법의 예외 전환이 있다.
- 복구할 수 없는 예외는 가능한 한 빨리 런타임 예외로 전환하는 것이 바람직하다.
- 애플리케이션의 로직을 담기 위한 예외는 체크 예외로 만든다.
- JDBC의 SQLException은 대부분 복구할 수 없는 예외이므로 런타임 예외로 포장해야 한다.
- SQLException의 에러 코드는 DB에 종속되기 떄문에 DB에 독립적인 예외로 전환될 필요가 있다.
- 스프링은 DataAccessException을 통해 DB에 독립적으로 적용 가능한 추상화된 런타임 예외 계층을 제공한다.
- DAO를 데이터 액세스 기술에서 독립시키려면 인터페이스 도입과 런타임 예외 전환, 기술에 독립적인 추상화된 예외로 전환이 필요하다.