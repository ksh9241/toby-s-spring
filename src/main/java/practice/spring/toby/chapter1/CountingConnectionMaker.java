package practice.spring.toby.chapter1;

import java.sql.Connection;
import java.sql.SQLException;

public class CountingConnectionMaker implements ConnectionMaker {
	int counter = 0;
	private ConnectionMaker realConnectionMaker;
	
	public CountingConnectionMaker () {}
	
	public CountingConnectionMaker (ConnectionMaker realConnectionMaker) {
		this.realConnectionMaker = realConnectionMaker;
	}
	
	public int getCounter() {
		return this.counter;
	}
	
	@Override
	public Connection getConnection() throws SQLException, ClassNotFoundException {
		counter++;
		return realConnectionMaker.getConnection();
	}

}
