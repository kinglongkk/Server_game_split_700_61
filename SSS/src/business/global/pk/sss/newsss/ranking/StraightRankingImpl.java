package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;
import business.sss.c2s.cclass.newsss.CardRankEnum;
import business.sss.c2s.cclass.newsss.PockerCard;
import business.sss.c2s.cclass.newsss.RankingEnum;

import java.util.ArrayList;
import java.util.List;

/**
 *  解析玩家手中的牌是不是顺子
 */
public class StraightRankingImpl extends AbstractRanking {

	@Override
	protected RankingResult doResolve(PlayerDun player) {
		RankingResult result = null;
		List<PockerCard> cards = player.getCards();
		ArrayList<CardRankEnum> newcards = new ArrayList<CardRankEnum>();
		if (5 == cards.size()) {
			for (int i = 0; i < cards.size(); i++) {
				newcards.add(cards.get(i).getRank());
			}
			int guicnt = player.getGuiCount();

			if (guicnt == 2) {
				for (int i = 0; i < AbstractRanking.threeCarsTwo.size(); i++) {
					if (newcards.containsAll(AbstractRanking.threeCarsTwo.get(i))) {
						result = new RankingResult();
						result.setRankingEnum(RankingEnum.STRAIGHT);
						break;
					}
				}
			} else if (guicnt == 1) {
				for (int i = 0; i < AbstractRanking.fourCars.size(); i++) {
					if (newcards.containsAll(AbstractRanking.fourCars.get(i))) {
						result = new RankingResult();
						result.setRankingEnum(RankingEnum.STRAIGHT);
						break;
					}
				}
			} else if (guicnt == 0) {
				for (int i = 0; i < AbstractRanking.fiveCars.size(); i++) {
					if (newcards.containsAll(AbstractRanking.fiveCars.get(i))) {
						result = new RankingResult();
						result.setRankingEnum(RankingEnum.STRAIGHT);
						break;
					}
				}
			}
		}
		return result;
	}

}
