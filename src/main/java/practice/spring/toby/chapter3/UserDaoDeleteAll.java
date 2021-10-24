package practice.spring.toby.chapter3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// 템플릿 메서드 패턴을 통한 동적 기능 확장
public class UserDaoDeleteAll extends UserDao{

	@Override
	protected PreparedStatement makeStatement(Connection c) throws SQLException {
		PreparedStatement ps = c.prepareStatement("DELETE FROM users");
		return ps;
	}

}
