# 템플릿
템플릿이란 코드에서 어떤 부분은 변경을 통해 그 기능이 다양해지고 확장하려는 성질이 있고, 어떤 부분은 고정되어 있고 변하지 않으려는 성질이 있는데 이렇게 바뀌는 성질이 다른 코드 중에서 변경이 거의 일어나지 않으며 일정한 패턴으로 유지되는 특성을 가진 부분을 자유롭게 변경되는 성질을 가진 부분으로부터 독립시켜서 효과적으로 활용할 수 있도록 하는 방법이다.

##### 리소스 반환과 close()
Connection과 PreparedStatement는 보통 풀(pool) 방식으로 운영된다. 미리 정해진 풀 안에 제한된 수의 리소스를 만들어두고 필요할 때 이를 할당하고, 반환하면 다시 풀에 넣는 방식으로 운영된다. 요청이 매우 많은 서버환경에서는 매번 새로운 리소스를 생성하는 대신 풀에 미리 만들어둔 리소스를 돌려가며 사용하는 편이 훨씬 유리하다. 대신 사용한 리소스는 빠르게 반환해야 한다. 그렇지 않으면 풀에 있는 리소스가 고갈되고 결국 문제가 발생한다. close()는 사용된 리소스를 반환해주는 메서드이다.

##### 분리와 재사용을 위한 디자인 패턴 적용
DB커넥션 연결과 같이 Dao에서 지속적으로 사용하는 코드가 메서드 단위로 반복적인 호출이 일어난다. 중복코드를 처리하는 방법으로는 변하는 부분과 변하지 않는 부분을 찾아서 처리하면 된다.
예제로 chapter3의 UserDao 클래스에서 PreparedStatement 의 부분을 제외한 Connection, ResultSet 등의 부분은 변하지 않는다. 원래는 변하지 않는 부분의 코드를 메서드로 만드는 것이 낫지만 변하는 부분이 변하지 않는 부분 중간에 껴있기 때문에 변하는 부분을 메서드로 만들고 하나의 메서드에서 재사용하는 방법으로 리팩토링을 선택하였다.

##### 템플릿 메서드 패턴의 적용
기존에 makeStatement 메서드를 추상메서드로 만들어서 자식클래스에서 재정의한 후 변하지 않는 부분에서 클래스를 호출하여 사용한다. 하지만 장점보다 단점이 더 많이 보인다. Dao 내의 메서드가 많을수록 서브클래스의 개수가 증가하기 때문이다.

##### 전략 패턴의 적용
개방 폐쇄 원칙을 잘 지키는 구조이면서도 템플릿 메서드 패턴보다 유연하고 확장성이 뛰어난 것이, 오브젝트를 아예 둘로 분리하고 클래스 레벨에서는 인터페이스를 통해서만 의존하도록 만드는 전략 패턴이다.
Service와 ServiceImple의 경우를 생각해볼 수 있다.

##### 테스트 클래스에서 배운점
테스트케이스는 추상클래스를 테스트할 때 extends 한 클래스로 오브젝트를 생성해서 테스트하여야 한다.

##### DI
앞에서 사용한 예로 컨텍스트(UserDao) 가 필요로 하는 전략 (ConnectionMaker.interface) 의 특정 구현 클래스 (DSimpleConnectionMaker) 오브젝트를 클라이언트 (UserDaoTest)에서 생성하여 제공해주는 방법. (DI) 위의 설명이 DI의 흐름이다. DI는 다양한 형태로 적용 할 수 있다. DI의 가정 중요한 개념은 제 3자의 도움을 통해 두 오브젝트 사이의 유연한 관계가 설정되도록 만든다는 것이다. 이 개념만 따른다면 DI를 이루는 오브젝트와 구성요소 구조나 관계는 다양하게 만들 수 있다. 일반적으로 DI는 두 개의 오브젝트와 두 오브젝트를 연결해주는 오브젝트 팩토리(DI 컨테이너), 그리고 이를 사용하는 클라이언트라는 4개의 오브젝트 사이에서 일어난다.

##### 전략과 클라이언트의 동거
지금까지 예제를 하면서 많은 중복코드가 해결되고 깔끔해졌지만 그에 따른 문제점도 발생했다. 전략패턴을 사용하게되면 상속받을 서브클래스를 메서드 단위로 만들어야되기 때문에 전략패턴을 사용하지 않을 때보다 훨씬 많은 클래스를 만든다.
첫번째 해결방법은 클래스파일을 각 메서드안에 내부 클래스 파일로 만드는 것이다. 마치 로컬 변수를 선언하듯이 선언하면 된다. 로컬클래스는 선언된 메서드 내에서만 사용할 수 있다. 또한 메서드 내부에 로컬클래스가 존재하기 때문에 가독성도 나쁘지 않다.

##### 중첩클래스의 종류
다른 클래스 내부에 정의되는 클래스를 중첩클래스(nested class) 라고 한다. 중첩 클래스는 독립적으로 오브젝트로 만들어질 수 있는 스태틱 클래스와 자신이 정의된 클래스의 오브젝트 안에서만 만들어질 수 있는 내부 클래스(inner class)로 구분된다. 내부 클래스는 다시 범위 (scope)에 따라 세 가지로 구분된다. 멤버 필드처럼 오브젝트 레벨에 정의되는 멤버 내부 클래스와 메서드 레벨에 정의되는 로컬클래스, 그리고 이름을 갖지 않는 익명 내부 클래스다. 익명 내부 클래스의 범위는 선언된 위치에 따라서 다르다.

##### 익명내부클래스 (nonymous inner class)
익명 내부 클래스는 이름을 갖지 않는 클래스이다. 오브젝트 생성이 결합된 형태로 만들어지며, 상속할 클래스나 구현할 인터페이스를 생성자 대신 사용한다. 클래스를 재사용할 필요도 없고, 구현한 인터페이스 타입으로만 사용할 경우에 유용하다.
ex) new 인터페이스명() { 클래스 본문 };

##### JdbcContext의 특별한 DI
의존관계 주입이라는 개념을 충실히 따르자면 인터페이스를 사이에 둬서 클래스 레벨에서는 의존관계가 고정되지 않게하고, 런타임 시에 의존할 오브젝트와의 관계를 다이나믹하게 주입해주는 것이 맞다. 따라서 인터페이스를 사용하지 않았다면 엄밀히 말해서 온전한 DI라고 볼 수는 없다. 그러나 스프링의 DI는 넓게 보자면 객체의 생성과 관계설정에 대한 제어권한을 오브젝트에서 제거하고 외부로 위임했다는 IoC라는 개념을 포괄한다. 그런 의미에서 JdbcContext를 스프링을 이용해 UserDao 객체에서 사용하게 주입했다는 건 DI의 기본을 따르고 있다고 볼 수 있다. 인터페이스를 사용하지 않는 구조로 작성한 이유로 첫번째는 JdbcContext가 스프링 컨테이너의 싱글톤 레지스트리에서 관리되는 싱글톤 빈이 되기 때문이다. JdbcContext는 그 자체로 변경되는 상태정보를 갖고 있지 않다. dataSource라는 인스턴스 변수가 존재하긴 하지만 DataSource 오브젝트는 읽기전용이기 때문에 아무런 문제가 없다. 두번째는 JdbcContext가 DI를 통해 다른 빈에 의존하고 있기 때문이다. 이 두번째 이유가 중요하다. JdbcContext는 DataSource 오브젝트를 주입받도록 되어있다. DI를 위해서는 주입되는쪽과 주입받는 쪽 모두가 스프링 빈으로 등록되어야 한다.

##### 코드를 이용한 수동 DI
기존의 DI는 빈을 생성하여 스프링의 컨테이너가 의존성을 주입하였지만, JdbcContext같은 XXXDao와 응집도가 높은 오브젝트의 경우 따로 빈으로 생성하지 않고 UserDao와 DataSource의 빈만으로 JdbcContext 오브젝트를 생성할 수 있다. 방법은 UserDao의 수정자 메서드 (setter)를 이용하는 것이다. UserDao에 DataSource를 주입 후 setDataSource의 메서드 안에서 JdbcContext 타입의 객체 생성 후 주입받은 DataSource를 사용하는 것이다. 이렇게 되면 UserDao가 임시로 DI 컨테이너처럼 동작하게 되는 것이다.

##### 템플릿과 콜백
템플릿과 콜백 패턴은 전략패턴의 익명 내부 클래스를 활용하는 방식을 말한다. 전략 패턴의 컨텍스트를 템플릿이라 부르고, 익명 내부 클래스로 만들어지는 오브젝트를 콜백이라고 부른다.
템플릿 : 어떤 목적을 위해 미리 만들어둔 모양이 있는 틀을 가리킨다. jsp는 html이라는 고정된 부분에 el과 스크립트릿이라는 변하는 부분을 넣은 일종의 템플릿 파일이다.
콜백 : 실행되는 것을 목적으로 다른 오브젝트의 메서드에 전달되는 오브젝트를 말한다. 파라미터로 전달되지만 값을 참조하기 위한 것이 아니라 특정 로직을 담은 메서드를 실행시키기 위해 사용한다. 자바에선 메서드 자체를 파라미터로 전달할 방법이 없기 때문에 메서드가 담긴 오브젝트를 전달해야 한다. 그래서 펑셔널 오브젝트(functional object) 라고도 한다.

특징 : 여러 개의 메서드를 가진 일반적인 인터페이스를 사용할 수 있는 전략 패턴의 전략과 달리 템플릿/콜백 패턴의 콜백은 보통 단일 메서드 인터페이스를 사용한다. (functional Interface로 이해함) 콜백 인터페이스에는 보통 파라미터가 있다. 이 파라미터는 템플릿의 작업 흐름 중에 만들어지는 컨텍스트 정보를 전달받을 때 사용된다. 매번 메서드 단위로 사용할 오브젝트를 새롭게 전달받는다는 것이 특징이다. 
템플릿/콜백 방식은 전략 패턴과 DI의 장점을 익명 내부 클래스 사용 전략과 결합한 독특한 방식이다. 이 패턴은 전략패턴과 수동 DI(생성자 메서드를 통한 인터페이스를 생성하지않는 의존성주입)를 이해할 수 있어야 한다.

##### 템플릿/콜백의 응용
스프링의 많은 API나 기능을 살펴보면 템플릿/콜백 패턴을 적용한 경우를 많이 발견할 수 있다. 따지고 보면 DI도 순수한 스프링의 기술은 아니다. 객체지향의 장점을 잘 살려서 설계하고 구현하도록 도와주는 여러가지 원칙과 패턴의 활용 결과일 뿐이다. 템플릿/콜백 패턴에 필요한 학습은 먼저 자주 반복되는 코드를 분리하는 연습을 진행하고, 그 중 일부 작업을 필요에 따라 바꾸어 사용해야 한다면 인터페이스를 통한 DI를 진행한다. 그 중에서 공통적으로 사용하는 기능을 템플릿/콜백 패턴을 사용하여 공통 오브젝트 내에 메서드로 만들어서 사용한다. 가장 전형적인 템플릿/콜백 패턴의 후보는 try/catch/finally 블록을 사용하는 코드이다.
- 예제 : Calculator.class

##### 제네릭스를 이용한 콜백 인터페이스
자바 5부터 나온 제네릭은 반환타입을 <T>로 설정하여 타입을 정의해주기만하면 메서드에서 다양한 타입으로 메서드를 재사용할 수 있다.

```JAVA
// Callback 인터페이스를 제네릭타입으로 변경
public interface LineCallback<T> {
	T doSomethingWithLine (String line, T value);
}

// 제네릭을 이용하여 여러 반환타입형태의 메서드를 만듬.
public int calcMul(String fileName) throws IOException {
		LineCallback<Integer> callback = new LineCallback<Integer>() {
			@Override
			public Integer doSomethingWithLine(String line, Integer value) {
				return value *= Integer.valueOf(line);
			}
		};
		return lineReaderTemplate(fileName, callback, 1);
	}
	
	public String concatenate(String fileName) throws IOException {
		LineCallback<String> callback = new LineCallback<String>() {
			@Override
			public String doSomethingWithLine(String line, String value) {
				return value += line;
			}
		}; 
		return lineReaderTemplate(fileName, callback, "");
	}
	
	// 파일을 한줄씩 읽는 기능은 공통기능이라 따로 분리함.
	public <T> T lineReaderTemplate (String fileName, LineCallback<T> callback, T initVal) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(path + fileName))){
			T res = initVal;
			String line = null;
			while ((line = br.readLine()) != null) {
				res = callback.doSomethingWithLine(line, res);
			}
			return res;
		} catch (IOException e) {
			throw e;
		}
	}
```

##### 스프링의 JdbcTemplate
JdbcTemplate은 기존에 만들어서 사용하던 StatementStrategy인터페이스의 makePreparedStatement 와 JdbcContext가 합쳐서 사용한 컨텍스트가 동일한 구조로 스프링에서 제공한다. 그렇기 때문에 jdbcTemplate.update(String SQL)만 넣어주면 앞에서 복잡하게 컨텍스트를 만들어서 메서드 재사용을 위한 작업 등을 할 필요가 없고, 템플릿/콜백 패턴을 몰라도 쉽게 사용할 수 있다. 또한 insert의 경우 ps.setOOO등을 통한 동적 데이터에 대한 바인딩이 필요했지만 JdbcTemplate은 update메서드의 파라미터만 추가로 넣어주면 자동으로 바인딩한 쿼리를 DB에 요청한다.

##### queryForInt()

```JAVA
public int getCount () throws ClassNotFoundException, SQLException {
		return template.query(new PreparedStatementCreator() /*첫 번째 콜백. Statement 생성*/ {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				return con.prepareStatement("SELECT COUNT(*) FROM users");
			}
		}, new ResultSetExtractor<Integer>() {
			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				rs.next();
				return rs.getInt(1);
			} // 두 번째 콜백 ResultSet으로 부터 값 추출
			
		});
	}
```

먼저 DB에 요청하는 부분을 PreparedStatementCreator() 익명내부함수를 사용해 만든 뒤 반환된 결과값을 ResultSetExtractor() 익명 내부함수를 사용해 읽어서 반환하였다. 여기서 중요한 부분은 ResultSetExtractor 인터페이스의 반환타입이 제네릭인 점이다. 이전에 만들었던 LineReadTemplate과 마찬가지로 반환되는 타입이 다양하기 때문에 제네릭을 사용하였다. ResultSetExtractor 콜백에 지정한 타입은 제네릭 메서드에 적용되서 query() 템플릿의 리턴 타입도 함께 바뀐다. 내부 코드에서 작성해야 될게 많은 query() 메서드와 달리 queryForInt()를 사용하게 되면 한줄로 DB에 요청한 개수를 반환할 수 있다.

```JAVA
public int getCount () throws ClassNotFoundException, SQLException {
	return template.queryForInt("SELECT COUNT(*) FROM users");
}
```
##### queryForOjbect()
queryForObject와 queryForInt의 차이는 ResultSetExtractor 콜백을 사용하지않고, RowMapper를 사용한다는 점이다. ResultSetExtractor의 경우 최종값을 반환해주지만, RowMapper의 경우 row값 한줄씩 읽어서 반환한다는 차이가 있다.

##### 테스트 보완
개발자들은 수동 테스트를 할 때 재수 좋게도 실패할 만하나 상황은 요리조리 잘 피해간다는 것이다. 동작흐름을 잘 알고있는 개발자의 경우 경계값 테스트의 범위만 잘 조절한다던가 입력값을 맞는 타입으로 넣는다던가 그런식으로 테스트를 진행하기 때문에 단위테스트를 통과할 경우가 많다. 하지만 QA팀의 경우는 테스트를 할 때 입력값의 타입을 잘못 넣는다던가, 입력값을 입력하지 않는다던가의 운영에서 발생할 수 있는 상황에 대해서 테스트를 진행한다. 그렇기 때문에 개발자가 개발을 진행할 때 예외상황에 따른 후처리도 진행해줘야 한다.

### 정리
3장에서는 예외 처리와 안전한 리소스 반환을 보장해주는 DAO 코드를 만들고 이를 객체지향 설계 원리와 디자인 패턴, DI 등을 적용해서 깔끔하고 유연하며 단순한 코드로 만드는 방법을 살펴봤다.
- JDBC와 같이 예외가 발생할 가능성이 있으며 공유 리소스의 반환이 필요한 코드는 반드시 try/catch/finally 블록에서 관리해야 한다. ( ex : connection, PreparedStatement, ResultSet.... )
- 일정한 작업 흐름이 반복되면서 그중 일부 기능만 바뀌는 코드가 존재한다면 전략 패턴을 적용한다. 바뀌지 않는 부분을 컨텍스트로, 바뀌는 부분을 전략으로 만들고 인터페이스로 유연하게 전략을 변경할 수 있도록 구성한다.
- 같은 애플리케이션 안에서 여러 가지 종류의 전략을 다이나믹하게 구성하고 사용해야 한다면 컨텍스트를 이용하는 클라이언트 메서드에서 직접 전략을 정의하고 제공하게 만든다.
- 클라이언트 메서드 안에 익명 내부 클래스를 사용해서 전략 오브젝트를 구현하면 코드도 간결해지고 메서드의 정보를 직접 사용할 수 있어서 편리하다. 또한 디렉토리에 재정의 클래스가 무수히 많아지는 것을 방지할 수 있다.
- 컨텍스트는 별도의 빈으로 등록해서 DI 받거나 클라이언트 클래스에서 직접 생성해서 사용한다. 클래스 내부에서 컨텍스트를 사용할 때 컨텍스트가 의존하는 외부의 오브젝트가 있다면 코드를 이용해서 직접 DI해줄 수 있다.
- 단일 전략 메서드를 갖는 전략 패턴이면서 익명 내부 클래스를 사용해서 매번 전략을 새로 만들어 사용하고, 컨텍스트 호출과 동시에 전략 DI를 수행하는 방식을 템플릿/콜백 패턴이라고 한다.
- 콜백의 코드에도 일정한 패턴이 반복된다면 콜백을 템플릿에 넣고 재활용하는 것이 편리하다. ( ex : 3장 Calculater.class )
- 템플릿과 콜백의 타입을 다양하게 바뀔 수 있다면 제네릭스를 이용한다.
- 스프링은 JDBC 코드 작성을 위해 jdbcTemplate을 기반으로 하는 다양한 템플릿과 콜백을 제공한다.