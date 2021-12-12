package practice.spring.toby.chapter7;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import practice.spring.toby.chapter7.jaxb.SqlType;
import practice.spring.toby.chapter7.jaxb.Sqlmap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/chapter7/OxmTest-context.xml")
public class OxmTest {

	@Autowired
	Unmarshaller unmarshaller;
	
	@Test
	public void unmarshallerSqlMap () throws XmlMappingException, IOException {
		Source xmlSource = new StreamSource(getClass().getResourceAsStream("/chapter7/sqlmap.xml"));	// InputStream을 이용한 Source 타입의 StreamSource를 만든다.
		
		Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(xmlSource); // 어떤 OXM 기술이든 언마샬은 이 한줄이면 끝이다.
		
		List<SqlType> sqlList = sqlmap.getSql();
		assertThat(sqlList.size(), is(6));
	}
	
	@Test
	public void OxmSqlServiceTest () {
		OxmSqlService sqlService = new OxmSqlService();
		sqlService.setUnmarshaller(unmarshaller);
		
		assertThat(sqlService.getSql("userGetAll"), is("SELECT * FROM users"));
	}
}
