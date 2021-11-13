package practice.spring.toby.chapter6;

import static org.junit.Assert.assertThrows;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * 클라이언트에 대해 UserService 타입 오브젝트의 하나로서 행세할 수 있다.
 * UserServiceTx는 사용자 관리라는 비즈니스 로직을 전혀 갖지 않고 고스란히 다른 userService 구현 오브젝트에 기능을 위임한다.
 * */ 
public class UserServiceTx implements UserService {
	UserService userService;
	PlatformTransactionManager transactionManager;
	
	public void setTransactionManager (PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	public void setUserService (UserService userService) { // UserService를 구현한 다른 오브젝트를 DI 받는다.
		this.userService = userService;
	}
	
	@Override
	public void upgradeLevels() {
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		try {
			userService.upgradeLevels();
			this.transactionManager.commit(status);
		} catch (Exception e) {
			this.transactionManager.rollback(status);
			throw e;
		}
	}

	@Override
	public void add(User user) {
		userService.add(user);
	}
}
