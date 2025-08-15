package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;


import business.sss.c2s.cclass.newsss.RankingEnum;

import java.util.Iterator;
import java.util.Map;

/**
 * 四套三条
 */
public class STaoSanTiaoRankingipml_T extends AbstractRanking {
	@Override
	protected RankingResult doResolve(PlayerDun player) {
		RankingResult result = null;
		boolean flag = false;
		if (player.getCardSize() == 13) {
			Map<Integer, Integer> rankCount = player.getCardsRankCountMap();
			int threecnt = 0;
			int twocnt = 0;
			int guicnt = player.getGuiCount();
			Iterator<Map.Entry<Integer, Integer>> it = rankCount.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, Integer> entry = it.next();
				int value = entry.getValue();
				if (value == 3 || value == 4) {
					threecnt++;
				}
				if (value == 2) {
					twocnt++;
				}
			}

			int total = threecnt;
			if (guicnt == 1 && twocnt >= 1) {
				total += 1;
			} else if (guicnt == 2) {
				if (twocnt < 2) {
					total += 1;
				} else if (twocnt >= 2) {
					total += 2;
				}
			}

			if (total >= 4) {
				flag = true;
			}

			if (flag) {
				result = new RankingResult();
				result.setPockerCards(player.getCards());
				result.setRankingEnum(RankingEnum.STaoSanTiao);
			}
		}
		return result;
	}
}