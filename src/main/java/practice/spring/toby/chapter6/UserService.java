package practice.spring.toby.chapter6;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

// <tx:method name = "*" />와 같은 설정 효과를 가져온다.
@Transactional
public interface UserService {
	public void add(User user1);
	public void deleteAll();
	public void update(User user);
	public void upgradeLevels ();
	
	// <tx:method name = "get*" />와 같은 설정 효과를 가져온다. 대체정책으로 인한 메서드 우선순위로 읽기전용 트랜잭션 속성이 적용된다.
	@Transactional(readOnly = true)
	public User get(String id);
	
	@Transactional(readOnly = true)
	public List<User> getAll();
}
