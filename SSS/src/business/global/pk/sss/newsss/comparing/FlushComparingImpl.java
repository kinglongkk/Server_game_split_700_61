package business.global.pk.sss.newsss.comparing;

import business.global.pk.sss.newsss.PlayerDun;
import business.sss.c2s.cclass.newsss.CardRankEnum;
import business.sss.c2s.cclass.newsss.CardSuitEnum;
import business.sss.c2s.cclass.newsss.PockerCard;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 同花大小比较(比较最大牌即可)
 */
public class FlushComparingImpl extends AbstractComparing {

	@Override
	public int compare(PlayerDun o1, PlayerDun o2) {
		int guicnt = o1.getGuiCount();
		List<PockerCard> cards = o1.getCards();
		List<PockerCard> newcards = new ArrayList<PockerCard>();
		if (0 == guicnt) {
			newcards = cards;
		} else {
			for (int i = 0; i < cards.size(); i++) {
				if (cards.get(i).getSuit() != CardSuitEnum.GUI) {
					newcards.add(cards.get(i));
				}
			}
			Collections.sort(newcards);

//			int max = newcards.get(0).getRankNumber();
			if (2 == guicnt) {
				newcards.add(new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.CARD_ACE));
				newcards.add(new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.CARD_ACE));
				Collections.sort(newcards);

			} else if (1 == guicnt) {
				newcards.add(new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.CARD_ACE));
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

//			int max = newcards2.get(0).getRankNumber();
			if (2 == guicnt2) {
				newcards2.add(new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.CARD_ACE));
				newcards2.add(new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.CARD_ACE));
				Collections.sort(newcards2);

			} else if (1 == guicnt2) {
				newcards2.add(new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.CARD_ACE));
				Collections.sort(newcards2);
			}
		}

		int ret = this.seqComparing(newcards, newcards2);
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
