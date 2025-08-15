package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;

import business.sss.c2s.cclass.newsss.PockerCard;
import business.sss.c2s.cclass.newsss.RankingEnum;

import java.util.List;

/**
 *全小
 */
public class QXiaoRankingImpl_T extends AbstractRanking {
	@Override
	protected RankingResult doResolve(PlayerDun player) {
		RankingResult result = null;
		if (player.getCardSize() == 13) {
			List<PockerCard> cards = player.getCards();
			boolean flag = true;
			for (int i = 0; i < cards.size(); i++) {
				if (cards.get(i).getRank().getNumber() > 8) {
					flag = false;
				}
			}
			if (flag) {
				result = new RankingResult();
				result.setPockerCards(player.getCards());
				result.setRankingEnum(RankingEnum.QXiao);
				return result;
			}
		}
		return result;
	}
}