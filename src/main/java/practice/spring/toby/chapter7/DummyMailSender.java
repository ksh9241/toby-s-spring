package practice.spring.toby.chapter7;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

// 인터페이스를 구현할 뿐 메서드 오버라이드는 하지않았다.
public class DummyMailSender implements MailSender {

	@Override
	public void send(SimpleMailMessage simpleMessage) throws MailException {
		System.out.println("메시지 발송 테스트 성공" + simpleMessage);
	}

	@Override
	public void send(SimpleMailMessage[] simpleMessages) throws MailException {
		// TODO Auto-generated method stub

	}

}
