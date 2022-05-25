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