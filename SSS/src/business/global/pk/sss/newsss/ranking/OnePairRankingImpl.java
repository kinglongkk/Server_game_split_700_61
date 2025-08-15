package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;

import business.sss.c2s.cclass.newsss.RankingEnum;
import java.util.Iterator;
import java.util.Map;

/**
 * Class {@code OnePairRankingImpl} 解析玩家手中的牌是不是一对(2+1+1+1)
 */
public class OnePairRankingImpl extends AbstractRanking {

	@Override
	protected RankingResult doResolve(PlayerDun player) {
		RankingResult result = null;
		Map<Integer, Integer> rankCount = player.getCardsRankCountMap();
		boolean hasOne = false;

		if (player.getGuiCount() > 0) {
			hasOne = true;
		} else {
			if (player.getCardSize() == 5) {
				if (rankCount.size() == 4) {
					Iterator<Map.Entry<Integer, Integer>> it = rankCount.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<Integer, Integer> next = it.next();
						if (next.getValue() == 2 || next.getValue() == 1) {
							hasOne = true;
							break;
						}
					}
				}
			} else if (player.getCardSize() == 3) {
				if (rankCount.size() == 2) {
					Iterator<Map.Entry<Integer, Integer>> it = rankCount.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<Integer, Integer> next = it.next();
						if (next.getValue() == 2 || next.getValue() == 1) {
							hasOne = true;
							break;
						}
					}
				}
			}
		}

		if (hasOne) {
			result = new RankingResult();
			result.setRankingEnum(RankingEnum.ONE_PAIR);
		}

		return result;
	}

}
