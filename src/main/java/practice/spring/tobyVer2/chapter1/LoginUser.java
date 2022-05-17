package practice.spring.tobyVer2.chapter1;

import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * 프록시 빈이 인터페이스를 구현하고 있고, 클라이언트에서 인터페이스를 DI 받는다면 proxyMode를 ScopedProxyMode.INTERFACES로 지정해주고, 
 * 프록시 빈 클래스를 직접 DI한다면 ScopedProxyMode.TARGET_CLASS로 지정하면 된다.
 * */
@Scope(value="session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class LoginUser {

	String loginId;
	String name;
	Date loginTime;
	
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}
}
