package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;

import business.sss.c2s.cclass.newsss.PockerCard;
import business.sss.c2s.cclass.newsss.RankingEnum;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 一对同花
 */
public class OnePairFlushRankingImpl extends AbstractRanking {
	@Override
	protected RankingResult doResolve(PlayerDun player) {
		boolean flag = false;
		RankingResult result = null;
		if (player.getCardSize() == 5) {
			int guicnt = player.getGuiCount();
			if (guicnt == 1) {
				List<PockerCard> cards = player.getCards();
				if (this.isSameSuit(cards)) { // 如果是同色
					Map<Integer, Integer> rankCount = player.getCardsRankCountMap();
					if (rankCount.size() == 4) {
						Iterator<Map.Entry<Integer, Integer>> it = rankCount.entrySet().iterator();
						while (it.hasNext()) {
							Map.Entry<Integer, Integer> next = it.next();
							if (next.getValue() == 1) {
								flag = true;
								break;
							}
						}
					}
				}
			} else if (guicnt == 0) {
				List<PockerCard> cards = player.getCards();
				if (this.isSameSuit(cards)) { // 如果是同色
					Map<Integer, Integer> rankCount = player.getCardsRankCountMap();
					if (rankCount.size() == 4) {
						Iterator<Map.Entry<Integer, Integer>> it = rankCount.entrySet().iterator();
						while (it.hasNext()) {
							Map.Entry<Integer, Integer> next = it.next();
							if (next.getValue() == 2 || next.getValue() == 1) {
								flag = true;
								break;
							}
						}
					}
				}
			}

		}
		if (flag) {
			result = new RankingResult();
			result.setRankingEnum(RankingEnum.FLUSH_ONE_PAIR);
		}
		return result;
	}

}