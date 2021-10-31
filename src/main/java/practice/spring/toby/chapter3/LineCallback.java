package practice.spring.toby.chapter3;

// 각 파일의 라인과 현재 계산된 토탈값 파라미터로 받아서 처리 후 반환한다.
public interface LineCallback<T> {
	T doSomethingWithLine (String line, T value);
}
