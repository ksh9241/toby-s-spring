package practice.spring.toby.chapter7;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Unmarshaller;

import practice.spring.toby.chapter7.jaxb.SqlType;
import practice.spring.toby.chapter7.jaxb.Sqlmap;

public class OxmSqlService implements SqlService{
	private final BaseSqlService baseSqlService = new BaseSqlService(); // SqlService의 실제 구현 부분을 위임할 대상인 BaseSqlService를 인스턴스 변수로 정의해둔다.
	private final OxmSqlReader oxmSqlReader = new OxmSqlReader();
	private SqlRegistry sqlRegistry = new HashMapSqlRegistry();
	
	public void setSqlmap (Resource sqlmap) {
		this.oxmSqlReader.setSqlmap(sqlmap);
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
	
	private class OxmSqlReader implements SqlReader{
		private Unmarshaller unmarshaller;
		private Resource sqlmap = new ClassPathResource("/chapter7/sqlmap.xml");
		
		public void setSqlmap (Resource sqlmap) {
			this.sqlmap = sqlmap;
		}
		
		@Override
		public void read(SqlRegistry sqlRegistry) {
			
			try {
				Source source = new StreamSource (sqlmap.getInputStream());
				Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(source); // OxmSqlService를 통해 전달받은 OXM 인터페이스 구현 오브젝트를 가지고 언마샬링 작업 수행
				
				for (SqlType sql : sqlmap.getSql()) {
					System.out.println(sql.getKey() + " : " + sql.getValue());
					sqlRegistry.registerSql(sql.getKey(), sql.getValue());
				}
				
			} catch (IOException e) {
				// 언마샬 작업 중 IO 에러가 났다면 설정을 통해 제공받은 XML 파일 이름이나 정보가 잘못됐을 가능성이 제일 높다. 이런 경우에 가장 적합한 런타임 예외 중 하나인 IllegalArgumentException으로 포장해서 던진다.
				throw new IllegalArgumentException(this.sqlmap.getFilename() + " 을 가져올 수 없습니다.", e);
			}
		}
	}
}
