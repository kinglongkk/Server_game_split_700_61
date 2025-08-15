package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;
import business.sss.c2s.cclass.newsss.RankingEnum;

import java.util.Map;

/**
 * Class {@code TwoPairsRankingImpl} 解析玩家手中的牌是不是两对(2+2+1)
 */
public class TwoPairsRankingImpl extends AbstractRanking {

	@Override
	protected RankingResult doResolve(PlayerDun player) {
		boolean flag = false;
		RankingResult result = null;
		if (player.getCardSize() == 5) {
			int guicnt = player.getGuiCount();
			Map<Integer, Integer> rankCount = player.getCardsRankCountMap();
//			List<PockerCard> cards = player.getCards();
			if (2 == guicnt || rankCount.size() == 3) {
				flag = true;
			}

		}

		if (flag) {
			result = new RankingResult();
			result.setRankingEnum(RankingEnum.TWO_PAIR);
		}

		return result;
	}

}
