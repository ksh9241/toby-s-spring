package practice.spring.toby.chapter5;

import java.util.ArrayList;
import java.util.List;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class MockMailSender implements MailSender {
	private List<String> requests = new ArrayList<>();

	public List<String> getRequests() {
		return this.requests;
	}
	
	@Override
	public void send(SimpleMailMessage simpleMessage) throws MailException {
		requests.add(simpleMessage.getTo()[0]); // 전송 요청을 받은 이메일 주소를 저장해두고 이를 읽을 수 있게 한다.
	}

	@Override
	public void send(SimpleMailMessage[] simpleMessages) throws MailException {
	}

}
