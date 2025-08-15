package business.global.pk.sss.newsss;

import BaseCommon.CommLog;
import business.global.pk.sss.SSSRoom;
import business.global.pk.sss.SSSRoomPos;
import business.global.pk.sss.SSSRoomSet;
import business.global.pk.sss.SSSSetPos;
import business.global.pk.sss.utlis.PublicUtlis;
import business.sss.c2s.cclass.SSSS_RankingResult;
import business.sss.c2s.cclass.entity.PlayerCardType;
import business.sss.c2s.cclass.entity.PlayerResult;
import business.sss.c2s.cclass.entity.Ranking;
import business.sss.c2s.cclass.newsss.Constants;
import business.sss.c2s.cclass.newsss.PockerCard;
import business.sss.c2s.cclass.newsss.RankingEnum;
import business.sss.c2s.iclass.CSSS_Ranked;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.utils.StringUtil;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Dealer {
	private Poker poker;
	public SSSRoom<?> room = null;
	private SSSRoomSet set = null;
	// 玩家列表
	public Map<Long, PlayerData> PlayerList = new HashMap<Long, PlayerData>();
	public Map<Long, PlayerData> tempList = new HashMap<Long, PlayerData>();

	// 对比所有结果
	private SSSS_RankingResult sRankingResult = new SSSS_RankingResult();
	// 特殊牌玩家
	public List<PlayerData> tesupaiPlayers = new ArrayList<PlayerData>();
	// 神牌列表
	// private static HashMap<Integer, List<PockerCard>> hMap = new
	// HashMap<Integer, List<PockerCard>>();

	private HashMap<Long, List<PockerCard>> pockerMap = new HashMap<>();
	
	private long zj = -1;
	public long winMaxPid = -1;
	private int winMax = -1;
	private int playerNum = 0;
	public Dealer() {
	}


	public void clean() {
		if (null != PlayerList) {
			this.PlayerList.forEach((key,value)->{
				if (null != value) {
					value.clean();
				}
			});
			this.PlayerList.clear();
			this.PlayerList = null;
		}
		
		if (null != tesupaiPlayers) {
			this.tesupaiPlayers.forEach(key->{
				if (null != key) {
					key.clean();
				}
			});
			this.tesupaiPlayers.clear();
			this.tesupaiPlayers = null;
		}
		if (null != this.poker) {
			this.poker.clean();
			this.poker = null;
		}
		if (null != this.sRankingResult) {
			this.sRankingResult.clean();
			this.sRankingResult = null;
		}
		if (null != this.pockerMap) {
			this.pockerMap.clear();
			this.pockerMap = null;
		}
		
		this.set = null;
		this.room = null;
	}


	
	/**
	 * 添加玩家
	 * 
	 * @param pid
	 * @param player
	 */
	public void addPlayer(long pid, PlayerData player) {
		if (null == player) {
			CommLogD.error("Dealer addPlayer null == player");
			return;
		}
		this.PlayerList.put(pid, player);
	}

	public PlayerData getPlayer(long pid) {
		return this.PlayerList.get(pid);
	}

	/**
	 * 对比所有结果
	 * 
	 * @return
	 */
	public SSSS_RankingResult getSRankingResult() {
		return this.sRankingResult;
	}

	public long getWinMaxPid() {
		return this.winMaxPid;
	}

	/**
	 * 全垒打结果
	 * 
	 * @return
	 */
	public PlayerResult getFourbagger() {
		return this.sRankingResult.fourbagger;
	}

	/**
	 * 玩家设置出牌顺序, 手动配置三顺子，不会提示三顺子，
	 * 
	 * @param cRanked
	 */
	public boolean setRanked(CSSS_Ranked cRanked) {
		PlayerData playerdata = PlayerList.get(cRanked.pid);
		if (null != playerdata) {
			if (cRanked.dunPos.first.size() != 3 || cRanked.dunPos.second.size() != 5
					|| cRanked.dunPos.third.size() != 5) {
				return false;
			}
			if (playerdata.setCards(cRanked.dunPos)) {
				// 牌墩检查正确，则将新的牌墩放在临时存储中。
				if (playerdata.checkDun()) {
					this.tempList.put(cRanked.pid,playerdata.deepClone());
					return true;
				}
				return false;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}


	/**
	 * 发牌
	 * 
	 * @return
	 */
	public void dispatch(PlayerData player, List<PockerCard> ret) {
		player.setPlayerCards(new ArrayList<>(ret));
		addPlayer(player.pid, player);
	}

	
	/**
	 * 发牌
	 * 
	 * @return
	 */
	public boolean dispatch(PlayerData player, int playerNum) {
		List<PockerCard> ret = new ArrayList<PockerCard>();
		for (int j = 0; j < Constants.HAND_CARD_NUMBER; j++) {
				ret.add(this.poker.dispatch());
		}
//		if(player.posIdx==0){
//			ret = new ArrayList<PockerCard>();
//			ret.add(new PockerCard("0x2e"));
//			ret.add(new PockerCard("0x2d"));
//			ret.add(new PockerCard("0x2c"));
//			ret.add(new PockerCard("0x2a"));
//			ret.add(new PockerCard("0x29"));
//			ret.add(new PockerCard("0x28"));
//			ret.add(new PockerCard("0x17"));
//			ret.add(new PockerCard("0x06"));
//			ret.add(new PockerCard("0x36"));
//			ret.add(new PockerCard("0x25"));
//			ret.add(new PockerCard("0x15"));
//			ret.add(new PockerCard("0x33"));
//			ret.add(new PockerCard("0x32"));
//		}

		boolean checkPoker = player.checkPoker(ret);
		addPlayer(player.pid, player);
		return checkPoker;
	}



	public void init(SSSRoom<?> room,SSSRoomSet set) {
		this.room = room;
		this.set = set;
		sRankingResult = new SSSS_RankingResult();
		this.PlayerList.clear();
		this.tesupaiPlayers.clear();
		this.playerNum = room.getSSSPlayerNum();
		List<Integer> huaseList = new ArrayList<Integer>();
		int count = playerNum - 4;
		for (int i = 0; i < count; i++) {
			huaseList.add(i);
		}
		this.poker = new Poker(playerNum, huaseList, this.room.isGuipai(), false, room.getXiPaiNum());
		room.addXiPaiNum(true);
	}

	/**
	 * 比牌
	 */
	public void Compare(long zj) {
		this.zj = zj;
		tesupaiPlayers.clear();
		if (this.room.isZJModel()) {
			ZJCompare();
		} else {
			allPlayerCompare();
			getResult();

		}
	}

	/**
	 * 获取比赛结果
	 */
	@SuppressWarnings("rawtypes")
	private void getResult() {
		// 基础数据
		Iterator iter = PlayerList.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			sRankingResult.rankeds.add(((PlayerData) entry.getValue()).getRanked());
			sRankingResult.pCard1.add(((PlayerData) entry.getValue()).getPlayerCardType(1));
			sRankingResult.pCard2.add(((PlayerData) entry.getValue()).getPlayerCardType(2));
			sRankingResult.pCard3.add(((PlayerData) entry.getValue()).getPlayerCardType(3));
			sRankingResult.pCardResult1.add(((PlayerData) entry.getValue()).getPlayerResult(1));
			sRankingResult.pCardResult2.add(((PlayerData) entry.getValue()).getPlayerResult(2));
			sRankingResult.pCardResult3.add(((PlayerData) entry.getValue()).getPlayerResult(3));
			sRankingResult.simPlayerResult.add(((PlayerData) entry.getValue()).getTotalShuiS());
		}

		// 计算打枪
		sRankingResult.killRankins.forEach(ps -> {
			if (this.room.getGunBS() == 1) {
				PlayerList.get(ps.getKeyPid()).addShuiCount((this.getBaseScore()));
				PlayerList.get(ps.getToPid()).addShuiCount(-(this.getBaseScore()));
			} else {
				PlayerList.get(ps.getKeyPid()).addShuiCount(ps.getShui() * (this.room.getGunBS() - 1));
				PlayerList.get(ps.getToPid()).addShuiCount(-ps.getShui() * (this.room.getGunBS() - 1));
			}
		});

		Iterator iter2 = PlayerList.entrySet().iterator();
		while (iter2.hasNext()) {
			Map.Entry entry = (Map.Entry) iter2.next();
			PlayerResult player = ((PlayerData) entry.getValue()).getTotalShuiS();
			sRankingResult.simResults.add(player);
		}

		// 计算全垒打
		Map<Long, Integer> tmpCount = new HashMap<Long, Integer>();
		for (int i = 0; i < sRankingResult.killRankins.size(); i++) {
			long pid = sRankingResult.killRankins.get(i).getKeyPid();
			if (tmpCount.containsKey(pid)) {
				tmpCount.put(pid, tmpCount.get(pid) + 1);
			} else {
				tmpCount.put(pid, 1);
			}
		}

		Iterator<Map.Entry<Long, Integer>> it = tmpCount.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Long, Integer> next = it.next();
			if (next.getValue() >= 2 && next.getValue() == PlayerList.size() - 1) {// 全雷达条件
				PlayerData qrdplayer = PlayerList.get(next.getKey());
				int pos = qrdplayer.posIdx;
				sRankingResult.fourbagger = new PlayerResult(next.getKey(), pos, 0);

				int cnt = qrdplayer.getFirstTypeShui() + qrdplayer.getSecondTypeShui() + qrdplayer.getThirdTypeShui();
				PlayerList.values().forEach(player -> {
					if (player.pid != qrdplayer.pid) {
						int beishu = getMapai(qrdplayer, player) ? 2 : 1;
						qrdplayer.addShuiCount(2 * cnt * beishu);
						player.addShuiCount(-2 * cnt * beishu);
					}

				});
				break;
			}
		}

		Set<Long> set = PlayerList.keySet();
		List<Long> list = new ArrayList<Long>(set);
		// 特殊牌处理
		for (int i = 0; i < this.tesupaiPlayers.size(); i++) {
			sRankingResult.specialPockerCard.add(new PlayerCardType(tesupaiPlayers.get(i).pid,
					tesupaiPlayers.get(i).posIdx, tesupaiPlayers.get(i).getSpecialType()));
			PlayerData p1 = PlayerList.get(tesupaiPlayers.get(i).pid);
			for (int j = 0; j < list.size(); j++) {
				PlayerData p2 = PlayerList.get(list.get(j));
				if (p1.pid != p2.pid) {
					if (!p2.getSpecial()) {
						// 和普通玩家比
						int tmp = p1.getSpecialType();
						int shui = SpecialShui(tmp);
						p1.addShuiCount(shui);
						p2.addShuiCount(-shui);
					}
				}
			}
		}
		// 特殊牌玩家对比
		for (int i = 0; i < this.tesupaiPlayers.size(); i++) {
			for (int j = i + 1; j < this.tesupaiPlayers.size(); j++) {
				PlayerData p1 = PlayerList.get(tesupaiPlayers.get(i).pid);
				PlayerData p2 = PlayerList.get(tesupaiPlayers.get(j).pid);
				if (p1.getSpecialType() > p2.getSpecialType()) {
					int tmp = p1.getSpecialType();
					int shui = SpecialShui(tmp);
					p1.addShuiCount(shui);
					p2.addShuiCount(-shui);
				} else if (p1.getSpecialType() < p2.getSpecialType()) {
					int tmp = p2.getSpecialType();
					int shui = SpecialShui(tmp);
					p1.addShuiCount(-shui);
					p2.addShuiCount(shui);
				}
			}
		}

		// 计算最终最终结果
		Iterator iter3 = PlayerList.entrySet().iterator();
		while (iter3.hasNext()) {
			Map.Entry entry = (Map.Entry) iter3.next();
			sRankingResult.posResultList.add(((PlayerData) entry.getValue()).getTotalScore(this.set));
		}
	}

	/**
	 * 一条龙 52分的时候  至尊清零104分。。。
	 * 一条龙 26分的时候  至尊清零52分
	 * @param tmp
	 * @return
	 */
	private int SpecialShui(int tmp) {
		int point= RankingEnum.valueOf(tmp).value();
		if(this.room.yiTiaoLongDouble()&&(tmp==100||tmp==97)){
			point*=2;
		}
		return point * this.getBaseScore();
	}

	/**
	 * 所有玩家比牌
	 */
	private void allPlayerCompare() {
		Set<Long> set = PlayerList.keySet();
		List<Long> list = new ArrayList<Long>(set);
		for (int i = 0; i < list.size(); i++) {
			PlayerData p1 = PlayerList.get(list.get(i));
			if (p1.getSpecial()) {
				tesupaiPlayers.add(p1);// 特殊牌玩家
			}
		}

		for (int i = 0; i < list.size(); i++) {
			for (int j = i + 1; j < list.size(); j++) {
				PlayerData p1 = PlayerList.get(list.get(i));
				PlayerData p2 = PlayerList.get(list.get(j));
				if (!p1.getSpecial() && !p2.getSpecial()) {
					this.compareAB(p1, p2);
				}
			}
		}
	}

	/**
	 * 庄家比牌
	 */
	private void ZJCompare() {
		sRankingResult.zjid = this.zj;
		sRankingResult.beishu = this.room.roomCfg.zhuangjiaguize == 4 ? this.room.getBS() : 0;
		PlayerData zj = this.PlayerList.get(this.zj);
		Set<Long> set = PlayerList.keySet();
		List<Long> list = new ArrayList<Long>(set);
		// 特殊牌处理
		for (int i = 0; i < list.size(); i++) {
			PlayerData p1 = PlayerList.get(list.get(i));
			if (p1.getSpecial()) {
				tesupaiPlayers.add(p1);
			}
		}

		// 比牌
		for (int i = 0; i < list.size(); i++) {
			PlayerData p1 = PlayerList.get(list.get(i));
			if (zj.pid != p1.pid) {
				this.compareZJ(zj, p1);
			}
		}

		// 基础数据
		Iterator iter = PlayerList.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			sRankingResult.rankeds.add(((PlayerData) entry.getValue()).getRanked());
			sRankingResult.pCard1.add(((PlayerData) entry.getValue()).getPlayerCardType(1));
			sRankingResult.pCard2.add(((PlayerData) entry.getValue()).getPlayerCardType(2));
			sRankingResult.pCard3.add(((PlayerData) entry.getValue()).getPlayerCardType(3));
			sRankingResult.pCardResult1.add(((PlayerData) entry.getValue()).getPlayerResult(1));
			sRankingResult.pCardResult2.add(((PlayerData) entry.getValue()).getPlayerResult(2));
			sRankingResult.pCardResult3.add(((PlayerData) entry.getValue()).getPlayerResult(3));
			sRankingResult.simPlayerResult.add(((PlayerData) entry.getValue()).getTotalShuiS());
		}

		// 计算打枪
		sRankingResult.killRankins.forEach(ps -> {
			if (this.room.getGunBS() == 1) {
				PlayerList.get(ps.getKeyPid()).addShuiCount((this.getBaseScore()));
				PlayerList.get(ps.getToPid()).addShuiCount(-(this.getBaseScore()));
			} else {
				PlayerList.get(ps.getKeyPid()).addShuiCount(ps.getShui() * (this.room.getGunBS() - 1));
				PlayerList.get(ps.getToPid()).addShuiCount(-ps.getShui() * (this.room.getGunBS() - 1));
			}
		});

		Iterator iter2 = PlayerList.entrySet().iterator();
		while (iter2.hasNext()) {
			Map.Entry entry = (Map.Entry) iter2.next();
			PlayerResult player = ((PlayerData) entry.getValue()).getTotalShuiS();
			sRankingResult.simResults.add(player);
		}

		// 特殊牌数据
		for (int i = 0; i < this.tesupaiPlayers.size(); i++) {
			sRankingResult.specialPockerCard.add(new PlayerCardType(tesupaiPlayers.get(i).pid,
					tesupaiPlayers.get(i).posIdx, tesupaiPlayers.get(i).getSpecialType()));
		}

		// 最终数据
		Iterator iter3 = PlayerList.entrySet().iterator();
		while (iter3.hasNext()) {
			Map.Entry entry = (Map.Entry) iter3.next();
			sRankingResult.posResultList.add(((PlayerData) entry.getValue()).getTotalScore(this.set));
			int count = ((PlayerData) entry.getValue()).getShui();
			if (count > this.winMax) {
				this.winMax = count;
				this.winMaxPid = ((PlayerData) entry.getValue()).pid;
			}
		}

	}

	private boolean getMapai(PlayerData p1, PlayerData p2) {
		if (this.room.mapai != null) {
			List<PockerCard> cards = p1.getCards();
			for (int i = 0; i < cards.size(); i++) {
				if (cards.get(i).equals(this.room.mapai)) {
					return true;
				}
			}
			List<PockerCard> cards2 = p2.getCards();
			for (int i = 0; i < cards2.size(); i++) {
				if (cards2.get(i).equals(this.room.mapai)) {
					return true;
				}
			}
		}
		return false;
	}

	private int getBeishu() {
		int beishu = 1;
		if (this.room.roomCfg.zhuangjiaguize == 4) {
			beishu = this.room.getBS();
		}
		return beishu;
	}

	private int getBaseScore() {
		return this.room.getBaseScore();
	}

	private void compareAB(PlayerData p1, PlayerData p2) {
		int mpbs = getMapai(p1, p2) ? 2 : 1;
		int beishu = mpbs * this.getBaseScore();

		if (!p1.checkDun() || !p2.checkDun()) {
			CommLog.error("compareAB !p1.checkDun() || !p2.checkDun()");
			return;
		}

		// 第一墩
		int s1 = p1.first.compareTo(p2.first);
		if (s1 == 1) {
			p1.addBaseShuiCount(-p2.getFirstTypeShui() * beishu, 1);
			p2.addBaseShuiCount(p2.getFirstTypeShui() * beishu, 1);
		} else if (s1 == -1) {
			p1.addBaseShuiCount(p1.getFirstTypeShui() * beishu, 1);
			p2.addBaseShuiCount(-p1.getFirstTypeShui() * beishu, 1);
		}
		// 第二墩
		int s2 = p1.second.compareTo(p2.second);
		if (s2 == 1) {
			p1.addBaseShuiCount(-p2.getSecondTypeShui() * beishu, 2);
			p2.addBaseShuiCount(p2.getSecondTypeShui() * beishu, 2);
		} else if (s2 == -1) {
			p1.addBaseShuiCount(p1.getSecondTypeShui() * beishu, 2);
			p2.addBaseShuiCount(-p1.getSecondTypeShui() * beishu, 2);
		}
		// 第三墩
		int s3 = p1.third.compareTo(p2.third);
		if (s3 == 1) {
			p1.addBaseShuiCount(-p2.getThirdTypeShui() * beishu, 3);
			p2.addBaseShuiCount(p2.getThirdTypeShui() * beishu, 3);
		} else if (s3 == -1) {
			p1.addBaseShuiCount(p1.getThirdTypeShui() * beishu, 3);
			p2.addBaseShuiCount(-p1.getThirdTypeShui() * beishu, 3);
		}
		// 打枪
		if (s1 == 1 && s2 == 1 && s3 == 1) {
			int total = (p2.getFirstTypeShui() + p2.getSecondTypeShui() + p2.getThirdTypeShui()) * beishu;
			sRankingResult.killRankins.add(new Ranking(p2.pid, p2.posIdx, total, p1.pid, p1.posIdx));
		}
		if (s1 == -1 && s2 == -1 && s3 == -1) {
			int total = (p1.getFirstTypeShui() + p1.getSecondTypeShui() + p1.getThirdTypeShui()) * beishu;
			sRankingResult.killRankins.add(new Ranking(p1.pid, p1.posIdx, total, p2.pid, p2.posIdx));
		}
	}

	private void compareZJ(PlayerData p1, PlayerData p2) {
		// 庄家倍数
		// int zjbs = this.getBeishu() * this.getBaseScore();
		int zjbs = this.getBeishu();

		if (!p1.getSpecial() && !p2.getSpecial()) {
			int beishu = (getMapai(p1, p2) ? 2 : 1) * zjbs * this.getBaseScore();

			// 第一墩
			int s1 = p1.first.compareTo(p2.first);
			if (s1 == 1) {
				p1.addBaseShuiCount(-p2.getFirstTypeShui() * beishu, 1);
				p2.addBaseShuiCount(p2.getFirstTypeShui() * beishu, 1);
			} else if (s1 == -1) {
				p1.addBaseShuiCount(p1.getFirstTypeShui() * beishu, 1);
				p2.addBaseShuiCount(-p1.getFirstTypeShui() * beishu, 1);
			}
			// 第二墩
			int s2 = p1.second.compareTo(p2.second);
			if (s2 == 1) {
				p1.addBaseShuiCount(-p2.getSecondTypeShui() * beishu, 2);
				p2.addBaseShuiCount(p2.getSecondTypeShui() * beishu, 2);
			} else if (s2 == -1) {
				p1.addBaseShuiCount(p1.getSecondTypeShui() * beishu, 2);
				p2.addBaseShuiCount(-p1.getSecondTypeShui() * beishu, 2);
			}
			// 第三墩
			int s3 = p1.third.compareTo(p2.third);
			if (s3 == 1) {
				p1.addBaseShuiCount(-p2.getThirdTypeShui() * beishu, 3);
				p2.addBaseShuiCount(p2.getThirdTypeShui() * beishu, 3);
			} else if (s3 == -1) {
				p1.addBaseShuiCount(p1.getThirdTypeShui() * beishu, 3);
				p2.addBaseShuiCount(-p1.getThirdTypeShui() * beishu, 3);
			}
			// 打枪
			if (s1 == 1 && s2 == 1 && s3 == 1) {
				int total = (p2.getFirstTypeShui() + p2.getSecondTypeShui() + p2.getThirdTypeShui()) * beishu;
				sRankingResult.killRankins.add(new Ranking(p2.pid, p2.posIdx, total, p1.pid, p1.posIdx));
			}
			if (s1 == -1 && s2 == -1 && s3 == -1) {
				int total = (p1.getFirstTypeShui() + p1.getSecondTypeShui() + p1.getThirdTypeShui()) * beishu;
				sRankingResult.killRankins.add(new Ranking(p1.pid, p1.posIdx, total, p2.pid, p2.posIdx));
			}
		} else {
			if (p1.getSpecial() && p2.getSpecial()) {
				// 特殊牌玩家对比
				if (p1.getSpecialType() > p2.getSpecialType()) {
					int tmp = p1.getSpecialType();
					int shui = SpecialShui(tmp) * zjbs;
					p1.addShuiCount(shui);
					p2.addShuiCount(-shui);
				} else if (p1.getSpecialType() < p2.getSpecialType()) {
					int tmp = p2.getSpecialType();
					int shui = SpecialShui(tmp) * zjbs;
					p1.addShuiCount(-shui);
					p2.addShuiCount(shui);
				}
			} else if (p1.getSpecial() && !p2.getSpecial()) {
				// 和普通玩家比
				int tmp = p1.getSpecialType();
				int shui = SpecialShui(tmp) * zjbs;
				p1.addShuiCount(shui);
				p2.addShuiCount(-shui);
			} else if (!p1.getSpecial() && p2.getSpecial()) {
				// 和普通玩家比
				int tmp = p2.getSpecialType();
				int shui = SpecialShui(tmp) * zjbs;
				p1.addShuiCount(-shui);
				p2.addShuiCount(shui);
			}
		}

	}

	/**
	 * 检查所有用户的牌
	 */
	public boolean checkAllPlayerCard() {
		Set<Long> set = PlayerList.keySet();
		List<Long> list = new ArrayList<Long>(set);
		if (list.size() <= 0) {
            return false;
        }

		for (int i = 0; i < list.size(); i++) {
			PlayerData p1 = PlayerList.get(list.get(i));
			if (null == p1) {
				return false;
			}
			if (!p1.getSpecial()) {
				if (!p1.checkDun()) {
					// 检查临时存在的玩家数据是否存在
					return checkTempPlayerData(list.get(i));
				}else {
					//检查没通过的时候 重置准备状态
					((SSSRoomPos)this.room.getRoomPosMgr().getPosList().get(p1.posIdx)).clearCardReady();
				}
			}
		}
		return true;
	}
	
	/**
	 * 检查临时存在的玩家数据是否存在
	 * @param pid 玩家PID
	 */
	private boolean checkTempPlayerData (long pid) {
		// 检查数据是否存在
		PlayerData temp = this.tempList.get(pid);
		if (null == temp) {
			// 找不到
			CommLogD.error("checkTempPlayerData null == temp RoomID:{},PID:{}",this.room.getRoomID(),pid);
			return false;
		}
		if (!temp.checkDun()) {
			CommLogD.error("checkTempPlayerData tempPlayerData error RoomID:{},PID:{}",this.room.getRoomID(),pid);
			return false;
		}
		// 你懂的。
		this.PlayerList.put(pid, temp.deepClone());
		return true;
	}


	/**
	 * 设置神牌
	 */
	private static List<PockerCard> godCard(int index) {
		// List<PockerCard> cards1 = new ArrayList<PockerCard>();
		// cards1.add(new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_QUEUE));
		// cards1.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_QUEUE));
		// cards1.add(new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_JACK));
		//
		// cards1.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_JACK));
		// cards1.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_EIGHT));
		// cards1.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_EIGHT));
		// cards1.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_SEVEN));
		// cards1.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_SEVEN));
		//
		// cards1.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_SEVEN));
		// cards1.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_SIX));
		// cards1.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_SIX));
		// cards1.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_THREE));
		// cards1.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_THREE));
		//
		// List<PockerCard> cards2 = new ArrayList<PockerCard>();
		// cards2.add(new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_FIVE));
		// cards2.add(new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_FOUR));
		// cards2.add(new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_TWO));
		//
		// cards2.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_ACE));
		// cards2.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_NINE));
		// cards2.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_KING));
		// cards2.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_QUEUE));
		// cards2.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_JACK));
		//
		// cards2.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_TEN));
		// cards2.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_NINE));
		// cards2.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_SIX));
		// cards2.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_FOUR));
		// cards2.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_TWO));
		//
		// List<PockerCard> cards3 = new ArrayList<PockerCard>();
		// cards3.add(new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_FIVE));
		// cards3.add(new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_FOUR));
		// cards3.add(new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_TWO));
		//
		// cards3.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_NINE));
		// cards3.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_ACE));
		// cards3.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_KING));
		// cards3.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_TEN));
		// cards3.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_JACK));
		//
		// cards3.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_TEN));
		// cards3.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_NINE));
		// cards3.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_SIX));
		// cards3.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_FOUR));
		// cards3.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_TWO));
		//
		// List<PockerCard> cards4 = new ArrayList<PockerCard>();
		// cards4.add(new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_FIVE));
		// cards4.add(new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_FOUR));
		// cards4.add(new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_TWO));
		//
		// cards4.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_NINE));
		// cards4.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_ACE));
		// cards4.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_KING));
		// cards4.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_TEN));
		// cards4.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_JACK));
		//
		// cards4.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_TEN));
		// cards4.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_NINE));
		// cards4.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_SIX));
		// cards4.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_FOUR));
		// cards4.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_TWO));
		//
		// List<PockerCard> cards5 = new ArrayList<PockerCard>();
		// cards5.add(new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_FIVE));
		// cards5.add(new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_FOUR));
		// cards5.add(new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_TWO));
		//
		// cards5.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_ACE));
		// cards5.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_NINE));
		// cards5.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_KING));
		// cards5.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_TEN));
		// cards5.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_JACK));
		//
		// cards5.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_TEN));
		// cards5.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_NINE));
		// cards5.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_SIX));
		// cards5.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_FOUR));
		// cards5.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_TWO));
		//
		// List<PockerCard> cards6 = new ArrayList<PockerCard>();
		// cards6.add(new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_FIVE));
		// cards6.add(new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_FOUR));
		// cards6.add(new PockerCard(CardSuitEnum.HEARTS,
		// CardRankEnum.CARD_TWO));
		//
		// cards6.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_NINE));
		// cards6.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_ACE));
		// cards6.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_KING));
		// cards6.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_TEN));
		// cards6.add(new PockerCard(CardSuitEnum.SPADES,
		// CardRankEnum.CARD_JACK));
		//
		// cards6.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_TEN));
		// cards6.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_NINE));
		// cards6.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_SIX));
		// cards6.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_FOUR));
		// cards6.add(new PockerCard(CardSuitEnum.DIAMONDS,
		// CardRankEnum.CARD_TWO));
		//
		// hMap.put(0, cards1);
		// hMap.put(1, cards2);
		// hMap.put(2, cards3);
		// hMap.put(3, cards4);
		// hMap.put(4, cards5);
		// hMap.put(5, cards6);
		// return hMap.get(index);

		List<List<PockerCard>> list = loadCardsList("shenpai.txt");
		return list.get(index);

	}

	private static List<List<PockerCard>> loadCardsList(String filePath) {
		List<List<PockerCard>> ret = new ArrayList<List<PockerCard>>();
		try {
			if(filePath != null && filePath.length()>0){
				File file = new File(filePath);
				if (file.exists() && file.isFile()) { // 判断文件是否存在
					InputStreamReader read = null;
					BufferedReader bufferedReader = null;
					try {
						read = new InputStreamReader(new FileInputStream(file), "GBK");// 考虑到编码格式
						bufferedReader = new BufferedReader(read);
						String lineTxt = null;
						while ((lineTxt = bufferedReader.readLine()) != null) {
							String[] aa = lineTxt.split(",");
							List<PockerCard> cardslst = new ArrayList<PockerCard>();
							for (int i = 0; i < aa.length; i++) {
								cardslst.add(new PockerCard(aa[i]));
							}
							ret.add(cardslst);
						}
					}catch (Exception e){
						CommLogD.error("loadCardsListxx:"+e.getMessage());
					}finally {
						if(read!=null){
							read.close();
						}
						if(bufferedReader!=null){
							bufferedReader.close();
						}
					}
				}
			}
		}catch (Exception e){
			CommLogD.error("loadCardsList:"+e.getMessage());
		}
		return ret;
	}

	/**
	 * 检查重置
	 */
	public boolean checkReset () {
//		if (null != sRankingResult) {
//			List<PlayerResult> results= sRankingResult.playerResults;
//			// 检查是否有数据
//			if (null == results || results.size() <= 0) {
//				return false;
//			}
//			// 从大到小排序。
//			results.sort((PlayerResult item1 ,PlayerResult item2) ->item2.getShui() - item1.getShui());
//			// 获取最大的进行计算。
//			PlayerResult pResult = results.get(0);
//			if(pResult.getShui() >= this.set.getsConfigMgr().getBasisRadix() *(this.playerNum - 1)) {
//				return true;
//			}
//		}
		return false;
	}
	
	
	public void set () {
		List<PlayerResult> results = new ArrayList<>(sRankingResult.posResultList);
		List<SSSSetPos> posList = new ArrayList<>(this.set.posDict.values());
		// 从大到小排序。
		results.sort((PlayerResult item1 ,PlayerResult item2) ->item2.getShui() - item1.getShui());
		// 从小到大排序。
		posList.sort((SSSSetPos item1 ,SSSSetPos item2) ->item1.roomPos.getPoint() - item2.roomPos.getPoint());
		// 从小到大分数遍历
		int loseRadix = 0;
		int basis = this.set.getsConfigMgr().getBasisRadix();
		for (SSSSetPos sPos : posList) {
			if (sPos.roomPos.getPoint() <= 0) {
				loseRadix = (int) (Math.abs(sPos.roomPos.getPoint()) * this.set.getsConfigMgr().getLoseRadix());
				loseRadix = this.getBasisRadix(loseRadix, basis);
			} else {
				loseRadix = (int) ((-sPos.roomPos.getPoint()) * this.set.getsConfigMgr().getLoseRadix());
				loseRadix = -Math.abs(this.getBasisRadix(loseRadix, basis));
			}
			pockerMap.put(sPos.roomPos.getPid(), getCard(loseRadix, results));
		}
	}
	
	private int getBasisRadix (int loseRadix,int basis) {
		return Math.abs(loseRadix) > basis ? basis : loseRadix;
	}
	
	
	public List<PockerCard> getPockerCardList (long pid) {
		return this.pockerMap.get(pid);
	}
	
	
	private List<PockerCard> getCard(int loseRadix,List<PlayerResult> results){
		// 是否随机中
		int size = results.size();
		int avgValue = 100 / size;
		int avgSize = size /2;
		HashMap<Integer, Integer> map = new HashMap<>();
		for (int i = 0; i< avgSize ;i++){
				int value = loseRadix / (i+1);
				if (loseRadix > 0 ) {
					map.put(i,(avgValue + value));
					map.put(((size-1)-i), (avgValue - value));		
				} else {
					map.put(i,avgValue - value);
					map.put(((size-1)-i),avgValue + value );	
				}
		}
		for (int i = 0;i<size ;i++) {
			if (!map.containsKey(i)) {
				map.put(i, avgValue);
			}
		}
		return PlayerList.get(results.remove(PublicUtlis.chanceSelect(map)).getPid()).allCards.getCards();
	}
	
	
	/**
	 * 检查玩家牌墩
	 * @param pid
	 * @return
	 */
	public boolean checkDun(long pid) {
		PlayerData playerData = this.PlayerList.get(pid);
		if (null != playerData) {
			return playerData.checkDun();
		}
		return false;
	}
	
	
	public static void main(String[] args) {
		int avgSize = 7 /2;
		
		for (int i = 0,size = avgSize; i< size ;i++){
				System.out.println("t: "+i);
				System.out.println("w: "+((7-1)-i));
		}
		
//		for (int i = (8-1); i>=avgSize ;i--){
//				System.out.println("w: "+i);
//		}
	}
}
