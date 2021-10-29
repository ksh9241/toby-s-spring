package practice.spring.toby.chapter3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
	String path = Calculator.class.getResource("").getPath();
	
	public Integer calcSum(String fileName) throws IOException {
		LineCallback callback = new LineCallback() {
			@Override
			public Integer doSomethingWithLine(String line, Integer value) {
				return value += Integer.valueOf(line);
			}
		};
		return LineReaderTemplate(fileName, callback, 0);
	}
	
	public int calcMul(String fileName) throws IOException {
		LineCallback callback = new LineCallback() {
			@Override
			public Integer doSomethingWithLine(String line, Integer value) {
				return value *= Integer.valueOf(line);
			}
		};
		return LineReaderTemplate(fileName, callback, 1);
	}
	
	public Integer LineReaderTemplate (String fileName, LineCallback callback, int initVal) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(path + fileName))){
			Integer res = initVal;
			String line = null;
			while ((line = br.readLine()) != null) {
				res = callback.doSomethingWithLine(line, res);
			}
			return res;
		} catch (IOException e) {
			throw e;
		}
	}
	
	public Integer fileReaderTemplate (String fileName, BufferedReaderCallback callback) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(path + fileName))){
			int result = callback.doSomethingWithReader(br);
			return result;
		} catch (IOException e) {
			throw e;
		}
	}
}
