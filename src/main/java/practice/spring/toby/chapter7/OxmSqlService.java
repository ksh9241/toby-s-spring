package practice.spring.toby.chapter7;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.oxm.Unmarshaller;

import practice.spring.toby.chapter7.jaxb.SqlType;
import practice.spring.toby.chapter7.jaxb.Sqlmap;

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
