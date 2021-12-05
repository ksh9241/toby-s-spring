package practice.spring.toby.chapter6;

import static practice.spring.toby.chapter5.UserServiceImple.MIN_LOGCOUNT_FOR_SILVER;
import static practice.spring.toby.chapter5.UserServiceImple.MIN_RECCOMEND_FOR_GOLD;

import java.util.List;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * 클라이언트에 대해 UserService 타입 오브젝트의 하나로서 행세할 수 있다.
 * UserServiceTx는 사용자 관리라는 비즈니스 로직을 전혀 갖지 않고 고스란히 다른 userService 구현 오브젝트에 기능을 위임한다.
 * */ 
public class UserServiceTx implements UserService {
	// userServiceImple의 데코레이터
	UserService userService; 
	PlatformTransactionManager transactionManager;
	UserDao dao;
	
	public void setTransactionManager (PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	public void setUserService (UserService userService) { // UserService를 구현한 다른 오브젝트를 DI 받는다. (프록시 패턴)
		this.userService = userService;
	}
	
	public void setUserDao (UserDao dao) {
		this.dao = dao;
	}
	
	public void upgradeLevels() {
		// 부가기능 수행
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			
			// 위임
			userService.upgradeLevels();
			
		// 부가기능 수행
			transactionManager.commit(status);
		} catch (RuntimeException e) {
			this.transactionManager.rollback(status);
			throw e;
		}
	}
	
	// 메서드 구현과 위임
	@Override
	public void add(User user) {
		userService.add(user);
	}

	@Override
	public User get(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(User user) {
		// TODO Auto-generated method stub
		
	}
}
