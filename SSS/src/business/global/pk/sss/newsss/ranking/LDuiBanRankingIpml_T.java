package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;
import business.global.pk.sss.newsss.Poker;

import business.sss.c2s.cclass.newsss.Constants;
import business.sss.c2s.cclass.newsss.PockerCard;
import business.sss.c2s.cclass.newsss.RankingEnum;

import java.util.*;

/**
 * 六对半
 */
public class LDuiBanRankingIpml_T extends AbstractRanking {
	@Override
	protected RankingResult doResolve(PlayerDun player) {
		RankingResult result = null;
		boolean flag = false;
		if (player.getCardSize() == 13) {
			Map<Integer, Integer> rankCount = player.getCardsRankCountMap();

			int twocnt = 0;
			int fourcnt = 0;
			int guicnt = player.getGuiCount();
			Iterator<Map.Entry<Integer, Integer>> it = rankCount.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, Integer> entry = it.next();
				if (entry.getKey() != 15 && entry.getKey() != 16) {
					if (entry.getValue() == 2) {
						twocnt++;
					} else if (entry.getValue() == 4) {
						fourcnt++;
					}
				}
			}
						
			if (twocnt == 6) {
				flag = true;
			}
			if (twocnt == 5 && guicnt == 1) {
				flag = true;
			}
			if (twocnt == 5 && guicnt >= 1) {
				flag = true;
			}
			if (twocnt == 4 && guicnt == 2) {
				flag = true;
			}
			if (twocnt == 4 && fourcnt == 1) {
				flag = true;
			}
			if (twocnt == 2 && fourcnt == 1 && guicnt == 2) {
				flag = true;
			}
			if (twocnt == 3 && fourcnt == 1 && guicnt == 1) {
				flag = true;
			}
			
			
			
			if (flag) {
				result = new RankingResult();
				result.setPockerCards(player.getCards());
				result.setRankingEnum(RankingEnum.LDuiBan);
			}
		}
		return result;
	}
	


	public static void main(String[] args) {
		List<Integer> acc = Arrays.asList(0,1,2,3);
		for (int i = 0;i<99999999;i++) {
			Poker poker = new Poker(8, acc, true, false,0);
			for (int p = 0;p<8;p++) {
				List<PockerCard> ret = new ArrayList<PockerCard>();
				for (int j = 0; j < Constants.HAND_CARD_NUMBER; j++) {
					ret.add(poker.dispatch());
				}
				LDuiBanRankingIpml_T lDuiBanRankingIpml_T = new LDuiBanRankingIpml_T();
				PlayerDun player = new PlayerDun();
				player.addData(ret);
				lDuiBanRankingIpml_T.doResolve(player);
			}
			

		}

	}
	
}