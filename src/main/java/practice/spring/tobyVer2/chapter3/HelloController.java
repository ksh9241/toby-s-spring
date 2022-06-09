package practice.spring.tobyVer2.chapter3;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.xml.MarshallingView;

public class HelloController {

	@Autowired
	MarshallingView helloMarsharllingView;
	
	public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception{
		Map<String, Object> model = new HashMap<>();
		model.put("info", new Info("Hello" + req.getParameter("name")));
		
		return new ModelAndView(helloMarsharllingView, model);
	}
}
