package business.sss.c2s.cclass;

import business.sss.c2s.cclass.entity.PlayerCardType;
import business.sss.c2s.cclass.entity.PlayerResult;
import business.sss.c2s.cclass.entity.Ranking;
import business.sss.c2s.iclass.CSSS_Ranked;


import java.util.ArrayList;
import java.util.List;

/**
 * 比牌结果
 * 
 * @author Huaxing TODO 添加特殊牌结果，
 */

public class SSSS_RankingResult {
	public long zjid = 0;
	public long beishu = 1;
	// 所有玩家设置好的牌序
	public List<CSSS_Ranked> rankeds = new ArrayList<CSSS_Ranked>();// 一个玩家，一条记录

	// 第一轮PockerCard
	public List<PlayerCardType> pCard1 = new ArrayList<PlayerCardType>();// 一个玩家，一条记录
	public List<PlayerResult> pCardResult1 = new ArrayList<PlayerResult>();// 一个玩家，一条记录

	// 第二轮PockerCard
	public List<PlayerCardType> pCard2 = new ArrayList<PlayerCardType>();
	public List<PlayerResult> pCardResult2 = new ArrayList<PlayerResult>();

	// 第三轮PockerCard
	public List<PlayerCardType> pCard3 = new ArrayList<PlayerCardType>();
	public List<PlayerResult> pCardResult3 = new ArrayList<PlayerResult>();

	// 打枪的
	public List<Ranking> killRankins = new ArrayList<Ranking>();
	// 全垒打的
	public PlayerResult fourbagger = null;
	// 特殊牌PockerCard
	public List<PlayerCardType> specialPockerCard = new ArrayList<PlayerCardType>();
	// 记录特殊牌的结果
	public List<PlayerResult> specialResults = new ArrayList<PlayerResult>();// 没用？

	// 没有打枪的结果
	public List<PlayerResult> simPlayerResult = new ArrayList<PlayerResult>();
	// 包括打枪的结果
	public List<PlayerResult> simResults = new ArrayList<PlayerResult>();
	// 总结算
	public List<PlayerResult> posResultList = new ArrayList<PlayerResult>();
	
	public void clean () {
		if (null != this.rankeds) {
			this.rankeds.clear();
			this.rankeds = null;
		}
		
		if (null != this.pCard1) {
			this.pCard1.clear();
			this.pCard1 = null;
		}
		if (null != this.pCardResult1) {
			this.pCardResult1.clear();
			this.pCardResult1 = null;
		}
		if (null != this.pCard2) {
			this.pCard2.clear();
			this.pCard2 = null;
		}
		if (null != this.pCardResult2) {
			this.pCardResult2.clear();
			this.pCardResult2 = null;
		}
		
		if (null != this.pCard3) {
			this.pCard3.clear();
			this.pCard3 = null;
		}
		if (null != this.pCardResult3) {
			this.pCardResult3.clear();
			this.pCardResult3 = null;
		}
		if (null != this.killRankins) {
			this.killRankins.clear();
			this.killRankins = null;
		}
		
		if (null != this.fourbagger) {
			this.fourbagger = null;
		}
		
		if (null != this.specialPockerCard) {
			this.specialPockerCard.clear();
			this.specialPockerCard = null;
		}
		
		
		if (null != this.specialResults) {
			this.specialResults.clear();
			this.specialResults = null;
		}
		
		if (null != this.simResults) {
			this.simResults.clear();
			this.simResults = null;
		}
		
		if (null != this.simResults) {
			this.simResults.clear();
			this.simResults = null;
		}
		
		if (null != this.posResultList) {
			this.posResultList.clear();
			this.posResultList = null;
		}
	}


}
