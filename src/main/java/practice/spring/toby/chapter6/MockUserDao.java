package practice.spring.toby.chapter6;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class MockUserDao implements UserDao {

	private List<User> users; 						// 레벨 업그레이드 후보 User 오브젝트 목록
	private List<User> updated = new ArrayList<>(); // 업그레이드 대상 오브젝트를 저장해둘 목록

	public MockUserDao (List<User> users) {
		this.users = users;
	}
	
	public List<User> getUpdate() {
		return this.updated;
	}
	
	@Override
	public List<User> getAll() {
		return this.users;
	}
	
	@Override
	public void update(User user) {
		updated.add(user);
	}
	
	
	// 테스트에 사용되지 않는 메서드
	@Override
	public void setDataSource(DataSource dataSource) {
	}

	@Override
	public void add(User user) { throw new UnsupportedOperationException(); }

	@Override
	public User get(String id) { throw new UnsupportedOperationException(); }

	@Override
	public void deleteAll() { throw new UnsupportedOperationException(); }
	
	@Override
	public int getCount() { throw new UnsupportedOperationException(); }

}
