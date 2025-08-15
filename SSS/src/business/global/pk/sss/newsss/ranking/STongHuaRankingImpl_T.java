package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;

import business.sss.c2s.cclass.newsss.CardSuitEnum;
import business.sss.c2s.cclass.newsss.PockerCard;
import business.sss.c2s.cclass.newsss.RankingEnum;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 三同花
 */
public class STongHuaRankingImpl_T extends AbstractRanking {
	@Override
	protected RankingResult doResolve(PlayerDun player) {
		RankingResult result = null;

		if (player.getCardSize() == 13) {
//			List<PockerCard> cards = player.getCards();
			Map<CardSuitEnum, List<PockerCard>> suitCount = player.getCardsSuitCountMap();
			int count = 0;
			int guicnt = player.getGuiCount();
			List<Integer> lst = new ArrayList<Integer>();
			Iterator<Map.Entry<CardSuitEnum, List<PockerCard>>> it = suitCount.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<CardSuitEnum, List<PockerCard>> entry = it.next();
				if (entry.getKey() != CardSuitEnum.GUI) {
					count++;
					lst.add(entry.getValue().size());
				}
			}
			if (count == 3) {// 三花色
				if (guicnt == 0) {
					List<Integer> caselst = new ArrayList<Integer>();
					caselst.add(3);
					caselst.add(5);
					caselst.add(5);
					if (lst.containsAll(caselst)) {
						result = new RankingResult();
						result.setPockerCards(player.getCards());
						result.setRankingEnum(RankingEnum.STongHua);
					}
				}
				if (guicnt == 1) {
					List<Integer> caselst = new ArrayList<Integer>();
					caselst.add(2);
					caselst.add(5);
					caselst.add(5);
					List<Integer> caselst1 = new ArrayList<Integer>();
					caselst1.add(3);
					caselst1.add(4);
					caselst1.add(5);
					if (lst.containsAll(caselst) || lst.containsAll(caselst1)) {
						result = new RankingResult();
						result.setPockerCards(player.getCards());
						result.setRankingEnum(RankingEnum.STongHua);
					}
				}
				if (guicnt == 2) {
					List<Integer> caselst = new ArrayList<Integer>();
					caselst.add(1);
					caselst.add(5);
					caselst.add(5);
					List<Integer> caselst1 = new ArrayList<Integer>();
					caselst1.add(2);
					caselst1.add(4);
					caselst1.add(5);
					List<Integer> caselst2 = new ArrayList<Integer>();
					caselst2.add(3);
					caselst2.add(3);
					caselst2.add(5);
					List<Integer> caselst3 = new ArrayList<Integer>();
					caselst3.add(3);
					caselst3.add(4);
					caselst3.add(4);
					if (lst.containsAll(caselst) || lst.containsAll(caselst1) || lst.containsAll(caselst2)
							|| lst.containsAll(caselst3)) {
						result = new RankingResult();
						result.setPockerCards(player.getCards());
						result.setRankingEnum(RankingEnum.STongHua);
					}
				}
			} else if (count == 2) {// 2花色
				if (guicnt == 0) {
					List<Integer> caselst = new ArrayList<Integer>();
					caselst.add(3);
					caselst.add(10);
					List<Integer> caselst1 = new ArrayList<Integer>();
					caselst1.add(8);
					caselst1.add(5);
					if (lst.containsAll(caselst) || lst.containsAll(caselst1)) {
						result = new RankingResult();
						result.setPockerCards(player.getCards());
						result.setRankingEnum(RankingEnum.STongHua);
					}
				}
				if (guicnt == 1) {
					List<Integer> caselst = new ArrayList<Integer>();
					caselst.add(2);
					caselst.add(10);
					List<Integer> caselst1 = new ArrayList<Integer>();
					caselst1.add(8);
					caselst1.add(4);
					List<Integer> caselst2 = new ArrayList<Integer>();
					caselst1.add(5);
					caselst1.add(7);
					List<Integer> caselst3 = new ArrayList<Integer>();
					caselst1.add(9);
					caselst1.add(3);
					if (lst.containsAll(caselst) || lst.containsAll(caselst1) || lst.containsAll(caselst2)
							|| lst.containsAll(caselst3)) {
						result = new RankingResult();
						result.setPockerCards(player.getCards());
						result.setRankingEnum(RankingEnum.STongHua);
					}
				}
				if (guicnt == 2) {
					List<Integer> caselst = new ArrayList<Integer>();
					caselst.add(3);
					caselst.add(8);
					List<Integer> caselst1 = new ArrayList<Integer>();
					caselst1.add(2);
					caselst1.add(9);
					List<Integer> caselst2 = new ArrayList<Integer>();
					caselst2.add(3);
					caselst2.add(8);
					List<Integer> caselst3 = new ArrayList<Integer>();
					caselst3.add(7);
					caselst3.add(4);
					List<Integer> caselst4 = new ArrayList<Integer>();
					caselst.add(6);
					caselst.add(5);
					List<Integer> caselst6 = new ArrayList<Integer>();
					caselst2.add(1);
					caselst2.add(10);

					if (lst.containsAll(caselst) || lst.containsAll(caselst1) || lst.containsAll(caselst2)
							|| lst.containsAll(caselst3) || lst.containsAll(caselst4) || lst.containsAll(caselst6)) {
						result = new RankingResult();
						result.setPockerCards(player.getCards());
						result.setRankingEnum(RankingEnum.STongHua);
					}
				}
			} else if (count == 1) {
				result = new RankingResult();
				result.setPockerCards(player.getCards());
				result.setRankingEnum(RankingEnum.STongHua);
			}
		}
		return result;
	}

}
