package practice.spring.toby.chapter6;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class UppercaseHandler implements InvocationHandler {

	Object target;
	
	// 생성된 다이나믹 프록시 오브젝트는 Hello 인터페이스를 구현하고 있으므로 Hello 타입으로 캐스팅해도 안전하다.
	Hello proxiedHello = (Hello) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {Hello.class}, new UppercaseHandler(new HelloTarget()));
	/*
	 첫번째 매개변수 : 동적으로 생성되는 다이나믹 프록시 클래스의 로딩에 사용할 클래스 로더
	 두번째 매개변수 : 구현할 인터페이스
	 세번째 매개변수 : 부가기능과 위임 코드를 담은 InvocationHandler
	 */
	
	private UppercaseHandler (Object target) {
		this.target = target;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object ret = method.invoke(target, args); // 타깃으로 위임. 인터페이스의 메서드 호출에 모두 적용된다.
		if (ret instanceof String && method.getName().startsWith("say")) { // 리턴 타입과 메서드 이름이 일치하는 경우에만 부가기능을 적용한다.
			return ((String)ret).toUpperCase();
		}
		else {
			return ret; // 조건이 일치하지 않으면 타깃 오브젝트의 호출 결과를 그대로 리턴한다.
		}
	}
}
