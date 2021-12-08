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
생성자에서 예외가 발생할 수도 있는 복잡한 작업을 다루는 것은 좋지 않다. 오브젝트를 생성하는 중에 생성자에서 발생하는 예외는 다루기 힘들고, 상속하기 불편하며, 보안에도 문제가 생길 수 있다. 또 다른 문제점은 읽어들일 파일의 위치와 이름이 코드에 고정되어 있다는 점을 들 수 있다. SQL을 담은 XML 파일의 위치와 이름을 코드에 고정하는 건 별로 좋은 생각이 아니다. 코드의 로직과 여타 이유로 바뀔 가능성이 있는 내용은 외부에서 DI로 설정해줄 수 있게 만들어야 한다.