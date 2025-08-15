package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;

import java.util.ArrayList;
import java.util.List;


/**
 *  牌型校验统一入口
 */
public class RankingFacade implements IRanking {
	private final static List<IRanking> rankings = new ArrayList<IRanking>();

	static {
		rankings.add(new ZZunQinLongRankingImpl_T());
		rankings.add(new BAXIANGGUOHAIImpl_T());
		rankings.add(new QIXINGLIANZHUImpl_T());
		rankings.add(new YTiaoLongRankingImpl_T());
		rankings.add(new SErHuangzuRankingImpl_T());
		rankings.add(new STongHuaShunRankingImpl_T());
		rankings.add(new LIULIUDASHUANImpl_T());
		rankings.add(new SFenTianXiaRankingImpl_T());
		rankings.add(new QDaRankingImpl_T());
		rankings.add(new QXiaoRankingImpl_T());
		rankings.add(new CYiSeRankingImpl_T());
		rankings.add(new ZhongYYDianImpl_T());
		rankings.add(new STaoSanTiaoRankingipml_T());
		rankings.add(new WDuiSanChongRankingImpl_T());
		rankings.add(new LDuiBanRankingIpml_T());
		rankings.add(new STongHuaRankingImpl_T());
		rankings.add(new SShunZiRankingImpl_T());

		rankings.add(new FiveOfTheKindRankingImpl());
		rankings.add(new StraightFlushRankingImpl());
		rankings.add(new FourOfTheKindRankingImpl());
		rankings.add(new FullHouseRankingImpl());
		rankings.add(new TwoPairFlushRankingImpl());
		rankings.add(new OnePairFlushRankingImpl());
		rankings.add(new FlushRankingImpl());
		rankings.add(new StraightRankingImpl());
		rankings.add(new ThreeOfTheKindRankingImpl());
		rankings.add(new TwoPairsRankingImpl());
		rankings.add(new OnePairRankingImpl());
		rankings.add(new HighCardRankingImpl());
		rankings.add(new DefaultRankingImpl());
	}


	private RankingFacade(){	
	}

	private static class RankingFacadeHolder {
		private final static RankingFacade instance = new RankingFacade();
	}
	
    public static RankingFacade getInstance() {
        return RankingFacadeHolder.instance;
    }

	

	@Override
	public RankingResult resolve(PlayerDun player) {
		RankingResult result = null;
		for (IRanking ranking : RankingFacade.rankings) {
			result = ranking.resolve(player);
			if (result != null) {
				return result;
			}
		}
		return result;
	}

}
