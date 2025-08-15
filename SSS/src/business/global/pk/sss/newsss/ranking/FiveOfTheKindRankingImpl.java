package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;
import business.sss.c2s.cclass.newsss.RankingEnum;

import java.util.Iterator;
import java.util.Map;

/**
 * 五同
 */
public class FiveOfTheKindRankingImpl extends AbstractRanking {
	@Override
	protected RankingResult doResolve(PlayerDun player) {
		RankingResult result = null;
		Map<Integer, Integer> rankCount = player.getCardsRankCountMap();
		boolean hasFour = false;
		if (player.getCardSize() == 5) {
			Iterator<Map.Entry<Integer, Integer>> it = rankCount.entrySet().iterator();
			int guicnt = player.getGuiCount();
			while (it.hasNext()) {
				int count = it.next().getValue();
				if (count == 5 || ((count + guicnt) == 5)) {
					hasFour = true;
					break;
				}
			}
		}

		if (hasFour) {
			result = new RankingResult();
			result.setRankingEnum(RankingEnum.FIVE_OF_THE_KIND);
		}

		return result;
	}

}