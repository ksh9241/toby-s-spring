package practice.spring.toby.chapter6;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class TransactionAdvice implements MethodInterceptor {
	
	PlatformTransactionManager transactionManager;
	
	public void setTransactionManager (PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	public Object invoke (MethodInvocation invocation) throws Throwable {
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		try {
			// 콜백을  호출해서 타깃의 메서드를 실행한다. 타깃 메서드 호출 전후로 필요한 부가기능을 넣을 수 있다. 경우에 따라서 타깃이 아예 호출되지 않게 하거나 재시도를 위한 반복적인 호출도 가능하다.
			Object ret = invocation.proceed();
			this.transactionManager.commit(status);
			return ret;
		} catch (RuntimeException e) { // JDK 다이나믹 프록시가 제공하는 Method와 달리 스프링의 MethodInvocation을 통한 타깃 호출은 예외가 포장되지 않고 타깃에서 보낸 그대로 전달된다.
			this.transactionManager.rollback(status);
			throw e;
		}
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}
}
