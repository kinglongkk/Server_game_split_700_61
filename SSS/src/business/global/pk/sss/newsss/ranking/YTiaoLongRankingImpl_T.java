package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;
import business.sss.c2s.cclass.newsss.RankingEnum;

import java.util.Map;

/**
 * 一条龙
 */
public class YTiaoLongRankingImpl_T extends AbstractRanking {
	@Override
	protected RankingResult doResolve(PlayerDun player) {
		RankingResult result = null;
		if (player.getCardSize() == 13) {
			Map<Integer, Integer> rankCount = player.getCardsRankCountMap();
			int guicnt = player.getGuiCount();
			if ((rankCount.size() + guicnt)== 13) {
				result = new RankingResult();
				result.setPockerCards(player.getCards());
				result.setRankingEnum(RankingEnum.YTiaoLong);
			}
		}
		return result;
	}
}
