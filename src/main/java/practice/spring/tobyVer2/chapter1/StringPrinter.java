package practice.spring.tobyVer2.chapter1;

public class StringPrinter implements Printer {
	StringBuffer buffer = new StringBuffer();

	@Override
	public void print(String message) {
		buffer.append(message);
	}
	
	@Override
	public String toString() {
		return this.buffer.toString();
	}

}
