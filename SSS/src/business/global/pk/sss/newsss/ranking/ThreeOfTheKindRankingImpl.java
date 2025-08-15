package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;
import business.sss.c2s.cclass.newsss.RankingEnum;

import java.util.Iterator;
import java.util.Map;

/**
 * 解析玩家手中的牌是不是三条(3+1+1)
 */
public class ThreeOfTheKindRankingImpl extends AbstractRanking {

	@Override
	protected RankingResult doResolve(PlayerDun player) {
		RankingResult result = null;
		Map<Integer, Integer> rankCount = player.getCardsRankCountMap();

		boolean hasThree = false;

		if (player.getGuiCount() == 2) {
			hasThree = true;
		} else {
			Iterator<Map.Entry<Integer, Integer>> it = rankCount.entrySet().iterator();
			while (it.hasNext()) {
				int count = it.next().getValue() ;
				if (count == 3  || (count == 2 && player.getGuiCount() == 1)) {
					hasThree = true;
					break;
				}
			}
		}

		if (hasThree) {
			result = new RankingResult();
			result.setRankingEnum(RankingEnum.THREE_OF_THE_KIND);
		}

		return result;
	}

}
