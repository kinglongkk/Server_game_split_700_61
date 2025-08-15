package business.global.pk.sss.newsss.comparing;

import business.global.pk.sss.newsss.PlayerDun;
import business.sss.c2s.cclass.newsss.CardSuitEnum;
import business.sss.c2s.cclass.newsss.PockerCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 一对的大小比较(先比对子, 再比三个单张)
 */
public class OnePairComparingImpl extends AbstractComparing {
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
			newcards1.add(newcards1.get(0));
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
			newcards2.add(newcards2.get(0));
			Collections.sort(newcards2);
		} else {
			newcards2 = o2.getCards();
		}

		Map<Integer, Integer> p1CardMap = getCardsRankCountMap(newcards1);
		Map<Integer, Integer> p2CardMap = getCardsRankCountMap(newcards2);
		int ret = this.pairComparing(p1CardMap, p2CardMap, 2, 2);
		if (ret == 0 && o1.getGuiCount() > 0 && o2.getGuiCount() == 0) {
			return 1;
		} else if (ret == 0 && o2.getGuiCount() > 0 && o1.getGuiCount() == 0) {
			return -1;
		}

		return ret;
	}

}
