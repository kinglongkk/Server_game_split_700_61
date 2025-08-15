package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;
import business.global.pk.sss.newsss.Poker;
import business.sss.c2s.cclass.newsss.CardSuitEnum;
import business.sss.c2s.cclass.newsss.Constants;
import business.sss.c2s.cclass.newsss.PockerCard;
import business.sss.c2s.cclass.newsss.RankingEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 中原一点
 */
public class ZhongYYDianImpl_T extends AbstractRanking {

	@Override
	protected RankingResult doResolve(PlayerDun player) {
		RankingResult result = null;
		if (player.getCardSize() == 13) {		
			
			if (pockerCardMapHei(player.getCards()) || pockerCardMapHong(player.getCards())) {
				result = new RankingResult();
				result.setPockerCards(player.getCards());
				result.setRankingEnum(RankingEnum.ZhongYYDian);
			}
		
		}
		return result;
	}
	
	
	private boolean pockerCardMapHong(List<PockerCard> pockerCards) {
		Map<Integer, Long> pockerCardMap = pockerCards.stream()
				.collect(Collectors.groupingBy(p -> p.type,Collectors.counting()));
		if (null == pockerCardMap) {
            return false;
        }
		// 检查是否有鬼牌，并且去除鬼牌
		if (pockerCardMap.containsKey(CardSuitEnum.GUI.getNumber())) {
			pockerCardMap.remove(CardSuitEnum.GUI.getNumber());
		}
		// 获取分组大小
		int sizePockerMap = pockerCardMap.size();
		if (sizePockerMap == 4) {
            return false;
        }
		long colorType = 0;

		
		if (pockerCardMap.containsKey(CardSuitEnum.SPADES.getNumber())) {
			colorType += pockerCardMap.get(CardSuitEnum.SPADES.getNumber());
		} 
		if (pockerCardMap.containsKey(CardSuitEnum.CLUBS.getNumber())) {
			colorType += pockerCardMap.get(CardSuitEnum.CLUBS.getNumber());
		}		
		
		if (colorType >= 2) {
			return false;
		}
		
		
		return true;
	}

	
	private boolean pockerCardMapHei(List<PockerCard> pockerCards) {
		Map<Integer, Long> pockerCardMap = pockerCards.stream()
				.collect(Collectors.groupingBy(p -> p.type,Collectors.counting()));
		if (null == pockerCardMap) {
            return false;
        }
		// 检查是否有鬼牌，并且去除鬼牌
		if (pockerCardMap.containsKey(CardSuitEnum.GUI.getNumber())) {
			pockerCardMap.remove(CardSuitEnum.GUI.getNumber());
		}
		// 获取分组大小
		int sizePockerMap = pockerCardMap.size();
		if (sizePockerMap == 4) {
            return false;
        }
		long colorType = 0;
		if (pockerCardMap.containsKey(CardSuitEnum.HEARTS.getNumber())) {
			colorType += pockerCardMap.get(CardSuitEnum.HEARTS.getNumber());
		} 
		
		if (pockerCardMap.containsKey(CardSuitEnum.DIAMONDS.getNumber())) {
			colorType += pockerCardMap.get(CardSuitEnum.DIAMONDS.getNumber());
		}
		
		if (colorType >= 2) {
			return false;
		}
		
		return true;
	}

	
	public static void main (String[] args) {
		List<Integer> ccc = Arrays.asList(0,1,2,3);
		for (int i = 0;i<99999999;i++) {
			
			Poker poker = new Poker(8, ccc, true, false,0);
			for (int p = 0;p<4;p++) {
				List<PockerCard> ret = new ArrayList<PockerCard>();
				for (int j = 0; j < Constants.HAND_CARD_NUMBER; j++) {
					ret.add(poker.dispatch());
				}
				ZhongYYDianImpl_T shunRankingImpl_T = new ZhongYYDianImpl_T();
				PlayerDun player = new PlayerDun();
				player.addData(ret);
				shunRankingImpl_T.doResolve(player);
			}
			

		}
	}
}
