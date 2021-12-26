package practice.spring.toby.chapter7;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import practice.spring.toby.chapter7.Level;
import practice.spring.toby.chapter7.User;
import practice.spring.toby.chapter7.UserDao;
import practice.spring.toby.chapter7.config.applicationContextConfig;

@ContextConfiguration(classes = applicationContextConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ActiveProfiles("test")
public class Chapter7UserDaoTest {
	
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMEND_FOR_GOLD = 30;

	@Autowired
	UserDao userDao;
	
	List<User> users;
	
	@Autowired
	DefaultListableBeanFactory bf;
	
	@Before
	public void setUp() {
		users = Arrays.asList(
				new User("1","1","1",Level.BASIC,MIN_LOGCOUNT_FOR_SILVER - 1, 0, "NO"),	
				new User("2","2","2",Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "YES"),	
				new User("3","3","3",Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD - 1, "NO"),	
				new User("4","4","4",Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD, "YES"),	
				new User("5","5","5",Level.GOLD, 100, 100, "NO")	
			);
	}
	
	@Test
	public void findAllBeanNames() {
		System.out.println("============== findAllBeanNames Start =================");
		for (String n : bf.getBeanDefinitionNames()) {
			System.out.println(bf.getBean(n).getClass().getName());
		}
		System.out.println("============== findAllBeanNames End =================");
	}
	
	@Test
	@Transactional ()
	public void addTest() {
		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));
		
		for (User user : users) {
			userDao.add(user);
		}
		assertThat(userDao.getCount(), is(5));
	}
}


