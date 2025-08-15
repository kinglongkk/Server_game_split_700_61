package business.global.pk.sss.newsss;

import business.sss.c2s.cclass.newsss.CardRankEnum;
import business.sss.c2s.cclass.newsss.CardSuitEnum;
import business.sss.c2s.cclass.newsss.PockerCard;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Class {@code Poker} 一副扑克牌.
 */
public class Poker {

	private List<PockerCard> cards;
	private Random random;

	public void clean () {
		if (null != this.cards) {
			this.cards.clear();
			this.cards = null;
		}
		this.random = null;
	}

	
	public Poker(int playercount, List<Integer> huase, boolean guipai, boolean jiasanzhan,int count) {
		this.random = new Random();
		this.init(playercount, huase, guipai, jiasanzhan,count);
	}

	private void init(int playercount, List<Integer> huase, boolean guipai, boolean jiasanzhan,int count) {
		this.cards = new ArrayList<PockerCard>();
		if (jiasanzhan) {
			for (CardSuitEnum suitEnum : CardSuitEnum.values()) {
				if (suitEnum != CardSuitEnum.GUI) {
					addYSCards(suitEnum, "");
				}
			}

			if (playercount == 4 || playercount == 5 || playercount == 6) {
				// 加一幅
				for (CardSuitEnum suitEnum : CardSuitEnum.values()) {
					if (suitEnum != CardSuitEnum.GUI) {
						addYSCards(suitEnum, "1");
					}
				}
			} else if (playercount == 7 || playercount == 8) {
				// 加两幅
				for (CardSuitEnum suitEnum : CardSuitEnum.values()) {
					if (suitEnum != CardSuitEnum.GUI) {
						addYSCards(suitEnum, "1");
						addYSCards(suitEnum, "2");
					}
				}
			}

			if (guipai) {
				cards.add(new PockerCard(CardSuitEnum.GUI, CardRankEnum.CARD_XGUI));
				cards.add(new PockerCard(CardSuitEnum.GUI, CardRankEnum.CARD_DGUI));
			}

		} else {
			for (CardSuitEnum suitEnum : CardSuitEnum.values()) {
				if (suitEnum != CardSuitEnum.GUI) {
					addYSCards(suitEnum, "");
				}
			}
			// 加一色
			if (playercount == 5) {
				CardSuitEnum suit = CardSuitEnum.SPADES;
				if (huase.size() == 1) {
					suit = CardSuitEnum.values()[huase.get(0)];

				}
				addYSCards(suit, "1");
			}
			// 加两色
			if (playercount == 6) {
				CardSuitEnum suit1 = CardSuitEnum.SPADES;
				CardSuitEnum suit2 = CardSuitEnum.HEARTS;
				if (huase.size() == 2) {
					suit1 = CardSuitEnum.values()[huase.get(0)];
					suit2 = CardSuitEnum.values()[huase.get(1)];
				}
				addYSCards(suit1, "1");
				addYSCards(suit2, "2");
			}
			// 加三色
			if (playercount == 7) {
				CardSuitEnum suit1 = CardSuitEnum.SPADES;
				CardSuitEnum suit2 = CardSuitEnum.HEARTS;
				CardSuitEnum suit3 = CardSuitEnum.DIAMONDS;
				if (huase.size() == 3) {
					suit1 = CardSuitEnum.values()[huase.get(0)];
					suit2 = CardSuitEnum.values()[huase.get(1)];
					suit3 = CardSuitEnum.values()[huase.get(2)];
				}
				addYSCards(suit1, "1");
				addYSCards(suit2, "2");
				addYSCards(suit3, "3");
			}
			// 加一幅
			if (playercount == 8) {
				CardSuitEnum suit1 = CardSuitEnum.values()[huase.get(0)];
				CardSuitEnum suit2 = CardSuitEnum.values()[huase.get(1)];
				CardSuitEnum suit3 = CardSuitEnum.values()[huase.get(2)];
				CardSuitEnum suit4 = CardSuitEnum.values()[huase.get(3)];
				addYSCards(suit1, "1");
				addYSCards(suit2, "2");
				addYSCards(suit3, "3");
				addYSCards(suit4, "4");
			}

			if (guipai) {
				cards.add(new PockerCard(CardSuitEnum.GUI, CardRankEnum.CARD_XGUI));
				cards.add(new PockerCard(CardSuitEnum.GUI, CardRankEnum.CARD_DGUI));
			}
		}

		Collections.shuffle(this.cards);		
		for (int i = 0; i<count;i++) {
			Collections.shuffle(this.cards);
		}
	}

	private void addYSCards(CardSuitEnum suit, String flag) {
		for (CardRankEnum rankEnum : CardRankEnum.values()) {
			if (rankEnum.getNumber() < 15) {
				if ("".equals(flag)) {
					cards.add(new PockerCard(suit, rankEnum));
				} else {
					cards.add(new PockerCard(suit, rankEnum, flag));
				}
			}
		}
	}

	public int getSize() {
		return this.cards.size();
	}

	public PockerCard dispatch() {
		return cards.remove(random.nextInt(cards.size()));
	}

	public static void main(String[] args) {
		
	}
}
