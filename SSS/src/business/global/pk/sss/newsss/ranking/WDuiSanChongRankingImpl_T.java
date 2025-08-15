package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;
import business.global.pk.sss.newsss.Poker;
import business.sss.c2s.cclass.newsss.Constants;
import business.sss.c2s.cclass.newsss.PockerCard;
import business.sss.c2s.cclass.newsss.RankingEnum;

import java.util.*;

/**
 * 五对三冲
 */
public class WDuiSanChongRankingImpl_T extends AbstractRanking {
	@Override
	protected RankingResult doResolve(PlayerDun player) {
		if (player.getCards().size() != 13) {
			return null;
		}
		RankingResult result = null;
		Map<Integer, Integer> rankCount = player.getCardsRankCountMap();

		boolean flag = false;
		int threecnt = 0;
		int twocnt = 0;
		int guicnt = player.getGuiCount();
		Iterator<Map.Entry<Integer, Integer>> it = rankCount.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Integer> entry= it.next();
			if (entry.getKey() != 15 && entry.getKey() != 16) {
				int value =entry.getValue();
				if (value == 3) {
					threecnt++;
				}
				if (value == 2) {
					twocnt++;
				} else if (value == 4) {
					twocnt += 2;
				}
			}
		}

		if (twocnt == 5 && threecnt == 1) {
			flag = true;
		} else if ((guicnt == 1 && twocnt == 6 )||(guicnt == 1 && twocnt == 4 && threecnt == 1)) {
			flag = true;
		} else if (guicnt == 2) {
			if (twocnt == 5) {
				flag = true;
			}
			if (twocnt == 3 && threecnt == 1) {
				flag = true;
			}
		}

		if (flag) {
			result = new RankingResult();
			result.setPockerCards(player.getCards());
			result.setRankingEnum(RankingEnum.WDuiSanChong);
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
				WDuiSanChongRankingImpl_T shunRankingImpl_T = new WDuiSanChongRankingImpl_T();
				PlayerDun player = new PlayerDun();
				player.addData(ret);
				if(shunRankingImpl_T.doResolve(player) != null) {
//					System.out.println(player.toString());
				}
			}
			

		}
		
		
//		List<PockerCard> ret = new ArrayList<PockerCard>();
//		ret.add(new PockerCard("0x0e"));
//		ret.add(new PockerCard("0x0e"));
//		ret.add(new PockerCard("0x0e"));
//		
//		ret.add(new PockerCard("0x0a"));
//		ret.add(new PockerCard("0x0a"));
//		
//		ret.add(new PockerCard("0x0b"));
//		ret.add(new PockerCard("0x0b"));
//
//		ret.add(new PockerCard("0x0c"));
//		ret.add(new PockerCard("0x0c"));
//		
//		ret.add(new PockerCard("0x0c"));
//		ret.add(new PockerCard("0x0c"));
//		
//		ret.add(new PockerCard("0x09"));
//		ret.add(new PockerCard("0x09"));
//
//
//		WDuiSanChongRankingImpl_T shunRankingImpl_T = new WDuiSanChongRankingImpl_T();
//		PlayerDun player = new PlayerDun();
//		player.addData(ret);
//		
//		if(shunRankingImpl_T.doResolve(player) != null) {
//		System.out.println(player.toString());
//		}
	}

	
}