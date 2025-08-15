package business.sss.c2s.cclass.newsss;

/**
 * Enum {@code CardSuitEnum} 扑克牌的花色 红桃A, 方块B, 黑桃C, 梅花D, E 大小王
 */
public enum CardSuitEnum {
	/**
	 * 红桃
	 */
	HEARTS("A"),
	/**
	 * 方块
	 */
	DIAMONDS("B"),
	/**
	 * 黑桃
	 */
	SPADES("C"),
	/**
	 * 梅花
	 */
	CLUBS("D"),
	/**
	 * 鬼牌
	 */
	GUI("E");

	private String name;

	CardSuitEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumber() {
		switch (this.name) {
		case "A":
			return 2;
		case "B":
			return 0;
		case "C":
			return 3;
		case "D":
			return 1;
		case "E":
			return 4;
		default:
			return 0;
		}
	}

}
