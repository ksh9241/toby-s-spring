package practice.spring.toby.chapter6;

public class Message {

	String text;
	
	// 생성자가 private으로 선언되어 있어서 외부에서 생성자를 통한 오브젝트를 만들 수 없다.
	private Message (String text) {
		this.text = text;
	}
	public String getMessage() {
		return this.text;
	}
	
	// 생성자 메서드 대신 사용할 수 있는 스태틱 팩토리 메서드를 제공한다.
	public static Message newMessage (String text) {
		return new Message(text);
	}
}
