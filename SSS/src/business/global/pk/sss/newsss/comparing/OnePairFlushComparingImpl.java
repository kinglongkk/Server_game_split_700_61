package business.global.pk.sss.newsss.comparing;

import business.global.pk.sss.newsss.PlayerDun;
import business.sss.c2s.cclass.newsss.CardRankEnum;
import business.sss.c2s.cclass.newsss.CardSuitEnum;
import business.sss.c2s.cclass.newsss.PockerCard;

import java.util.*;

/**
 * 一对同花
 */
public class OnePairFlushComparingImpl extends AbstractComparing {

	@Override
	public int compare(PlayerDun o1, PlayerDun o2) {
		int guicnt = o1.getGuiCount();
		List<PockerCard> cards = o1.getCards();
		List<PockerCard> newcards = new ArrayList<PockerCard>();
		if (0 == guicnt) {
			newcards = cards;
		} else if (guicnt > 0) {
			for (int i = 0; i < cards.size(); i++) {
				if (cards.get(i).getSuit() != CardSuitEnum.GUI) {
					newcards.add(cards.get(i));
				}
			}
			Collections.sort(newcards);

			int max = newcards.get(0).getRankNumber();
			if (1 == guicnt) {
				newcards.add(new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.valueOf(max)));
				Collections.sort(newcards);
			}
		}

		int guicnt2 = o2.getGuiCount();
		List<PockerCard> cards2 = o2.getCards();
		List<PockerCard> newcards2 = new ArrayList<PockerCard>();
		if (0 == guicnt2) {
			newcards2 = cards2;
		} else {
			for (int i = 0; i < cards2.size(); i++) {
				if (cards2.get(i).getSuit() != CardSuitEnum.GUI) {
					newcards2.add(cards2.get(i));
				}
			}
			Collections.sort(newcards2);

			int max = newcards2.get(0).getRankNumber();
			 if (1 == guicnt2) {
				newcards2.add(new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.valueOf(max)));
				Collections.sort(newcards2);
			}
		}
		Map<Integer, Integer> p1CardMap = this.getCardsRankCountMap(newcards);
		Map<Integer, Integer> p2CardMap = this.getCardsRankCountMap(newcards2);

		int ret = this.pairComparing(p1CardMap, p2CardMap, 2, 2);
		if (ret == 0) {
			if (o1.getGuiCount() > 0 && o2.getGuiCount() == 0) {
				return 1;
			} else if (o2.getGuiCount() > 0 && o1.getGuiCount() == 0) {
				return -1;
			} else if (o1.getGuiCount() == 1 && o2.getGuiCount() == 1) {
				return 0;
			}
		}
		return ret;
	}

	@Override
    public Map<Integer, Integer> getCardsRankCountMap(List<PockerCard> cards) {
		Map<Integer, Integer> rankCount = new HashMap<Integer, Integer>();
		for (PockerCard PockerCard : cards) {
			Integer number = new Integer(PockerCard.getRank().getNumber());
			if (!rankCount.containsKey(number)) {
				rankCount.put(number, 1);
			} else {
				rankCount.put(number, rankCount.get(number) + 1);
			}
		}
		return rankCount;
	}
}
