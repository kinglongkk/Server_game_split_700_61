package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;

import business.sss.c2s.cclass.newsss.RankingEnum;

import java.util.Iterator;
import java.util.Map;

/**
 *八仙过海
 */
public class BAXIANGGUOHAIImpl_T  extends AbstractRanking {
	@Override
	protected RankingResult doResolve(PlayerDun player) {
		RankingResult result = null;
		
		boolean flag = false;

		if (player.getCardSize() == 13) {
			Map<Integer, Integer> rankCount = player.getCardsRankCountMap();
			Iterator<Map.Entry<Integer, Integer>> it = rankCount.entrySet().iterator();
			int guicnt = player.getGuiCount();
			while (it.hasNext()) {
				int count = it.next().getValue();
				if (count == 8 || ((count + guicnt) == 8)) {
					flag = true;
					break;
				}
			}
		}

		if (flag) {
			result = new RankingResult();
			result.setPockerCards(player.getCards());
			result.setRankingEnum(RankingEnum.BAXIANGGUOHAI);
		}

		return result;
	}
}