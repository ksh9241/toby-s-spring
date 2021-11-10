package practice.spring.toby.chapter5;
import static practice.spring.toby.chapter5.UserServiceImple.MIN_LOGCOUNT_FOR_SILVER;
import static practice.spring.toby.chapter5.UserServiceImple.MIN_RECCOMEND_FOR_GOLD;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


@Service
public class UserTransactionExceptionService {
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	DataSource dataSource;
	
	@Autowired
	PlatformTransactionManager transactionManager;
	
	@Autowired
	MailSender mailSender;
	
	private String id;
	public UserTransactionExceptionService() {}
	
	public UserTransactionExceptionService (String id) {
		this.id = id;
	}
	
	public void setTransactionManager (PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	public void setUserDao (UserDao userDao) {
		this.userDao = userDao;
	}
	
	public void setMailSender (MailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	public void upgradeLevels() {
		// 트랜잭션 추상화 API를 적용
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition()); 
		// DefaultTransactionDefinition 오브젝트는 트랜잭션에 대한 속성을 담고 있다.
		// TransactionStatus는 트랜잭션에 대한 조작이 필요할 때 PlatformTransactionManager 메서드의 파라미터로 전달해주면 된다.
		
//		TransactionSynchronizationManager.initSynchronization(); // 트랜잭션 동기화 관리자를 이용해 동기화 작업을 초기화한다.
//		Connection c = null;
//		System.out.println("dataSource " + dataSource);
//		c = DataSourceUtils.getConnection(dataSource); // DB 커넥션을 생성하고 트랜잭션을 시작한다. 이후의 DAO작업은 모두 여기서 시작한 트랜잭션 안에서 진행된다.
//		c.setAutoCommit(false);
		
		try {
			List<User> users = userDao.getAll();
			for (User user : users) {
				if (changeUpgradeLevel(user)) {
					upgradeLevel(user);
				}
			}
			transactionManager.commit(status);
		} catch (Exception e) {
			transactionManager.rollback(status);
			throw e;
		} 
//		finally {
//			// 스프링 유틸리티 메서드를 이용해 DB 커넥션을 안전하게 닫는다.
//			DataSourceUtils.releaseConnection(c, dataSource);
//			
//			// 동기화 작업 종료 및 정리
//			TransactionSynchronizationManager.unbindResource(this.dataSource);
//			TransactionSynchronizationManager.clearSynchronization();
//		}
	}
	
	private void upgradeLevel(User u) {
		if (u.getId().equals(this.id)) throw new TestUserServiceException();
		
		u.upgradeLevel();
		userDao.update(u);
		sendUpgradeEmail(u);
	}
	
	private void sendUpgradeEmail(User u) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(u.getEmail());
		mailMessage.setFrom("useradmin@ksug.org");
		mailMessage.setSubject("Upgrade 안내");
		mailMessage.setText("사용자님의 등급이 " + u.getLevel().name());
		
		mailSender.send(mailMessage);
	}

	private boolean changeUpgradeLevel (User user) {
		Level currentLevel = user.getLevel();
		switch (currentLevel) {
			case BASIC : return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
			case SILVER : return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
			case GOLD : return false;
			default : throw new IllegalArgumentException("Unknown Level : " + currentLevel);
		}
	}

	public void add(User user) {
		if (user.getLevel() == null) user.setLevel(Level.BASIC);
		userDao.add(user);
	}

	

}
