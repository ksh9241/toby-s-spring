package practice.spring.toby.chapter3;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionMaker {
	public Connection getConnection () throws SQLException, ClassNotFoundException;
}
