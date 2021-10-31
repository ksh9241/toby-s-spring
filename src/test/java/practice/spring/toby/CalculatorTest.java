package practice.spring.toby;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import practice.spring.toby.chapter3.Calculator;


//@RunWith(SpringJUnit4ClassRunner.class) ApplicationContext가 필요없을 경우 주석 제거
public class CalculatorTest {
	Calculator calculator;
	String fileName;
	
	@Before
	public void setUp () {
		calculator = new Calculator();
		fileName = "number.txt";
	}

	@Test
	public void sumOfNumbers() throws IOException {
		int sum = calculator.calcSum(fileName);
		assertThat(sum, is(10));
	}
	
	@Test
	public void multipleOfNumbers() throws IOException {
		int total = calculator.calcMul(fileName);
		assertThat(total, is(24));
	}
	
	@Test
	public void concatenate() throws IOException{
		String result = calculator.concatenate(fileName);
		assertThat(result, is("1234"));
	}
}
