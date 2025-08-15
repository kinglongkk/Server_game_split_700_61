package business.global.pk.sss.newsss;

import business.global.pk.sss.newsss.comparing.ComparingFacade;
import business.global.pk.sss.newsss.comparing.IComparing;
import business.global.pk.sss.newsss.ranking.RankingResult;
import business.sss.c2s.cclass.newsss.CardRankEnum;
import business.sss.c2s.cclass.newsss.CardSuitEnum;
import business.sss.c2s.cclass.newsss.PockerCard;
import business.sss.c2s.cclass.newsss.RankingEnum;


import java.io.Serializable;
import java.util.*;

/**
 * 一个玩家, 持有3或者5张牌, 并伴随牌型的属性.
 */
@SuppressWarnings("serial")
public class PlayerDun implements Comparable<PlayerDun> ,Serializable {
	private List<PockerCard> cards = new ArrayList<PockerCard>(); // 牌
	private RankingResult rankingResult; // 牌型校验结果
	private int shuicnt = 0;

	public PlayerDun() {
	}

	public PlayerDun addData(List<PockerCard> cards) {
		this.cards = cards;
		Collections.sort(this.cards);
		return this;
	}

	public PlayerDun(List<String> list) {
		this.cards = new ArrayList<PockerCard>();
		for (int i = 0; i < list.size(); i++) {
			this.cards.add(new PockerCard(list.get(i)));
		}
		Collections.sort(this.cards);
	}

	public List<String> get0xStr() {
		List<String> ret = new ArrayList<String>();
		cards.forEach(PockerCard -> {
			ret.add(PockerCard.toString());
		});
		return ret;
	}

	public int getShuiCount() {
		return this.shuicnt;
	}

	public void addShuiCount(int count) {
		this.shuicnt += count;
	}

	/**
	 * 获得手上的牌的张数
	 *
	 * @return
	 */
	public int getCardSize() {
		return this.cards.size();
	}

	/**
	 * 增加手牌
	 *
	 * @param PockerCard
	 */
	public void addCard(PockerCard PockerCard) {
		this.cards.add(PockerCard);
		Collections.sort(this.cards);
	}

	public List<PockerCard> getCards() {
		return cards;
	}

	public List<String> getCardsString() {
		List<String> ret = new ArrayList<String>();
		for (int i = 0; i < cards.size(); i++) {
			ret.add(cards.get(i).toString());
		}
		return ret;
	}

	public ArrayList<CardRankEnum> getRanks() {
		ArrayList<CardRankEnum> ret = new ArrayList<CardRankEnum>();
		for (int i = 0; i < cards.size(); i++) {
			if (cards.get(i).getSuit() != CardSuitEnum.GUI) {
				ret.add(cards.get(i).getRank());
			}
		}
		return ret;
	}

	public RankingResult getRankingResult() {
		if (rankingResult == null) {
			rankingResult = new RankingResult();
			rankingResult.setRankingEnum(RankingEnum.HIGH_CARD);
			rankingResult.setHighCard(this.cards.get(0));
		}
		return rankingResult;
	}

	public Map<Integer, Integer> getCardsRankCountMap() {
		List<PockerCard> cards = this.getCards();
		Map<Integer, Integer> rankCount = new HashMap<Integer, Integer>();
		for (PockerCard PockerCard : cards) {
			if (PockerCard.getSuit() != CardSuitEnum.GUI) {
				Integer number = new Integer(PockerCard.getRank().getNumber());
				if (!rankCount.containsKey(number)) {
					rankCount.put(number, 1);
				} else {
					rankCount.put(number, rankCount.get(number) + 1);
				}
			}
		}
		return rankCount;
	}

	public Map<CardSuitEnum, List<PockerCard>> getCardsSuitCountMap() {
		List<PockerCard> cards = this.getCards();
		Map<CardSuitEnum, List<PockerCard>> SuitCount = new HashMap<CardSuitEnum, List<PockerCard>>();
		for (PockerCard PockerCard : cards) {
			CardSuitEnum Suit = PockerCard.getSuit();
			if(Suit != CardSuitEnum.GUI)
			{
				if (!SuitCount.containsKey(Suit)) {
					List<PockerCard> list = new ArrayList<PockerCard>();
					list.add(PockerCard);
					SuitCount.put(Suit, list);
				} else {
					SuitCount.get(Suit).add(PockerCard);
				}
			}
		}
		return SuitCount;
	}

	public int getGuiCount() {
		int count = 0;
		for (PockerCard PockerCard : cards) {
			if (PockerCard.getSuit() == CardSuitEnum.GUI) {
				count++;
			}
		}
		return count;
	}

	public void setRankingResult(RankingResult rankingResult) {
		this.rankingResult = rankingResult;
	}

	@Override
	public int compareTo(PlayerDun o) {
		if (o == null) {
            return 0;
        }
		int selfPriority = this.getRankingResult().getRankingEnum().getPriority();
		int otherPriority = o.getRankingResult().getRankingEnum().getPriority();

		if (selfPriority < otherPriority) {
			return 1;
		}
		if (selfPriority > otherPriority) {
			return -1;
		}

		if (selfPriority == otherPriority) {
			IComparing cmp = ComparingFacade.getComparing(this.getRankingResult().getRankingEnum());
			return cmp.compare(this, o);
		}
		return 0;
	}

	@Override
	public String toString() {
		return "Player{" + "cards=" + cards + ", rankingResult=" + rankingResult + '}';
	}

	public static void main(String[] args) {
		// 牌型解析
		PockerCard card1 = new PockerCard(CardSuitEnum.GUI, CardRankEnum.CARD_XGUI);
		PockerCard card2 = new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.CARD_KING);
		PockerCard card3 = new PockerCard(CardSuitEnum.CLUBS, CardRankEnum.CARD_KING);
		PockerCard card4 = new PockerCard(CardSuitEnum.DIAMONDS, CardRankEnum.CARD_KING);
		PockerCard card5 = new PockerCard(CardSuitEnum.SPADES, CardRankEnum.CARD_NINE);

		PockerCard card6 = new PockerCard(CardSuitEnum.DIAMONDS, CardRankEnum.CARD_NINE);
		PockerCard card7 = new PockerCard(CardSuitEnum.DIAMONDS, CardRankEnum.CARD_EIGHT,"1");
		PockerCard card8 = new PockerCard(CardSuitEnum.DIAMONDS, CardRankEnum.CARD_EIGHT);
		PockerCard card9 = new PockerCard(CardSuitEnum.CLUBS, CardRankEnum.CARD_FIVE);
		PockerCard card10 = new PockerCard(CardSuitEnum.CLUBS, CardRankEnum.CARD_FIVE);

		PockerCard card11 = new PockerCard(CardSuitEnum.DIAMONDS, CardRankEnum.CARD_FOUR);
		PockerCard card12 = new PockerCard(CardSuitEnum.CLUBS, CardRankEnum.CARD_THREE);
		PockerCard card13 = new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.CARD_THREE);

		PlayerDun player = new PlayerDun();
		player.addCard(card2);
		player.addCard(card1);
		player.addCard(card3);
		player.addCard(card4);
		player.addCard(card5);

		 player.addCard(card6);
		 player.addCard(card7);
		 player.addCard(card8);
		 player.addCard(card9);
		 player.addCard(card10);
		
		 player.addCard(card11);
		 player.addCard(card12);
		 player.addCard(card13);

//		IRanking ranking = new WDuiSanChongRankingImpl_T();
//		RankingResult result = ranking.resolve(player);
		System.out.println(player.toString());
		//
		// // 大小比较测试
		// PockerCard card1 = new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_TEN);
		// PockerCard card2 = new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_JACK);
		// PockerCard card3 = new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_QUEUE);
		// PockerCard card4 = new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_KING);
		// PockerCard card5 = new PockerCard(CardSuitEnum.GUI,
		// CardRankEnum.CARD_XGUI);
		//
		// PlayerDun p = new PlayerDun();
		// p.addCard(card1);
		// p.addCard(card2);
		// p.addCard(card3);
		// p.addCard(card4);
		// p.addCard(card5);
		//
		// IRanking ranking = new StraightFlushRankingImpl();
		// RankingResult result = ranking.resolve(p);
		//
		// PockerCard card21 = new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_TEN);
		// PockerCard card22 = new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_QUEUE);
		// PockerCard card23 = new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_JACK);
		// PockerCard card25 = new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_ACE);
		// PockerCard card24 = new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_KING);
		//
		// PlayerDun p2 = new PlayerDun();
		// p2.addCard(card21);
		// p2.addCard(card22);
		// p2.addCard(card23);
		// p2.addCard(card24);
		// p2.addCard(card25);
		//
		// IRanking ranking2 = new StraightFlushRankingImpl();
		// RankingResult result2 = ranking2.resolve(p2);
		//
		// System.out.println(p.compareTo(p2));
		//
		// PockerCard card45 = new PockerCard(CardSuitEnum.GUI,
		// CardRankEnum.CARD_XGUI);
		// PockerCard card445 = new PockerCard(CardSuitEnum.GUI,
		// CardRankEnum.CARD_DGUI);
		//
		// System.out.println(card45.toString2());
		// System.out.println(card445.toString2());
	}
}
