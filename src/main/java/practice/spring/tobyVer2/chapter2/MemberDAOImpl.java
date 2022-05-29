package practice.spring.tobyVer2.chapter2;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class MemberDAOImpl implements MemberDAO{
	
	SimpleJdbcInsert insert;
	SimpleJdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
		this.insert = new SimpleJdbcInsert(dataSource).withTableName("member");
	}

	@Override
	public void add(Member m) {
	}

	@Override
	public void add(List<Member> members) {
	}

	@Override
	public void deleteAll() {
	}

	@Override
	public void count() {
	}

	@Override
	public void addMember(Member m) {
	}

}
