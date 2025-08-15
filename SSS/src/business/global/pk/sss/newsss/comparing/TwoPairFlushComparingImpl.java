package business.global.pk.sss.newsss.comparing;

import business.global.pk.sss.newsss.PlayerDun;
import business.sss.c2s.cclass.newsss.CardRankEnum;
import business.sss.c2s.cclass.newsss.CardSuitEnum;
import business.sss.c2s.cclass.newsss.PockerCard;

import java.util.*;

/**
 * 两对同花
 */
public class TwoPairFlushComparingImpl extends AbstractComparing {

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

			Map<Integer, Integer> tmpmap = this.getCardsRankCountMap(newcards);
			List<Integer> temp = new ArrayList<Integer>();

			for (Map.Entry<Integer, Integer> entry : tmpmap.entrySet()) {
				if (entry.getValue() == 1) {
					temp.add(entry.getKey());
				}
			}
	        Collections.sort(temp, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
	                return new Integer(o2).compareTo(o1);
				}
	        });
			
			if (1 == guicnt) {
				int max = temp.get(0);
				newcards.add(new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.valueOf(max)));
				Collections.sort(newcards);
			} else if (2 == guicnt) {
				int max = newcards.get(0).getRankNumber();
				int max2 = newcards.get(1).getRankNumber();
				newcards.add(new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.valueOf(max)));
				newcards.add(new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.valueOf(max2)));
				Collections.sort(newcards);
			}
		}

		int guicnt2 = o2.getGuiCount();
		List<PockerCard> cards2 = o2.getCards();
		List<PockerCard> newcards2 = new ArrayList<PockerCard>();
		if (0 == guicnt2) {
			newcards2 = cards2;
		} else if (guicnt2 > 0) {
			for (int i = 0; i < cards2.size(); i++) {
				if (cards2.get(i).getSuit() != CardSuitEnum.GUI) {
					newcards2.add(cards2.get(i));
				}
			}
			Collections.sort(newcards2);

			Map<Integer, Integer> tmpmap = this.getCardsRankCountMap(newcards2);
			List<Integer> temp = new ArrayList<Integer>();

			for (Map.Entry<Integer, Integer> entry : tmpmap.entrySet()) {
				if (entry.getValue() == 1) {
					temp.add(entry.getKey());
				}
			}
			Collections.sort(temp);
			if (1 == guicnt2) {
				int max = temp.get(0);
				newcards2.add(new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.valueOf(max)));
				Collections.sort(newcards2);
			} else if (2 == guicnt2) {
				int max = newcards2.get(0).getRankNumber();
				int max2 = newcards2.get(1).getRankNumber();
				newcards2.add(new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.valueOf(max)));
				newcards2.add(new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.valueOf(max2)));
				Collections.sort(newcards2);
			}
		}
		Map<Integer, Integer> p1CardMap = this.getCardsRankCountMap(newcards);
		Map<Integer, Integer> p2CardMap = this.getCardsRankCountMap(newcards2);

		int ret = this.pairComparing(p1CardMap, p2CardMap, 2, 3);
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

}