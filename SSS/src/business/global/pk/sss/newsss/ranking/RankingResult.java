package business.global.pk.sss.newsss.ranking;

import business.sss.c2s.cclass.newsss.PockerCard;
import business.sss.c2s.cclass.newsss.RankingEnum;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class {@code RankingResult} 牌型解析接口的返回值
 */
@SuppressWarnings("serial")
public class RankingResult implements Serializable {

    private PockerCard highCard; // 5张牌中最大的值
    private RankingEnum RankingEnum; // 牌型
    private List<PockerCard> pockerCards = new ArrayList<PockerCard>();
    public PockerCard getHighCard() {
        return highCard;
    }

    public void setHighCard(PockerCard highCard) {
        this.highCard = highCard;
    }

    public RankingEnum getRankingEnum() {
        return RankingEnum;
    }

    public void setRankingEnum(RankingEnum RankingEnum) {
        this.RankingEnum = RankingEnum;
    }

    
    
    public List<PockerCard> getPockerCards() {
		return pockerCards;
	}

	public void setPockerCards(List<PockerCard> pockerCards) {
		this.pockerCards = pockerCards;
	}

	@Override
    public String toString() {
        return "RankingResult{" +
                "RankingEnum=" + RankingEnum.getType() +
                '}';
    }
}
