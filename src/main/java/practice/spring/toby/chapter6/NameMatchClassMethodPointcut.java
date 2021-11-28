package practice.spring.toby.chapter6;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.util.PatternMatchUtils;

public class NameMatchClassMethodPointcut extends NameMatchMethodPointcut{
	private static final long serialVersionUID = 1L;
	
	public void setMappedClassName (String mappedClassName) {
		this.setClassFilter(new SimpleClassFilter(mappedClassName));
	}
	
	static class SimpleClassFilter implements ClassFilter{
		String mappedName;
		
		private SimpleClassFilter (String mappedName) {
			this.mappedName = mappedName;
		}
		
		@Override
		public boolean matches(Class<?> clazz) {
			return PatternMatchUtils.simpleMatch(mappedName, clazz.getSimpleName()); // 와일드카드(*) 가 들어간 문자열 비교를 지원하는 스프링의 유틸리티 메서드다. [*name, name*, *name* 지원]
		}
	}
}
