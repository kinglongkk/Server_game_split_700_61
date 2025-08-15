package business.global.pk.sss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;

import com.ddm.server.common.utils.CommTime;
import com.ddm.server.websocket.handler.requset.WebSocketRequestDelegate;
import com.google.gson.Gson;

import BaseCommon.CommLog;
import business.global.pk.sss.newsss.Dealer;
import business.global.pk.sss.newsss.PlayerData;
import business.global.pk.sss.newsss.PlayerDun;
import business.global.pk.sss.newsss.ranking.RankingFacade;
import business.global.pk.sss.utlis.SSSConfigMgr;
import business.global.room.base.AbsRoomPos;
import business.global.room.base.AbsRoomSet;
import business.sss.c2s.cclass.CSSS_PlayerRanked;
import business.sss.c2s.cclass.SSSRoomSetInfo;
import business.sss.c2s.cclass.SSSRoomSet_End;
import business.sss.c2s.cclass.SSSRoomSet_Pos;
import business.sss.c2s.cclass.SSSS_RankingResult;
import business.sss.c2s.cclass.SSSSetEndResult;
import business.sss.c2s.cclass.entity.PlayerResult;
import business.sss.c2s.cclass.newsss.PockerCard;
import business.sss.c2s.iclass.CSSS_Ranked;
import business.sss.c2s.iclass.SSSS_BeiShuSelect;
import business.sss.c2s.iclass.SSSS_Result;
import business.sss.c2s.iclass.SSSS_SetEnd;
import business.sss.c2s.iclass.SSSS_SetStart;
import business.sss.c2s.iclass.SSSS_ZJBeiShu;
import cenum.PrizeType;
import cenum.room.SetState;
import cenum.room.TrusteeshipState;
import core.db.entity.clarkGame.GameSetBO;
import core.db.persistence.BaseDao;
import core.db.service.clarkGame.GameSetBOService;
import core.ioc.ContainerMgr;
import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 自由扑克一局游戏逻辑
 */
public class SSSRoomSet extends AbsRoomSet  {
	private SSSConfigMgr sConfigMgr = new SSSConfigMgr();
	public Hashtable<Integer, SSSSetPos> posDict = new Hashtable<>(); // 每个位置信息
	public SSSRoom<?> room = null;
	public long startMS = 0;
	public final int InitTime = 4500;
	public SetState state = SetState.Waiting;// 牌局状态
	public Dealer dealer = new Dealer();
	public SSSRoomSet_End setEnd =new SSSRoomSet_End();
	public SSSS_RankingResult sRankingResult;
	public PlayerResult fourbagger;// 全垒打
	public GameSetBO bo = null;
	Map<Long, Integer> beishulist = new HashMap<Long, Integer>();
	Timer timer = new Timer();// 倍率计时

	// 参与的玩家人数
	public int playerNum;
	HashMap<Integer, List<PockerCard>> hMap = new HashMap<Integer, List<PockerCard>>();
	private ArrayList<String> first = null;
	private ArrayList<String> second = null;
	private ArrayList<String> third = null;
	private ArrayList<PockerCard> seconds = null;
	private ArrayList<PockerCard> thirds = null;
	private ArrayList<PockerCard> firsts = null;
	private long setStartTime = 0;
	private int playerMaxNum = 8;

	@SuppressWarnings("rawtypes")
	public SSSRoomSet(SSSRoom room) {
		super(room.getCurSetID());
		this.startMS = CommTime.nowMS();
		this.room = room;
		this.playerNum = room.getSSSPlayerNum();
		this.playerMaxNum = room.getPlayerNum();
		this.room.setEnterRoomFlag(false);
		((SSSRoomPosMgr) this.room.getRoomPosMgr()).clearCardReady();
		this.startSet();

	}
	
	/**
	 * 标识Id
	 * 
	 * @return
	 */
	@Override
	public int getTabId() {
		return this.room.getTabId();
	}
	
	@Override
	public void clear() {
		if (null != this.posDict) {
			this.posDict.forEach((key,value)->{
				if (null != value) {
					value.clean();
				}
			});
			this.posDict.clear();
			this.posDict = null;
		}
		if (null != this.dealer) {
			this.dealer.clean();
			this.dealer = null;
		}
		
		if (null != this.sRankingResult) {
			this.sRankingResult.clean();
			this.sRankingResult = null;
		}
		if (null != this.timer) {
			timer.cancel();
			timer = null;
		}
		this.room = null;
		this.setEnd = null;
		this.fourbagger = null;
		this.bo = null;
		this.beishulist = null;
		this.hMap = null;
		this.first = null;
		this.second = null;
		this.third = null;
		this.seconds = null;
		this.thirds = null;
		this.sConfigMgr = null;
		
		
	}



	/**
	 * 开始设置
	 */
	public void startSet() {
		// 庄家处理
		if (this.room.isZJModel()) {
			zjDeal();
		}
		if (this.room.roomCfg.zhuangjiaguize == 4) {
			this.room.getRoomPosMgr().notify2All(SSSS_BeiShuSelect.make(this.room.getRoomID()));
//			timer.schedule(this, 10 * 1000);
		} else {
			startGame();
		}

	}

	public void setBeiShu(long pid, int pos, int beishu) {
		if (!SetState.Waiting.equals(this.state)) {
			return;
		}
		if (!this.beishulist.containsKey(pid)) {
			this.beishulist.put(pid, beishu);
			if (beishulist.size() == this.playerNum) {
				timer.cancel();
				timer = null;
				// 确认庄家，广播
				beishuD();
				startGame();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void beishuD() {
		int max = 1;
		if (beishulist.size() != 0) {

			for (Integer value : beishulist.values()) {
				if (max < value) {
					max = value;
				}
			}
			if (max == 1) {
				// 随机
				Random random = new Random();
				int ran = random.nextInt(playerNum);
				this.room.setZJ(getPid(ran));
				this.room.setBS(max);
				this.room.getRoomPosMgr().notify2All(SSSS_ZJBeiShu.make(this.room.getRoomID(), ran, max));
			} else {
				List<Long> list = new ArrayList<Long>();
				Iterator iter = beishulist.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					long key = (long) entry.getKey();
					int val = (int) entry.getValue();
					if (max == val) {
						list.add(key);
					}
				}
				if (list.size() > 0) {
					Random random = new Random();
					// int ran = random.nextInt(playerNum);
					Long pid = list.get(random.nextInt(list.size()));
					this.room.setZJ(pid);
					this.room.setBS(max);
					this.room.getRoomPosMgr().notify2All(SSSS_ZJBeiShu.make(this.room.getRoomID(),
							this.room.getRoomPosMgr().getPosByPid(this.room.getZJ()).getPosID(), max));
				}
			}
		} else {
			// 随机
			Random random = new Random();
			int ran = random.nextInt(playerNum);
			this.room.setZJ(getPid(ran));
			this.room.setBS(max);
			this.room.getRoomPosMgr().notify2All(SSSS_ZJBeiShu.make(this.room.getRoomID(), ran, max));
		}

	}

	private void startGame() {
		// 设置参与游戏的玩家

		for (int i = 0; i < playerMaxNum; i++) {
			SSSRoomPos<?> sssRoomPos = (SSSRoomPos<?>) this.room.getRoomPosMgr().getPosByPosID(i);
			if (sssRoomPos == null || sssRoomPos.getPid() == 0) {
                continue;
            }

			if (sssRoomPos.isReady() && this.room.getCurSetID() == 1
					|| (sssRoomPos.isGameReady() && this.room.getCurSetID() > 1 && sssRoomPos.getPid() != 0)) {
				sssRoomPos.setPlayTheGame(true);
			}

		}
		this.room.getRoomPosMgr().clearGameReady();

		// 对每个位置的人设置牌
		int randomPos = SSSRandomPos.randomSaizi(playerNum);
		for (int idx = 0; idx < playerMaxNum; idx++) {
			int i = (randomPos + idx) % playerMaxNum;
			SSSRoomPos<?> sssRoomPos = (SSSRoomPos<?>) this.room.getRoomPosMgr().getPosByPosID(i);
			if (sssRoomPos == null || sssRoomPos.getPid() == 0 || !sssRoomPos.isPlayTheGame()) {
                continue;
            }
			posDict.put(i, new SSSSetPos(i, sssRoomPos, this));

		}
		this.testReSet();
		this.state = SetState.Init;// 牌局状态
		// 开始发牌
		this.setStartTime = CommTime.nowMS();
		for (int i = 0; i < playerMaxNum; i++) {
			SSSRoomPos<?> sssRoomPos = (SSSRoomPos<?>) this.room.getRoomPosMgr().getPosByPosID(i);
			if (sssRoomPos == null || sssRoomPos.getPid() == 0 || !sssRoomPos.isPlayTheGame()) {
                continue;
            }
			long pid = sssRoomPos.getPid();
			this.room.getRoomPosMgr().notify2Pos(i, SSSS_SetStart.make(this.room.getRoomID(), this.getNotify_set(pid)));
		}

		room.getRoomPosMgr().setAllLatelyOutCardTime();
		this.room.getTrusteeship().setTrusteeshipState(TrusteeshipState.Normal);
		this.room.setEnterRoomFlag(true);
	}

	private void testReSet () {
		Dealer test = new Dealer();
		test.init(this.room,this);
		boolean isReset = false;
		for (SSSSetPos sPos :this.posDict.values()) {
			if(!test.dispatch(new PlayerData(this.room.getRoomID(), sPos.roomPos.getPid(), sPos.posID), playerNum)) {
				isReset = true;
				break;
			}
		}
		if (isReset) {
			test = null;
			testReSet();
		}
		if(test != null){
			test.Compare(this.room.getZJ());
			if (test.checkReset()) {
				test = null;
				testReSet();
			}
			if(test != null){
				test.set();
				this.dealer.init(this.room, this);
				List<PockerCard> cards = null;
				for (SSSSetPos sPos :this.posDict.values()) {
					cards = test.getPockerCardList(sPos.roomPos.getPid());
					this.dealer.dispatch(new PlayerData(this.room.getRoomID(), sPos.roomPos.getPid(), sPos.posID),cards);
					sPos.init(cards);
				}
			}
		}
	}
	
	
	

	/**
	 * 庄家设置
	 */
	private void zjDeal() {
		// 0:房主坐庄 1:随机坐庄 2轮流坐庄 3赢分坐庄 4倍率抢庄
		Random random = new Random();
		switch (this.room.roomCfg.zhuangjiaguize) {
		case 0:
			this.room.setZJ(this.room.getOwnerID());
			break;
		case 1:
			int ran = random.nextInt(playerNum);
			this.room.setZJ(getPid(ran));
			break;
		case 2:
			if (this.room.getZJ() == -1) {
				this.room.setZJ(getPid(random.nextInt(playerNum)));
//				this.room.setZJ(this.room.getOwnerID());
			} else {
				long zhuanjia = this.room.getZJ();
				int pos = this.room.getRoomPosMgr().getPosByPid(zhuanjia).getPosID();
				pos++;
				if (pos >= playerNum) {
					pos = 0;
				}
				this.room.setZJ(this.room.getRoomPosMgr().getPosByPosID(pos).getPid());
			}
			break;
		case 3:
			if (this.room.getZJ() == -1) {
				this.room.setZJ(getPid(random.nextInt(playerNum)));
			}
			// else {
			// long zjid = this.dealer.getWinMaxPid();
			// if(zjid !=-1)
			// {
			// this.room.setZJ(zjid);
			// }
			// }
			break;
		case 4:
			break;
		}
	}

	/**
	 * 每200ms更新1次 秒
	 * 
	 * @param sec
	 * @return T 是 F 否
	 */
	@Override
	public boolean update(int sec) {
		boolean isClose = false;
		if (this.state == SetState.Init) {
			// if (CommTime.nowMS() > this.startMS + InitTime) {
			this.state = SetState.Playing;
			if (!this.startNewRound()) {
				this.endSet();
			}
			// }
		} else if (this.state == SetState.Playing) {
			boolean isStartNewRound = !this.startNewRound();
			if (isStartNewRound) {
				this.endSet();
			}
		} else if (this.state == SetState.End) {
			isClose = true;
		}
		return isClose;
	}



	@Override
	public void clearBo() {

	}

	/**
	 * 局结束
	 */
	@Override
	public void endSet() {
		if (this.state == SetState.End) {
            return;
        }
		this.state = SetState.End;
		setEnd(true);
		this.calcPoint();
		// 赢分最多做庄处理
		if (this.room.roomCfg.zhuangjiaguize == 3) {
			if (this.dealer.winMaxPid != -1) {
				this.room.setZJ(this.dealer.winMaxPid);
			}
		}
		SSSS_SetEnd end=SSSS_SetEnd.make(this.room.getRoomID(), this.state);
		this.room.getRoomPosMgr().notify2All(SSSS_Result.make(this.room.getRoomID(), this.getSRankingResult()));
		this.room.getRoomPosMgr().notify2All(end);
		end.reason= BaseDao.stackTrace1();
	}

	/**
	 * 设置玩家的牌序
	 */
	public boolean setRankeds(CSSS_Ranked cRanked, int posIndex, boolean isSpecial) {
		if (isSpecial) {
			return this.dealer.getPlayer(this.posDict.get(posIndex).roomPos.getPid()).getSpecial();
		} else {
			return this.dealer.setRanked(cRanked);

		}
	}

	public boolean checkRanked(long pid) {
		return this.dealer.getPlayer(pid).checkCards();
	}

	/**
	 * 获取所有人的牌序列表
	 */
	@SuppressWarnings("rawtypes")
	public List<CSSS_Ranked> getAllRankeds() {
		List<CSSS_Ranked> rankeds = new ArrayList<CSSS_Ranked>();
		Iterator iter = this.dealer.PlayerList.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			rankeds.add(((PlayerData) entry.getValue()).getRanked());
		}
		return rankeds;
	}

	/**
	 * 牌准备的托管
	 */
	public void roomTrusteeship(int posID) {
		// 判断玩家是否处于配牌阶段
		if (this.state != SetState.Playing) {
			return;
		}
		for (int num = 0; num < playerMaxNum; num++) {
			SSSRoomPos<?> sssRoomPos = (SSSRoomPos<?>) this.room.getRoomPosMgr().getPosByPosID(num);
			if (sssRoomPos == null || sssRoomPos.getPid() <= 0) {
                continue;
            }
			if (!posDict.containsKey(sssRoomPos.getPosID())) {
                continue;
            }
			SSSSetPos sssSetPos = posDict.get(sssRoomPos.getPosID());
			if (posID > -1) {
				if (sssSetPos.posID != posID) {
                    continue;
                }
			} else if (!sssSetPos.roomPos.isRobot() && !sssSetPos.roomPos.isTrusteeship()) {
				continue;
			}
			if(sssRoomPos.isCardReady()) continue;

			// if (sssSetPos.isGameCard())
			// continue;

			List<PockerCard> shouCard = sssSetPos.privateCards;
			sortCards(shouCard);
			CSSS_PlayerRanked dunPos = new CSSS_PlayerRanked();


			for(int j=0;j<shouCard.size();j++){
				first = new ArrayList<String>();
				firsts = new ArrayList<PockerCard>();
				second = new ArrayList<String>();
				seconds = new ArrayList<PockerCard>();
				third = new ArrayList<String>();
				thirds = new ArrayList<PockerCard>();
				List<Integer> containsCardId = new ArrayList<Integer>();
				for (int k = 0, size = shouCard.size(); k < size; k++) {
					int i=(k+j)%shouCard.size();
					if (!containsCardId.contains(shouCard.get(i).cardID) && containsCardId.size() < 3) {
						containsCardId.add(shouCard.get(i).cardID);
						first.add(shouCard.get(i).toString());
						firsts.add(new PockerCard(shouCard.get(i).toString()));
					} else if (seconds.size() < 5) {
						second.add(shouCard.get(i).toString());
						seconds.add(new PockerCard(shouCard.get(i).toString()));
					} else if (thirds.size() < 5) {
						third.add(shouCard.get(i).toString());
						thirds.add(new PockerCard(shouCard.get(i).toString()));
					}
				}
				// 判断中墩和前墩墩的大小是否倒水。
				PlayerDun firstdun = new PlayerDun();
				firstdun.addData(firsts);
				RankingFacade.getInstance().resolve(firstdun);
				PlayerDun seconddun = new PlayerDun();
				seconddun.addData(seconds);
				RankingFacade.getInstance().resolve(seconddun);
				// 判断中墩和后墩的大小是否倒水。
				PlayerDun thirddun = new PlayerDun();
				thirddun.addData(thirds);
				RankingFacade.getInstance().resolve(thirddun);
				if(firstdun.compareTo(seconddun)==1&&firstdun.compareTo(thirddun)==1){
					if (seconddun.compareTo(thirddun) == -1) {
						dunPos.first = first;
						dunPos.second = third;
						dunPos.third = second;
					} else {
						dunPos.first = first;
						dunPos.second = second;
						dunPos.third = third;
					}
					CSSS_Ranked cRanked = new CSSS_Ranked(room.getRoomID(), sssSetPos.roomPos.getPid(), sssSetPos.roomPos.getPosID(),
							dunPos);
					WebSocketRequestDelegate request = new WebSocketRequestDelegate();
					room.cardReady(request, true, sssSetPos.roomPos.getPid(), sssSetPos.roomPos.getPosID(), cRanked, false);
					break;
				}

			}
		}
	}

	/**
	 * 手牌排序
	 */
	private void sortCards(List<PockerCard> shouCard) {
		Collections.sort(shouCard, new Comparator<PockerCard>() {
			@Override
			public int compare(PockerCard o1, PockerCard o2) {
				return o1.cardID - o2.cardID;
			}
		});
	}

	@SuppressWarnings("rawtypes")
	public long getPid(int idx) {
		List<Long> list = new ArrayList<Long>();
		List<SSSRoomPos> rPos = this.room.getAllSSSRoomPosList();
		for (SSSRoomPos sRoomPos : rPos) {
			list.add(sRoomPos.getPid());
		}
		if (list.size() == idx || idx < 0) {
            return list.get(0);
        }
		return list.get(idx);

	}

	/**
	 * 结算积分
	 */
	public void calcPoint() {

		GameSetBOService gameSetBoService = ContainerMgr.get().getComponent(GameSetBOService.class);
		GameSetBO gameSetBO = gameSetBoService.findOne(room.getRoomID(), this.room.getCurSetID());
		this.bo = gameSetBO == null ? new GameSetBO() : gameSetBO;
		if(gameSetBO==null){
			bo.setRoomID(room.getRoomID());
			bo.setSetID(getSetID());
		}

		fourbagger = this.getFourbagger();
		sRankingResult = this.getSRankingResult();
		List<PlayerResult> allPlayerResults = sRankingResult.posResultList;
		SSSRoomPosMgr roomPos = (SSSRoomPosMgr) this.room.getRoomPosMgr();
		roomPos.clearCardReady();
		room.playerResult.add(allPlayerResults);
		for (PlayerResult pResult : allPlayerResults) {
			posDict.get(pResult.getPosIdx()).calcPosEnd();
		}

		// 总结每个人积分
		for (PlayerResult pResult : allPlayerResults) {
			setEnd.pResults.add(pResult);
			goldEnd(pResult.getPosIdx(), pResult.getShui());
		}
		room.getRoomPosMgr().setAllLatelyOutCardTime();
		setEnd.endTime = CommTime.nowSecond();
		SSSSetEndResult setEndResult = new SSSSetEndResult(sRankingResult.rankeds, this.getPlayerResults(),
				this.room.getZJ(), this.room.roomCfg.zhuangjiaguize == 4 ? this.room.getBS() : 0);
		String dataJsonRes = new Gson().toJson(setEndResult);
		bo.setDataJsonRes(dataJsonRes);
		bo.setEndTime(setEnd.endTime);
		if (this.checkExistPrizeType(PrizeType.Gold)) {
            return;
        }
		gameSetBoService.saveOrUpDate(bo);
	}

	public  List<PlayerResult> getPlayerResults(){
		List<PlayerResult> playerResults=sRankingResult.posResultList;

		for (AbsRoomPos pos : this.room.getRoomPosMgr().posList) {
				if(pos.getPid()<=0L) {
                    continue;
                }
				boolean addFlag=true;
		       for(PlayerResult playerResult:playerResults){
		       		if(playerResult.getPid()==pos.getPid()){
						addFlag=false;
						break;
					}
			   }
		       if(addFlag){
				   PlayerResult playerResult=new PlayerResult();
				   playerResult.setBaseMark(room.getBaseMark());
				   playerResult.setDoubleNum(room.getBS());
				   playerResult.setPid(pos.getPid());
				   playerResult.setPosIdx(pos.getPosID());
				   playerResults.add(playerResult);
			   }
		}
//		this.room.
		return playerResults;
	}
	/**
	 * 练习场结算
	 */
	private void goldEnd(int posID, int shui) {
//		if (room.getBaseRoomConfigure().getPrizeType() == PrizeType.Gold) {
//			SSSRoomPos<?> pos = (SSSRoomPos<?>) room.getRoomPosMgr().getPosByPosID(posID);
//			if (!RobotMgr.getInstance().isRobot((int) pos.getPid())) {
//				Player player = PlayerMgr.getInstance().getPlayer(pos.getPid());
//				player.getFeature(PlayerCurrency.class).goldRoomEnd(shui, room.getBaseMark(), ItemFlow.CreateRoom_SSS,
//						GameType.SSS.value());
//			}
//		}
	}

	/**
	 * 开启新的回合
	 */
	public boolean startNewRound() {
		SSSRoomPosMgr roomPosMgr = (SSSRoomPosMgr) this.room.getRoomPosMgr();
		if (roomPosMgr.isAllCardReady()) {
			// if(this.dealer.checkPlayerList())
			// {
			// this.room.curSet.dealer.Compare(this.room.getZJ());
			// return false;
			// }
			if (this.dealer.checkAllPlayerCard()) {
				this.dealer.Compare(this.room.getZJ());
			} else {
				CommLog.error("checkAllPlayerCard not false");
				return true;
			}
			return false;
		}
		return true;
	}

	/**
	 * 获取通知设置
	 * 
	 * @param pid
	 *            用户ID
	 * @return
	 */
	@Override
	@SuppressWarnings("unused")
	public SSSRoomSetInfo getNotify_set(long pid) {
		SSSRoomSetInfo ret = new SSSRoomSetInfo();
		ret.setSetID(this.room.getCurSetID());
		ret.setStartTime = this.setStartTime;
		ret.mapai=this.room.mapai == null ? "" : this.room.mapai.toString();
		ret.beishu =this.room.roomCfg.zhuangjiaguize == 4? this.room.getBS():0;
		if (this.room.isZJModel()) {
			AbsRoomPos roomPosDelegateAbstract = this.room.getRoomPosMgr().getPosByPid(this.room.getZJ());
			if (roomPosDelegateAbstract != null) {
				ret.backerPos = roomPosDelegateAbstract.getPosID();
			}
		} else {
			ret.backerPos = -1;
		}

		ret.setCurrentTime = CommTime.nowMS();
		// 每个玩家的牌面
		// ret.posInfo = new ArrayList<>();
		List<SSSRoomSet_Pos> posInfo = new ArrayList<SSSRoomSet_Pos>();
		for (int i = 0; i < playerMaxNum; i++) {
			SSSRoomPos<?> sssRoomPos = (SSSRoomPos<?>) this.room.getRoomPosMgr().getPosByPosID(i);
			if (sssRoomPos == null || sssRoomPos.getPid() == 0) {
                continue;
            }
			if (pid == sssRoomPos.getPid()) {
				PlayerData player = this.dealer.getPlayer(sssRoomPos.getPid());
				if (player == null) {
                    continue;
                }
				ret.isXiPai = player.isXiPai();
				ret.setPosList.add(player.getSetPosInfo());
				ret.isPlaying = sssRoomPos.isPlayTheGame();
			}
			posInfo.add(new SSSRoomSet_Pos(sssRoomPos.getPosID(), sssRoomPos.getPid(), sssRoomPos.isPlayTheGame()));
		}
		ret.posInfo = posInfo;
		ret.state = this.state == SetState.Waiting ? SetState.Init : this.state;// 牌局状态
		// ret.state = this.state; // 当前局状态 Init；End； Playing不需要信息
		// 如果是等待状态： waiting；
		if (this.state == SetState.Playing) {
			int pos = this.room.getRoomPosMgr().getPosByPid(pid).getPosID();

		}
		return ret;
	}

	public void setXiPai(long pid) {
		if (this.dealer.PlayerList.containsKey(pid)) {
			this.dealer.getPlayer(pid).setXiPai(true);
		}
	}

	/**
	 * 获取通知设置结束
	 * 
	 * @return
	 */
	@Override
	public SSSRoomSet_End getNotify_setEnd() {
		return setEnd;
	}

	public void run() {
		beishuD();
		startGame();
	}

	// =================================================================
	// 对比最终结果
	private PlayerResult getFourbagger() {
		return ((SSSRoomSet)room.getCurSet()).dealer.getFourbagger();
	}

	// 对比所有结果
	private SSSS_RankingResult getSRankingResult() {
		SSSS_RankingResult ret = ((SSSRoomSet)room.getCurSet()).dealer.getSRankingResult();
//		for(PlayerResult con :ret.posResultList){
//			SSSRoomPos sssRoomPos=(SSSRoomPos)this.room.getRoomPosMgr().getPosByPosID(con.getPosIdx());
//			Double sportsPoint;
//			if(sssRoomPos.setSportsPoint(con.getPoint())==null){
//				sportsPoint=0.0;
//			}else {
//				sportsPoint=sssRoomPos.setSportsPoint(con.getPoint());
//			}
//			con.setSportsPoint(sportsPoint);
//		}
		return ret;
	}


	public SSSConfigMgr getsConfigMgr() {
		return sConfigMgr;
	}
	
	/**
	 * 回放记录谁发起解散
	 */
	@Override
	public void addDissolveRoom(BaseSendMsg baseSendMsg) {
		
	}
	
	/**
	 * 回放记录添加游戏配置
	 */
	@Override
	public void addGameConfig() {
		
	}


	/**
     * 检查是否存在指定消耗类型
     * @return
     */
    @Override
    public boolean checkExistPrizeType(PrizeType prizeType) {
        return prizeType.equals(this.room.getBaseRoomConfigure().getPrizeType());
    }
	
	
}
