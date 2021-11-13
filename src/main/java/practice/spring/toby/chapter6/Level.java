package practice.spring.toby.chapter6;

public enum Level {
	 GOLD(3, null), SILVER(2, GOLD), BASIC(1, SILVER); // 이늄 선언에 DB에 저장할 값과 함께 다음 단계의 레벨 정보도 추가한다.
	
	private final int value;
	private final Level next;
	
	Level(int value, Level next) {
		this.value = value;
		this.next = next;
	}
	
	public int intValue() { // 값을 가져오는 메서드 
		return value;
	}
	
	public Level nextLevel() {
		return this.next;
	}
	
	public static Level valueOf(int value) {
		switch (value) {
		case 1 : return BASIC;
		case 2 : return SILVER;
		case 3 : return GOLD;
		default : throw new AssertionError("Unknown value : " + value);
		}
	}
}
