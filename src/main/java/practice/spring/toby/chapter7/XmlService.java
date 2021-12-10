package practice.spring.toby.chapter7;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import practice.spring.toby.chapter7.jaxb.SqlType;
import practice.spring.toby.chapter7.jaxb.Sqlmap;

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
	
	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		try {
			return this.sqlRegistry.findSql(key);
		} catch (SqlNotFoundException e) {
			throw new SqlRetrievalFailureException(e+"");
		}
	}
	
	@PostConstruct
	public void loadSql() {
		// 빈 후처리기에 의해 오브젝트 생성 이후 sqlReader에게 SQL를 읽어서 sqlRegistry에 저장해둔다.
		this.sqlReader.read(this.sqlRegistry);
	}
	
	@Override
	public void registerSql(String key, String sql) {
		sqlMap.put(key, sql);
	}

	@Override
	public String findSql(String key) throws SqlNotFoundException {
		String sql = sqlMap.get(key);
		
		if (sql == null) throw new SqlNotFoundException(key + " 를 찾을 수 없습니다.");
		return sql;
	}

	@Override
	public void read(SqlRegistry sqlRegistry) {
		String contextPath = Sqlmap.class.getPackage().getName();
		
		try {
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStream is = UserDao.class.getResourceAsStream(this.sqlmapFile); // 프로퍼티로 설정을 통해 제공받은 파일 이름을 사용한다.
			Sqlmap sqlmap = (Sqlmap)unmarshaller.unmarshal(is);
			
			for (SqlType sql : sqlmap.getSql()) {
				sqlRegistry.registerSql(sql.getKey(), sql.getValue());
			}
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
}
