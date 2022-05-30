# 2장. 데이터 액세스 기술
대부분의 엔터프라이즈 애플리케이션은 어떤 방법으로든 백엔드 시스템, 특히 데이터베이스와 연결돼서 동작한다. 스프링은 주요 자바 데이터 액세스 기술을 모두 지원한다.

## 2.1 공통 개념

### 2.1.1 DAO 패턴
데이터 액세스 계층은 DAO 패턴이라 불리는 방식으로 분리하는 것이 원칙이다. DAO 패턴은 DTO 또는 도메인 오브젝트만을 사용하는 인터페이스를 통합 데이터 액세스 기술을 외부에 노출하지 않도록 만드는 것이다. 가장 중요한 장점은 DAO를 이용하는 서비스 계층의 코드를 기술이나 환경에 종속되지 않는 순수한 POJO로 개발할 수 있다는 것이다. DAO 인터페이스는 기술과 상관없이 DTO나 도메인 모델만을 사용하기 때문에 언제든지 목 오브젝트 같은 테스트 대역 오브젝트로 대체해서 단위 테스트를 작성할 수 있다.

#### DAO 인터페이스와 DI
인터페이스를 만들 때 습관적으로 DAO 클래스의 모든 public 메서드를 추가하지 않도록 주의하자. DAO를 사용하는 서비스 계층 코드에서 의미 있는 메서드만 인터페이스로 공개해야 한다. DAO 클래스에 DI를 위해 setDataSource()  같은 수정자 메서드를 public 메서드라고 해서 기계적으로 인터페이스에 추가하지 않도록 주의하자.

특정 데이터 액세스 기술에서만 의미 있는 DAO 메서드의 이름은 피하자 예로 JPA의 persist(), merge() 등의 메서드를 JPA로 DAO를 만든다고 이러한 메서드 사용을 피하자. 좀 더 일반적으로 add(), update() 와 같은 이름을 선택하자.


#### 예외처리
데이터 액세스 중에 발생하는 예외는 대부분 복구할 수 없다. 따라서 DAO 밖으로 던져질 때 런타임 예외여야 한다. 또한 DAO 메서드 선언부에 throws SQLException 과 같은 내부 기술을 드러내는 예외를 직접 노출해서는 안된다. throws Exception 과 같은 무책임한 예외도 마찬가지다.

그러나 때로는 의미 있는 DAO가 던지는 예외를 잡아서 비즈니스 로직에서 적용하는 경우가 있다. 중복키 예외나 낙관적인 락킹 등이 대표적인 예다. 이 때문에 스프링은 특정 기술이나 DB의 종류와 상관없이 일관된 의미를 갖는 데이터 예외 추상화를 제공하고, 각 기술과 DB에서 발생하는 예외를 스프링의 데이터 예외로 변환해주는 변환 서비스를 제공한다.

JdbcTemplate와 같은 템플릿/콜백 기능을 사용하면 변환 서비스가 자동으로 적용된다.


### 2.1.2 템플릿과 API
스프링은 DI의 응용 패턴인 템플릿/콜백 패턴을 이용해 이런 판에 박힌 코드를 피하고 꼭 필요한 바뀌는 내용만을 담을 수 있도록 데이터 액세스 기술을 위한 템플릿을 제공한다.
템플릿의 단점은 데이터 액세스 기술의 API를 직접 사용하는 대신 템플릿이 제공하는 API를 이용해야 한다는 점이다. 또 콜백 오브젝트를 익명 내부 클래스로 작성해야 하는 경우에는 코드를 이해하기가 조금 불편할 수 있다. 물론 대부분의 기능은 내장 콜백을 사용하는 편리한 메서드를 이용하면 되기 때문에 큰 문제는 아니다.

데이터 액세스 기술이 제공하는 확장 기법과 AOP 등을 이용해 예외 변환과 트랜잭션 동기화를 제공해줄 수 있기 때문이다.


### 2.1.3 DataSource
JDBC를 통해 DB를 사용하려면 Connection 타입의 DB연결 오브젝트가 필요하다 Connection은 모든 데이터 액세스 기술에서 사용되는 필수 리소스다.
스프링은 성능저하를 방지하기 위해 DataSource를 하나의 독립된 빈으로 등록하도록 강력하게 권장한다.

다중 사용자를 갖는 엔터프라이즈 시스템에서라면 반드시 DB 연결 풀 기능을 지원하는 DataSource를 사용해야 한다.

#### 학습 테스트와 통합 테스트를 위한 DataSource
개발 중 사용하던 테스트용 DataSource를 그대로 운영서버에 적용해서 애를 먹는 경우가 종종 있으니 주의해야 한다.

- SimpleDriverDataSource

스프링이 제공하는 가장 단순한 DataSource 구현 클래스다. getDataSource를 호출할 때마다 매번 새로운 DBConnection를 반환하기 때문에 실환경에선 절대 사용해선 안된다. 테스트용으로 사용하기 바란다.

- SingleConnectionDataSource

하나의 물리적인 DataSource만을 만들어 사용한다. 순차적으로 진행되는 통합테스트에서는 사용이 가능하지만 멀티 스레드 환경에서는 하나의 커넥션을 공유하게 되므로 위험하다.


#### 오픈소스 또는 상용 DB 커넥션 풀
일반적으로 애플리케이션 레벨에서 애플리케이션 전용 DB 풀을 만들어 사용한다. 아래 소개하는 DB 커넥션 풀 모두 스프링의 빈으로 바로 등록해서 사용할 수 있는 수정자 메서드를 가진 클래스가 제공되서 사용하기 편리하다.

- 아파치 Commons DBCP

가장 유명한 오픈소스 DB 커넥션 풀 라이브러리다.

- c3p0 JDBC/DataSource Resource Pool

JDBC 3.0 스펙을 준수하는 Connection  과 Statement 풀을 제공하는 라이브러리다.

- 상용 DB 커넥션 풀


#### JNDI/ WAS DB 풀
서버가 제공하는 DB 풀을 사용해야 하는 경우에는 JNDI를 통해 서버의 DataSource에 접근해야 한다. <jee:jndi-lookup> 태그를 이용하면 JNDI 를 통해 가져온 오브젝트를 스프링의 빈으로 사용할 수 있다.
그런데 위와 같이 JNDI에서 검색해서 가져오는 빈 오브젝트는 서버 밖에서는 제대로 동작하지 않기 때문에 테스트환경에서 활용하기 어렵다. 테스트 환경에서 JNDI를 사용하고 싶다면 JNDI 목 오브젝트를 사용할 수 있다.

```JAVA
public class JNDIExample {
	public static void main (String [] args) throws IllegalStateException, NamingException {
		SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
		SimpleDriverDataSource ds = new SimpleDriverDataSource();	// DB정보 매개변수 입력
		builder.bind("jdbc/DefaultDS", ds);		// JNDI <jee:jndi-lookup id="dataSource" name="jdbc/DefaultDS"> 으로 생성한 빈 name입력
		builder.activate();
	}
}
```


## 2.2 JDBC
JDBC는 자바의 데이터 액세스 기술의 기본이 되는 로우레벨 API다.

스프링 JDBC는 JDBC 개발의 장점과 단순성을 그대로 유지하면서도 기존 JDBC API 사용 방법의 단점을 템플릿/콜백 패턴을 이용해 극복할 수 있게 해주고, 가장 간결한 형태의 API의 사용법을 제공하며, JDBC API에서는 지원되지 않는 편리한 기능을 제공해주기도 한다.

### 2.2.1 스프링 JDBC 기술과 동작원리

#### 스프링의 JDBC 접근 방법

- SimpleJdbcTemplate

JdbcTemplate 과 NamedParameterJdbcTemplate 에서 가장 많이 사용되는 기능을 통합하고 자바 5 이상의 장점을 최대한 활용할 수 있게 만든 것이다. 방대한 템플릿 메서드와 내장형 콜백을 제공하며, JDBC의 모든 기능을 최대한 활용할 수 있는 유연성을 갖고 있다.

- SimpleJdbcInsert, SimpleJdbcCall

이 두 가지 접근 방법은 DB가 제공해주는 메타정보를 활용해서 최소한의 코드만으로 단순한 JDBC 코드를 작성하게 해준다.


#### 스프링 JDBC가 해주는 작업

- Connection 열기와 닫기
	- 스프링 JDBC를 사용하면 코드에서 직접 Connection을 열고 닫는 작업을 할 필요가 없다. 스프링 JDBC가 알아서 처리를 해주고 진행 중 예외가 발생해도 열린 Connection 오브젝트를 닫아준다. 열고 닫는 시점은 스프링 트랜잭션 기능과 맞물려서 결정된다.

- Statement 준비와 닫기
	- SQL정보가 담긴 Statement와 PreparedStatement를 생성하고 필요한 준비 작업을 해주는 것도 대부분 스프링 JDBC의 몫이다.

- Statement 실행
	- 실행 역시 스프링 JDBC의 몫이다.

- ResultSet 루프
	- ResultSet에 담긴 쿼리 실행 결과가 한 건 이상이라면 루프를 돌면서 각각의 로우를 처리해줘야 한다. 이것을 진행하는 것도 스프링 JDBC가 해주는 작업이다.

- 예외처리와 변환
	- 스프링 JDBC의 예외 변환기가 처리해준다. 런타임 예외인 DataAccessException 타입으로 변환해준다.

- 트랜잭션 처리
	- 트랜잭션을 시작한 후에 스프링 JDBC의 작업을 요청하면 진행 중인 트랜잭션에 참여한다. 트랜잭션이 없다면 새로만들어서 참여하고 끝나면 종료한다. 트랜잭션을 단일 리소스를 사용하는 로컬 트랜잭션을 적용할지, 서버가 제공하는 JTA 글로벌 트랜잭션에 참여할 지 등의 문제는 스프링이 알아서 트랜잭션 선언을 이용해 처리하도록 맡기면 된다.

	- 스프링 JDBC가 이런 대부분의 작업을 해주기 때문에 개발자는 데이터 액세스 로직마다 달라지는 부분만 정의해주면 된다.


### 2.2.2 SimpleJdbcTemplate
SimpleJdbcTemplate 이 제공하는 기능은 실행, 조회, 배치의 세 가지 작업으로 구분할 수 있다. 실행은 INSERT, UPDATE 등 DB 데이터 변경이며 조회는 SELECT 를 통한 결과 반환이다. 배치는 하나 이상의 실행 작업을 한 번에 수행해줘야 할 때 사용한다.

#### SimpleJdbcTemplate 생성
DataSource는 보통 빈으로 등록해두므로 SimpleJdbcTemplate이 필요한 DAO에서 DataSource 빈을 DI 받아 SimpleJdbcTemplate을 생성해두고 사용하면 된다. SimpleJdbcTemplate은 멀티스레드 환경에서도 안전하게 공유해서 쓸 수 있기 때문에 DAO의 인스턴스 변수에 저장해두고 사용할 수 있다. 혹은 SimpleJdbcTemplate 자체를 싱글톤으로 등록하고 모든 DAO가 공유해서 사용하도록 만들어도 된다.

#### SQL 파라미터
SimpleJdbcTemplate은 SQL을 작성하는 문구에서 치환자를 통한 동적 바인딩이 가능하다. 또한 순서를 통한 바인딩이 아닌 이름을 통한 바인딩으로 :name 등으로 바인딩을 할 수 있는데 이름 치환자의 장점은 맵이나 오브젝트에 담긴 내용을 키 값이나 프로퍼티 이름을 이용해 바인딩할 수 있다는 것이다.

- Map/MapSqlParameterSource

```JAVA
MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", 1).addValue("name", "spring");
```

- BeanPropertySqlParameterSource

BeanPropertySqlParameterSource 는 맵 대신 도메인 오브젝트나 DTO를 사용하게 해준다. 도메인 오브젝트의 파라미터와 SQL 치환자의 이름만 같게 만들어주면 매우 편리하게 쓸 수 있다.

```JAVA
Member m = new Member("name", "addr");
BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(m);
```

#### SQL 조회 메서드
- queryForObject(String sql, Class<T> requiredType, [SQL파라미터])

쿼리 실행 시 하나의 값을 가져올 때 사용한다. 검색된 값이 없다면 EmptyResultDataAccessException 예외가 발생한다. 예외가 던져지길 원하지 않는다면 DAO에서 예외처리를 해줘야 한다.

- queryForObject(String sql, RowMapper<T> rm, [SQL파라미터])

앞의 매개변수의 클래스 타입과 다르게 다중 조건을 처리할 수 있다. 결과값은 동일하게 하나의 로우만 반환한다.

- <T>List<T> query(String sql, RowMapper<T>rm, [SQL파라미터])

SQL 실행 결과로 돌아온 여러 개의 컬럼을 가진 로우를 RowMapper 콜백을 이용해 도메인 오브젝트나 DTO에 매핑해준다. query()는 여러 개의 로우를 처리할 수 있다.

- Map<String, Object> queryForMap(String sql, [SQL파라미터])

단일 로우 결과를 처리하는 데 사용된다. 다만 queryForObject와 다르게 반환타입이 Map이다. 다건의 로우가 반환되면 예외를 던진다.

- List<Map<String,Object>> queryForList(String sql, [SQL파라미터])

queryForMap()의 다중버전이다.


#### SQL 배치 메서드
update()로 실행하는 SQL 들을 배치모드로 실행하게 해준다. addBatch() 와 executeBatch() 메서드를 이용해 여러 개의 SQL을 한 번에 처리한다. 많은 SQL을 한번에 처리하기 때문에 DB호출이 적어져서 성능 향상에 도움이 된다.

SimpleJdbcTemplate은 내부적으로 스프링의 템플릿/콜백 방식을 사용하지만 직접 콜백 오브젝트를 작성해서 사용하는 것은 RowMapper뿐이다. 그나마도 미리 만들어진 BeanPropertyRowMapper를 사용하면 충분하기 때문에 굳이 콜백 오브젝트를 익명 내부 클래스로 구현할 필요가 없다. 대부분의 메서드는 SimpleJdbcTemplate에 내장된 콜백을 이용하기 때문에 편리하게 사용할 수 있다.


### 2.2.3 SimpleJdbcInsert

#### SImpleJdbcInsert 생성
SImpleJdbcInsert 는 테이블별로 만들어서 사용한다. 따라서 하나의 DAO에 여러개의 SImpleJdbcInsert 를 사용할 수 있다. SImpleJdbcInsert 는 멀티스레드 환경에서 안전하게 공유해서 사용할 수 있다.
SImpleJdbcInsert 를 생성할 때는 DataSource가 필요하다. 생성 후 반드시 어떤 테이블을 적용할 지 초기화를 해줘야 한다.

```JAVA

/**
 SimpleJdbcInsert 활용 예제
*/
SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("member");
Member member = new Member(1, "Spring", 3.5);
insert.execute(new BeanPropertySqlParameterSource(member));
```


### 2.2.4 SimpleJdbcCall
SimpleJdbcCall 은 DB에 생성해둔 저장 프로시저 또는 저장 펑션을 호출할 때 사용한다.

#### SimpleJdbcCall 생성
SimpleJdbcCall 은 dataSource를 이용해 생성한다. 멀티스레드 환경에 안전하게 공유 가능하므로 인스턴스 변수에 저장해두고 공유해서 사용해도 된다. 기본적으로 저장 프로시저나 저장 펑션 중의 하나로 초기화해줘야 한다.

주요 메서드

- SimpleJdbcCallOperations withProcedurName(String ProcedurName)
- SimpleJdbcCallOperations withFunctionName(String functionName)
- SimpleJdbcCallOperations returningResultSet(String parameterName, ParameterizedRowMapper rowMapper)


### 2.2.5 스프링 JDBC DAO
가장 권장하는 DAO 작성 방법은 DAO는 DataSource에만 의존하게 만들고 스프링 JDBC 오브젝트는 코드를 이용해 직접 생성하거나 초기화해서 DAO의 인스턴스 변수에 저장해두고 사용하는 것이다.
모든 JDBC 오브젝트는 한번 만들어지면 반복적으로 사용할 수 있기 떄문에 초기에 만들어 인스턴스 변수에 저장해두는 게 좋다.

스프링에서 중복코드를 제거하는 방법은 두 가지가 있다.
하나는 빈으로 등록하여 주입받는 방법과 공통 DAO를 뽑아서 추상클래스로 만들어주는 방법이 있다.

SimplJdbcTemplate을 지원하는 추상클래스로 JdbcDaoSupport 가 존재한다.

## 2.3 iBatis SqlMaps
iBatis는 자바오브젝트와 SQL문 사이의 자동매핑 기능을 지원하는 ORM 프레임 워크다. 도메인 오브젝트나 DTO 중심으로 개발이 가능하다는 장점이 있다.

### 2.3.1 SqlMapClient 생성
스프링에서는 SqlMapClient를 빈으로 등록해주고 DAO에서 DI 받아 사용해야 하기 때문에 SqlMapClientFactoryBean을 통해 SqlMapClient를 빈으로 등록해줘야 한다.

#### iBatis 설정파일과 매핑파일

- 설정파일 : \<sqlMapConfig> 태그 사용
	- DB커넥션과 트랜잭션 관리를 위한 정보는 설정파일에 넣지 않고 빈으로 등록한 것을 사용하는 게 바람직하다.

- 매핑파일 : xml파일 내에 관련 쿼리를 작성하면 된다.

### 2.3.2 SqlMapClientTemplate
SqlMapClient를 직접 사용하는 대신 스프링이 제공하는 템플릿 오브젝트인 SqlMapClientTemplate를 이용하는 것이 좋다.

예외 변환, 스프링 트랜잭션과 동기화 등이 지원된다.

#### SqlTemplate 기능
- insert
- update
- delete

- 조회
	- 단일로우 조회 : queryForObject
	- 다중로우 조회 : queryForList
	- 다중로우 조회 : queryForMap
	- 다중로우 조회 : queryWithRowHandler

- 콜백
	- SqlMapClientCallback


## 2.4 JPA
JPA는 Java Persistent API 의 약자로 JavaEE와 JavaSE를 위한 영속성 관리와 ORM매핑을 위한 표준 기술이다.

ORM이란 오브젝트와 RDB 사이에 존재하는 개념과 접근 방법, 성격의 차이 때문에 요구되는 불편한 작업을 제거해줘서 자바 개발자가 오브젝트를 가지고 정보를 다루면 ORM 프레임워크가 이를 RDB에 적절한 형태로 변환해주거나 그 반대로 RDB에 저장되어 있는 정보를 자바 오브젝트가 다루기 쉬운 형태로 변환해주는 기술이다.

따라서 ORM을 사용하는 개발자는 모든 데이터를 오브젝트 관점으로만 본다.

현재 가장 많이 사용되는 대표적인 JPA 구현 제품으로는 JBoss의 하이버네이트, 아파치의 OpenJPA, 이클립스의 EclipseLink 등이 있다.

### 2.4.1 EntityManagerFactory 등록
JPA 퍼시스턴스 컨텍스에 접근하고 엔티티 인스턴스를 관리하려면 JPA의 핵심 인터페이스인 EntitiyManager를 구현한 오브젝트가 필요하다. EntityManager는 JPA에서 두 가지 방식으로 관리된다.

1. 애플리케이션 관리 EntityManager

	애플리케이션이 관리하는 EntityManager는 JavaEE와 JavaSE모두 사용할 수 있다.

2. 컨테이너가 관리하는 EntityManager

	컨테이너가 관리하는 EntityManager는 JavaEE 환경과 서버가 필요하다.


#### LocalEntityManagerFactoryBean
LocalEntityManagerFactoryBean은 JPA 스펙의 JavaSE 기동 방식을 이용해 EntityManagerFactory를 생성해준다.

이 빈은 PersistentProvider 자동 감지 기능을 이용해 프로바이더를 찾고 META-INF/persistence.xml에 담긴 퍼시스턴스 유닛의 정보를 활용해서 EntityManagerFactory를 생성한다.

이 방식의 가장 큰 단점은 DataSource를 사용할 수 없다는 것이다.
이러한 제약 때문에 굳이 사용하려고 한다면 스프링 기반의 독립형 애플리케이션이나 통합테스트 정도에서 사용할 수 있다. 이런 방법이 가능하다는 정도만 인지하고 실전에서 사용하지 않는 게 좋다.

#### JavaEE 5 서버가 제공하는 EntityManagerFactory
JPA는 JavaSE 환경보다는 JavaEE에서 서버가 제공하는 JPA 프로바이더를 통해 사용하는 것이 일반적이다.

스프링에서는 DAO에서 사용할 수 있도록 EntityManagerFactory를 다음과 같이 JNDI 검색을 통한 빈 등록 기능을 이용해 넣으면된다.
```
<jee:jndi-lookup id="emf" jndi-name="persistence/myPersistenceUnit" /> 
```
이 방법의 장점은 기존에 JavaEE 서버에서 사용되도록 개발된 JPA 모듈을 그대로 활용할 수 있다는 것이다.

#### LocalContainerEntityManagerFactoryBean
LocalContainerEntityManagerFactoryBean은 스프링이 직접 제공하는 컨테이너 관리 EntityManager를 위한 EntityManagerFactory를 만들어준다. 이 방법을 이용하면 JavaEE를 서버에 배치하지 않아도 컨테이너에서 동작하는 JPA의 기능을 활용할 수 있을 뿐 아니라 스프링이 제공하는 일관성 있는 데이터 액세스 기술의 접근 방법을 적용할 수 있고 스프링의 JPA 확장 기능도 활용할 수 있다.

LocalContainerEntityManagerFactoryBean에는 dataSource 외에도 다음과 같은 프로퍼티를 추가할 수 있다.

- persistenceUnitName
	- 하나 이상의 퍼시스턴스 유닛이 정의될 수 있다.

- persistenceXmlLocation
	- LocalContainerEntityManagerFactoryBean은 디폴트 위치인 META-INF에서 persistence.xml 파일을 찾는다. 따라서 파일 위치나 이름을 지정하지 않아도 된다.
	- 일부 WAS에서 디폴트 위치 파일을 자동으로 인식해서 서버가 관리하는 EntityManagerFactory를 만들어서 스프링이 제공하는 빈과 충돌이 발생할 수 있어 주의해야 한다.

- jpaProperties, jpaPropetyMap
	- EntityManagerFactory를 위한 프로퍼티를 지정할 때 사용한다.

- jpaVendorAdapter
	- jpaVendorAdapter를 이용하면 showSql 옵션 뿐 아니라 매핑 DDL을 자동 생성해주는 generateDdl과 database, DB 플랫폼 정보 등을 지정할 수 있다.
	- 스프링은 현재 EclipseLink, Hibernate, OpenJpa, TopLink 의 네 가지 벤더를 위한 JpaVendorAdapter를 지원한다.

- loadtimeWeaver
	- JPA는 POJO 클래스를 ORM의 엔티티로 사용한다. 단순한 자바 코드로 만들어진 엔티티 클래스의 바이트코드를 직접 조작해서 확장된 기능을 추가하는 방식을 이용한다. 이를 통해 엔티티 오브젝트 사이에 지연로딩이 가능하고, 엔티티 값의 변화를 추적할 수 있으며, 최적화와 그룹 페칭(fetching) 등의 고급 기능을 적용할 수 있다.
	- 런타임 시에 클래스를 로딩하면서 기능을 추가하는 것을 로드타임 위빙이라고 하며 이런 기능을 가진 클래스를 로드타임 위버라고 한다.
	- 스프링에서는 JPA 벤더에 종속되지 않는 방법을 통해 로드타임 위빙 기능을 적용할 수 있다.

#### 트랜잭션 매니저
컨테이너가 관리하는 EntityManager 방식에서는 컨테이너가 제공하는 트랜잭션 매니저가 반드시 필요하다. 스프링의 LocalContainerEntityManagerFactoryBean 빈을 통해 컨테이너 관리 EntityManager를 사용할 때는 트랜잭션 매니저를 추가해야 한다.

@Transactional이나 트랜잭션 AOP를 이용해서 트랜잭션 경계설정을 해주면 자동으로 JPA 트랜잭션을 시작하고 커밋하도록 만들 수 있다.

### 2.4.2 EntityManager와 JpaTemplate
JPA DAO에서 EntityManager를 사용하는 네 가지 방법

#### JpaTemplate
JdbcTemplate이나 SqlMapClientTemplate과 동일하게 템플릿 방식으로 JPA 코드를 작성할 수 있게 해준다.

JpaDaoSupport 클래스를 상속해서 DAO를 만들면 아래 코드를 생략할 수 있다.

JpaTemplate을 사용할 때는 기본적으로 JpaCallback 인터페이스의 doInJpa() 메서드에 필요한 작업을 넣는다.

```java
/**
@Description : JpaTemplate을 사용하는 DAO
*/
private JpaTemplate jpaTemplate;

@Autowired
public void init (EntityManagerFactory emf) {
	jpaTemplate = new JpaTemplate(emf);
}
```

#### 애플리케이션 관리 EntityManager와 @PersistenceUnit
EntityManager를 사용하는 두 번째 방법은 컨테이너 대신 애플리케이션 코드가 관리하는 EntityManager를 이용하는 것이다.

```java
// DI를 통한 EntityManagerFactory 의존성 주입
@Autowired
EntityManagerFactory entityManagerFactory;

/** 
JPA 표준 스펙에 나온 방식 
javax.persistence
*/
@PersistenceUnit
EntityManagerFactory emf;

EntityManager em = entityManagerFactory.createEntityManager();

em.getTransaction().begin();
... // JPA 코드 생략
em.getTransaction().commit();
```

#### 컨테이너 관리 EntityManager 와 @PersistenceContext
EntityManager를 사용해 JPA 코드를 작성하는 가장 대표적인 방법은 컨테이너가 제공하는 EntityManager를 직접 제공받아서 사용하는 것이다.

DAO가 컨테이너로부터 EntityManager를 직접 주입받으려면 JPA의 @PersistenceContext 어노테이션을 사용해야 한다.

```java
@PersistenceContext 
EntityManager em;

public void addMember(Member member) {
	em.persist(member);
}
```

EntityManager는 그 자체로 멀티스레드에서 공유해서 사용할 수 없다. 사용자 요청에 따라 만들어지는 스레드별로 독립적인 EntityManager가 만들어져 사용돼야 한다. 그런데 지금 코드를 보면 인스턴스 변수에 한 번 DI받아놓고 같은 오브젝트를 여러 스레드가 동시에 사용한다.
이유는 @persistenceContext로 주입받은 EntityManager는 실제 EntityManager가 아니라 현재 진행 중인 트랜잭션에 연결되는 퍼시스턴스 컨텍스트를 갖는 일종의 프록시이기 때문이다.

#### @PersistenceContext와 확장된 퍼시스턴스 컨텍스트
위의 방법과 동일하게 @PersistenceContext를 통해 EntityManager를 주입받는다.
다만 앞에 기본 옵션인 PersistenceContextType.TRANSACTION이 아닌
PersistenceContextType.EXTENDED로 생성한다.
이렇게 하면 트랜잭션 스코프 대신 확장된 스코프를갖는 EntityManager가 만들어진다.

JPA에서 이 확장된 퍼시스턴스 컨텍스트는 상태유지 세션빈에 바인딩되는 것을 말한다.

이 옵션을 적용한 EntityManager DI하면 멀티스레드에서 안전한 프록시 오브젝트가 아닌 멀티스레드에서 안전하지 않은 실제 EntityManager 상태다.

확장된 퍼시스턴스 컨텍스트는 싱글톤 빈에 적용하면 안 된다.

#### JPA 예외 변환
JpaTemplate이 JPA API보다 나은점은 예외를 DataAccessException의 예외로 변환해준다는 점이다.

#### JPA 에외 변환 AOP
JpaTemplate을 사용하지 않고 JPA API를 사용하여 예외를 변환시킬 수도 있는데 스프링의 AOP를 이용하면 된다.
이 방법을 이용하려면 다음 두 가지 작업이 필요하다.

##### @Repository
예외 변환이 필요한 DAO 클래스에 @Repository 스테레오 타입 어노테이션을 부여한다. 이 어노테이션이 붙은 클래스의 메서드는 AOP를 이용한 예외 변환 기능이 부가될 빈으로 선정된다.

##### PersistenceException TranslationPostProcessor
@Repository 어노테이션이 붙은 빈을 찾아서 예외 변환 기능을 가진 AOP 어드바이스를 적용해주는 후처리기가 필요하다.
간단히 PersistenceExceptionTranslationPostProcessor를 빈으로 등록해주기만 하면 된다.

하지만 이렇게 변환을 했다고 하더라도 JpaTemplate처럼 디테일한 예외로 변환시켜주진 못한다.

## 2.5 하이버네이트
하이버네이트는 가장 크게 성공한 오픈소스 ORM 프레임워크다. 하이버네이트는 그 자체로 독립적인 API와 기능을 가진 ORM 제품이면서 동시에 JPA의 핵심 구현 제품이기도 하다.

### 2.5.1 SessionFactory 등록
JPA의 EntityManagerFactory 처럼 핵심 엔진 역할을 하는 SessionFactory가 있다. SessionFactory는 엔티티 매핑정보와 설정 프로퍼티 등을 이용해 초기화한 뒤에 애플리케이션에서 사용해야 한다.

#### LocalSessionFactoryBean
LocalSessionFactoryBean은 빈으로 등록된 DataSource를 이용해서 스프링이 제공하는 트랜잭션 매니저와 연동할 수 있도록 설정된 SessionFactory를 만들어주는 팩토리 빈이다.

```java
@Bean
public LocalSessionFactoryBean sessionFactory() {
    LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
    localSessionFactoryBean.setDataSource(dataSource());
    localSessionFactoryBean.setPackagesToScan(packagesToScan());
    localSessionFactoryBean.setHibernateProperties(additionalProperties());
    return localSessionFactoryBean;
}
```

### AnnotationSessionFactoryBean
기본적으로 JPA에 정의된 매핑용 어노테이션을 그대로 사용할 수 있으며, 추가로 하이버네이트가 제공하는 확장 어노테이션을 이용하면 하이버네이트의 고급 매핑정보를 어노테이션을 이용해 정의해줄 수 있다.

### 트랜잭션 매니저
SessionFacotry를 통해 하이버네이트 DAO를 만들 때 스프링의 트랜잭션 경계설정 기능을 적용하려면 스프링이 제공하는 트랜잭션 매니저를 이용해야 한다.

#### HibernateTransactionManager
단일 DB를 사용하고 JTA를 이용할 필요가 없다면 간단히 HibernateTransactionManager 빈을 추가해주면 된다. HibernateTransactionManager 를 사용하면 하이버네이트 DAO와 JDBC DAO를 같은 트랜잭션으로 묶어서 동작시킬 수 있다.

#### JtaTransactionManager
여러 개의 DB에 대한 작업을 하나의 트랜잭션으로 묶으려면 JTA를 통해 서버가 제공하는 글로벌 트랜잭션 기능을 이용해야 한다. 스프링에서 JtaTransactionManager를 빈으로 등록해주면 된다.

### 2.5.2 Session과 HibernateTemplate
Session은 하이버네이트의 핵심 API다. 보통 트랜잭션과 동일한 스코프를 갖고 있다. 하이버네이트 DAO는 스프링이 관리하는 트랜잭션과 동기화된 Session을 가져와 사용한다.

### HibernateTemplate
스프링 템플릿/콜백 패턴이 적용된 HibernateTemplate을 이용하는 방법이다.
하이버네이트 3.0.2부터 트랜잭션과 동기화된 Session을 손쉽게 가져올 수 있는 기능을 제공하기 시작했다. 스프링에서는 하이버네이트가 제공하는 Session 관리 기능의 확장 포인트를 이용해서 스프링의 트랜잭션 관리 기능을 하이버네이트의 기능과 맞물려 동작하게 한다.

데이터 액세스 기술의 템플릿 스타일을 특별히 선호하는 경우가 아니라면 HibernateTemplate의 사용은 그다지 권장하지 않는다.

### SessionFactory.getCurrentSession()
하이버네이트 SessionFactory의 getCurrentSession() 메서드는 현재 트랜잭션에 연결되어 있는 하이버네이트 Session을 돌려준다. 이를 이용하면 스프링의 트랜잭션 매니저 또는 JTA의 트랜잭션에 연동되어 만들어지는 Session을 가져올 수 있다.

하이버네이트 API는 런타임 예외를 던지기 때문에 try/catch 나 throws 선언은 필요없다.

## 2.6 트랜잭션
EJB (Enterprise Java Bean) 가 제공했던 엔터프라이즈 서비스에서 가장 매력적인 것은 바로 선언적 트랜잭션이다. 코드 내에서 직접 트랜잭션을 관리하고 트랜잭션 정보를 파라미터로 넘겨서 사용하지 않아도 된다. 트랜잭션이 시작되고 종료되는 지점은 별도의 설정을 통해 결정된다. 또 작은 단위로 분리되어 있는 데이터 액세스 로직과 비즈니스 로직 컴포넌트와 메서드를 조합해서 하나의 트랜잭션에서 동작하게 만드는 것도 간단하다. 의미있는 단위로 만들어진 오브젝트와 메서드를 적절한 순서대로 조합해서 호출하기만 하면 코드의 중복 없이 다양한 트랜잭션 안에서 동작하는 코드를 만들 수 있다.

선언적 트랜잭션 경계설정을 사용하면 결국 코드의 중복을 제거하고 작은 단위의 컴포넌트로 쪼개서 개발한 후에 이를 조합해서 쓸 수 있다. 다양한 로직이 복잡하게 결합돼서 하나의 업무를 처리하는 엔터프라이즈 시스템의 요구조건을 가장 잘 충족시켜줄 기술이다.

스프링의 트랜잭션은 매우 매력적인 기능이다. JavaEE 서버에서 동작하는 엔티티빈이나 JPA로 만든 컴포넌트에 JTA를 이용한 글로벌 트랜잭션을 적용해야만 가능했던 고급 기능을 간단한 톰캣 서버에서 동작하는 가벼운 애플리케이션에도 적용해주기 때문이다.

### 2.6.1 트랜잭션 추상화와 동기화
스프링이 제공하는 트랜잭션 서비스는 트랜잭션 추상화와 트랜잭션 동기화 두 가지로 생각해볼 수 있다. 트랜잭션 서비스는 데이터 액세스 기술은 변하지 않더라도 환경에 따라 바뀔 수 있기 때문이다. 또 스프링 없이 선언적 트랜잭션을 이용하려면 특정 기술과 서버 플랫폼, 특정 트랜잭션 서비스에 종속될 수밖에 없다.

스프링은 데이터 액세스 기술과 트랜잭션 서비스 사이의 종속성을 제거하고 스프링이 제공하는 트랜잭션 추상 계층을 이용해서 트랜잭션 기능을 활용하도록 만들어준다.
이를 통해 트랜잭션을 사용하는 코드는 그대로 유지할 수 있는 유연성을 얻을 수 있다.

#### PlatformTransactionManager
스프링의 트랜잭션 추상화의 핵심 인터페이스는 PlatformTransactionManager다.
다음과 같이 세 개의 메서드를 가지고 있다.

- getTransaction(TransactionDefinition definition) throws TransactionException
- commit(TransactionStatus status) throws TransactionException
- rollback(TransactionStatus status) throws TransactionException

PlatformTransactionManager 는 트랜잭션 경계를 지정하는 데 사용한다.
TransactionDefinition은 트랜잭션의 네 가지 속성을 나타내는 인터페이스다.
TransactionStatus 는 현재 참여하고 있는 트랜잭션 ID와 구분정보를 담고있다.

#### 트랜잭션 매니저의 종류
##### - DataSourceTransactionManager
Connection의 트랜잭션 API를 이용해서 트랜잭션을 관리해주는 트랜잭션 매니저다.
DataSourceTransactionManager 가 사용할 DataSource는 getConnection()이 호출될 때마다 매번 새로운 Connection을 돌려줘야 한다.

애플리케이션 코드에서 트랜잭션 매니저가 관리하는 Connections을 가져오려면 스프링의 DataSourceUtils 클래스의 getConnection(DataSource) 를 사용해야 한다.

##### - JpaTransactionManager
JPA를 이용하는 DAO는 JpaTransactionManager를 사용한다. 물론 JTA로 트랜잭션 서비스를 이용하는 경우에는 JpaTransactionManager가 필요없다.
사용 시 LocalContainerEntityManagerFactoryBean 을 프로퍼티로 등록해줘야 한다.

##### - HibernateTransactionManager
하이버네이트 DAO에는 HibernateTransactionManager를 사용한다. 사용 시 SessionFactory 타입의 빈을 프로퍼티로 넣어주면 된다.

##### - JmsTransactionManager, CciTransactionManager
스프링은 DB뿐 아니라 트랜잭션이 지원되는 JMS와 CCI를 위해서도 트랜잭션 매니저를 제공한다.

##### - JtaTransactionManager
하나 이상의 DB 또는 트랜잭션 리소스가 참여하는 글로벌 트랜잭션을 이용하려면 JTA를 사용해야 한다. JTA는 여러 개의 트랜잭션 리소스에 대한 작업을 하나의 트랜잭션으로 묶을 수 있다.

JTA 트랜잭션을 이용하려면 트랜잭션 서비스를 제공하는 WAS를 이용하거나 독립 JTA 서비스를 제공해주는 프레임워크를 사용해야 한다.

### 2.6.2 트랜잭션 경계설정 전략
트랜잭션의 시작과 종료과 되는 경계는 보통 서비스 계층 오브젝트의 메서드다. 트랜잭션 경계를 설정하는 방법은 코드에 의한 프로그램적인 방법과, AOP를 이용한 선언적인 방법으로 구분할 수 있다.

#### 코드에 의한 트랜잭션 경계설정
스프링의 트랜잭션 매니저는 모두 PlatformTransactionManager를 구현하고 있다.  따라서 이 트랜잭션 매니저 빈을 가져올 수 있다면 트랜잭션 매니저의 종류에 상관없이 동일한 방식으로 트랜잭션을 제어하는 코드를 만들 수 있다.

롤백처리를 해줘야 하기 때문에 try/catch 를 사용해줘야하지만 PlatformTransactionManager를 직접 사용하는 대신 템플릿/콜백 방식의 TransactionTemplate을 이용하면 편리하다.

코드에 의한 트랜잭션 경계설정은 실제로는 많이 사용하지 않는다. 대개는 선언적 트랜잭션 방식으로 충분하기 때문이다. 반면 테스트코드에서 의도적으로 트랜잭션을 만들고 종료시킬 때 유용하다.


#### 선언적 트랜잭션 경계설정
선언적 트랜잭션 경계설정은 트랜잭션 프록시 빈 덕분에 가능하다. 트랜잭션은 대부분 성격이 비슷하기 때문에 적용 대상마다 선언해주기보다는 일괄적으로 선언하는 것이 편리하다. 간단한 설정으로 특정 부가기능을 임의의 타깃 오브젝트에 부여해줄 수 있는 프록시 AOP를 주로 활용한다. AOP 설정방법은 크게 두 가지로 나누어진다.

##### - aop와 tx 네임스페이스
스프링은 AOP 기능과 트랜잭션 설정을 위해 편리하게 사용할 수 있는 전용 태그를 제공한다. 트랜잭션 경계설정이라는 부가기능을 AOP를 이용해 빈에게 적용하려면 두 가지 정보가 필요하다. AOP용어로 어드바이스와 포인트컷이 필요하다.

어드바이스는 어떤 부가기능을 사용할지 결정하는 것이고, 포인트컷은 선정 대상을 결정하는 것이다.

##### - @Transactional
트랜잭션 AOP를 적용하는 두 번째 방법은 @Transactional 어노테이션을 이용하는 것이다. 이 접근 방법은 명시적으로 포인트컷과 어드바이스를 정의하지 않는다. 대신 트랜잭션이 적용될 타깃 인터페이스나 클래스, 메서드 등에 @Transactional 어노테이션을 부여해서 트랜잭션 대상으로 지정하고 트랜잭션의 속성을 제공한다.

메서드에 @Transactional이 있으면 클래스 레벨의 @Transactional 선언보다 우선 적용된다. 클래스의 @Transactional은 인터페이스의 @Transactional보다 우선한다. 적용순서는 클래스메서드 -> 클래스 -> 인터페이스 메서드 -> 인터페이스 순이다.

#### 프록시 모드 : 인터페이스와 클래스
스프링의 AOP는 기본적으로 다이나믹 프록시 기법을 이용해 동작한다. 하지만 특별한 경우에 인터페이스를 구현하지 않은 클래스에 트랜잭션을 적용해야 할 수 있다. 이때는 스프링이 지원하는 클래스 프록시 모드를 사용하면 된다.

##### aop/tx 스키마 태그의 클래스 프록시 설정
\<aop:config> 태그의 proxy-target-class 속성을 true바꿔주면 된다. 이때 포인트컷의 선정 대상도 클래스여야 한다.

##### @Transactional의 클래스 프록시 설정
\<tx:annotation-dirver proxy-target-class="true"/> 로 속성값을 변경해주면 된다.

클래스 프록시 적용 시 주의사항
- @Transactional은 클래스에 부여해야 한다.
	- 속성값을 주었을 때부터는 인터페이스에 붙은 @Transactional 어노테이션이 구현 클래스로 그 정보가 전달되지 않는다.

- 클래스 프록시의 제약사항을 알아야 한다.
	- 클래스 프록시는 final 클래스에는 적용할 수 없다. 클래스 프록시는 타깃 클래스를 상속해서 프록시를 만드는 방법을 사용하기 때문에 상속이 불가능한 final 클래스에는 적용되지 않는다.
	- 클래스 프록시를 적용하면 클래스 생성자가 두번 호출되는데 생성자 메서드에서 중복 호출 시 문제가 될 만한 작업에는 주의를 해야한다.

- 불필요한 메서드에 트랜잭션이 적용될 수 있다.
	- 클래스 프록시 방식을 사용하면 클래스의 모든 public 메서드에 트랜잭션이 적용된다. 인터페이스를 이용하는 경우에는 인터페이스에 정의된 메서드로 트랜잭션 적용이 제한되지만 클래스에는 그런 구분을 둘 수 없다.
	- 수정자 같은 클라이언트가 사용하지는 않지만 public 으로 정의된 메서드에도 트랜잭션이 적용되는 문제가 발생한다.
	- 실제 문제는 없지만 트랜잭션이 시작되고 종료됨에 따라 시간과 비용이 낭비된다.

클래스 프록시는 코드를 함부로 손댈 수 없는 레거시 코드나, 여타 제한 때문에 인터페이스를 사용하지 못했을 경우에만 사용해야 한다.

#### AOP 방식 : 프록시와 AspectJ
스프링의 프록시 AOP 대신 AOP 전용 프레임워크인 AspectJ의 AOP를 사용할 수 있다. AspectJ AOP는 스프링과 달리 프록시를 타깃 오브젝트 앞에 두지 않는다. 대신 타깃 오브젝트 자체를 조작해서 부가기능을 넣는 방식이다.

기본적인 스프링 AOP 동작방식은 클라이언트가 타깃오브젝트를 호출하는 과정에서 프록시 객체가 생성되어 부가기능을 타깃오브젝트에 주입하는 방법이다. 그렇기 때문에 타깃오브젝트에서 자기함수를 재호출하는 경우 스프링의 AOP기능이 적용되지 않는다.

해결방법으로는 두 가지 방법을 고려해볼 수 있다.
- AopContext.currentProxy()
	- 프록시 설정에서 현재 진행중인 프록시를 노출하도록 설정해두면 스프링 API를 이용해서 현재 진행중인 프록시를 가져올 수 있다.
	- 단순하고 효과적이지만 사용을 권장할 수는 없다. 스프링 API가 비즈니스 로직을 가진 POJO 클래스 코드에 등장한다는 문제와 더불어 프록시를 통하지 않고는 아예 동작하지 않는 한심한 코드가 되기 때문이다.

- AspectJ AOP
	- AspectJ는 프록시 대신 클래스 바이트코드를 직접 변경해서 부가기능을 추가하기 때문에 타깃 오브젝트의 자기 호출 중에도 트랜잭션 부가기능이 잘 적용된다.

### 2.6.3 트랜잭션 속성
모든 트랜잭션이 같은 방식으로 동작하는 건 아니다. 전체가 같이 실패하거나 성공하는 하나의 작업으로 묶인다는 점은 동일하지만 세밀히 따져보면 몇 가지 차이점이 있다. 

트랜잭션의 경계를 설정할 때 네 가지 트랜잭션 속성이 있으며 선언적 트랜잭션에서는 롤백과 커밋의 기준을 변경하기 위해 두 가지 추가 속성을 지정할 수 있다. 총 6개의 속성을 갖고 있는 셈이다.

#### 트랜잭션 전파 : propagation
트랜잭션을 시작하거나 기존 트랜잭션에 참여하는 방법을 결정하는 속성이다. 트랜잭션 경계의 시작 지점에서 트랜잭션 전파 속성을 참조해서 해당 범위의 트랜잭션을 어떤식으로 진행시킬 지 결정할 수 있다.

스프링이 지원하는 트랜잭션 전파 속성은 여섯 가지가 있다.
##### - REQUIRED
미리 시작된 트랜잭션이 있으면 참여하고 없으면 새로 시작한다. 하나의 트랜잭션이 시작된 후에 다른 트랜잭션 경계가 설정된 메서드를 호출하면 자연스럽게 같은 트랜잭션으로 묶인다.

##### - SUPPORTS
이미 시작된 트랜잭션이 있으면 참여하고 없으면 트랜잭션 없이 진행한다.

##### - MANDATORY
이미 시작된 트랜잭션이 있으면 참여한다. 없다면 예외를 발생시킨다. 혼자서는 독립적으로 트랜잭션을 진행하면 안되는 경우에 사용한다.

##### - REQUIRES_NEW
항상 새로운 트랜잭션을 시작한다. 이미 진행중인 트랜잭션이 있으면 트랜잭션을 잠시 보류시킨다.

##### - NOT_SUPPORTED
트랜잭션을 사용하지 않게 한다. 이미 진행중인 트랜잭션이 있다면 보류시킨다.

##### - NEVER
트랜잭션을 사용하지 않도록 강제한다. 이미 진행중인 트랜잭션도 존재하면 안된다.

##### - NESTED
이미 진행중인 트랜잭션이 있다면 중첩 트랜잭션을 시작한다. 중첩 트랜잭션은 트랜잭션 안에 다른 트랜잭션을 만드는 것이다.

다른점은 부모의 롤백과 커밋에는 영향을 받지만 자식 트랜잭션의 롤백과 커밋에는 부모 트랜잭션에게 영향을 주지 않는다.

#### 트랜잭션 격리수준 : isolation
동시에 여러 트랜잭션이 진행될 때에 트랜잭션의 작업 결과를 여타 트랜잭션에게 어떻게 노출할 것인지를 결정하는 기준이다. 스프링은 다음 다섯 가지 격리수준 속성을 지원한다.

##### - DEFAULT
사용하는 데이터 액세스 기술 또는 DB 드라이버의 디폴트 설정을 따른다.

##### - READ_UNCOMMITTED
가장 낮은 격리 수준이다. 하나의 트랜잭션이 커밋되기 전에 그 변화가 다른 트랜잭션에 그대로 노출되는 문제가 있다. 하지만 가장 빠르기 때문에 데이터의 일관성이 조금 떨어지더라도 성능을 극대화할 때 사용한다.

##### - READ_COMMITTED
실제로 가장 많이 사용되는 격리수준이다. 다른 트랜잭션이 커밋하지 않은 정보는 읽을 수 없다. 대신 하나의 트랜잭션이 읽은 로우를 다른 트랜잭션이 수정할 수 있다.

##### - REPEATABLE_READ
하나의 트랜잭션이 읽은 로우를 다른 트랜잭션이 수정하는 것을 막아준다.

##### - SERIALIZABLE
가장 강력한 트랜잭션 격리수준이다. 트랜잭션을 순차적으로 진행시켜주기 때문에 여러 트랜잭션이 동시에 같은 테이블의 정보를 액세스하지 못한다.


#### 트랜잭션 제한시간 : timeout
트랜잭션에 제한시간을 지정할 수 있다. 값은 초 단위로 지정한다. @Transactional에서는 timeout 엘리먼트로 지정할 수 있다.

#### 읽기전용 트랜잭션 : read-only, readOnly
트랜잭션을 읽기전용으로 설정할 수 있다. 성능을 최적화하기 위해 사용할 수도 있고 특정 트랜잭션 작업 안에서 쓰기 작업이 일어나는 것을 의도적으로 방지하기 위해 사용된다. 이 옵션을 적용 후 INSERT, UPDATE, DELETE 같은 쓰기 작업이 진행되면 예외가 발생한다.

#### 트랜잭션 롤백 예외 : rollback-for, rollbackFor, rollbackForClassName
스프링에서는 데이터 액세스 기술의 예외는 런타임 예외로 전환돼서 던져지므로 런타임 예외만 롤백 대상으로 삼은 것이다. 하지만 체크 예외지만 롤백 대상으로 삼아야 하는 것이 있다면 rollback-for, rollbackFor, rollbackForClassName 를 이용해서 예외를 지정하면 된다.

#### 트랜잭션 커밋 예외 : no-rollback-for, noRollbackFor, noRollbackForClassName
기본적으로 롤백 대상인 런타임 예외를 트랜잭션 커밋 대상으로 지정해준다.


### 2.6.4 데이터 액세스 기술 트랜잭션의 통합
스프링은 두 개 이상의 데이터 액세스 기술로 만든 DAO를 (JPA, myBatis 등) 하나의 트랜잭션으로 묶어서 사용하는 방법을 제공한다. 물론 이때도 DB당 트랜잭션 매니저는 하나만 사용한다는 원칙은 바뀌지 않는다.

#### 트랜잭션 매니저별 조합 가능 기술
##### - DataSourceTransactionManager
DataSourceTransactionManager를 트랜잭션 매니저로 등록하면 JDBC와 iBatis 두 가지 기술을 함께 사용할 수 있다. 트랜잭션을 통합하려면 동일한 DataSource를 사용해야 한다는 점을 잊지 말자.

DataSourceTransactionManager는 DataSource로부터 Connection 정보를 가져와 같은 DataSource를 사용하는 JDBC DAO 와 iBatis DAO 작업에 트랜잭션 동기화 기능을 제공한다.

##### - JpaTransactionManager
JPA의 트랜잭션은 JPA API를 이용해 처리된다. 스프링에서는 JPA의 EntityManagerFactory가 스프링의 빈으로 등록된 DataSource를 사용할 수 있다.
같은 DataSource를 JDBC나 iBatis에서 공유하게 해주면 JPA의 트랜잭션을 담당하는 JpaTransactionManager에 의해 세 가지 기술을 이용하는 DAO 작업을 하나의 트랜잭션으로 관리해줄 수 있다.

##### - HibernateTransactionManager
HibernateTransactionManager도 JpaTransactionManager와 동일한 방식을 이용해서 SessionFactory와 같은 DataSource를 공유하는 다른 데이터 액세스 트랜잭션을 공유하게 해준다.

##### - JtaTransactionManager
서비스가 제공하는 트랜잭션 서비스를 JTA를 통해 이용하면 모든 종류의 데이터 액세스 기술의 DAO가 같은 트랜잭션 안에서 동작하게 만들 수 있다. 가장 강력하고 편리한 기능이지만 JTA 서버환경을 구성해야 하고 서버의 트랜잭션 매니저와 XA를 지원하는 특별한 DataSource를 구성하는 등의 부가적인 준비 작업이 필요하다.

#### ORM과 비 ORM DAO를 함께 사용할 때 주의사항
ORM과 비 ORM의 저장시점이 다르기 때문에 주의해서 사용해야 한다.
ORM의 경우 엔티티 매니저나 세션에만 저장해둔다. 이는 지연로딩을 의미한다. 이후 트랜잭션이 종료됨과 동시에 엔티티 매니저나 세션에 데이터 값을 DB에 처리한다.

그렇기에 하나의 트랜잭션 안에서 처리할 때는 이러한 특성을 잘 생각하여 선택하고 사용해야 한다.

가장 단순한 방법은 ORM의 데이터액세스 처리가 끝난 뒤 flush()를 통한 수동처리를 하는 것이다.

#### JTA를 이용한 글로벌/분산 트랜잭션
스프링에서는 서버에 설정해둔 XA DataSource와 트랜잭션 매니저 그리고 UserTransaction 등을 JNDI를 통해 가져와 모든 데이터 액세스 기술에서 사용할 수 있다.

JtaTransactionManager는 다른 트랜잭션 매니저와 다르게 DataSource나 SessionFactory 등의 빈을 참조하지 않는다. 대신 서버에 등록된 트랜잭션 매니저를 가져와 JTA를 이용해서 트랜잭션을 관리해줄 뿐이다.

#### 독립형 JTA 트랜잭션 매니저
JTA는 WAS가 제공하는 서비스를 이용하는 경우가 일반적이지만, 원한다면 서버의 지원 없이도 애플리케이션 안에 JTA 서비스 기능을 내장하는 독립형 JTA 방식으로 이용할 수 있다.

독립형 JTA 트랜잭션 매니저는 ObjectWeb의 JTA 엔진인 JOTM과 Atomikos의 TransactionalEssentials가 대표적이다. 이 두가지 모두 JtaTransactionManager와 결합해서 JTA 트랜잭션 서비스로 사용할 수 있다.

#### WAS 트랜잭션 매니저의 고급 기능 사용하기

##### - WebSphereUowTransactionManager
WebSphereUowTransactionManager 를 JtaTransactionManager 대신 사용하면 IBMWebSphere의 UOWManager를 통해 WebSphere가 제공하는 트랜잭션 서비스의 기능을 최대한 활용할 수 있다. JTA에서 기본적으로 보장되지 않는 트랜잭션 일시중단 기능이 제공되며, 이를 통해 REQUIRES_NEW 같은 트랜잭션 전파 속성을 사용할 수 있다.

##### - WebLogicJtaTransactionManager
WebLogic 서버의 트랜잭션 서비스를 최대한 활용할 수 있게 해준다. 트랜잭션 이름, 트랜잭션별 격리수준 설정, 트랜잭션의 일시중지와 재시작 등을 모두 활용할 수 있다.

##### - OC4JJtaTransactionManager
OCJ4 서버의 트랜잭션 기능에 최적화된 트랜잭션 매니저다. 오라클팀이 만들어서 스프링에 제공한 코드를 바탕으로 만들어졌다.

위 세가지 서버의 경우 JtaTransactionManager를 사용하는 것보다 더 좋다.
\<tx:jta-transaction-manager/> 를 통해 서버에서 자동으로 서버에 맞는 트랜잭션 매니저를 등록해준다.

## 2.7 스프링 3.1의 데이터 액세스 기술

### 2.7.1 persistence.xml 없이 JPA 사용하기
JPA 엔티티 클래스가 담긴 패키지를 LocalContainerEntityManagerFactoryBean 빈의 packagesToScan 프로퍼티에 넣어주면 된다.

### 2.7.2 하이버네이트 4지원
org.springframework.orm.hibernate4 패키지 아래 클래스를 사용해야 한다.

#### LocalSessionFactoryBean
LocalSessionFactoryBean 는 hibernate3 의 LocalSessionFactoryBean와 이름은 같지만 기능은 AnnotationSessionFactoryBean 과 유사하다.

#### LocalSessionFactoryBuilder
LocalSessionFactoryBuilder 는 @Configuration 클래스에서 세션 팩토리 빈을 등록할 때 편리하게 사용할 수 있도록 만들어진 빌더 클래스다.

### 2.7.3 @EnableTransactionManager
@EnableTransactionManager 는 XML의 \<tx:annotation-driver />과 동일한 컨테이너 인프라 빈을 등록해주는 자바 코드 설정용 어노테이션이다. @Transactional 어노테이션을 이용한 트랜잭션 설정을 가능하게 해준다.

트랜잭션 AOP 관련 인프라 빈들은 @EnableTransactionManager로 모두 등록된다.
@EnableTransactionManager 는 PlatformTransactionManager 타입으로 등록된 빈을 찾기 때문에 이름은 신경쓰지 않아도 된다.

트랜잭션 매니저가 두 개 이상 등록되어 있어서 어느 트랜잭션 매니저를 사용할지 @EnableTransactionManager 가 결정할 수 없거나 명시적으로 사용할 트랜잭션 매니저 빈을 설정하고 싶다면 TransactionManagementConfigurer 타입의 트랜잭션 관리 설정자를 이용해야 한다.


### 2.7.4 JdbcTemplate 사용 권장
스프링 3.1부터는 SimpleJdbcTemplate의 모든 기능을 JdbcTemplate과 NamedParameterJdbcTemplate이 제공하게 만들었고, SimpleJdbcTemplate은 더 이상 사용을 권장하지 않도록 @Deprecated 해버렸다.

## 2.8 정리
2장에서는 스프링이 지원하는 데이터 액세스 기술의 종류와 활용 방법을 알아봤다.

- DAO 패턴을 이용하면 데이터 액세스 계층과 서비스 계층을 깔끔하게 분리하고 데이터 액세스 기술을 자유롭게 변경해서 사용할 수 있다.
- 스프링 JDBC는 JDBC DAO 를 템플릿/콜백 방식을 이용해 편리하게 작성할 수 있게 해준다.
- SQL 매핑 기능을 제공하는 iBatis로 DAO를 만들 때도 스프링의 템플릿/콜백 지원 기능을 사용할 수 있다.
- JPA와 하이버네이트를 이용하는 DAO에서는 템플릿/콜백과 자체적인 API를 선택적으로 사용할 수 있다.
- 트랜잭션 경계설정은 XML의 스키마 태그와 어노테이션을 이용해 정의할 수 있다. 또한 트랜잭션 AOP를 적용할 때는 프록시와 AspectJ를 사용할 수 있다.
- 스프링은 하나 이상의 데이터 액세스 기술로 만들어진 DAO를 같은 트랜잭션 안에서 동작하도록 만들어준다. 하나 이상의 DB를 사용할 때는 JTA 지원 기능을 활용해야 한다.