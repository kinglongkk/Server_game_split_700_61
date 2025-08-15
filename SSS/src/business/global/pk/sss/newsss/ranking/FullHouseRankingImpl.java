package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;
import business.sss.c2s.cclass.newsss.RankingEnum;

import java.util.Iterator;
import java.util.Map;

/**
 * 解析玩家手中的牌是不是葫芦
 */
public class FullHouseRankingImpl extends AbstractRanking {

	@Override
	protected RankingResult doResolve(PlayerDun player) {
		RankingResult result = null;

		boolean isFullHouse = false;
		if (player.getCardSize() == 5) {
			Map<Integer, Integer> rankCount = player.getCardsRankCountMap();
			int guicnt = player.getGuiCount();
			if (guicnt == 1 && rankCount.size() == 2) {
				Iterator<Map.Entry<Integer, Integer>> it = rankCount.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<Integer, Integer> next = it.next();
					if(next.getKey() <15)
					{
						if (next.getValue() == 2 ) {
							isFullHouse = true;
							break;
						}
					}
				}
			} else if (guicnt == 0 && rankCount.size() == 2) {
				Iterator<Map.Entry<Integer, Integer>> it = rankCount.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<Integer, Integer> next = it.next();
					if (next.getValue() == 2 || next.getValue() == 3) {
						isFullHouse = true;
						break;
					}
				}
			}
		}

		if (isFullHouse) {
			result = new RankingResult();
			result.setRankingEnum(RankingEnum.FULL_HOUSE);
		}

		return result;
	}

}
