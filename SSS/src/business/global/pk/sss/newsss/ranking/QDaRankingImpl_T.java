package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;

import business.sss.c2s.cclass.newsss.PockerCard;
import business.sss.c2s.cclass.newsss.RankingEnum;

import java.util.List;

/**
 *全大
 */
public class QDaRankingImpl_T extends AbstractRanking {
	@Override
	protected RankingResult doResolve(PlayerDun player) {
		RankingResult result = null;
		if (player.getCardSize() == 13) {
			List<PockerCard> cards = player.getCards();
			boolean flag = true;
			for (int i = 0; i < cards.size(); i++) {
				if (cards.get(i).getRank().getNumber() <= 7) {
					flag = false;
				}
			}
			if (flag) {
				result = new RankingResult();
				result.setPockerCards(player.getCards());
				result.setRankingEnum(RankingEnum.QDa);
				return result;
			}
		}
		return result;
	}
}