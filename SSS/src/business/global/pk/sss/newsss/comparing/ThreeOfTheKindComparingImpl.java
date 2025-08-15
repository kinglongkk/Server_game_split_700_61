package business.global.pk.sss.newsss.comparing;

import business.global.pk.sss.newsss.PlayerDun;
import business.sss.c2s.cclass.newsss.CardRankEnum;
import business.sss.c2s.cclass.newsss.CardSuitEnum;
import business.sss.c2s.cclass.newsss.PockerCard;

import java.util.*;

/**
 * 三条的大小比较(直接比较三条)
 */
public class ThreeOfTheKindComparingImpl extends AbstractComparing {

	@Override
	public int compare(PlayerDun o1, PlayerDun o2) {
		List<PockerCard> newcards1 = new ArrayList<PockerCard>();
		List<PockerCard> newcards2 = new ArrayList<PockerCard>();

		if (o1.getGuiCount() > 0) {// 鬼牌处理
			List<PockerCard> cards = o1.getCards();
			for (int i = 0; i < cards.size(); i++) {
				if (cards.get(i).getSuit() != CardSuitEnum.GUI) {
					newcards1.add(cards.get(i));
				}
			}

			if (o1.getGuiCount() == 1) {
				Map<Integer, Integer> tmp = getCardsRankCountMap(o1.getCards());
				Iterator iter = tmp.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					Integer key = (Integer) entry.getKey();
					Integer count = (Integer) entry.getValue();
					if (count == 2) {
						newcards1.add(new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.valueOf(key)));
					}
				}

			} else if (o1.getGuiCount() == 2) {
				for (int i = 0; i < o1.getGuiCount(); i++) {
					newcards1.add(newcards1.get(0));
				}
			}

			Collections.sort(newcards1);
		} else {
			newcards1 = o1.getCards();
		}
		if (o2.getGuiCount() > 0) {// 鬼牌处理
			List<PockerCard> cards = o2.getCards();
			for (int i = 0; i < cards.size(); i++) {
				if (cards.get(i).getSuit() != CardSuitEnum.GUI) {
					newcards2.add(cards.get(i));
				}
			}
			if (o2.getGuiCount() == 1) {
				Map<Integer, Integer> tmp = getCardsRankCountMap(o2.getCards());
				Iterator iter = tmp.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					Integer key = (Integer) entry.getKey();
					Integer count = (Integer) entry.getValue();
					if (count == 2) {
						newcards2.add(new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.valueOf(key)));
					}
				}

			} else if (o2.getGuiCount() == 2) {
				for (int i = 0; i < o2.getGuiCount(); i++) {
					newcards2.add(newcards2.get(0));
				}
			}
			Collections.sort(newcards2);
		} else {
			newcards2 = o2.getCards();
		}

		Map<Integer, Integer> p1CardMap = getCardsRankCountMap(newcards1);
		Map<Integer, Integer> p2CardMap = getCardsRankCountMap(newcards2);
		int ret = this.multiComparing(p1CardMap, p2CardMap, 3);
		if (ret == 0 && o1.getGuiCount() > 0 && o2.getGuiCount() == 0) {
			return 1;
		} else if (ret == 0 && o2.getGuiCount() > 0 && o1.getGuiCount() == 0) {
			return -1;
		}

		return ret;
	}

}
