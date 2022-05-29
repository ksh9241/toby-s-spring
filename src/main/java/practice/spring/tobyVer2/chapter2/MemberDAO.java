package practice.spring.tobyVer2.chapter2;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MemberDAO {
	public void add(Member m);
	
	public void add(List<Member> members);
	
	public void deleteAll();
	
	@Transactional(readOnly = true)	// readOnly : 읽기 전용 [Default 속성은 false이다] 
	public void count();
	
	public void addMember(Member m);
}
