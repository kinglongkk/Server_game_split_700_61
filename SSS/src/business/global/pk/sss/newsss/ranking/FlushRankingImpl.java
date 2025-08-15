package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;
import business.sss.c2s.cclass.newsss.CardSuitEnum;
import business.sss.c2s.cclass.newsss.PockerCard;
import business.sss.c2s.cclass.newsss.RankingEnum;


import java.util.ArrayList;
import java.util.List;

/**
 *  解析玩家手中的牌是不是同花(花色一样)
 */
public class FlushRankingImpl extends AbstractRanking {

	@Override
	protected RankingResult doResolve(PlayerDun player) {
		RankingResult result = null;
//		int guicnt = player.getGuiCount();
		if (player.getCardSize() == 5) {
			List<PockerCard> cards = player.getCards();
			List<PockerCard> newcards = new ArrayList<PockerCard>();
			for (int i = 0; i < cards.size(); i++) {
				if (cards.get(i).getSuit() != CardSuitEnum.GUI) {
					newcards.add(cards.get(i));
				}
			}
			if (this.isSameSuit(newcards)) { // 如果是同色
				result = new RankingResult();
				result.setRankingEnum(RankingEnum.FLUSH);
			}
		}

		return result;
	}

}
