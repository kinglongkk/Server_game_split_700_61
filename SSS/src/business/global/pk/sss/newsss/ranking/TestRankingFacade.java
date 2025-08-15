package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;

import java.util.ArrayList;
import java.util.List;

public class TestRankingFacade {

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
		rankings.add(new STaoSanTiaoRankingipml_T());
		rankings.add(new WDuiSanChongRankingImpl_T());
		rankings.add(new LDuiBanRankingIpml_T());
		rankings.add(new STongHuaRankingImpl_T());
		rankings.add(new SShunZiRankingImpl_T());
	}



	private TestRankingFacade(){	
	}

	private static class TestRankingFacadeHolder {
		private final static TestRankingFacade instance = new TestRankingFacade();
	}
	
    public static TestRankingFacade getInstance() {
        return TestRankingFacadeHolder.instance;
    }

	
	
	public RankingResult resolve(PlayerDun player, int special) {
		RankingResult result = null;
		if (special == 0) {
			for (IRanking ranking : TestRankingFacade.rankings) {
				result = ranking.resolve(player);
				if (result != null) {
					return result;
				}
			}
		} else {
			IRanking iRanking= specialIRanking(special);
			if (iRanking != null) {
				result = iRanking.resolve(player);
				if (result != null) {
					return result;
				}
			}
		}
		return result;
		
	}
	

	private IRanking specialIRanking (int special) {
		IRanking iRanking = null;
		switch (special) {
		case 85:
			iRanking = new SShunZiRankingImpl_T();
			break;
		case 86:
			iRanking = new STongHuaRankingImpl_T();
			break;
		case 87:
			iRanking = new LDuiBanRankingIpml_T();
			break;
		case 88:
			iRanking = new WDuiSanChongRankingImpl_T();
			break;
		case 89:
			iRanking = new STaoSanTiaoRankingipml_T();
			break;
		case 90:
			iRanking = new CYiSeRankingImpl_T();
			break;
		case 91:
			iRanking = new QXiaoRankingImpl_T();
			break;
		case 92:
			iRanking = new QDaRankingImpl_T();
			break;
		case 93:
			iRanking = new SFenTianXiaRankingImpl_T();
			break;
		case 94:
			iRanking = new LIULIUDASHUANImpl_T();
			break;
		case 95:
			iRanking = new STongHuaShunRankingImpl_T();
			break;
		case 96:
			iRanking = new SErHuangzuRankingImpl_T();
			break;
		case 97:
			iRanking = new YTiaoLongRankingImpl_T();
			break;
		case 98:
			iRanking = new QIXINGLIANZHUImpl_T();
			break;
		case 99:
			iRanking = new BAXIANGGUOHAIImpl_T();
			break;
		case 100:
			iRanking = new ZZunQinLongRankingImpl_T();
			break;
		default:
			break;
		}
		return iRanking;
	}
	


}
