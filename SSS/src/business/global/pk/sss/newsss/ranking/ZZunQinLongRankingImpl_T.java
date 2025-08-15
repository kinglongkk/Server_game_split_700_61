package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;
import business.sss.c2s.cclass.newsss.PockerCard;
import business.sss.c2s.cclass.newsss.RankingEnum;


import java.util.List;
import java.util.Map;

/**
 * 至尊清龙
 */
public class ZZunQinLongRankingImpl_T extends AbstractRanking {
	@Override
	protected RankingResult doResolve(PlayerDun player) {
		RankingResult result = null;
		if (player.getCardSize() == 13) {
			List<PockerCard> cards = player.getCards();
			Map<Integer, Integer> rankCount = player.getCardsRankCountMap();
			if (this.isSameSuit(cards)) { // 如果是同色
				if((rankCount.size()+ player.getGuiCount()) ==13)
				{
					result = new RankingResult();
					result.setPockerCards(cards);
					result.setRankingEnum(RankingEnum.ZZunQinLong);
				}
			}
		}
		return result;
	}
}