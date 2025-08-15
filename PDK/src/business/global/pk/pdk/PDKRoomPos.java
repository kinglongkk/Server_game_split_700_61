package business.global.pk.pdk;

import business.global.room.base.AbsBaseRoom;
import business.global.room.base.AbsRoomPos;
import business.pdk.c2s.cclass.PDKRoom_PosEnd;
import business.pdk.c2s.cclass.PDKRoom_RecordPosInfo;
import cenum.RoomTypeEnum;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.utils.CommMath;
import jsproto.c2s.cclass.pk.BasePockerLogic;
import jsproto.c2s.cclass.pk.Victory;
import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 房间内每个位置信息
 * 
 * @author Huaxing
 *
 */
public class PDKRoomPos extends AbsRoomPos {

	public 		ArrayList<Integer>  	privateCards = new ArrayList<>(); // 手牌
	public 		ArrayList<Integer>  	backupsCards = new ArrayList<>(); // 手牌
	public 		ArrayList<Integer>  	privateCardsCopy = new ArrayList<>(); // 手牌
	private  	int  				m_nWin  = 0 ; // 赢场数
	private  	int    				m_nLose = 0; // 输场数
	private  	int 				m_nFlat = 0; // 平场数
	public int maxPoint = 0; //单局最高
	public int outBombNum = 0; //炸弹数
	private Boolean beginFlag=false;//开始标识
	private int tuoGuanSetCount = 0; // 连续托管局数

	//整大局剩余的时间，剩余几秒，几秒后进入托管
	private long secTotal = 0;


	public PDKRoomPos(int getPosID, AbsBaseRoom room) {
		super(getPosID, room);
	}

	public Boolean getBeginFlag() {
		return beginFlag;
	}

	public void setBeginFlag(Boolean beginFlag) {
		this.beginFlag = beginFlag;
	}

	/**
	 * 初始化手牌
	 * @param cards
	 */
	public void init(List<Integer> cards) {
		this.privateCards = new ArrayList<>(cards);
		this.backupsCards = new ArrayList<>(cards);
	}
	
	/**
	 * 初始化手牌
	 * @param card
	 */
	public void addCard(Integer card) {
		this.privateCards.add(card);
		this.backupsCards.add(card);
	}
	
//
//	/**
//	 * 作弊初始化
//	 * 暂时没用到。。。先留着
//	 * @param privateCards
//	 * @param publicCards
//	 * @param handCard
//	 */
//	public void init(List<Integer> cards, List<List<Integer>> publicCards, int handCard) {
//		this.privateCards = new ArrayList<>(cards);
//		this.backupsCards = new ArrayList<>(cards);
//	}
	
	
	/**
	 * 获取牌组信息
	 * @return
	 */
	public ArrayList<Integer> getNotifyCard(long pid, boolean isOpenCard) {
		boolean isSelf = pid == this.getPid();
		if (isOpenCard) {
			isSelf = true;
		}
		ArrayList<Integer> sArrayList = new ArrayList<Integer>();
		// 是自己
		int length = privateCards.size();
		for (int i = 0; i < length; i++) {
			sArrayList.add(isSelf ? privateCards.get(i) : Integer.valueOf((byte) 0x00));
		}
		return sArrayList;
	}


	/**
	 * 删除牌组信息
	 * @return
	 */
	public boolean deleteCard(ArrayList<Integer> cradList) {
		int sizeCardList = cradList.size();
		this.privateCardsCopy = (ArrayList<Integer>) this.privateCards.clone();
		int sizePrivateCards =this.privateCardsCopy.size();
		for (Integer byte1 : cradList) {
			boolean flag =  this.privateCards.remove(byte1);
			if (!flag) {
				CommLogD.error("deleteCard error :Pid:{},PosID:{},privateCardsCopy:{},cradList:{},privateCards:{}",this.getPid(),this.getPosID(),privateCardsCopy,cradList,privateCards);
				this.privateCards = this.privateCardsCopy;
				return false;
			}
		}
		if(sizePrivateCards - sizeCardList != this.privateCards.size()) {
			CommLogD.error("deleteCard error :Pid:{},PosID:{},privateCardsCopy:{},cradList:{},privateCards:{}",this.getPid(),this.getPosID(),privateCardsCopy,cradList,privateCards);
			this.privateCards = this.privateCardsCopy;
			return false;
		}
		return true;
	}

	
	//获取牌的位置
	public boolean checkCard(Integer card){
		return BasePockerLogic.getCardCount(privateCards, card, false) > 0;
	}
	
	//获取牌的位置
	public boolean checkCardHasFocus(Integer card){
		return BasePockerLogic.getCardCount(privateCards, card, false) > 0;
	}
	
	
	public PDKRoom_PosEnd calcPosEnd(PDKSetPos setPos){
		PDKRoomSet set = (PDKRoomSet) this.getRoom().getCurSet();

		PDKRoom_PosEnd posEnd =  new PDKRoom_PosEnd();
		int setPoint = set.pointList.get(this.getPosID());
		List<Victory> roomDoubleList = set.getRoomDoubleList();
		Optional<Victory> first = roomDoubleList.stream().filter(n -> n.getPos() == this.getPosID()).findFirst();
		posEnd.doubleNum = first.isPresent()?first.get().getNum():0;
		posEnd.point = (int) set.pointList.get(this.getPosID());
		posEnd.pos = this.getPosID();
		posEnd.pid = this.getPid();	// 竞技点分数
		if (set.room.calcFenUseYiKao() && RoomTypeEnum.UNION.equals(this.getRoom().getRoomTypeEnum())) {
			posEnd.sportsPoint = setPos.getDeductPointYiKao();
		} else {
			posEnd.sportsPoint = setSportsPoint(setPoint);
		}
		posEnd.baseMark = this.getRoom().getBaseMark();
		Optional<Victory> victory = set.robCloseList.stream().filter(n->n.getPos() == this.getPosID()).findFirst();
		posEnd.robClose = victory.isPresent() ? victory.get().getNum() : 0;
		for (ArrayList<Integer> sizeList : set.surplusCardRecordList) {
			posEnd.surplusCardList.add(sizeList.get(this.getPosID()));
		}
		this.calcRoomPoint(set.pointList.get(this.getPosID()));
		return posEnd;
	}
	/**
	 * 计算房间分数
	 */
	@Override
	public void calcRoomPoint(int point) {
		PDKRoomSet set = (PDKRoomSet) this.getRoom().getCurSet();
		if(set==null|| MapUtils.isEmpty(set.getPosDict())){
			// todo 防止当前局出现空（例如吊蟹在牌局开始之前需扣底分）
			super.calcRoomPoint(point);
			return;
		}
		PDKSetPos setPos = set.getPosDict().get(this.getPosID());
		this.setPoint(this.getPoint() + point);
		this.setPointYiKao(CommMath.addDouble(this.getPointYiKao() ,setPos.getDeductEndPoint()));
		this.calcRoomSportsPoint(point,setPos.getDeductEndPoint());
		if (getRoom().isRulesOfCanNotBelowZero() && RoomTypeEnum.UNION.equals(this.getRoom().getRoomTypeEnum())) {
			//小于0的 直接重置0
			if (this.getRoomSportsPoint() <= 0) {
				this.setRoomSportsPoint(0D);
			}
		}
	}

	public void calcPosResult() {
		PDKRoomSet set = (PDKRoomSet) this.getRoom().getCurSet();
		PDKRoom_RecordPosInfo cRecord = (PDKRoom_RecordPosInfo)getResults();
		if (null == cRecord) {
			cRecord = new PDKRoom_RecordPosInfo();
		}
		cRecord.setPid(getPid());
		cRecord.setPosId(getPosID());
		cRecord.addToPoint(set.pointList.get(this.getPosID()));
		setResults(cRecord);
	}
	/**
	 * 竞技点
	 */
	@Override
	public Double sportsPoint() {
		if ((getRoom().calcFenUseYiKao()||getRoom().isRulesOfCanNotBelowZero()) && RoomTypeEnum.UNION.equals(this.getRoom().getRoomTypeEnum())) {
			if (RoomTypeEnum.UNION.equals(this.getRoom().getRoomTypeEnum())) {
				return CommMath.FormatDouble(this.getPointYiKao());
			} else {
				return null;
			}
		}
		return super.sportsPoint();
	}
//	/**
//	 * 房间记录结束
//	 * @return
//	 */
//	public Room_RecordPosEnd getRecordPosEnd() {
//		Room_RecordPosEnd ret = new Room_RecordPosEnd();
//		GameRecordBO sssBO = new GameRecordBO();
//		
//		ret.pid = this.pid; // 玩家ID
//		ret.setID = this.room.getCurSetID();//玩家局数
//		ret.roomID = this.room.getRoomID();//房间ID
//		
//		PDKCard_Recoed sRecoed = new PDKCard_Recoed();
//		
//		PDKRoomSet set  = (PDKRoomSet) this.room.getCurSet();
//		
//		int point = (int)set.pointList.get(getPosID());
//
//		sRecoed.setCardList(privateCards);
//		sRecoed.setPoint(point);
//		
//		ret.gameType = GameType.PDK.value();
//		ret.setEnd = TimeUtil.getStringDate();
//		ret.point = point;
//
//		
//		GameJson<PDKCard_Recoed> gameJson = new GameJson<PDKCard_Recoed>(sRecoed);
//		ret.gameJson = gameJson.toJson(PDKCard_Recoed.class);
//		sssBO.setRoomRecordPosEnd(ret);
//		sssBO.insert_sync();
//		
//		return ret;
//	}

	/**
	 * @return privateCards
	 */
	public ArrayList<Integer> getPrivateCards() {
		return privateCards;
	}

	/**
	 * @return m_nWin
	 */
	public int getWin() {
		return m_nWin;
	}

	/**
	 * @param nWin 要设置的 m_nWin
	 */
	public void addWin(int nWin) {
		this.m_nWin += nWin;
	}

	/**
	 * @return m_nLose
	 */
	public int getLose() {
		return m_nLose;
	}

	/**
	 * @param nLose 要设置的 m_nLose
	 */
	public void addLose(int nLose) {
		this.m_nLose += nLose;
	}

	/**
	 * @return m_nFlat
	 */
	public int getFlat() {
		return m_nFlat;
	}

	/**
	 * @param nFlat 要设置的 m_nFlat
	 */
	public void addFlat(int nFlat) {
		this.m_nFlat += nFlat;
	}

	/**
	 * @return backupsCards
	 */
	public ArrayList<Integer> getBackupsCards() {
		return backupsCards;
	}

	/**
	 * 清除最近出手的时间
	 */
	public void clearLatelyOutCardTime() {
		this.setLatelyOutCardTime(0L);
	}

	public void setMaxPoint(int maxPoint) {
		this.maxPoint = this.maxPoint > maxPoint ? this.maxPoint : maxPoint;
	}

	/**
	 * 新增炸弹数
	 */
	public void addOutBomb() {
		this.outBombNum++;
	}

	public int getTuoGuanSetCount() {
		return tuoGuanSetCount;
	}

	public void setTuoGuanSetCount(int tuoGuanSetCount) {
		this.tuoGuanSetCount = tuoGuanSetCount;
	}

	/**
	 * 增加托管局数
	 */
	public void addTuoGuanSetCount(){
		tuoGuanSetCount += 1;
	}

	/**
	 * 连续托管局数清零
	 */
	public void clearTuoGuanSetCount(){
		tuoGuanSetCount = 0;
	}

	/**
	 * 设置托管状态
	 *
	 * @param isTrusteeship 托管状态
	 * @param isOwn         是否屏蔽自己
	 */
	public void setTrusteeship(boolean isTrusteeship, boolean isOwn) {
		if (this.isTrusteeship() == isTrusteeship) {
			return;
		}
		// 托管2小局解散：连续2局托管
		if(!isTrusteeship){ // 玩家取消托管，连续托管局数清零
			this.clearTuoGuanSetCount();
		}else{
			this.addTuoGuanSetCount();
		}
		this.setTrusteeship(isTrusteeship);
		if (isOwn) {
			this.getRoom().getRoomPosMgr().notify2ExcludePosID(this.getPosID(), this.getRoom().Trusteeship(this.getRoom().getRoomID(), this.getPid(), this.getPosID(), this.isTrusteeship()));
		} else {
			this.getRoom().getRoomPosMgr().notify2All(this.getRoom().Trusteeship(this.getRoom().getRoomID(), this.getPid(), this.getPosID(), this.isTrusteeship()));
		}
	}

	public long getSecTotal() {
		return secTotal;
	}

	public void setSecTotal(long secTotal) {
		this.secTotal = secTotal;
	}
}
