package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;

/**
 * Interface {@code IRanking} 牌型解析接口, 负责解析玩家手中的牌处于什么牌型
 */
public interface IRanking {
    RankingResult resolve(PlayerDun player);
}
