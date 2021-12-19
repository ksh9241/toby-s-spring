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

### 서비스 추상화 적용

##### OXM 서비스 추상화
JAXB 외에도 실전에서 자주 사용되는 XML과 자바오브젝트 매핑 기술이 있다.
1. Castor XML : 설정파일이 필요 없는 인트로스펙션 모드를 지원하기도 하는 매우 간결하고 가벼운 바인딩 프레임워크다.
2. JiBX : 뛰어난 퍼포먼스를 자랑하는 XML 바인딩 기술이다.
3. XmlBeans : 아파치 XML 프로젝트의 하나다. XML의 정보셋을 효과적으로 제공해준다.
4. Xstream : 관례를 이용해서 설정이 없는 바인딩을 지원하는 XML 바인딩 기술의 하나다.

이렇게 XML과 자바오브젝트를 매핑해서 상호 변환해주는 기술을 간단히 OXM (Object-XML Mapping) 이라고 한다. OXM 프레임워크와 기술들은 기능 면에서 상호 호환성이 있다. JAXB를 포함해서 다섯 가지 기술 모두 사용 목적이 동일하기 때문에 유사한 기능과 API를 제공한다. 기능이 같은 여러 가지 기술이 존재한다는 이야기가 나오면 떠오르는 게 있다. 바로 서비스 추상화다. 스프링은 트랜잭션, 메일전송 뿐 아니라 OXM에 대해서도 서비스 추상화 기능을 제공한다.

##### OXM 서비스 인터페이스
스프링이 제공하는 OXM 추상화 서비스 인터페이스에는 자바오브젝트를 XML로 변환하는 Marshaller와 반대로 XML을 자바 오브젝트로 변환하는 Unmarshaller이 있다. Unmarshaller 인터페이스는 아주 간단하다. XML 파일에 대한 정보를 담은 Source 타입의 오브젝트를 주면, 설정에서 지정한 OXM 기술을 이용해 자바오브젝트 트리로 변환하고, 루트 오브젝트를 돌려준다.

##### Castor 구현
Castor로 OXM 기술을 바꿔보자. Castor에서 필요한 매핑정보가 준비됐다면 unmarshaller 빈 설정만 바꿔주면 된다. 간단하게 정의해서 사용할 수 있는 XML 매핑파일을 이용해보자. [라이브러리 이슈로 테스트 진행까진 완료하지 못했다.]

##### 멤버 클래스를 참조하는 통합 클래스
의존 오브젝트를 자신만이 사용하도록 독점하는 구조로 만드는 방법이다. SqlReader 구현을 외부에서 사용하지 못하도록 제한하고 스스로 최적화된 구조로 만들어두는 것이다. 밖에서 볼 때는 하나의 오브젝트로 보이지만 내부에서는 의존관계를 가진 두 개의오브젝트가 깔끔하게 결합돼서 사용된다. 유연성은 조금 손해를 보더라도 내부적으로 낮은 결합도를 유지한 채로 응집도가 높은 구현을 만들 때 유용하게 쓸 수 있는 방법이다.

OxmSqlReader를 private으로 설정하여 내부에서만 사용하도록 클래스를 만들고, final을 통해 OxmSqlReader 오브젝트를 생성하여 DI 하거나 변경할 수 없게 강한 결합으로 확장이나 변경에 제한을 두었다. 이유는 OXM을 이용하는 서비스 구조로 최적화하기 위해서다. 하나의 클래스로 만들어두기 때문에 빈의 등록과 설정은 단순해지고 쉽게 사용할 수 있다.
OxmSqlReader는 OXM을 사용하므로 Unmarshaller가 필요하다. 또한 매핑파일도 외부에서 지정할 수 있게 해줘야 한다. 이 두개의 필요한 정보를 OxmSqlService의 프로퍼티로 정의해두고 이를 통해 전달받게 만든다.

```JAVA
public class OxmSqlService implements SqlService{
	private final OxmSqlReader oxmSqlReader = new OxmSqlReader();
	private SqlRegistry sqlRegistry = new HashMapSqlRegistry();
	
	public void setUnmarshaller (Unmarshaller unmarshaller) {
		oxmSqlReader.setUnmarshaller(unmarshaller);
	}
	
	public void setSqlmapFile (String sqlmapFile) {
		oxmSqlReader.setSqlmapFile(sqlmapFile);
	}
	
	public void setSqlRegistry (SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}
	
	@PostConstruct
	public void loadSql() {
		this.oxmSqlReader.read(this.sqlRegistry);
	}
	
	@Override
	public String getSql (String key) throws SqlRetrievalFailureException {
		try {
			return this.sqlRegistry.findSql(key);
		} catch (SqlNotFoundException e) {
			throw new SqlRetrievalFailureException(e + "");
		}
	}
	
	private class OxmSqlReader implements SqlReader{
		private Unmarshaller unmarshaller;
		private final static String DEFAULT_SQLMAP_FILE = "/chapter7/sqlmap.xml";
		private String sqlmapFile = DEFAULT_SQLMAP_FILE;
		
		public void setUnmarshaller (Unmarshaller unmarshaller) {
			this.unmarshaller = unmarshaller;
		}
		
		public void setSqlmapFile (String sqlmapFile) {
			this.sqlmapFile = sqlmapFile;
		}
		
		@Override
		public void read(SqlRegistry sqlRegistry) {
			try {
				Source source = new StreamSource (getClass().getResourceAsStream(sqlmapFile));
				Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(source); // OxmSqlService를 통해 전달받은 OXM 인터페이스 구현 오브젝트를 가지고 언마샬링 작업 수행
				
				for (SqlType sql : sqlmap.getSql()) {
					sqlRegistry.registerSql(sql.getKey(), sql.getValue());
				}
				
			} catch (IOException e) {
				// 언마샬 작업 중 IO 에러가 났다면 설정을 통해 제공받은 XML 파일 이름이나 정보가 잘못됐을 가능성이 제일 높다. 이런 경우에 가장 적합한 런타임 예외 중 하나인 IllegalArgumentException으로 포장해서 던진다.
				throw new IllegalArgumentException(this.sqlmapFile + " 을 가져올 수 없습니다.", e);
			}
		}
	}
}
```

이 방법은 앞서 UserDaoJdbc 안에서 JdbcTemplate을 직접 만들어 사용할 때 적용했던 것과 비슷하다. UserDaoJdbc는 스스로 DataSource가 필요하지 않지만 자신의 프로퍼티로 DataSource를 등록해두고 이를 DI 받아서 JdbcTemplate을 생성하면서 전달해줬다.

##### 위임을 이용한 BaseSqlService의 재사용
OxmlSqlService는 SqlReader를 스태틱 멤버 클래스로 고정시켜서 OXM에 특화된 형태로 재구성했기 때문에 설정은 간결해지고 의도되지 않은 방식으로 확장될 위험이 없다. 하지만 OxmSqlService와 기존에 만들었던 BaseSqlService의 loadSql() 과 getSql() 의 코드 중복이 발생한다. 간단한 경우 중복코드로 사용해도 큰 문제가 없지만 복잡한 코드일 경우 코드의 중복은 심각한 문제가 될 수도 있다. 수정이 필요할 때마다 두 오브젝트를 따로 수정하다가 실수를 발생할 경우도 생겨난다. 이런 경우에는 위임 구조를 이용해 코드의 중복을 제거할 수도 있다. loasSql()과 getSql()의 구현 로직은 BaseSqlService에만 두고, OxmSqlService는 일종의 설정과 기본 구성을 변경해주기 위한 어댑터 같은 개념으로 BaseSqlService의 앞에 두는 설계가 가능하다.

```JAVA
public class OxmSqlService implements SqlService{
	private final BaseSqlService baseSqlService = new BaseSqlService(); // SqlService의 실제 구현 부분을 위임할 대상인 BaseSqlService를 인스턴스 변수로 정의해둔다.
	private final OxmSqlReader oxmSqlReader = new OxmSqlReader();
	private SqlRegistry sqlRegistry = new HashMapSqlRegistry();
	
	public void setUnmarshaller (Unmarshaller unmarshaller) {
		oxmSqlReader.setUnmarshaller(unmarshaller);
	}
	
	public void setSqlmapFile (String sqlmapFile) {
		oxmSqlReader.setSqlmapFile(sqlmapFile);
	}
	
	public void setSqlRegistry (SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}
	
	@PostConstruct
	public void loadSql() {
		// OxmSqlService의 프로퍼티를 통해서 초기화된 SqlReader와 SqlRegistry를 실제 작업을 위임할 대상인 baseSqlService에 주입한다.
		this.baseSqlService.setSqlReader(this.oxmSqlReader);
		this.baseSqlService.setSqlRegistry(this.sqlRegistry);
		
		// SQL을 등록하는 초기화 작업을 baseSqlService에 위임한다.
		this.baseSqlService.loadSql();
	}
	
	@Override
	public String getSql (String key) throws SqlRetrievalFailureException {
		return baseSqlService.getSql(key);
	}
}
```
이렇게 위임구조를 이용하면 중복코드를 깔끔하게 제거할 수 있다. 이와 관련된 로직이 변경되면 BaseSqlService만 수정해주면 된다.

##### 리소스 추상화
자바에는 다양한 위치에 존재하는 리소스에 대해 단일화된 접근 인터페이스를 제공해주는 클래스가 없다. 그나마 URL을 이용해 웹상의 리소스에 접근할 때 사용할 수 있는 java.net.URL 클래스가 있을 뿐이다. 이 클래스는 http, ftp, file과 같은 접두어를 지정할 수 있어서 다양한 원격 리소스에 접근이 가능하다는 장점이 있다.

##### 리소스
스프링은 자바에 존재하는 일관성 없는 리소스 접근 API를 추상화해서 Resource라는 추상화 인터페이스를 정의했다. 다른 서비스 추상화 오브젝트와 달리 Resource는 스프링에서 빈이 아니라 값으로 취급된다. 단순한 정보를 가진 값으로 지정된다. Resource는 빈으로 등록하지 않는다고 했으니 기껏 외부에서 지정한다고 해봐야 Property의 value 어트리뷰트에 넣는 방법밖에 없다.

```JAVA
public interface Resource extends InputStreamSource {
	// 리소스의 존재나 읽기 가능한지 여부를 확인할 수 있다. 또 현재 리소스에 대한 입력 스트림이 열려 있는지도 확인 가능하다.
	boolean exists();
	boolean isReadable();
	boolean isOpen();

	// JDK의 URL, URI, File 형태로 전환 가능한 리소스에 사용된다.
	URL getURL() throws IOException;
	URI getURI() throws IOException;
	File getFile() throws IOException;


	Resource createRelative(String relativePath) throws IOException;

	// 리소스에 대한 이름과 부가적인 정보를 제공한다.
	long lastModified() throws IOException;
	String getFilename();
	String getDescription();
}

public interface InputStreamSource {
	// 모든 리소스는 InputStream 형태로 가져올 수 있다.
	InputStream getInputStream() throws IOException;
}
```

##### 리소스 로더
그래서 스프링은 URL 클래스와 유사하게 접두어를 이용해 Resource 오브젝트를 선언하는 방법이 있다. 문자열로 정의된 리소스를 실제 Resource 타입 오브젝트로 변환해주는 ResourceLoader를 제공한다.

```JAVA
public interface ResourceLoader {
	// location에 담긴 스프링 정보를 바탕으로 그에 적절한 Resource로 변환해준다.
	Resource getResource(String location);
}
```

접두어		예  				설명

file : 		 file:/C:/temp/file.txt 	 	: 파일 시스템의 C:/temp 폴더에 있는 file.txt를 리소스로 만들어준다.
classpath : 	 classpath:file.txt 		 	: 클래스패스의 루트에 존재하는 file.txt 리소스에 접근하게 해준다.
없음 :		 WEB-INF/test.dat 		 	: 접두어가 없는 경우에는 ResourceLoader 구현에 따라 리소스의 위치가 결정된다. ServletResourceLoader라면 서블릿 컨텍스트의 루트를 기준으로 해석한다. 
http: 		 http://www.myserver.com/test/dat 	: HTTP 프로토콜을 사용해 접근할 수 있는 웹상의 리소스를 지정한다. ftp:도 사용할 수 있다. 

리소스로더의 대표적인 예는 바로 스프링의 애플리케이션 컨텍스트다. 애플리케이션 컨텍스트가 구현해야 하는 인터페이스인 ApplicationContext는 ResourceLoader 인터페이스를 상속하고 있다. 예를 들어 애플리케이션 컨텍스트가 사용할 스프링 설정정보가 담긴 XML 파일도 리소스로더를 이용해 Resource 형태로 읽어온다. 또한 빈의 프로퍼티 값을 변환할 때도 리소스 로더가 자주 사용된다.

##### Resource를 이용해 XML 파일 가져오기
Resource를 사용할 떄는 Resource 오브젝트가 실제 리소스는 아니라는 점을 주의해야 한다. Resource는 단지 리소스에 접근할 수 있는 추상화된 핸들러일 뿐이다. 공개적인 웹 서버에서 SQL 정보를 가져올 일이야 아마도 없겠지만, 기업의 DB관련 정보를 관리하는 내부 서버가 있다면 SQL 정보를 HTTP 프로토콜로 가져올 수 있게 하는 건 좋은 방법이다.

### 인터페이스 상속을 통한 안전한 기능확장
원칙적으로 권장되진 않지만 때로는 서버가 운영 중인 상태에서 서버를 재시작하지 않고 긴급하게 애플리케이션이 사용 중인 SQL을 변경해야 할 수도 있다. 운영시간 중에 예상하지 못한 SQL의 오류를 발견했다거나, 아니면 특별한 이유로 SQL 조건이나 참조 테이블을 급하게 변경할 수도 있다.

##### DI를 의식하는 설계
SqlService의 내부 기능을 적절한 책임과 역할에 따라 분리하고, 인터페이스를 정의해 느슨하게 연결해주고, DI를 통해 유연하게 의존관계를 지정하도록 설계해뒀기 때문에 그 뒤의 작업은 매우 쉬워졌다. 결국 유연하고 확장 가능한 좋은 오브젝트 설계와 DI 프로그래밍 모델은 서로 상승작용을 한다.
DI에 필요한 유연하고 확장성이 뛰어난 오브젝트 설계를 하려면 많은 고민과 학습, 훈련, 경험이 필요하다. DI를 적용하려면 커다란 오브젝트 하나만 존재해서는 안된다. 최소한 두 개 이상의 의존관계를 가지고 서로 협력해서 일하는 오브젝트가 필요하다. 그래서 적절한 책임에 따라 오브젝트를 분리해줘야 한다. DI는 확장을 위해 필요한 것이므로 항상 미래에 일어날 변화를 예상하고 고민해야 적합한 설계가 가능해진다.

##### DI와 인터페이스 프로그래밍
DI를 DI답게 만들려면 두 개의 오브젝트가 인터페이스를 통해 느슨하게 연결돼야 한다. 인터페이스를 사용하는 첫 번째 이유는 다형성을 얻기 위해서다. 하나의 인터페이스를 통해 여러 개의 구현을 바꿔가면서 사용할 수 있게 하는 것이 DI가 추구하는 첫 번째 목적이다. 물론 지금까지 여러 가지 DI 적용 예를 살펴봤듯이 의존 오브젝트가 가진 핵심 로직을 바꿔서 적용하는 것 외에도 프록시, 데코레이터, 어댑터, 테스트 대역 등의 다양한 목적을 위해 인터페이스를 통한 다형성이 활용된다.

두 번째 이유는 인터페이스 분리 원칙을 통해 클라이언트와 의존 오브젝트 사이의 관계를 명확하게 해줄 수 있기 때문이다. 클라이언트 A와 의존 오브젝트 B가 있다고 가정했을 때 A와 B가 인터페이스로 연결되어 있다면 A는 B의 인터페이스만 바라볼 뿐이다. B인터페이스를 통해 C, D 등의 오브젝트를 구현해도 A는 B의 인터페이스를 바라보기 때문에 A에게 DI가 가능하다. 그런데 C, D 오브젝트는 B가 아니라 E라는 다른 인터페이스를 구현하고 있을 수도 있다. 그렇다면 C, D 오브젝트는 왜 E 인터페이스를 구현하고 있을까?? 그 이유는 E라는 인터페이스가 그려주는 창으로 C, D를 바라보는 다른 종류의 클라이언트 (AA 등) 이 존재하기 때문이다. 각기 다른 관심과 목적을 가지고 어떤 오브젝트에 의존하고 있을 수 있다는 의미다. 굳이 E라는 인터페이스에 정의된 내용에는 아무런 관심이 없는 A 오브젝트가 E 인터페이스 메서드까지 모두 노출되어 있는 C, D 라는 클래스에 직접 의존할 이유가 없다. 게다가 E 인터페이스가 변하면 관심도 없는 A 오브젝트의 코드에 영향을 줄 수도 있다. 오브젝트가 그 자체로 충분히 응집도가 높은 작은 단위로 설계됐더라도, 목적과 관심이 각기 다른 클라이언트가 있다면 인터페이스를 통해 적절하게 분리해줄 필요가 있고, 이를 객체지향 설계 원칙에서는 인터페이스 분리 원칙 (Interface Segregation Principle) 이라고 부른다.

다형성은 물론이고 클라이언트별 다중 인터페이스 구현과 같은 유연하고 확장성 높은 설계가 가능함에도 인터페이스를 피할 이유가 없다.

##### 인터페이스 상속
때로는 인터페이스를 여러 개 만드는 대신 기존 인터페이스를 상속을 통해 확장하는 방법도 있다. 인터페이스 분리 원칙이 주는 장점은 모든 클라이언트가 자신의 관심에 따른 접근 방식을 불필요한 간섭 없이 유지할 수 있다는 점이다. 그래서 기존 클라이언트에 영향을 주지 않은 채로 오브젝트의 기능을 확장하거나, 수정할 수 있다.

### DI를 이용해 다양한 구현 방법 적용하기

##### ConcurrentHashMap을 이용한 수정 가능 SQL 레지스트리
HashMap으로는 멀티스레드 환경에서 동시에 수정을 시도하거나 수정과 동시에 요청하는 경우 예상하지 못한 결과가 발생할 수 있다. 멀티스레드 환경에서 안전하게 HashMap을 조작하려면 Collections.synchronizedMap() 등을 이용해 외부에서 동기화해줘야 한다. 하지만 이렇게 HashMap에 대한 전 작업을 동기화하면 SqlService 처럼 DAO의 요청이 많은 고성능 서비스에서는 성능에 문제가 생긴다. 그래서 동기화된 해시 데이터 조작에 최적화되도록 만들어진 ConcurrentHashMap을 사용하는 방법이 일반적으로 권장된다. 데이터 조작 시 전체 데이터에 대해 락을 걸지 않고 조회는 락을 아예 사용하지 않는다.

##### 내장형 데이터베이스를 이용한 SQL 레지스트리 만들기
ConcurrentHashMap이 멀티스레드 환경에서 최소한의 동시성을 보장해주고 성능도 그리 나쁜 편은 아니지만, 저장되는 데이터의 양이 많아지고 잦은 조회와 변경이 일어나는 환경이라면 한계가 있다. 인덱스를 이용한 최적화된 검색을 지원하고 동시에 많은 요청을 처리하면서 안정적인 변경 작업이 가능한 기술은 바로 데이터베이스다. DB의 장점과 특징은 그대로 갖고 있으면서도 애플리케이션 외부에 별도로 설치하고 셋업하는 번거로움은 없는 내장형 DB를 사용하는 것이 적당하다. 내장형 DB는 애플리케이션에 내장돼서 애플리케이션과 함께 시작되고 종료되는 DB를 말한다. 데이터는 메모리에 저장되기 때문에 IO로 인해 발생하는 부하가 적어서 성능이 뛰어나다. 동시에 Map과 같은 컬렉션이나 오브젝트를 이용해 메모리에 데이터를 저장해두는 방법에 비해 매우 효과적이고 안정적인 방법으로 등록, 수정, 검색이 가능하다. 최적화된 락킹, 격리수준, 트랜잭션을 적용할 수도 있다.

##### 스프링의 내장형 DB 지원 기능
자바에서 많이 사용되는 내장형 DB는 Derby, HSQL, H2를 꼽을 수가 있다. JDBC 방식의 접근이라고 해서 기존의 DataSource와 DAO를 사용하는 모델을 그대로 사용하는 건 좋은 방법은 아니다. 애플리케이션 내에서 DB를 기동시키고 초기화 SQL 스크립트 등을 실행시키는 등의 초기화 작업이 별도로 필요하기 때문이다. 스프링은 내장형 DB를 손쉽게 이용할 수 있도록 내장형 DB 지원 기능을 제공하고 있다. 일종의 내장형 DB를 위한 서비스 추상화 기능이다. 하지만 다른 서비스 추상화처럼 별도의 레이어와 인터페이스를 제공하지 않는다. 대신 스프링은 내장형 DB를 초기화하는 작업을 지원하는 편리한 내장형 DB 빌더를 제공한다. 다만 내장형 DB는 애플리케이션 안에서 직접 DB 종료를 요청할 수도 있어야 한다. 이를 위해 스프링은 DataSource 인터페이스를 상속해서 shutdown() 이라는 내장형 DB용 메서드를 추가한 EmbeddedDatabase 인터페이스를 제공한다.

```JAVA
new EmbeddedDatabaseBuilder() // 빌더 오브젝트 생성
	.setType("내장형DB종류") // H2, HSQL< Derby 중 택1
	.addScript("초기화에 사용할 DB 스크립트의 리소스") // /chapter7/schema.sql
	.build(); // 주어진 조건에 맞는 내장형 DB를 준비하고 초기화 스크립트를 모두 실행한 뒤에 EmbeddedDatabase를 반환한다.
```

 ##### 내장형 DB를 이용한 SqlRegistry 만들기
EmbeddedDatabaseBuilder 오브젝트는 한 번 초기화를 거쳐서 내장형 DB를 기동하고 이에 접근할 수 있는 EmbeddedDatabase를 만들어주면 그 이후로는 사용할 일이 없다. 다행이 스프링에는 팩토리 빈을 만드는 번거로운 작업을 대신해주는 전용 태그가 있다.

```JAVA
<!-- 내장형 DB 설정 --> 
<!-- jdbc:embedded-database 태그에 의해 만들어지는 타입 빈은 스프링 컨테이너가 종료될 때 자동으로 shotdown() 메서드가 호출되도록 설정되어 있다. -->
<jdbc:embedded-database id="embeddedDatabase" type="HSQL"> 
	<jdbc:script location="classpath:scema.sql"/>
</jdbc:embedded-database>
```

이렇게 설정하면 embeddedDatabase 아이디를 가진 빈이 등록되며, 타입은 EmbeddedDatabase다.

##### 트랜잭션 적용
기본적으로 HashMap과 같은 컬렉션은 트랜잭션 개념을 적용하기가 매우 힘들다. 엘리먼트 하나를 수정하는 정도의 간단한 락킹을 잉요해 안전성을 보장해줄 수 있다고 해도, 여러 개의 엘리먼트를 트랜잭션과 같은 원자성이 보장된 상태에서 변경하려면 매우 복잡한 과정이 필요하기 때문이다. 반면 내장형 DB를 사용하는 경우에는 트랜잭션 적용이 상대적으로 쉽다. 스프링에서 트랜잭션을 적용할 때 트랜잭션 경계가 DAO 밖에 있고 범위가 넓은 경우라면 AOP를 이용하는 것이 편리하다. 하지만 SQL 레지스트리라는 제한된 오브젝트 내에서 서비스에 특화된 간단한 트랜잭션이 필요한 경우라면 간단히 트랜잭션 추상화 API를 직접 사용하는 게 편리할 것이다.

##### 코드를 이용한 트랜잭션 적용
일반적으로 트랜잭션 매니저를 싱글톤 빈으로 등록해서 사용하는데, 그 이유는 어려 개의 AOP를 통해 만들어지는 트랜잭션 프록시가 같은 트랜잭션 매니저를 공유해야 하기 때문이다. 반면에 EmbeddedDbSqlRegistry가 사용할 내장형 DB에 대한 트랜잭션 매니저는 공유할 필요가 없다. 따라서 번거롭게 빈으로 등록하는 대신 EmbeddedDbSqlRegistry 내부에서 직접 만들어 사용하는 게 낫다.

```JAVA
// dataSource로 TransactionManager를 만들고 이를 이용해 TransactionTemplate을 생성한다.
transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(dataSource));

@Override
public void updateSql(final Map<String, String> sqlmap) throws SqlUpdateFailureException {
	// 익명 내부 클래스로 사용하기 위해 프로퍼티를 final로 받는다.
	transactionTemplate.execute(new TransactionCallbackWithoutResult() {
		@Override
		protected void doInTransactionWithoutResult(TransactionStatus status) {
			for (Map.Entry<String, String> entry : sqlmap.entrySet()) {
				updateSql(entry.getKey(), entry.getValue());
			}
		}
	});
}
```

##### 내장형 DB의 트랜잭션 격리수준 지원
HSQL1.8 이하의 버전에서는 트랜잭션 격리수준이 READ_UNCOMMITTED라고 보통 불리는 레벨 0만을 지원한다. READ_UNCOMMITTED 트랜잭션 격리수준의 문제점은 한 트랜잭션이 종료되기 전의 작업 내용을 다른 트랜잭션이 읽을 위험성이 있다는 것이다. 만약 다른 트랜잭션이 끝나기 전에 변경한 정보를 읽어버렸는데 해당 트랜잭션이 롤백돼버리면 실제로는 DB에 반영되지 않은 유효하지 않은 데이터를 사용하는 문제가 발생한다. 낮은 격리수준의 위험성을 피하려면 READ_UNCOMMITTED 격리수준을 지원하는 HSQL 1.9 이상을 사용하거나 H2, 또는 Derby를 사용해야 한다.

### 스프링 3.1의 DI

##### 자바 언어의 변화와 스프링
스프링이 제공하는 모든 기술의 기초가 되는 DI의 원리는 변하지 않았지만 자바 언어에는 적지 않은 변화가 있었다. 이런 변화들이 DI 프레임워크로서 스프링의 사용 방식에도 여러 가지 영향을 줬다.

- 어노테이션의 메타정보 활용
첫째는 자바 코드의 메타정보를 이용한 프로그래밍 방식이다. 자바는 소스코드가 컴파일 된 후 클래스 파일에 저장됐다가, JVM에 의해 메모리로 로딩되어 실행된다. 그런데 때로는 자바 코드가 실행되는 것이 목적이 아니라 다른 자바 코드에 의해 데이터처럼 취급되기도 한다. 코드의 일부를 리플렉션 API 등을 이용해 어떻게 만들었는지 살펴보고 그에 따라 동작하는 기능이 점점 많이 사용되고 있다. 리플렉션 API는 자바 코드나 컴포넌트를 작성하는데 사용되는 툴을 개발할 때 이용하도록 만들어졌는데, 언제부턴가 본래 목적보다는 자바 코드의 메타정보를 데이터로 활용하는 스타일의 프로그래밍 방식에 더 많이 활용되고 있다.
어노테이션 활용이 늘어난 이유는 애플리케이션을 핵심 로직을 담은 자바 코드와 이를 지원하는 IoC 방식의 프레임워크, 그리고 프레임워크가 참조하는 메타정보라는 세 가지로 구성하는 방식에 잘 어울리기 때문일 것이다. 어노테이션은 프레임워크가 참조하는 메타정보로 사용되기에 여러가지 유리한 점이 많다. 다음과 같이 간단한 어노테이션이 사용된 코드를 살펴보자.

```JAVA
@Special
public class MyClass {

}
```
간단한 어노테이션 하나를 클래스 위에 선언했을 뿐이지만 여러가지 정보를 추가로 얻을 수 있다. 가장 먼저 @Special 어노테이션이 부여된 클래스의 패키지, 클래스 이름, 접근 제한자, 상속한 클래스나 구현 인터페이스가 무엇인지 알 수 있다. 원한다면 클래스의 필드나 메서드 구성도 확인할 수 있다. 반면에 XML로 표현하려면 모든 내용을 명시적으로 나타내야 한다. 리팩토링에서도 많은 차이점이 드러난다. 어노테이션은 클래스명을 변경하는 것이 간단한 일이지만 XML의 경우 클래스 오브젝트명을 바꿔주고 XML에서 한번 더 변경을 해줘야 한다. 물론 어노테이션에도 단점이 있다. XML은 어느 환경에서나 손쉽게 편집이 가능하고, 내용을 변경하더라고 다시 빌드를 거칠 필요가 없다. 반면 어노테이션은 자바 코드에 존재하므로 변경할 때마다 매번 클래스를 새로 컴파일해줘야 한다.

- 정책과 관례를 이용한 프로그래밍
어노테이션 같은 메타정보를 활용하는 프로그래밍 방식은 코드를 이용해 명시적으로 동작 내용을 기술하는 대신 코드 없이도 미리 약속한 규칙 또는 관례를 따라서 프로그램이 동작하도록 만드는 프로그래밍 스타일을 적극적으로 포용하게 만들어왔다. 그 때문에 정책을 기억 못하거나 잘못 알고 있을 경우 의도한 대로 동작하지 않는 코드가 만들어질 수 있다. 어쨋든 스프링은 점차 어노테이션으로 메타정보를 작성하고, 미리 정해진 정책과 관례를 활용해서 간결한 코드에 많은 내용을 담을 수 있는 방식을 적극 도입하고 있다.

