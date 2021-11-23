package practice.spring.toby.chapter6;

import org.springframework.beans.factory.FactoryBean;

public class MessageFactoryBean implements FactoryBean<Message> {
	String text;
	
	public void setText (String text) { // 오브젝트를 생성할 때 필요한 정보를 팩토리 빈의 프로퍼티로 설정해서 대신 DI 받을 수 있게 한다.
		this.text = text;
	}
	
	@Override
	public Message getObject() throws Exception { // 실제 빈으로 사용될 오브젝트를 직접 생성한다.
		return Message.newMessage(this.text);
	}
	@Override
	public Class<?> getObjectType() {
		return Message.class;
	}
	@Override
	public boolean isSingleton() { // getObject() 메서드가 돌려주는 오브젝트가 싱글톤인지 알려준다. 이 팩토리 빈은 매번 요청할 때마다 새로운 오브젝트를 만들므로 false로 설정한다.
		return false;			   // 이것은 팩토리 빈의 동작방식에 관한 설정이고 만들어진 빈 오브젝트는 싱글톤으로 스프링이 관리해줄 수 있다.
	}
}
