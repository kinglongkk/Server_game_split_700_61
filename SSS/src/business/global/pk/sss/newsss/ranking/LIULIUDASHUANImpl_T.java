package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;
import business.sss.c2s.cclass.newsss.RankingEnum;

import java.util.Iterator;
import java.util.Map;

/**
 *66大顺
 */
public class LIULIUDASHUANImpl_T extends AbstractRanking {
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
				if (count == 6 || ((count + guicnt) == 6)) {
					flag = true;
					break;
				}
			}
		}

		if (flag) {
			result = new RankingResult();
			result.setPockerCards(player.getCards());
			result.setRankingEnum(RankingEnum.LIULIUDASHUAN);
		}

		return result;
	}
}