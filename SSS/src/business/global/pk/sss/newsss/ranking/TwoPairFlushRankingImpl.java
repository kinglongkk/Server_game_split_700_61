package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;
import business.sss.c2s.cclass.newsss.PockerCard;
import business.sss.c2s.cclass.newsss.RankingEnum;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 两对对花
 */
public class TwoPairFlushRankingImpl extends AbstractRanking {

	@Override
	protected RankingResult doResolve(PlayerDun player) {
		boolean flag = false;
		RankingResult result = null;
		if (player.getCardSize() == 5) {
			List<PockerCard> cards = player.getCards();
			int guicnt = player.getGuiCount();
			Map<Integer, Integer> rankCount = player.getCardsRankCountMap();
			if (2 == guicnt && this.isSameSuit(cards)) {
				flag = true;
			} else if (1 == guicnt && this.isSameSuit(cards) && rankCount.size() == 3) {
				flag = true;
			} else {
				if (this.isSameSuit(cards) && rankCount.size() == 3) { // 如果是同色
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

		if (flag) {
			result = new RankingResult();
			result.setRankingEnum(RankingEnum.FLUSH_TWO_PAIR);
		}
		return result;
	}

	public Map<Integer, Integer> getCardsRankCountMap(List<PockerCard> cards) {
		Map<Integer, Integer> rankCount = new HashMap<Integer, Integer>();
		for (PockerCard PockerCard : cards) {
			Integer number = new Integer(PockerCard.getRank().getNumber());
			if (!rankCount.containsKey(number)) {
				rankCount.put(number, 1);
			} else {
				rankCount.put(number, rankCount.get(number) + 1);
			}
		}
		return rankCount;
	}
}