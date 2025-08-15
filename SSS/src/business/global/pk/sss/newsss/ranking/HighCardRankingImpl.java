package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;
import business.sss.c2s.cclass.newsss.Constants;
import business.sss.c2s.cclass.newsss.PockerCard;
import business.sss.c2s.cclass.newsss.RankingEnum;

import java.util.List;
import java.util.Map;

/**
 * Class {@code HighCardRankingImpl} 解析玩家手中的牌是不是单牌(1+1+1+1+1)
 */
public class HighCardRankingImpl extends AbstractRanking {

	@Override
	protected RankingResult doResolve(PlayerDun player) {
		RankingResult result = null;
		Map<Integer, Integer> rankCount = player.getCardsRankCountMap();

		boolean allOne = false;
		if (player.getCardSize() == 5) {
			if (rankCount.size() == Constants.SECOND_CARD_NUMBER) {
				List<PockerCard> cards = player.getCards();
				if (!this.isSameSuit(cards)) {
					PockerCard maxCard = cards.get(0);
					PockerCard minCard = cards.get(cards.size() - 1);
					if (maxCard.getRank().getNumber() - minCard.getRank().getNumber() >= Constants.SECOND_CARD_NUMBER) {
						allOne = true;
					}
				}
			}
		}
		else if (player.getCardSize() == 3) {
			if (rankCount.size() == Constants.FIRST_CARD_NUMBER) {
				List<PockerCard> cards = player.getCards();
				if (!this.isSameSuit(cards)) {
					PockerCard maxCard = cards.get(0);
					PockerCard minCard = cards.get(cards.size() - 1);
					if (maxCard.getRank().getNumber() - minCard.getRank().getNumber() >= Constants.FIRST_CARD_NUMBER) {
						allOne = true;
					}
				}
			}
		}

		if (allOne) {
			result = new RankingResult();
			result.setRankingEnum(RankingEnum.HIGH_CARD);
		}

		return result;
	}

}
