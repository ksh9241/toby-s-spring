package practice.spring.tobyVer2.chapter2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @Description : 트랜잭션 경계 설정 예제
 * 트랜잭션 기본 속성을 변경하려면 TransactionTemplate을 만들 때 TransactionDefinition 오브젝트를 만들어서 파라미터로 제공하주면 된다.
 * */
public class TransactionConfig {

	@Autowired
	private MemberDAO memberDao;
	private TransactionTemplate transactionTemplate;
	
	@Autowired
	public void init(PlatformTransactionManager transactionManager) {
		this.transactionTemplate = new TransactionTemplate(transactionManager);
	}
	
	public void addMembers(final List<Member> members) {
		this.transactionTemplate.execute(new TransactionCallback() {
			@Override
			public Object doInTransaction(TransactionStatus status) {	// 트랜잭션 안에서 동작하는 코드, 트랝개션 매니저와 연결되어 있는 모든 DAO는 같은 트랜잭션에 참여한다.
				for (Member m : members) {
					memberDao.addMember(m);
				}
				return null;	// 정상적으로 작업을 마치고 리턴되면 트랜잭션은 커밋된다.
			}
		});
	}
}
