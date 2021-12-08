package practice.spring.toby;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import practice.spring.toby.chapter7.jaxb.SqlType;
import practice.spring.toby.chapter7.jaxb.Sqlmap;

public class Chapter7JaxbTest {

	@Test
	public void readSqlmap() throws JAXBException, IOException {
		String contextPath = Sqlmap.class.getPackage().getName();
		JAXBContext context = JAXBContext.newInstance(contextPath);
		
		Unmarshaller unmarshaller = context.createUnmarshaller(); // 언마샬러 생성 
		
		System.out.println("Root "+System.getProperty("user.dir")+"\\src\\main\\java\\practice\\spring\\toby\\chapter7\\jaxb");
		
		Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(getClass().getResourceAsStream("sqlmap.xml"));
		
		List<SqlType> sqlList = sqlmap.getSql();
		
		assertThat(sqlList.size(), is(3));
		assertThat(sqlList.get(0).getKey(), is("add"));
		assertThat(sqlList.get(0).getValue(), is("insert"));
		assertThat(sqlList.get(0).getKey(), is("get"));
		assertThat(sqlList.get(0).getValue(), is("select"));	
		assertThat(sqlList.get(0).getKey(), is("delete"));
		assertThat(sqlList.get(0).getValue(), is("delete"));
	}
}
