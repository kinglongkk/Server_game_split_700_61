package business.global.pk.sss.newsss.comparing;

import business.global.pk.sss.newsss.PlayerDun;
import business.sss.c2s.cclass.newsss.CardRankEnum;
import business.sss.c2s.cclass.newsss.CardSuitEnum;
import business.sss.c2s.cclass.newsss.PockerCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 同花顺的大小比较(按顺序比较)
 */
public class StraightFlushComparingImpl extends AbstractComparing {

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
				int newnum1 = getAddNum(newcards,newcards.get(0).getRankNumber(),
						newcards.get(newcards.size() - 1).getRankNumber());
				newcards.add(new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.valueOf(newnum1)));
				Collections.sort(newcards);
				int newnum2 = getAddNum(newcards,newcards.get(0).getRankNumber(),
						newcards.get(newcards.size() - 1).getRankNumber());
				newcards.add(new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.valueOf(newnum2)));
				Collections.sort(newcards);

			} else if (1 == guicnt) {
				int num = getAddNum(newcards,newcards.get(0).getRankNumber(), newcards.get(newcards.size() - 1).getRankNumber());
				newcards.add(new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.valueOf(num)));
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

			if (2 == guicnt2) {
				int newnum1 = getAddNum(newcards2,newcards2.get(0).getRankNumber(),
						newcards2.get(newcards2.size() - 1).getRankNumber());
				newcards2.add(new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.valueOf(newnum1)));
				Collections.sort(newcards2);
				int newnum2 = getAddNum(newcards2,newcards2.get(0).getRankNumber(),
						newcards2.get(newcards2.size() - 1).getRankNumber());
				newcards2.add(new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.valueOf(newnum2)));
				Collections.sort(newcards2);

			} else if (1 == guicnt2) {
				int num = getAddNum(newcards2,newcards2.get(0).getRankNumber(),
						newcards2.get(newcards2.size() - 1).getRankNumber());
				newcards2.add(new PockerCard(CardSuitEnum.HEARTS, CardRankEnum.valueOf(num)));
				Collections.sort(newcards2);
			}
		}

		int ret = this.seqComparing(newcards, newcards2);
		if (ret == 0 && o1.getGuiCount() > 0 && o2.getGuiCount() == 0) {
			return 1;
		} else if (ret == 0 && o2.getGuiCount() > 0 && o1.getGuiCount() == 0) {
			return -1;
		}
		return ret;

	}

	private int getAddNum (List<PockerCard> newcards,int max,int min) {
		if (max == 5 && min == 2) {
			return CardRankEnum.CARD_ACE.getNumber();
		}
		if (max == 14 && (min <= 5 && min >2)) {
			return min-1;
		}
		if (max <= 5) {
			return CardRankEnum.CARD_ACE.getNumber();
		}
		int cardId = min;
		for (int i = newcards.size() - 1;i >= 0;i--) {
			if (cardId == newcards.get(i).getRankNumber() && cardId < CardRankEnum.CARD_ACE.getNumber()) {
				cardId++;
			} else {
				cardId = min - 1;
			}
			if (isContains(newcards,cardId)) {
				continue;
			}
			break;	
		}
		return cardId;
	}
	
	private boolean isContains (List<PockerCard> newcards ,int cardId) {
		for (PockerCard pCard : newcards) {
			if (pCard.getRankNumber() == cardId) {
				return true;
			}
		}
		return false;
	}



}
