package business.sss.c2s.cclass.newsss;

import java.io.Serializable;

/**
 * Class {@code Card} 一张扑克牌.
 */
public class PockerCard implements Serializable,Comparable<PockerCard> {

	private CardSuitEnum suit;
	private CardRankEnum rank;

	private String key;
	public int type;// 0-3 方块 梅花 红桃 黑桃
	public int cardID;// 1-13
	public int ownnerPos = -1;

	public PockerCard(CardSuitEnum suit, CardRankEnum rank) {
		this.suit = suit;
		this.rank = rank;
		this.cardID = this.rank.getNumber() - 1;
		this.type = this.suit.getNumber();

		if (suit == CardSuitEnum.GUI) {
			if (rank == CardRankEnum.CARD_DGUI) {
				this.key = "0x41";
			} else if (rank == CardRankEnum.CARD_XGUI) {
				this.key = "0x42";
			}
		} else {
			this.key = "0x" + String.valueOf(this.type) + Integer.toHexString(this.cardID + 1);
		}
	}

	public PockerCard(CardSuitEnum suit, CardRankEnum rank, String keyflag) {
		this.suit = suit;
		this.rank = rank;
		this.cardID = this.rank.getNumber() - 1;
		this.type = this.suit.getNumber();

		if (suit == CardSuitEnum.GUI) {
			if (rank == CardRankEnum.CARD_DGUI) {
				this.key = "0x41";
			} else if (rank == CardRankEnum.CARD_XGUI) {
				this.key = "0x42";
			}
		} else {
			this.key = "0x" + String.valueOf(this.type) + Integer.toHexString(this.cardID + 1) + keyflag;
		}

	}

	public PockerCard(String key) {
		this.type = Integer.parseInt(key.substring(2, 3));
		this.key = key;

		switch (this.type) {
		case 0:
			this.suit = CardSuitEnum.DIAMONDS;
			break;
		case 1:
			this.suit = CardSuitEnum.CLUBS;
			break;
		case 2:
			this.suit = CardSuitEnum.HEARTS;
			break;
		case 3:
			this.suit = CardSuitEnum.SPADES;
			break;
		case 4:
			this.suit = CardSuitEnum.GUI;
			break;
		default:
			this.suit = CardSuitEnum.DIAMONDS;
			break;
		}
		if (this.suit == CardSuitEnum.GUI) {
			this.cardID = Integer.parseInt(key.substring(3, 4), 16);
			if (this.cardID == 1) {
				this.rank = CardRankEnum.CARD_DGUI;
			} else if (this.cardID == 2) {
				this.rank = CardRankEnum.CARD_XGUI;
			}
		} else {
			this.cardID = Integer.parseInt(key.substring(3, 4), 16) - 1;
			this.rank = CardRankEnum.values()[this.cardID - 1];
		}
	}

	public CardSuitEnum getSuit() {
		return suit;
	}

	public int getRankNumber() {
		return this.rank.getNumber();
	}

	public void setSuit(CardSuitEnum suit) {
		this.suit = suit;
	}

	public CardRankEnum getRank() {
		return rank;
	}

	public void setRank(CardRankEnum rank) {
		this.rank = rank;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
            return true;
        }
		if (o == null || getClass() != o.getClass()) {
            return false;
        }

		PockerCard card = (PockerCard) o;

		if (this.suit.getName() != card.suit.getName()) {
            return false;
        }
		return this.rank.getNumber().equals(card.rank.getNumber());

	}

	@Override
	public int hashCode() {
		return this.suit.ordinal() + this.rank.getNumber();
	}

	public String toString2() {
		return this.suit.getName() + this.rank.getNumber() + "【" + key + "】";
	}

	@Override
	public String toString() {
		return key;
	}
	
	

	public String getKey() {
		return key;
	}

	/**
	 * 实现Comparable接口, 获取最大的单牌, 直接使用牌的数字大小比较即可 使用降序排序, 因为第一个Card极为单牌最大值
	 *
	 * @param o
	 * @return
	 */
	@Override
	public int compareTo(PockerCard o) {
		int selfNumber = this.rank.getNumber();
		int otherNumber = o.rank.getNumber();

		if (selfNumber < otherNumber) {
			return 1;
		}
		if (selfNumber > otherNumber) {
			return -1;
		}
		return 0;
	}
}
