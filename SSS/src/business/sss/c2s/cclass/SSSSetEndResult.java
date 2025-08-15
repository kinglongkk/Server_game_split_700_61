package business.sss.c2s.cclass;

import business.sss.c2s.cclass.entity.PlayerResult;
import business.sss.c2s.iclass.CSSS_Ranked;

import java.util.List;

/**
 *
 * @author Huaxing
 *
 */
public class SSSSetEndResult {
	private List<CSSS_Ranked> rankeds;
	private List<PlayerResult> posResultList;
	private long zjid = -1;
	private int beishu = 1;

	public SSSSetEndResult(List<CSSS_Ranked> rankeds, List<PlayerResult> playerResults, long zjid, int beishu) {
		super();
		this.rankeds = rankeds;
		this.posResultList = playerResults;
		this.zjid = zjid;
		this.beishu = beishu;
	}

	public List<CSSS_Ranked> getRankeds() {
		return rankeds;
	}

	public void setRankeds(List<CSSS_Ranked> rankeds) {
		this.rankeds = rankeds;
	}

	public List<PlayerResult> getPlayerResults() {
		return posResultList;
	}

	public void setPlayerResults(List<PlayerResult> playerResults) {
		this.posResultList = playerResults;
	}

}
