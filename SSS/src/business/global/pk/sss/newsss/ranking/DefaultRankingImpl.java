package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;
import business.sss.c2s.cclass.newsss.RankingEnum;

/**
 * Class {@code DefaultRankingImpl}
 * 默认实现, 返回HIGH_CARD类型
 */
public class DefaultRankingImpl extends AbstractRanking {

    @Override
	protected RankingResult doResolve(PlayerDun player) {

        RankingResult result = new RankingResult();
        result.setRankingEnum(RankingEnum.HIGH_CARD);

        return result;
    }

}
