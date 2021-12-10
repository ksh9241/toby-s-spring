package practice.spring.toby;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import practice.spring.toby.chapter7.SqlService;
import practice.spring.toby.chapter7.XmlService;
import practice.spring.toby.chapter7.jaxb.SqlType;
import practice.spring.toby.chapter7.jaxb.Sqlmap;

public class Chapter7JaxbTest {

	@Test
	public void readSqlmap() throws JAXBException, IOException {
		String contextPath = Sqlmap.class.getPackage().getName();
		JAXBContext context = JAXBContext.newInstance(contextPath);
		
		Unmarshaller unmarshaller = context.createUnmarshaller(); // 언마샬러 생성 
		
		Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(new ClassPathResource("/chapter7/sqlmap.xml").getInputStream());
		
		List<SqlType> sqlList = sqlmap.getSql();
		
		assertThat(sqlList.size(), is(6));
		assertThat(sqlList.get(0).getKey(), is("userAdd"));
		assertThat(sqlList.get(1).getKey(), is("userGet"));
		assertThat(sqlList.get(2).getKey(), is("userGetAll"));
		assertThat(sqlList.get(3).getKey(), is("userGetCount"));	
		assertThat(sqlList.get(4).getKey(), is("userDeleteAll"));
		assertThat(sqlList.get(5).getKey(), is("userUpdate"));
	}
}
