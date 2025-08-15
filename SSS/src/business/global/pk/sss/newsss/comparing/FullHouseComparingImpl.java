package business.global.pk.sss.newsss.comparing;

import business.global.pk.sss.newsss.PlayerDun;
import business.sss.c2s.cclass.newsss.CardRankEnum;
import business.sss.c2s.cclass.newsss.CardSuitEnum;
import business.sss.c2s.cclass.newsss.PockerCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 葫芦的大小比较(直接比较三条，在比较对子)
 */
public class FullHouseComparingImpl extends AbstractComparing {

	@Override
	public int compare(PlayerDun o1, PlayerDun o2) {
		List<PockerCard> newcards1 = new ArrayList<PockerCard>();
		List<PockerCard> newcards2 = new ArrayList<PockerCard>();
		if (o1.getGuiCount() == 1) {// 鬼牌处理 忽略两个鬼牌的情况
			List<PockerCard> cards = o1.getCards();
			for (int i = 0; i < cards.size(); i++) {
				if (cards.get(i).getSuit() != CardSuitEnum.GUI) {
					newcards1.add(cards.get(i));
				}
			}
			Map<Integer, Integer> tmp = getCardsRankCountMap(o1.getCards());
			int MaxNum = this.findMaxNumber(tmp, 2);
			newcards1.add(new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.valueOf(MaxNum)));
			Collections.sort(newcards1);
		} else {
			newcards1 = o1.getCards();
		}

		if (o2.getGuiCount() == 1) {// 鬼牌处理
			List<PockerCard> cards = o2.getCards();
			for (int i = 0; i < cards.size(); i++) {
				if (cards.get(i).getSuit() != CardSuitEnum.GUI) {
					newcards2.add(cards.get(i));
				}
			}
			Map<Integer, Integer> tmp = getCardsRankCountMap(o2.getCards());
			int MaxNum = this.findMaxNumber(tmp, 2);
			newcards2.add(new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.valueOf(MaxNum)));
			Collections.sort(newcards2);
		} else {
			newcards2 = o2.getCards();
		}

		Map<Integer, Integer> p1CardMap = getCardsRankCountMap(newcards1);
		Map<Integer, Integer> p2CardMap = getCardsRankCountMap(newcards2);
		int ret = this.multiComparing(p1CardMap, p2CardMap, 3);
		if (ret == 0) {
			ret = this.multiComparing(p1CardMap, p2CardMap, 2);
			if (ret == 0 && o1.getGuiCount() > 0 && o2.getGuiCount() == 0) {
				return 1;
			} else if (ret == 0 && o2.getGuiCount() > 0 && o1.getGuiCount() == 0) {
				return -1;
			}
		}
		return ret;

	}

}
