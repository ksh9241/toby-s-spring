package practice.spring.tobyVer2.chapter1;

import java.util.Date;

import javax.inject.Provider;

import org.springframework.beans.factory.annotation.Autowired;

public class LoginService {

	@Autowired 
	Provider<LoginUser> loginUserProvider;	// DL방식으로 접근할 수 있도록 Provider로 DI 받는다.
	
	public void login(Object login) {
		// 로그인처리
		LoginUser user = loginUserProvider.get();   // 같은 사용자의 세션 안에서는 매번 같은 오브젝트를 가져온다.
		user.setLoginId("a");
		user.setName("b");
		user.setLoginTime(new Date());
	}
	
}
