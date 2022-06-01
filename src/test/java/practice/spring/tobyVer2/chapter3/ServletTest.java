package practice.spring.tobyVer2.chapter3;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

public class ServletTest {

	@Test
	public void mockTest() throws ServletException, IOException {
		// 클라이언트 요청정보 설정
		MockHttpServletRequest req = new MockHttpServletRequest("GET", "/hello");
		req.addParameter("name", "String");
		
		// 클라이언트 응답 정보 설정
		MockHttpServletResponse res = new MockHttpServletResponse();
		
		// 테스트용 Servlet 생성
		SimpleGetServlet servlet = new SimpleGetServlet();
		servlet.service(req, res);
		
		assertThat(res.getContentAsString().contains("Hello String"), is(true));
	}
	
	@Test
	public void mockSessionTest() {
		MockHttpSession session = new MockHttpSession();
//		session.putValue("cart", new ShoppingCart(...));
		
//		req.setSession(session);
	}
}
