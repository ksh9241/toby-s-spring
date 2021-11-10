package practice.spring.toby;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.aspectj.lang.annotation.AdviceName;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import practice.spring.toby.chapter5.Level;
import practice.spring.toby.chapter5.User;
import practice.spring.toby.chapter5.UserDaoJdbc;
import practice.spring.toby.chapter5.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/chapter5/applicationContext.xml")
public class chapter5UserDaoTest {

	@Autowired
	UserDaoJdbc dao;
	
	@Autowired
	UserService service;
	
	User user1;
	User user2;
	User user3;
	
	private void checkSameUser (User user1, User user2) {
		assertThat(user1.getId(), is(user2.getId()));
		assertThat(user1.getName(), is(user2.getName()));
		assertThat(user1.getPassword(), is(user2.getPassword()));
		assertThat(user1.getLogin(), is(user2.getLogin()));
		assertThat(user1.getRecommend(), is(user2.getRecommend()));
		assertThat(user1.getLevel(), is(user2.getLevel()));
	}
	
	@Before
	public void setUp() {
		user1 = new User("gyumee", "박성철", "springno1", Level.BASIC, 1, 0, null);
		user2 = new User("leegw700", "이길원", "springno2", Level.SILVER, 55, 10, null);
		user3 = new User("bumjin", "박범진", "springno3", Level.GOLD, 100, 40, null);
	}
	
	@Test
	public void addAndGet() {
		dao.deleteAll();
		
		dao.add(user1);
		dao.add(user2);
		dao.add(user3);
		
		User getUser1 = dao.get(user1.getId());
		checkSameUser(getUser1, user1);
		
		User getUser2 = dao.get(user2.getId());
		checkSameUser(getUser2, user2);
	}
	
	@Test
	@AdviceName("쿼리문 실수방지를 위한 user2")
	public void update() { 
		dao.deleteAll();
		
		dao.add(user1);	// 수정한 사용자
		dao.add(user2);	// 수정하지 않은 사용자
		
		user1.setName("변경맨");
		user1.setPassword("바뀐비번");
		user1.setLevel(Level.GOLD);
		user1.setLogin(1000);
		user1.setRecommend(999);
		
		dao.update(user1);
		
		User changeUser = dao.get(user1.getId());
		checkSameUser(changeUser, user1);
		
		User nonChangeUser = dao.get(user2.getId());
		checkSameUser(nonChangeUser, user2);
	}
	
	@Test
	public void getAll () {
		dao.deleteAll();
		dao.add(user1);
		dao.add(user2);
		dao.add(user3);
		
		assertThat(dao.getAll().size(), is(3));
	}
	
	@Test
	public void bean() {
		assertNotNull(service);
	}
}
