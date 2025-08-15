package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;

import business.sss.c2s.cclass.newsss.RankingEnum;

import java.util.Iterator;
import java.util.Map;

/**
 * 三分天下
 */
public class SFenTianXiaRankingImpl_T extends AbstractRanking {
	@Override
	protected RankingResult doResolve(PlayerDun player) {
		RankingResult result = null;
		boolean flag = false;
		if (player.getCardSize() == 13) {
			Map<Integer, Integer> rankCount = player.getCardsRankCountMap();

			int fourcnt = 0;
			int threecnt = 0;
//			int twocnt = 0;
			int guicnt = player.getGuiCount();
			Iterator<Map.Entry<Integer, Integer>> it = rankCount.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, Integer> entry = it.next();
				if (entry.getKey() != 15 && entry.getKey() != 16) {
					int value = entry.getValue() ;
					if (value== 4) {
						fourcnt++;
					}
					if (value== 3) {
						threecnt++;
					}
					if (value== 2) {
//						twocnt++;
					}
				}
			}

			int totalcnt = fourcnt;
			if (threecnt >= guicnt) {
				totalcnt += guicnt;
				guicnt = 0;
			} else if (guicnt == 2) {
				totalcnt += 1;
				guicnt = 0;
			}

			if (totalcnt >= 3) {
				flag = true;
			}

			if (flag) {
				result = new RankingResult();
				result.setPockerCards(player.getCards());
				result.setRankingEnum(RankingEnum.SFenTianXia);
			}
		}
		return result;
	}
}
