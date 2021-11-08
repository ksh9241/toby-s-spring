package practice.spring.toby;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import practice.spring.toby.chapter5.Level;
import practice.spring.toby.chapter5.User;

public class Chapter5UserTest {

	User user;
	
	@Before
	public void setUp() {
		user = new User();
	}
	
	@Test
	public void upgradeLevel() {
		Level[] levels = Level.values();
		for (Level l : levels) {
			if (l.nextLevel() == null) continue;
			user.setLevel(l);
			user.upgradeLevel();
			assertThat(user.getLevel(), is(l.nextLevel()));
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void cannotUpgradeLevel() {
		Level[] l = Level.values();
		for (Level level : l) {
			if (level.nextLevel() != null) continue;
			user.setLevel(level);
			user.upgradeLevel();
		}
	}
}
