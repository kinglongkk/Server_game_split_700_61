package business.sss.c2s.cclass.newsss;

/**
 * Enum {@code CardRankEnum} 扑克牌的数字.
 */
public enum CardRankEnum {
    CARD_TWO(2),
    CARD_THREE(3),
    CARD_FOUR(4),
    CARD_FIVE(5),
    CARD_SIX(6),
    CARD_SEVEN(7),
    CARD_EIGHT(8),
    CARD_NINE(9),
    CARD_TEN(10),
    CARD_JACK(11),
    CARD_QUEUE(12),
    CARD_KING(13),
    CARD_ACE(14),
	CARD_XGUI(15),
	CARD_DGUI(16);

    private Integer number;

    CardRankEnum(Integer number) {
        this.number = number;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
    
    public static CardRankEnum valueOf(int value) {  
		 for (CardRankEnum flow : CardRankEnum.values()) {
				if (flow.getNumber() == value) {
					return flow;
				}
			}
		 System.out.println("CardRankEnum not Card.........");
		 return null;
	 }  
}
