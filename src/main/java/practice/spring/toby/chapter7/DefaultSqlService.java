package practice.spring.toby.chapter7;

public class DefaultSqlService extends BaseSqlService{
	
	public DefaultSqlService () {
		// 생성자에서 디폴트 의존 오브젝트를 직접 만들어서 스스로 DI해준다.
		setSqlReader (new JaxbXmlSqlReader());
		setSqlRegistry(new HashMapSqlRegistry());
	}
}
