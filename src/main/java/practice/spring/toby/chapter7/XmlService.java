package practice.spring.toby.chapter7;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import practice.spring.toby.chapter7.jaxb.SqlType;
import practice.spring.toby.chapter7.jaxb.Sqlmap;

public class XmlService implements SqlService {
	private String sqlmapFile;
	Map<String, String> sqlMap = new HashMap<>();
	
	// 오브젝트의 초기화 방법
	public XmlService() {
		XmlService sqlProvider = new XmlService();
		sqlProvider.setSqlmapFile("/chapter7/sqlmap.xml");
		sqlProvider.loadSql();
	}

	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		String sql = sqlMap.get(key);
		if (sql == null) throw new SqlRetrievalFailureException(key + " 를 이용해서 SQL을 찾을 수 없습니다.");
		return sql;
	}
	
	public void setSqlmapFile(String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
	}
	
	public void loadSql () {
		String contextPath = Sqlmap.class.getPackage().getName();
		
		try {
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStream is = UserDao.class.getResourceAsStream(this.sqlmapFile); // 프로퍼티로 설정을 통해 제공받은 파일 이름을 사용한다.
			Sqlmap sqlmap = (Sqlmap)unmarshaller.unmarshal(is);
			
			for (SqlType sql : sqlmap.getSql()) {
				sqlMap.put(sql.getKey(), sql.getValue());
			}
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
}
