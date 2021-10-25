package practice.spring.toby.chapter3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// 전략패턴
// 동적인 기능을 인터페이스로 만들어서 확장하는 방법
public interface StatementStrategy {

	PreparedStatement makePreparedStatement(Connection c) throws SQLException;
}
