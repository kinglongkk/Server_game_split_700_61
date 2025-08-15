package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;
import business.sss.c2s.cclass.newsss.CardSuitEnum;
import business.sss.c2s.cclass.newsss.PockerCard;
import business.sss.c2s.cclass.newsss.RankingEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 凑一色
 */
public class CYiSeRankingImpl_T extends AbstractRanking {
	@Override
	protected RankingResult doResolve(PlayerDun player) {
		RankingResult result = null;
		if (player.getCardSize() == 13) {
//			List<PockerCard> cards = player.getCards();
			Map<CardSuitEnum, List<PockerCard>> suitCount = player.getCardsSuitCountMap();
			if(suitCount.size() == 2)
			{
				Set<CardSuitEnum> keySet = suitCount.keySet();
				List<CardSuitEnum> list = new ArrayList<CardSuitEnum>(keySet);  
				if( (list.get(0)==CardSuitEnum.CLUBS || list.get(0)==CardSuitEnum.SPADES) && (list.get(1)==CardSuitEnum.CLUBS || list.get(1)==CardSuitEnum.SPADES) )
				{
					result = new RankingResult();
					result.setPockerCards(player.getCards());
					result.setRankingEnum(RankingEnum.CYiSe);
				}
				if( (list.get(0)==CardSuitEnum.HEARTS || list.get(0)==CardSuitEnum.DIAMONDS) && (list.get(1)==CardSuitEnum.HEARTS || list.get(1)==CardSuitEnum.DIAMONDS) )
				{
					result = new RankingResult();
					result.setPockerCards(player.getCards());
					result.setRankingEnum(RankingEnum.CYiSe);
				}
			}
		}
	return result;
}}