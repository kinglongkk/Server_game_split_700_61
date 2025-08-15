package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;


import business.sss.c2s.cclass.newsss.CardRankEnum;
import business.sss.c2s.cclass.newsss.RankingEnum;

import java.util.ArrayList;

/**
 * 三顺子
 */
public class SShunZiRankingImpl_T extends AbstractRanking {
	@Override
	protected RankingResult doResolve(PlayerDun player) {
		RankingResult result = null;
		int guicnt = player.getGuiCount();
		ArrayList<CardRankEnum> cardsnew = player.getRanks();
		ArrayList<ArrayList<CardRankEnum>> rets = new ArrayList<ArrayList<CardRankEnum>>();
		if (player.getCardSize() == 13) {
			if (guicnt == 0) {
				rets.addAll(check(cardsnew, 5, 5, 3));
			} else if (guicnt == 1) {
				rets.addAll(check(cardsnew, 5, 4, 3));
				rets.addAll(check(cardsnew, 5, 5, 2));
			} else if (guicnt == 2) {
				rets.addAll(check(cardsnew, 5, 5, 1));
				rets.addAll(check(cardsnew, 5, 4, 2));
				rets.addAll(check(cardsnew, 5, 3, 3));
				rets.addAll(check(cardsnew, 4, 4, 3));
			}
		}
		
		if (rets.size() > 2) {
			result = new RankingResult();
			result.setPockerCards(player.getCards());
			result.setRankingEnum(RankingEnum.SShunZi);
		}
		return result;
	}

	public static void main(String[] args) {
		SShunZiRankingImpl_T aaa = new SShunZiRankingImpl_T();
		ArrayList<CardRankEnum> cards = new ArrayList<CardRankEnum>();
//		cards.add(CardRankEnum.CARD_TWO);
		cards.add(CardRankEnum.CARD_THREE);
		cards.add(CardRankEnum.CARD_FOUR);
		cards.add(CardRankEnum.CARD_FIVE);
		cards.add(CardRankEnum.CARD_SIX);
		cards.add(CardRankEnum.CARD_SEVEN);
		cards.add(CardRankEnum.CARD_EIGHT);
		cards.add(CardRankEnum.CARD_NINE);
		cards.add(CardRankEnum.CARD_TEN);
		cards.add(CardRankEnum.CARD_JACK);
		cards.add(CardRankEnum.CARD_KING);
		cards.add(CardRankEnum.CARD_QUEUE);
//		cards.add(CardRankEnum.CARD_ACE);
		ArrayList<ArrayList<CardRankEnum>> ret = aaa.check(cards, 5, 5, 1);
		ret.forEach(System.out::println);
	}
}