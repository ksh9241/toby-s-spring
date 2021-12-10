# 스프링의 핵심 기술의 응용
스프링의 3대 핵심 기술
1.  IoC/DI
2. 서비스 추상화
3. AOP

스프링이 가장 가치를 두고 적극적으로 활용하려고 하는 것은 결국 자바 언어가 기반을 두고 있는 객체지향 기술이다. 스프링의 모든 기술은 결국 객체지향적인 언어의 장점을 적극적으로 활용해서 코드를 작성하도록 도와주는 것이다. 스프링을 사용하는 개발자도 스프링이 제공하는 세 가지 기술을 필요에 따라 스스로 응용할 수 있어야 한다. 이번 장에서는 스프링의 개발철학과 추구하는 가치, 스프링 사용자에게 요구되는게 무엇인지 살펴볼 것이다.

### SQL과 DAO의 분리
데이터를 가져오고 인터페이스 역할을 하는 것이 DAO다. 데이터 액세스 로직은 바뀌지 않더라도 DB의 테이블, 필드 이름과 SQL 문장이 바뀔 수 있다. 테이블이나 필드 이름이 바뀔 수도 있고, 하나의 필드에 담았던 정보가 두 개의 필드로 쪼개져서 들어가거나 그 반대가 될 수도 있다. 어떤 이유든지 SQL 변경이 필요한 상황이 발생하면 SQL을 담고 있는 DAO 코드가 수정될 수밖에 없다. 따라서 SQL을 적절히 분리해 DAO코드와 다른 파일이나 위치에 두고 관리할 수 있다면 좋을 것이다.

### XML 설정을 이용한 분리
가장 손쉽게 생각해볼 수 있는 SQL분리 방법은 SQL을 스프링의 XML 설정파일로 빼내는 것이다. 스프링은 설정을 이용해 빈에 값을 주입해줄 수 있다. SQL은 문자열로 되어 있으니 설정파일에 프로퍼티 값으로 정의해서 DAO에 주입해줄 수 있다. 이렇게 하면 설정파일에 있는 SQL을 코드와는 독립적으로 수정할 수가 있다.

##### 개별 SQL 프로퍼티 방식
```JAVA
<bean id="userDao" class="practice.spring.toby.chapter7.UserDaoJdbc">
	<property name="dataSource" ref="dataSource" />
	<property name="sqlAdd" value="INSERT INTO users(id, name, password, email, user_level, login, recommend) VALUES(?,?,?,?,?,?,?)" />
</bean>

public void add (User user) {
	try {
		jdbcTemplate.update(sqlAdd, user.getId(), user.getName(), user.getPassword(), user.getEmail(), user.getLevel().intValue(), user.getLogin(), user.getRecommend());
	} catch (DuplicateKeyException e) {
		throw new DuplicateUserIdException(e);
	}
}

// 수정자 DI
public void setSqlAdd(String sqlAdd) {
	this.sqlAdd = sqlAdd;
}
```

##### SQL 맵 프로퍼티 방식
개별 프로퍼티 방법은 SQL이 많아질수록 수정자 DI 메서드도 쿼리 개수만큼 추가해줘야 한다. 이러한 귀찮은 부분을 해결하기 위해 Map<String, String> 프로터피 방식으로 처리할 수 있다. 하나의 수정자 DI 메서드만 있으면 되기 때문에 코드가 깔끔해진다. XML 설정에서 프로퍼티가 Map인 타입은 하나 이상의 복잡한 정보를 담고있기 때문에 value 애트리뷰트로는 정의해줄 수가 없다. 이때는 스프링이 제공하는 <map> 태그를 사용해야 한다.

```JAVA
<bean id="userDao" class="practice.spring.toby.chapter7.UserDaoJdbc">
	<property name="dataSource" ref="dataSource" />
	<property name="sqlMap">
		<map>
			<entry key="add" value="INSERT INTO users(id, name, password, email, user_level, login, recommend) VALUES(?,?,?,?,?,?,?)" />
			<entry key="get" value="SELECT * FROM users WHERE id = ?" />
			<entry key="getAll" value="SELECT * FROM users" />
			<entry key="deleteAll" value="DELETE FROM users" />
			<entry key="getCount" value="SELECT COUNT(*) FROM users" />
			<entry key="update" value="UPDATE USERS SET name = ?, password = ?, email = ?, user_level = ?, login = ?, recommend = ? WHERE id = ?" />
		</map>
	</property>
</bean>
```

##### SQL 제공 서비스
스프링의 설정파일로부터 생성된 오브젝트와 정보는 애플리케이션을 다시 시작하기 전에는 변경이 매우 어렵다는 점도 문제다. 주입된 SQL 맵 오브젝트를 직접 변경하는 방법을 생각해볼 수는 있겠지만 싱글톤인 DAO의 인스턴스 변수에 접근해서 실시간으로 내용을 수정하는 건 간단한 일이 아니다. 운영중인 애플리케이션에서 참조되는 맵 내용을 수정할 경우 동시성 문제를 일으킬 수도 있다. 이러한 문제를 해결하기 위해서는 DAO가 사용하는 SQL을 독립시킬 필요가 있다.

### 인터페이스의 분리와 자기참조 빈
인터페이스가 하나 있으니 기계적으로 구현 클래스 하나만 만들면 될 거라고 생각하면 오산이다. 어떤 인터페이스는 그 뒤에 숨어 있는 방대한 서브 시스템의 관문에 불과할 수도 있다. 인터페이스로 대표되는 기능을 구현 방법과 확장 가능성에 따라 유연한 방법으로 재구성할 수 있도록 설계할 필요도 있다.

##### XML 파일 매핑
스프링의 XML 설정파일에서 bean 태그 안에 SQL 정보를 넣어놓고 활용하는 건 좋은 방법이 아니다. 그보다는 SQL을 저장해두는 전용 포맷을 가진 독립적인 파일을 이용하는 편이 바람직하다.

##### JAXB ( Java Architecture for XML Binding)
JDK 6 이상이라면 java.xml.bind 패키지 안에 JAXB의 구현 클래스를 찾을 수 있다. DOM과 같은 전통적인 XML API와 비교했을 때 JAXB의 장점은 XML 문서정보를 거의 동일한 구조로 오브젝트로 직접 매핑해준다는 것이다. DOM은 XML 정보를 마치 자바의 리플렉션 API를 사용해서 오브젝트를 조작하는 것처럼 간접적으로 접근해야 하는 불편이 있다. 그에 비해 JAXB는 XML의 정보를 그대로 담고 있는 오브젝트 트리 구조로 만들어주기 때문에 XML 정보를 오브젝트처럼 다룰 수 있어 편리하다. JAXB는 XML 문서의 구조를 정의한 스키마를 이용해서 매핑할 오브젝트의 클래스까지 자동으로 만들어주는 컴파일러도 제공한다.

xjc -p [생성할 클래스 패키지] [변환할 스키마 파일] -d 의 명령어를 사용하여 스키마 파일을 오브젝트로 생성한다. 단, 자바 빈 디렉토리를 환경변수로 설정해서 xjc를 어느 디렉토리에서든 사용할 수 있어야 한다.

자바 빈 스타일의 접근자와 수정자 메서드를 갖는 프로퍼티와 컬렉션으로 정의되어 있기 때문에 어떻게 XML 문서가 오브젝트로 전환될지는 쉽게 파악할 수 있을 것이다.

##### 언마샬링
XML 문서를 읽어서 자바의 오브젝트로 변환하는 것을 JAXB에서는 언마샬링 (unmarshalling)이라고 부른다. 자바 오브젝트를 바이트 스트림으로 바꾸는 걸 직렬화 (serialization) 라고 부르는 것과 비슷하다.

##### XML SQL 서비스
언제 JAXB를 사용해 XML 문서를 가져올 지 생각해봐야 한다. DAO가 SQL을 요청할 때마다 매번 XML 파일을 다시 읽어서 SQL을 찾는 건 너무 비효율적이다. 특별한 이유가 없는 한 XML 파일은 한 번만 읽도록 해야 한다. XML 파일로부터 읽은 내용은 어딘가에 저장해두고 DAO에서 요청이 올 때 사용해야 한다. 일단 간단히 생성자에서 SQL을 읽어와 내부에서 저장해두는 초기 작업을 하자.

##### 빈의 초기화 작업
생성자에서 예외가 발생할 수도 있는 복잡한 작업을 다루는 것은 좋지 않다. 오브젝트를 생성하는 중에 생성자에서 발생하는 예외는 다루기 힘들고, 상속하기 불편하며, 보안에도 문제가 생길 수 있다. 또 다른 문제점은 읽어들일 파일의 위치와 이름이 코드에 고정되어 있다는 점을 들 수 있다. SQL을 담은 XML 파일의 위치와 이름을 코드에 고정하는 건 별로 좋은 생각이 아니다. 코드의 로직과 여타 이유로 바뀔 가능성이 있는 내용은 외부에서 DI로 설정해줄 수 있게 만들어야 한다. 빈 후처리기는 스프링 컨테이너가 빈을 생성한 뒤에 부가적인 작업을 수행할 수 있게 해주는 특별한 기능이다. 프록시 자동생성기 외에도 스프링이 제공하는 여러가지 빈 후처리기가 존재한다. 그 중에서 어노테이션을 이용한 빈 설정을 지원해주는 몇 가지 빈 후처리기가 있다. 이 빈 후처리기는 bean 태그를 이용해서 하나씩 등록할 수도 있지만, 그보다는 context 스키마의 annotation-config 태그를 사용하면 더 편리하다.

##### context:annotation-config 태그
- @PostConstruct : 스프링은 @PostConstruct 어노테이션을 빈 오브젝트의 초기화 메서드를 지정하는 데 사용한다. 초기화 작업을 수행할 메서드에 부여해주면 스프링은 클래스를 빈의 오브젝트로 생성하고 DI 작업을 마친 뒤 @PostConstruct가 붙은 메서드를 자동으로 실행해준다. 생성자와는 달리 프로퍼티까지 모두 준비된 후에 실행된다는 면에서 @PostConstruct 초기화 메서드는 매우 유용하다.

- Flow 
1. XML 빈 설정을 읽는다. [ applicationContext.xml ]
2. 빈의 오브젝트를 생성한다. [ <bean id =".." class="..." > ]
3. 프로퍼티에 의존 오브젝트 또는 값을 주입한다.
4. 빈이나 태그로 등록된 후처리기를 동작시킨다. 코드에 달린 어노테이션에 대한 부가작업 진행 [ @PostConstruct ]

##### 책임에 따른 인터페이스 정의
관심사를 구분해보는 것이다. 현재 XmlService에서는 두 가지를 생각해볼 수 있다.
1. SQL 정보를 외부의 리소스로부터 읽어오는 것이다. 리소스는 단순 텍스트 파일일 수도 있고, 미리 정의된 스키마를 가진 XML일 수도 있고, 엑셀파일 일수도 있고, DB일 수도 있다.
2. 읽어온 SQL을 보관해두고 있다가 필요할 때 제공해주는 것이다. SQL의 양에 따라 다양한 방식의 저장 방법을 생각해볼 수 있다.

부가적인 책임 : 서비스를 위해서 한 번 가져온 SQL을 필요에 따라 수정할 수 있게 하는 것이다. 시스템 운영 중 서버를 재시작하거나 애플리케이션을 재설치하지 않고도 SQL을 긴급히 변경해야 하는 경우가 있다.

##### 다중 인터페이스 구현과 간접 참조
모든 클래스는 인터페이스에만 의존하도록 만들어야 스프링의 DI를 적용할 수 있다. 굳이 DI를 적용하지 않았더라도 자신이 사용하는 오브젝트의 클래스가 어떤 것인지를 알지 못하게 만드는 것이 좋다. 그래야 구현 클래스를 바꾸고 의존 오브젝트를 변경해서 자유롭게 확장할 기회를 제공해주는 것이다.

##### 자가참조 빈
스프링은 프로퍼티의 ref 항목에 자기 자신을 넣는 것을 허용한다. 이를 통해 sqlService를 구현한 메서드와 초기화 메서드는 외부에서 DI된 오브젝트라고 생각하고 결국 자신의 메서드에 접근한다. 인터페이스를 사용하고 DI를 이용하면 이렇게 특별한 구조까지도 유연하게 구성할 수 있다. 자기 자신을 참조하는 빈은 사실 흔히 쓰이는 방법은 아니다. 책임이 다르다면 클래스를 구분하고 각기 다른 오브젝트로 만들어지는 것이 자연스럽다. 다만 자기참조 빈을 만들어보는 것은, 책임과 관심사가 복잡하게 얽혀 있어서 확장이 힘들고 변경에 취약한 구조의 클래스를 유연한 구조로 만들려고 할 때 처음 시도해볼 수 있는 방법이다.

```JAVA
// 수정자 메서드로 주입만 가능하다면 된다.

<bean id="sqlService" class="practice.spring.toby.chapter7.XmlService">
	<property name="sqlReader" ref="sqlService" />
	<property name="sqlRegistry" ref="sqlService" />
	<property name="sqlmapFile" value="/chapter7/sqlmap.xml" />
</bean>

public class XmlService implements SqlService, SqlRegistry, SqlReader {
	private String sqlmapFile;
	
	private SqlRegistry sqlRegistry;
	private SqlReader sqlReader;
	
	Map<String, String> sqlMap = new HashMap<>();
	
	public void setSqlmapFile(String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
	}
	
	public void setSqlRegistry (SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}
	 
	public void setSqlReader (SqlReader sqlReader) {
		this.sqlReader = sqlReader;
	}
}
```

##### 확장 가능한 기반 클래스

```JAVA
// 기존 하나의 오브젝트에서 3개의 인터페이스를 재정의 한 자가참조 빈에서 각 인터페이스 별 오브젝트를 만들어서 확장 가능하게 처리하였다.
<bean id="sqlService" class="practice.spring.toby.chapter7.BaseSqlService">
	<property name="sqlReader" ref="sqlReader" />
	<property name="sqlRegistry" ref="sqlRegistry" />
</bean>

<bean id="sqlRegistry" class="practice.spring.toby.chapter7.HashMapSqlRegistry">
</bean>

<bean id="sqlReader" class="practice.spring.toby.chapter7.JaxbXmlSqlReader">
	<property name="sqlmapFile" value="/chapter7/sqlmap.xml" />
</bean>

```

##### 디폴트 의존관계를 갖는 빈 만들기
BaseSqlService는 SqlReader 와 SqlRegistry 프로퍼티의 DI를 통해 의존관계를 자유롭게 변경해가면서 기능을 확장할 수 있다. 유연성을 보장하려면 이런 구조가 꼭 필요 하지만 반대로 적어도 3개의 빈을 등록해줘야 한다는 점이 귀찮게 느껴지기도 하다. 이렇게 확장을 고려해서 기능을 분리하고, 인터페이스와 전략 패턴을 도입하고, DI를 적용한다면 늘어난 클래스와 인터페이스 구현과 의존관계 설정에 대한 부담은 감수해야 한다. 특정 오브젝트가 대부분의 환경에서 거의 디폴트라고 해도 좋을 만큼 기본적으로 사용될 가능성이 있다면, 디폴트 의존관계를 갖는 빈을 만드는 것을 고려해볼 필요가 있다.

디폴트 의존관계란 외부에서 DI 받지 않는 경우 기본적으로 자동 적용되는 의존관계를 말한다. DI 설정이 없을 경우 디폴트로 적용하고 싶은 의존 오브젝트를 생성자에서 넣어준다. 원래 DI란 외부에서 오브젝트를 주입해주는 것이지만 이렇게 자신이 사용할 디폴트 의존 오브젝트를 스스로 DI 하는 방법도 있다.

```JAVA
public class DefaultSqlService extends BaseSqlService{
	
	public DefaultSqlService () {
		// 생성자에서 디폴트 의존 오브젝트를 직접 만들어서 스스로 DI해준다.
		setSqlReader (new JaxbXmlSqlReader());
		setSqlRegistry(new HashMapSqlRegistry());
	}
}

<!-- 디폴트 의존관계 빈 설정 -->
<bean id="sqlServiceDefault" class="practice.spring.toby.chapter7.DefaultSqlService" />
```

여기까지 진행 후 테스트를 해보면 실패할 것이다. 이유는 SqlReader 에서 sqlmapFile 프로퍼티가 모두 비어있기 때문에 xml을 읽어서 언마샬링을 할 수 없기 때문이다. 이 부분을 해결할 수 있는 몇 가지 방법이 존재한다.
1. sqlmapFile을 DefaultSqlService 의 프로퍼티로 정의하는 방법이다.
DefaultSqlService가 sqlmapFile을 받아서 내부적으로 SqlReader를 만들면서 다시 프로퍼티를 넣어주는 것이다. 즉 DefaultSqlService 빈 생성 시 외부에서 sqlmapFile 프로퍼티를 DI 받아서 진행해서 할 수 있지만 디폴트 의존 오브젝트를 사용하기 때문에 적용하기엔 적절치 않다. 디폴트라는 건 다른 명시적인 설정이 없는 경우에 기본적으로 사용하겠다는 의미인데 설정이 있다면 의미가 달라지기 때문이다.

2. sqlmapFile의 경우도 SqlReader에 의해 기본적으로 사용될 만한 디폴트 값을 가질 수 있지 않을까? 그렇게 된다면 DefaultSqlService도 SqlReader를 사용할 때 sqlmapFile의 디폴트 값이 있기 때문에 문제가 없을 것이다.

```JAVA
public class JaxbXmlSqlReader implements SqlReader {
	// 디폴트값을 설정해준다.
	private static final String DEFAULT_SQLMAP_FILE = "/chapter7/sqlmap.xml";
	
	private String sqlmapFile = DEFAULT_SQLMAP_FILE;
	
	// 디폴트 값으로 초기화를 해주고 외부 DI가 들어왔을 경우 경로를 변경해준다.
	public void setSqlmapFile (String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
	}
```

디폴트 의존 오브젝트를 사용하는 방법에는 한 가지 단점이 있다. 설정을 통해 다른 구현 오브젝트를 사용하게 해도 DefaultSqlService는 생성자에서 일단 디폴트 의존 오브젝트를 다 만들어버린다는 점이다. DefaultSqlService의 생성자에서 extends한 오브젝트가 만들어진다. 물론 property로 설정한 오브젝트로 바로 재정의 되지만 사용하지 않는 오브젝트가 만들어진다는 점이 조금 아쉽다. 하지만 그 아쉬움을 감내하고도 남을 장점이 많기 때문에 신경쓰지 않고 사용한다. 그리고 오브젝트가 매우 복잡하고 많아서 만들어지는 것 자체가 부담이 된다면 @PostConstruct 초기화 메서드를 이용해 프로퍼티가 설정됐는지 확인하고 없는 경우에만 디폴트 오브젝트를 만드는 방법을 사용하면 된다.