package practice.spring.toby.chapter7;

import java.util.Map;

public class SqlAdminService implements AdminEventListener {
	private UpdatableSqlRegistry updatableSqlRegistry;
	
	public void setSqlRegistry (UpdatableSqlRegistry updatableSqlRegistry) {
		this.updatableSqlRegistry = updatableSqlRegistry;
	}
	
	

}
