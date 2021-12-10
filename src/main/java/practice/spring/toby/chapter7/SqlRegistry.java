package practice.spring.toby.chapter7;

public interface SqlRegistry {
	void registerSql (String key, String sql); // SqlReader는 읽어들인 SQL을 이 메서드를 이용해 레지스트리에 저장한다.
	
	String findSql (String key) throws SqlNotFoundException;
}
