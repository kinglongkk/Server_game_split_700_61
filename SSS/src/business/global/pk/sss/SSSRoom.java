package business.global.pk.sss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import business.global.room.base.AbsRoomSet;
import business.sss.c2s.cclass.entity.SSSXianShiConfigEnum;
import cenum.RoomTypeEnum;
import cenum.room.CKickOutType;
import cenum.room.GaoJiTypeEnum;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.utils.CommTime;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.global.pk.sss.utlis.SSSConfigMgr;
import business.global.room.RoomRecordMgr;
import business.global.room.base.AbsRoomPos;
import business.global.room.base.AbsRoomPosMgr;
import business.global.room.base.DissolveRoom;
import business.global.room.pk.PockerRoom;
import business.player.feature.PlayerCurrency;
import business.sss.c2s.cclass.SSSResults;
import business.sss.c2s.cclass.SSSRoomSetInfo;
import business.sss.c2s.cclass.SSSRoom_Record;
import business.sss.c2s.cclass.entity.PlayerResult;
import business.sss.c2s.cclass.newsss.CardRankEnum;
import business.sss.c2s.cclass.newsss.CardSuitEnum;
import business.sss.c2s.cclass.newsss.PockerCard;
import business.sss.c2s.iclass.CSSS_CreateRoom;
import business.sss.c2s.iclass.CSSS_Ranked;
import business.sss.c2s.iclass.SSSS_ChangePlayerNum;
import business.sss.c2s.iclass.SSSS_ChangePlayerNumAgree;
import business.sss.c2s.iclass.SSSS_ChangeRoomNum;
import business.sss.c2s.iclass.SSSS_ChatMessage;
import business.sss.c2s.iclass.SSSS_Dissolve;
import business.sss.c2s.iclass.SSSS_GetRoomInfo;
import business.sss.c2s.iclass.SSSS_LostConnect;
import business.sss.c2s.iclass.SSSS_PosContinueGame;
import business.sss.c2s.iclass.SSSS_PosDealVote;
import business.sss.c2s.iclass.SSSS_PosLeave;
import business.sss.c2s.iclass.SSSS_PosReadyChg;
import business.sss.c2s.iclass.SSSS_PosUpdate;
import business.sss.c2s.iclass.SSSS_RoomEnd;
import business.sss.c2s.iclass.SSSS_RoomRecord;
import business.sss.c2s.iclass.SSSS_StartVoteDissolve;
import business.sss.c2s.iclass.SSSS_Trusteeship;
import business.sss.c2s.iclass.SSSS_Voice;
import business.sss.c2s.iclass.SSSS_XiPai;
import cenum.ChatType;
import cenum.ClassType;
import cenum.PKCEnum.SSSDaQiang;
import cenum.PKCEnum.SSSDiFen;
import cenum.PrizeType;
import cenum.room.GameRoomConfigEnum;
import cenum.room.RoomState;
import core.db.entity.clarkGame.ClubMemberBO;
import core.db.other.AsyncInfo;
import core.network.http.proto.SData_Result;
import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.RoomEndResult;
import jsproto.c2s.cclass.pk.PKRoom_Record;
import jsproto.c2s.cclass.pk.PKRoom_RecordPosInfo;
import jsproto.c2s.cclass.room.BaseRoomConfigure;
import jsproto.c2s.cclass.room.GetRoomInfo;
import jsproto.c2s.cclass.room.RoomPosInfo;
import jsproto.c2s.iclass.room.SBase_Dissolve;
import jsproto.c2s.iclass.room.SBase_PosLeave;

/**
 * 自由扑克 房间
 * 
 * @author Huaxing
 * @param <T>
 *
 */

public class SSSRoom<T> extends PockerRoom {
	public CSSS_CreateRoom roomCfg = null;// 开房配置
	private SSSConfigMgr configMgr = new SSSConfigMgr();

	public List<List<PlayerResult>> playerResult = new ArrayList<List<PlayerResult>>();
	public List<SSSResults> countRecords = new ArrayList<SSSResults>();
	public PockerCard mapai;
	private long zhuanjia = -1;// 庄家ID
	private int beishu = 1;
	private int xiPaiNum = 0;
	private boolean enterRoomFlag=false;//加入房间标志

	private Map<Long, Integer> xiPaiList = new HashMap<Long, Integer>();
	/**
	 * 记录第一个用户的牌
	 */
	private List<String> playerCard = new ArrayList<String>();
	private long pid;
	private String name;
	private String content;
	private ChatType type;
	private long toCId;
	private int quickID;

	/**
	 * 扑克公共父类构造函数
	 *
	 * @param baseRoomConfigure 公共配置
	 * @param roomKey           房间key
	 * @param ownerID
	 */
	protected SSSRoom(BaseRoomConfigure<CSSS_CreateRoom> baseRoomConfigure, String roomKey, long ownerID) {
		super(baseRoomConfigure, roomKey, ownerID);
		initShareBaseCreateRoom(CSSS_CreateRoom.class, baseRoomConfigure);
		this.roomCfg = (CSSS_CreateRoom) baseRoomConfigure.getBaseCreateRoom();
		if (baseRoomConfigure.getPrizeType() == PrizeType.RoomCard && (this.isMapai() || this.isMapaiHei())) {
			this.initMapai();
		}
	}

	@Override
	public int getTimerTime() {
		return 200;
	}
//	public void createInit(long ownerID, String key) {
//		this.roomCfg = (SSSRoom_Cfg) this.getBaseRoomConfigure().getArenaRoomCfg();
//		this.m_createSec = CommTime.nowSecond();
//
//		this.m_posMgr = new SSSRoomPosMgr(this);
////		if (this.getRoomConfigure().getCreateType() == CreateType.NORMAL.value()) {
////			this.m_posMgr.getPos(0).setReady(true);
////		}
//		if (getPrizeType() == PrizeType.RoomCard && (this.isMapai() || this.isMapaiHei())) {
//			this.initMapai();
//		}
//
//		// 将配置转成JSON
//		GameJson<SSSRoom_Cfg> gameJson = new GameJson<SSSRoom_Cfg>(this.cfg);
//		String dataJsonCfg = gameJson.toJson(SSSRoom_Cfg.class);
//
//		m_GameRoomBO.setCreateTime(this.m_createSec);
//		m_GameRoomBO.setSetCount(this.cfg.setCount);
//		m_GameRoomBO.setPlayerNum(ShareDefine.MAXPLAYERNUM_NN);
//		m_GameRoomBO.setDataJsonCfg(dataJsonCfg);
//		m_GameRoomBO.setType(ClassType.PK.value());
//		m_GameRoomBO.insert_sync();
//
////		setContinueTime(this.getPlayerNum()*7000);
//	}

	/**
	 * 一条龙翻倍
	 * @return
	 */
	public boolean yiTiaoLongDouble(){
		return this.roomCfg.paixingfenshu==1;
	}
	public boolean isEnterRoomFlag() {
		return enterRoomFlag;
	}

	public void setEnterRoomFlag(boolean enterRoomFlag) {
		this.enterRoomFlag = enterRoomFlag;
	}

	public boolean isMapai() {
		return this.roomCfg.getKexuanwanfa().contains(0);
	}
	
	public boolean isMapaiHei() {
		return this.roomCfg.getKexuanwanfa().contains(1);
	}
	
	public boolean isGuipai() {
		return this.roomCfg.getKexuanwanfa().contains(2);
	}

	public long getZJ() {
		return this.zhuanjia;
	}

	public void setZJ(long zjid) {
		this.zhuanjia = zjid;
	}

	public void setBS(int beishu) {
		this.beishu = beishu;
	}

	public int getBS() {
		return this.beishu;
	}
	
	public int getBaseScore() {
		return SSSDiFen.valueOf(this.roomCfg.difen).value();
	}

	public int getGunBS() {
		return SSSDaQiang.valueOf(this.roomCfg.daqiang).value();
	}
	public void setBeishu(WebSocketRequest request, long pid, int pos, int beishu) {
		((SSSRoomSet)this.getCurSet()).setBeiShu(pid, pos, beishu);
		request.response();
	}
	
	
	public int getXiPaiNum () {
		return this.xiPaiNum;
	}
	
	public void addXiPaiNum (boolean isClean) {
		if (isClean) {
			this.xiPaiNum = 0;
			return;
		}
		this.xiPaiNum++;
	}
	
	@Override
	public SData_Result opXiPai(long pid) {
		if (!xiPaiList.containsKey(pid)) {
			xiPaiList.put(pid, 1);
			this.getRoomPosMgr().notify2All(this.XiPai(this.getRoomID(), pid,
					this.getBaseRoomConfigure().getGameType().getType()));
			addXiPaiNum(false);
			return SData_Result.make(ErrorCode.Success);
		} else {
			int curcnt = xiPaiList.get(pid);
			int totalcnt =this.roomCfg.getSetCount();
			if(curcnt < totalcnt)
			{
				xiPaiList.put(pid, curcnt + 1);
				if (null != this.getCurSet()) {
					((SSSRoomSet)this.getCurSet()).setXiPai(pid);
				}
				this.getRoomPosMgr().notify2All(this.XiPai(this.getRoomID(), pid,
						this.getBaseRoomConfigure().getGameType().getType()));
				addXiPaiNum(false);
				return SData_Result.make(ErrorCode.Success);
			}
			else
			{
				return SData_Result.make(ErrorCode.NotAllow, "not in pos");
			}
		}
	}

	public boolean isZJModel() {
		if (this.roomCfg.zhuangjiaguize == -1) {
			return false;
		} else {
			return true;
		}
	}

	public void initMapai() {
		if (isMapaiHei()) {
			this.mapai = new PockerCard(CardSuitEnum.SPADES, CardRankEnum.CARD_FIVE);
		}else {
			Random random = new Random();
			int randNum = random.nextInt(4);
			int randNum2 = random.nextInt(13);
			this.mapai = new PockerCard(CardSuitEnum.values()[randNum], CardRankEnum.values()[randNum2]);
		}
		

	}

	/**
	 * 房间内玩家的牌已经准备好了
	 * 
	 * @param request
	 * @param isReady
	 * @param pid
	 * @param posIndex
	 */
	public void playerCardReady(WebSocketRequest request, boolean isReady, long pid, int posIndex, CSSS_Ranked req,
			boolean isSpecial) {
		try {
			lock();
			cardReady(request, isReady, pid, posIndex, req, isSpecial);
		}catch (Exception e){
			CommLogD.error(e.getMessage());
		}finally {
			unlock();
		}
	}

	/**
	 * 牌准备
	 * 
	 * @param request
	 * @param isReady
	 * @param pid
	 * @param posIndex
	 * @param req
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean cardReady(WebSocketRequest request, boolean isReady, long pid, int posIndex, CSSS_Ranked req,
			boolean isSpecial) {
		SSSRoomPos pos = (SSSRoomPos) this.getRoomPosMgr().getPosByPosID(posIndex);
		if (null == pos) {
			request.error(ErrorCode.NotAllow, "cardReady not find posIndex:" + (posIndex));
			return false;
		}

		if (null == this.getCurSet()) {
			request.error(ErrorCode.NotExist_CurSet, "cardReady not curset null ");
			return false;
		}

		if (!isSpecial) {

			if (null == req) {
				request.error(ErrorCode.NotExist_Ranked, "cardReady not CSSS_Ranked null ");
				return false;
			}

			if (req.pid == 0L || pid == 0L) {
				request.error(ErrorCode.NotExist_Ranked, "cardReady not find pid " + pid);
				return false;
			}

			if (req.pid != pid || pos.getPid() != pid) {
				CommLogD.error("req.pid != pid || pos.pid != pid");
				request.error(ErrorCode.NotExist_Ranked, "req.pid != pid || pos.pid != pid " + pid);
				return false;
			}
			
			if (null != req.dunPos) {
				if (req.dunPos.first.size() <= 0) {
					request.error(ErrorCode.NotAllow, "cardReady req.dunPos.first.size 0");
					return false;
				}
			} else if (null == req.dunPos) {
				request.error(ErrorCode.NotAllow, "cardReady not req.dunPos null");
				return false;
			}
		}

		if(pos.isCardReady()) {
			request.error(ErrorCode.NotAllow, "card already Ready!");
			return false;
		}
		
		if (!((SSSRoomSet)this.getCurSet()).setRankeds(req, posIndex, isSpecial)) {
			request.error(ErrorCode.Card_Error, "Card Error ");
			return false;
		}

		if (!((SSSRoomSet)this.getCurSet()).checkRanked(pid)) {
			request.error(ErrorCode.Card_Error, "Card Error ");
			return false;
		}

		if (!((SSSRoomSet)this.getCurSet()).dealer.checkDun(pos.getPid())) {
			CommLogD.error("checkDun RoomID:{},PID:{}",this.getRoomID(),pos.getPid());
			request.error(ErrorCode.Card_Error, "Card Error ");
			return false;
		}
		pos.playerCardReady(request, isReady, pid);
		return true;
	}





	@Override
	public void clearEndRoom() {

		super.clear();
		this.roomCfg = null;
		this.configMgr = null;
	}


	@Override
	public void roomTrusteeship(int pos) {


		((SSSRoomSet) this.getCurSet()).roomTrusteeship(pos);

	}
	/**
	 * 托管时间值
	 *
	 * @return
	 */
	@Override
	public int trusteeshipTimeValue() {
		CSSS_CreateRoom cfg=(CSSS_CreateRoom)this.getBaseRoomConfigure().getBaseCreateRoom();
		return SSSXianShiConfigEnum.valueOf(cfg.guize).getValue();
	}
	@Override
	public void cancelTrusteeship(AbsRoomPos pos) {
		((SSSRoomSet) this.getCurSet()).roomTrusteeship(pos.getPosID());
	}

	@Override
	public boolean isCanChangePlayerNum() {
		return this.getBaseRoomConfigure().getBaseCreateRoom().getFangjian().contains(GameRoomConfigEnum.FangJianQieHuanRenShu.ordinal());
	}





	@Override
	public void startNewSet() {
		this.setCurSetID(this.getCurSetID() + 1);
		this.createSet();
		// 每个位置，清空准备状态
		this.getRoomPosMgr().clearGameReady();
		// 通知局数变化
		this.getRoomTyepImpl().roomSetIDChange();
	}
	//创建set
	public void  createSet(){
		if (null != this.getCurSet()) {
			this.getCurSet().clear();
			this.setCurSet(null);
		}
		this.setCurSet(new SSSRoomSet(this));
	}
	@Override
	public void setEndRoom() {

		if (null != this.getCurSet()) {
			// 房间管理注销
//			SSSRoomRecord record = new SSSRoomRecord(this);
			if (getHistorySet().size() > 0) {
				// 增加房局记录
				RoomRecordMgr.getInstance().add(this);
				SSSRoom_Record sRecord = new SSSRoom_Record();
				sRecord.players = ((SSSRoomPosMgr)this.getRoomPosMgr()).getShortPlayerList(countRecords);
				int maxPoint=0;
				for(SSSResults sssResults:countRecords){
					if(sssResults.getPoint()>=maxPoint){
						maxPoint=sssResults.getPoint();
					}
				}
				for(SSSResults sssResults:countRecords){
					if(sssResults.getPoint()==maxPoint){
						sssResults.bigWinner=true;
					}
				}
				sRecord.recordPosInfosList = countRecords;
				sRecord.key = this.getRoomKey();
				sRecord.roomID = getRoomID();
				sRecord.endSec = getRoomEndResult().getEndTime();
				sRecord.setCnt = getCurSetID();
				 
//				sRecord.posCnt= ShareDefine.MAXPLAYERNUM_SSS;
				this.getRoomPosMgr().notify2All(SSSS_RoomEnd.make(sRecord));
				refererReceiveList();
			}
		}
	}

	/**
	 * 中途解散保存战绩
	 * @return T:保存战绩,F:不保存
	 */
	@Override
	public  boolean isMidwayDisbandmentPreservation() {
		return false;
	}


	/**
	 * 构建房间回放返回给客户端
	 * @return 通知结构体
	 */
	public PKRoom_Record getPKRoomRecordInfo(){
		PKRoom_Record pkRoom_record = new PKRoom_Record();
		pkRoom_record.setCnt = this.getHistorySetSize();
		pkRoom_record.recordPosInfosList  = this.getRecordPosInfoList();
		pkRoom_record.roomID = this.getRoomID();
		pkRoom_record.endSec = this.getGameRoomBO().getEndTime();
		return pkRoom_record;
	}
	@Override
	public void calcEnd() {
		for (AbsRoomPos pos : this.getRoomPosMgr().posList) {
			SSSRoomPos<?> sssPos = (SSSRoomPos<?>) pos;
			if (null == sssPos) {
				continue;
			}
			if (!sssPos.isPlayTheGame()) {
				continue;
			}
			SSSResults countRecord = (SSSResults)pos.getResults();
			if (countRecord != null||(countRecord==null&&pos.getPid()>=0L)) {
				if(countRecord == null){
					countRecord=new SSSResults();
					countRecord.setPid(pos.getPid());
					countRecord.setPosId(pos.getPosID());
				}
				countRecords.add(countRecord);
			}

			if (PrizeType.Gold.equals(this.getBaseRoomConfigure().getPrizeType())) {
                return;
            }
		}
		// 更新游戏房间BO和更新玩家个人游戏记录BO
		this.updateGameRoomBOAndPlayerRoomAloneBO();
	}



//	public int getMaxPoint() {
//		int max = 0;
//		for (AbsRoomPos pos : this.getRoomPosMgr().posList) {
//			if (pos.getPoint() > max) {
//				max = pos.getPoint();
//			}
//		}
//		return max;
//	}

//	public List<Integer> getBigWinnerList() {
//		List<Integer> intlist = new ArrayList<Integer>();
//		int max = getMaxPoint();
//		for (RoomPosDelegateAbstract<?> pos : this.m_posMgr.posList) {
//			SSSRoomPos<?> sssPos = (SSSRoomPos<?>) pos;
//			if (null == sssPos)
//				continue;
//			if (!sssPos.isPlayTheGame())
//				continue;
//			if (max == pos.point) {
//				intlist.add(pos.posID);
//			}
//		}
//		return intlist;
//	}
	

//	public  boolean isAllAgree(){
//		return this.getRoomPosMgr().isAllAgreeEx(((SSSRoomPosMgr<?>) this.getRoomPosMgr()).getPlayerNum());
//	}
//

//	@SuppressWarnings("unchecked")
////	@Override
//	public T getAllDisplayInfo(long pid) {
//		SSSS_GetRoomInfo ret = new SSSS_GetRoomInfo();
//		ret.roomID = this.getRoomID(); // ID
//		ret.key = this.getRoomKey(); // ID
//		ret.createSec = this.m_createSec; // 创建时间
//		ret.prizeType = this.getBaseRoomConfigure().getPrizeType();
//		ret.state = this.m_roomState; // 状态
//		ret.setID = this.getCurSetID(); // 进行到第几个局了
//		ret.ownerID  = this.getControllerID();
//		ret.createID = this.getOwnerIDOnGetRoomInfo();
//		ret.cfg = this.cfg; // 开房配置
//		ret.mapai = this.mapai == null ? "" : this.mapai.toString();
//		ret.zjid = this.getZJ();
////		ret.huase = this.cfg.huase;
//		ret.playerResult = playerResult;
//
//		ret.beishu =this.cfg.zhuangjiaguize == 4? this.beishu:0;
//		if (PrizeType.RoomCard.equals(this.getPrizeType()) && this.isMapai()) {
//			ret.mapai = this.mapai.toString();
//		}
//		if (null != this.curSet) {
//			if (SetState.End.equals(curSet.state)) {
//				ret.rankeds = this.curSet.getAllRankeds();
//			}
//			ret.set = this.curSet.getNotify_set(pid);
//		} else {
//			ret.set = new SSSRoom_Set(); // 当前局的信息
//			ret.rankeds = new ArrayList<CSSS_Ranked>();
//		}
//
//		ret.posList = this.m_posMgr.getNotify_PosList(); // 玩家信息
//
//		if (null != m_dissolveRoom) {
//			ret.dissolve = m_dissolveRoom.getNotify();
//		} else {
//			ret.dissolve = new Room_Dissolve();
//		}
//		return (T) ret;
//	}





	public List<String> getPlayerCard() {
		return playerCard;
	}

	public void setPlayerCard(List<String> playerCard) {
		this.playerCard = playerCard;
	}

//
//	public boolean onXiPai(long pid) {
//		// TODO 自动生成的方法存根
////		return xiPai(pid);
//	}


//	public void startGameBase(WebSocketRequest request, long pid,
//			RoomPosMgrDelegateAbstract<?> m_posMgr2) {
//
//	}
	
	public int getSSSPlayerNum () {
		return ((SSSRoomPosMgr) this.getRoomPosMgr()).getAllPlayerNum();
	}
	
	@SuppressWarnings("rawtypes")
	public List<SSSRoomPos> getAllSSSRoomPosList() {
		return ((SSSRoomPosMgr) this.getRoomPosMgr()).getAllSSSRoomPosList();

	}
	
	/*
	 * 开始游戏的其他条件 条件不满足不进入
	 * */
	public boolean startGameOtherCondition(WebSocketRequest request, long pid) {
		// 如果人数 >= 8 ? 2-8人，可以中途加入:人满开始
		if (this.getPlayerNum() >= 8 ? ((SSSRoomPosMgr) this.getRoomPosMgr()).isSSSAllReady():this.getRoomPosMgr().isAllReady()) {
			return true;
		} else {
			request.error(ErrorCode.NotAllow, "ready player is last two!");
			return false;
		}
	}

	@Override
	public String dataJsonCfg() {
		// 获取房间配置
		return new Gson().toJson(this.getRoomCfg());
	}
	/**
	 * 获取房间配置
	 *
	 * @return
	 */
	public CSSS_CreateRoom getRoomCfg() {
		if(this.roomCfg == null){
			initShareBaseCreateRoom(CSSS_CreateRoom.class, getBaseRoomConfigure());
			return (CSSS_CreateRoom) getBaseRoomConfigure().getBaseCreateRoom();
		}
		return this.roomCfg;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCfg() {
		return (T) getRoomCfg();
	}
	
	@Override
	public AbsRoomPosMgr initRoomPosMgr() {
		return new SSSRoomPosMgr(this);
	}

	/*
	 * 加入房间的其他条件 条件不满足不进入
	 * */
//	@Override
//	public boolean enterRoomOtherCondition(long pid){
//		if(this.roomCfg.isKeptOutAfterStartGame && this.getRoomState() != RoomState.Init) {
//			return false;
//		}
//		return true;
//	}
	
	
	/*
	 * 主动离开房间的其他条件 条件不满足不退出
	 * */
	@Override
	public boolean exitRoomOtherCondition(long pid){
		// 游戏已经开始，不能自由离开 防止一人加入一人退出时bug
		if(this.getCurSet()!=null&&this.getCurSet().getSetID()==0&&!RoomState.Init.equals(this.getRoomState())){
			return false;
		}
		// 玩家玩过游戏就不能离开
		SSSRoomPos<?> pos = (SSSRoomPos<?>) this.getRoomPosMgr().getPosByPid(pid);
		if (pos != null && pos.isPlayTheGame()) {
			return false;
		}
		return true;
	}
	

	@Override
	protected List<PKRoom_RecordPosInfo> getRecordPosInfoList() {
		List<PKRoom_RecordPosInfo> sRecord = new ArrayList<PKRoom_RecordPosInfo>();
		for(int i = 0; i < this.getPlayerNum() ; i++){
			PKRoom_RecordPosInfo posInfo = new PKRoom_RecordPosInfo();
			SSSRoomPos roomPos = (SSSRoomPos) this.getRoomPosMgr().getPosByPosID(i);
			if(roomPos.getPid()==0){
				continue;
			}

			SSSResults cRecord = (SSSResults)roomPos.getResults();
			if(null!=cRecord){
				posInfo.flatCount = cRecord.flatCount;
				posInfo.loseCount = cRecord.loseCount;
				posInfo.winCount = cRecord.winCount;
				posInfo.setSportsPoint(cRecord.getSportsPoint());
			}
			posInfo.setPoint( roomPos.getPoint());
			posInfo.setPos(i);
			posInfo.setPid(roomPos.getPid());
			sRecord.add(posInfo);
		}
		return sRecord;
	}
	@Override
	public int getPlayingCount() {
		return ((SSSRoomPosMgr) this.getRoomPosMgr()).getPlayerNum();
	}
	/**
	 * 30秒未准备自动退出
	 *
	 * @return
	 */
	@Override
	public boolean is30SencondTimeOut() {
		return this.getRoomCfg().getGaoji().contains(GaoJiTypeEnum.SECOND_TIMEOUT_30.ordinal());
	}

	@Override
	public DissolveRoom initDissolveRoom(int posID,int WaitSec) {
		return new SSSDissolveRoom(this, posID,WaitSec);
	}

	/**
	 * 加入房间的其他条件 条件不满足不进入
	 */
	@Override
	public boolean enterRoomOtherCondition(long pid) {
		if(this.getBaseRoomConfigure().getBaseCreateRoom().getPlayerMinNum()!=this.getBaseRoomConfigure().getBaseCreateRoom().getPlayerNum()&&this.isEnterRoomFlag()){
			return true;
		}
		return RoomState.Init.equals(this.getRoomState());
	}
	@Override
	public BaseSendMsg Trusteeship(long roomID, long pid, int pos, boolean trusteeship) {
		return SSSS_Trusteeship.make(roomID, pid, pos, trusteeship);
	}

	@Override
	public BaseSendMsg PosLeave(SBase_PosLeave posLeave) {
		return SSSS_PosLeave.make(posLeave);
	}


	@Override
	public BaseSendMsg LostConnect(long roomID, long pid, boolean isLostConnect,boolean isShowLeave) {
		return SSSS_LostConnect.make(roomID, pid, isLostConnect,isShowLeave);
	}

	@Override
	public BaseSendMsg PosContinueGame(long roomID, int pos) {
		return SSSS_PosContinueGame.make(roomID, pos);
	}

	@Override
	public BaseSendMsg PosUpdate(long roomID, int pos, RoomPosInfo posInfo, int custom) {
		return SSSS_PosUpdate.make(roomID, pos, posInfo, custom);
	}

	@Override
	public BaseSendMsg PosReadyChg(long roomID, int pos, boolean isReady) {
		return SSSS_PosReadyChg.make(roomID, pos, isReady);
	}

	@Override
	public BaseSendMsg Dissolve(SBase_Dissolve dissolve) {
		return SSSS_Dissolve.make(dissolve);
	}

	@Override
	public BaseSendMsg StartVoteDissolve(long roomID, int createPos, int endSec) {
		return SSSS_StartVoteDissolve.make(roomID, createPos, endSec);
	}

	@Override
	public BaseSendMsg PosDealVote(long roomID, int pos, boolean agreeDissolve,int endSec) {
		return SSSS_PosDealVote.make(roomID, pos, agreeDissolve);
	}

	@Override
	public BaseSendMsg Voice(long roomID, int pos, String url) {
		return SSSS_Voice.make(roomID, pos, url);
	}

	@Override
	public BaseSendMsg XiPai(long roomID, long pid, ClassType cType) {
		return SSSS_XiPai.make(roomID, pid, cType);
	}

	@Override
	public BaseSendMsg ChatMessage(long pid, String name, String content, ChatType type, long toCId, int quickID) {
		return SSSS_ChatMessage.make(pid, name, content, type, toCId, quickID);
	}


	@Override
	public <T> BaseSendMsg RoomRecord(List<T> records) {
		return SSSS_RoomRecord.make(records);
	}

	@Override
	public BaseSendMsg ChangePlayerNum(long roomID, int createPos, int endSec, int playerNum) {
		return SSSS_ChangePlayerNum.make(roomID, createPos, endSec, playerNum);
	}

	@Override
	public BaseSendMsg ChangePlayerNumAgree(long roomID, int pos, boolean agreeChange) {
		return SSSS_ChangePlayerNumAgree.make(roomID, pos, agreeChange);
	}

	@Override
	public BaseSendMsg ChangeRoomNum(long roomID, String roomKey, int createType) {
		return SSSS_ChangeRoomNum.make(roomID, roomKey, createType);
	}

	@Override
	public GetRoomInfo getRoomInfo(long pid) {
		SSSS_GetRoomInfo ret = new SSSS_GetRoomInfo();
		// 设置房间公共信息
		this.getBaseRoomInfo(ret);
		if (null != this.getCurSet()) {
			ret.setSet(this.getCurSet().getNotify_set(pid));
			ret.setBeishu(this.getBS());
			ret.zjid=this.getZJ();
			ret.rankeds=((SSSRoomSet)this.getCurSet()).dealer.getSRankingResult().rankeds==null? new ArrayList<>():((SSSRoomSet)this.getCurSet()).dealer.getSRankingResult().rankeds;
			ret.posResultList=((SSSRoomSet)this.getCurSet()).dealer.getSRankingResult().posResultList==null? new ArrayList<>():((SSSRoomSet)this.getCurSet()).dealer.getSRankingResult().posResultList;
		} else {
			SSSRoomSetInfo sssRoomSetInfo=new SSSRoomSetInfo();
			sssRoomSetInfo.mapai=this.mapai == null ? "" : this.mapai.toString();
			ret.setSet(sssRoomSetInfo);
		}
		return ret;
	}


	@Override
	public boolean autoStartGame() {
		//亲友圈 大联盟2-8人自动开始游戏
		if(this.getRoomTyepImpl().getRoomTypeEnum().equals(RoomTypeEnum.CLUB)||this.getRoomTyepImpl().getRoomTypeEnum().equals(RoomTypeEnum.UNION)){
			return true;
		}
		//特殊情况 2-8人不自动开始游戏
		if(this.getBaseRoomConfigure().getBaseCreateRoom().getPlayerNum()!=this.getBaseRoomConfigure().getBaseCreateRoom().getPlayerMinNum()){
			return false;
		}
		return true;
	}
	/**
	 * 是否需要解散次数
	 * @return
	 */
	@Override
	public boolean needDissolveCount(){
		return roomCfg.getFangjian().contains(1);
	}

	/**
	 * 获取解散次数
	 * @return
	 */
	@Override
	public int getJieShanShu(){
		return 5;
	}
//	/**
//	 * 进入房间  2-8人场在亲友圈时要手动设置房主
//	 *
//	 * @param pid
//	 *            玩家Pid
//	 * @param posID
//	 *            位置ID
//	 * @param isRobot
//	 *            T:机器人,F:玩家
//	 * @param isReady
//	 *            T:准备,F:没准备
//	 * @return
//	 */
//	@SuppressWarnings("rawtypes")
//	public SData_Result enterRoom(long pid, int posID, boolean isRobot, boolean isReady, int initPoint,
//								  ClubMemberBO clubMemberBO) {
//		try {
//			lock();
//			// 玩家ID不存在。
//			if (pid <= 0L) {
//				return SData_Result.make(ErrorCode.NotAllow, "pid <= 0L PID:{%d},posID:{%d}", pid, posID);
//			}
//			// 加入房间的其他条件 条件不满足不进入
//			if (!this.enterRoomOtherCondition(pid)) {
//				return SData_Result.make(ErrorCode.Room_STATUS_ERROR, "enterRoomOtherCondition RoomState:{%s}",
//						this.getRoomState());
//			}
//			// 通过pid获取玩家信息
//			AbsRoomPos roomPos = this.getRoomPosMgr().getPosByPid(pid);
//			if (null != roomPos) {
//				// 该玩家已经加入房间.
//				return SData_Result.make(ErrorCode.NotAllow, "getPosByPid null != roomPos pid:{%d}", pid);
//			}
//			if (posID >= 0) {
//				// 通过PosID获取玩家信息
//				roomPos = this.getRoomPosMgr().getPosByPosID(posID);
//				if (null == roomPos) {
//					// 找不到位置数据
//					return SData_Result.make(ErrorCode.NotAllow, "null == roomPos posID:{%d}", posID);
//				}
//				// 指定的位置有人了。
//				if (roomPos.getPid() > 0) {
//					return SData_Result.make(ErrorCode.NotAllow, "roomPos.getPid() > 0 PID:{%d}", roomPos.getPid());
//				}
//			} else {
//				// 获取一个空的位置
//				roomPos = this.getRoomPosMgr().getEmptyPos();
//				if (null == roomPos) {
//					// 没有空的位置
//					return SData_Result.make(ErrorCode.ErrorSysMsg, "MSG_ROOM_FULL_PLAYER");
//				}
//			}
//			// 设置玩家位置
//			if (!roomPos.seat(pid, initPoint, isRobot, clubMemberBO)) {
//				return SData_Result.make(ErrorCode.NotAllow, "enterRoom seat");
//			}
//			if (isReady) {
//				roomPos.setReady(true);
//			}
//			this.setClubOwnerId(pid);
//			if (!isRobot) {
//				// 玩家进入房间 标识进入的房间ID
//				roomPos.getPlayer().onEnterRoom(this.getRoomID());
//			}
//			this.getOpChangePlayerRoom().changePlayerNumEnterRoom(roomPos.getPosID());
//		} finally {
//			unlock();
//		}
//		return SData_Result.make(ErrorCode.Success);
//	}
//
//	/**
//	 * 2-8人场设置房主id
//	 * @param pid
//	 */
//	public void setClubOwnerId(long pid){
//		//亲友圈房间 并且是2-8人场是 第一个人进入需要设置为房主id
//		if((this.getRoomTyepImpl().getRoomTypeEnum().equals(RoomTypeEnum.CLUB)||(this.getRoomTyepImpl().getRoomTypeEnum().equals(RoomTypeEnum.UNION)))
//				&&this.getBaseRoomConfigure().getBaseCreateRoom().getPlayerNum()!=this.getBaseRoomConfigure().getBaseCreateRoom().getPlayerMinNum()){
//			//第一个人进入
//			if(this.getRoomPosMgr().getPosList().stream().filter(k->k.getPid()>0).count()==1){
//				this.setOwnerID(pid);
//			}
//		}
//	}
//	/**
//	 * 主动离开房间
//	 *
//	 * @param pid
//	 *            用户ID
//	 * @return
//	 */
//
//	@SuppressWarnings("rawtypes")
//	public SData_Result exitRoom(long pid) {
//		try {
//			lock();
//			// 主动离开房间的其他条件 条件不满足不退出
//			if (!this.exitRoomOtherCondition(pid)) {
//				return SData_Result.make(ErrorCode.NotAllow, "exitRoom not exitRoomOtherCondition ");
//			}
//			// 房主不能主动离开房间
//			if (pid == this.getOwnerID()) {
//				return SData_Result.make(ErrorCode.NotAllow, "exitRoom pid:{%d} == ownerID:{%d}", pid,
//						this.getOwnerID());
//			}
//			AbsRoomPos roomPos = this.getRoomPosMgr().getPosByPid(pid);
//			if (null == roomPos) {
//				// 找不到指定pid玩家信息。
//				return SData_Result.make(ErrorCode.NotAllow, "exitRoom null == roomPos Pid:{%d}", pid);
//			}
//			roomPos.leave(false, this.getOwnerID(), CKickOutType.None);
//			return SData_Result.make(ErrorCode.Success);
//		} finally {
//			unlock();
//		}
//	}
	/**
	 * 需要立马打牌
	 * @return
	 */
	public boolean needAtOnceOpCard(){
		return true;
	}
}
