package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;
import business.global.pk.sss.newsss.Poker;
import business.global.pk.sss.utlis.SSSConfigMgr;
import business.sss.c2s.cclass.newsss.Constants;
import business.sss.c2s.cclass.newsss.PockerCard;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试 自由扑克 特殊牌
 * @author Huaxing
 *
 */
public class TestSSSSpecial {
	
	public void SSSpecial () {
		SSSConfigMgr sssMgr = new SSSConfigMgr();
		
		for (Integer intr : sssMgr.getSpecialList()) {
			int count = 0;
			for (;;) {
				Poker poker = new Poker(sssMgr.getSetCount(), sssMgr.getHuase(), sssMgr.getGui(), false,0);
				for (int p = 0;p<sssMgr.getSetCount();p++) {
					List<PockerCard> ret = new ArrayList<PockerCard>();
					for (int j = 0; j < Constants.HAND_CARD_NUMBER; j++) {
						ret.add(poker.dispatch());
					}
					PlayerDun player = new PlayerDun();
					player.addData(ret);
					RankingResult pResult = TestRankingFacade.getInstance().resolve(player,intr);
					if (pResult != null) {
						count++;
//						CommLog.info("Special：{},PockerCard：{},Count：{}",pResult.getRankingEnum(),pResult.getPockerCards().toString(),count);
					}
				}
				if (count >= 1000) {
                    break;
                }
			}
		}
		
		

	}
	
	
	public static void main (String[] args) {
		TestSSSSpecial dd = new TestSSSSpecial();
		dd.SSSpecial();
	}
	
}
