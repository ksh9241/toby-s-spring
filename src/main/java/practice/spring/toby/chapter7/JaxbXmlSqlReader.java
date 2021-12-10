package practice.spring.toby.chapter7;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import practice.spring.toby.chapter7.jaxb.SqlType;
import practice.spring.toby.chapter7.jaxb.Sqlmap;

public class JaxbXmlSqlReader implements SqlReader {
	private static final String DEFAULT_SQLMAP_FILE = "/chapter7/sqlmap.xml";
	
	private String sqlmapFile = DEFAULT_SQLMAP_FILE;
	
	public void setSqlmapFile (String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
	}
	
	@Override
	public void read(SqlRegistry sqlRegistry) {
		String contextPath = Sqlmap.class.getPackage().getName();
		
		try {
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStream is = UserDao.class.getResourceAsStream(sqlmapFile);
			Sqlmap sqlMap = (Sqlmap) unmarshaller.unmarshal(is);
			
			for (SqlType sql : sqlMap.getSql()) {
				sqlRegistry.registerSql(sql.getKey(), sql.getValue());
			}
			
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
}
