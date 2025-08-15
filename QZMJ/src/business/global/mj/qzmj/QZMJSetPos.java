package business.global.mj.qzmj;

import business.global.mj.AbsMJSetPos;
import business.global.mj.AbsMJSetRoom;
import business.global.mj.MJCard;
import business.global.mj.hu.QiangJinHuBTJCardImpl;
import business.global.mj.manage.MJFactory;
import business.global.mj.qzmj.hutype.QZMJTingImpl;
import business.global.mj.ting.TingNormalImpl;
import business.global.room.mj.MJRoomPos;
import business.qzmj.c2s.cclass.QZMJResults;
import business.qzmj.c2s.cclass.QZMJSet_Pos;
import cenum.PrizeType;
import cenum.mj.HuType;
import cenum.mj.MJHuOpType;
import cenum.mj.OpPointEnum;
import cenum.mj.OpType;
import jsproto.c2s.cclass.mj.BaseMJRoom_PosEnd;
import jsproto.c2s.cclass.mj.BaseMJSet_Pos;
import jsproto.c2s.cclass.room.AbsBaseResults;

import java.util.ArrayList;
import java.util.List;

/**
 * 麻将 每一局每个位置信息
 * 
 * @author Huaxing
 *
 */

public class QZMJSetPos extends AbsMJSetPos {
	private boolean qiangJinFlag=true;
	private boolean isOutJinCard = false;
	public List<Integer> tingYous = new ArrayList<Integer>();
	public boolean isYouJin=false;	// 游金 --双游三游
	public List<MJCard> qiangJinCards = new ArrayList<MJCard>(); // 抢金牌
	private boolean isTing=false;
	public HuType qzmjHutype=HuType.NotHu;
	private MJCard dPosQiangJinOutCard=null;
	public QZMJSetPos(int posID, MJRoomPos roomPos, AbsMJSetRoom set) {
		super(posID, roomPos, set, QZMJTingImpl.class);
		this.setMSetOp(new QZMJSetOp(this));
		this.setCalcPosEnd(new QZMJCalcPosEnd(this));
	}

	public HuType getQzmjHutype() {
		return qzmjHutype;
	}

	public void setQzmjHutype(HuType qzmjHutype) {
		this.qzmjHutype = qzmjHutype;
	}

	public boolean isQiangJinFlag() {
		return qiangJinFlag;
	}

	public void setQiangJinFlag(boolean qiangJinFlag) {
		this.qiangJinFlag = qiangJinFlag;
	}

	public boolean isTing() {
		return isTing;
	}

	public void setTing(boolean ting) {
		isTing = ting;
	}

	public MJCard getdPosQiangJinOutCard() {
		return dPosQiangJinOutCard;
	}

	public void setdPosQiangJinOutCard(MJCard dPosQiangJinOutCard) {
		this.dPosQiangJinOutCard = dPosQiangJinOutCard;
	}

	/**
	 * 获取手牌通知信息
	 */
	@Override
	public BaseMJSet_Pos getNotify(boolean isSelf) {
		if (isRevealCard()) {
			isSelf = true;
		}
		QZMJSet_Pos ret = this.newMJSetPos();
		// 玩家位置
		ret.setPosID(this.getPosID());
		// 是自己
		int length = sizePrivateCard();
		for (int i = 0; i < length; i++) {
			ret.getShouCard().add(isSelf ? getPCard(i).cardID : 0);
		}
		// 可胡的牌
		ret.setHuCard(isSelf ? this.getHuCardTypes() : null);
		if (this.getHandCard() != null) {
			// 首牌
			ret.setHandCard(isSelf ? this.getHandCard().getCardID() : 5000);
		}
		// 打出的牌
		ret.setOutCard(this.getOutCardIDs());
		// 公共牌
		ret.setPublicCardList(this.getPublicCardList());
		// 掉线连接
		ret.setIsLostConnect(null);
		ret.setTing(this.isTing());

		ret.setLianZhuangNum(getRoom().getDPos() == this.getPosID() ? getRoom().getEvenDpos()-1  : 0);

		// 获取手牌通知信息
		return ret;
	}

	/**
	 * 计算位置小局分数
	 */
	@Override
	public void calcPosPoint() {
		this.getCalcPosEnd().calcPosPoint(this);
	}
	/**
	 * 玩家总分和胡牌次数
	 */
	public void pidPointEnd() {
		this.getRoomPos().calcRoomPoint(this.getEndPoint());
		this.getRoomPos().setTempPoint(this.getRoomPos().getPoint());
		if (PrizeType.RoomCard.equals(getRoom().getBaseRoomConfigure().getPrizeType())) {
			this.getRoomPos().addCountPoint(this.getEndPoint());
		}
		if (this.getHuType() != HuType.NotHu && this.getHuType() != HuType.DianPao) {
			this.getRoomPos().setHuCnt(this.getRoomPos().getHuCnt() + 1);
		}
	}
	/**
	 * 操作类型
	 */
	@Override
	public boolean doOpType(int cardID, OpType opType) {
		return this.getmSetOp().doOpType(cardID, opType);
	}
	/**
	 * 清空操作状态
	 *
	 * @param
	 */
	public void cleanOp() {
		((QZMJSetOp)this.getmSetOp()).cleanOp();
//		this.getmSetOp().clear();
	}
	/**
	 * 检测类型
	 */
	@Override
	public boolean checkOpType(int cardID, OpType opType) {
		return this.getmSetOp().checkOpType(cardID, opType);
	}

	/**
	 * 检测自摸胡
	 */
	@Override
	public List<OpType> recieveOpTypes() {
		this.clearOutCard();
		List<OpType> opTypes = new ArrayList<OpType>();

		boolean isPosOutJin = ((QZMJSetPosMgr)this.getSet().getSetPosMgr()).isAllPosOutJin();
		if (checkOpType(0, OpType.SanJinDao)) {
			this.getPosOpRecord().addOpHuList(QZMJRoomEnum.QZMJOpPoint.SanJinDao);
			opTypes.add(OpType.SanJinDao);
		}
		if (checkOpType(0, OpType.SanYou)) {
			opTypes.add(OpType.SanYou);
		} else if (checkOpType(0, OpType.ShuangYou)) {
			opTypes.add(OpType.ShuangYou);
		} else if (checkOpType(0, OpType.SanJinYou)) {
			opTypes.add(OpType.SanJinYou);
		} else if (checkOpType(0, OpType.DanYou)) {
			opTypes.add(OpType.DanYou);

		} else if (checkOpType(0, OpType.Hu)&&(this.isOpSize()>0||!isPosOutJin||(checkShuangYou(isPosOutJin)&&!checkSanYou(isPosOutJin)))){
			/**
			 * // 没有人打出金 则可以自摸
			 * 			//有人打出金的话 可能是双游 或者三游
			 *
			 * 			//1.如果是杠上开花的话 都可以胡
			 * 			//2. 双游的时候可以自摸
			 * 			//3.三游的时候可以杠上开花  三游的时候不能自摸
			 */
			opTypes.add(OpType.Hu);

			this.getPosOpRecord().addOpHuList(QZMJRoomEnum.QZMJOpPoint.ZiMo);
		}
		if (opTypes.size() > 0) {
			if (isOutJinCard()) {
				opTypes.clear();
			} else {
				this.setmHuOpType(MJHuOpType.ZiMo);
			}
		}
		if (checkOpType(0, OpType.TingYouJin)) {
			opTypes.add(OpType.TingYouJin);
		} else {
			if (checkOpType(0, OpType.Ting))
				opTypes.add(OpType.Ting);
		}
		if (this.isJinCard() < 4) { 
			if (checkOpType(0, OpType.AnGang)) {
                opTypes.add(OpType.AnGang);
            }
			if (checkOpType(0, OpType.Gang)) {
                opTypes.add(OpType.Gang);
            }
		}
		opTypes.add(OpType.Out);
		return opTypes;
	}
	/**
	 *
	 */
	public boolean checkShuangYou( boolean isPosOutJin){
		for(int i=0;i<this.getSet().getPlayerNum();i++){
			QZMJSetPos setPos=(QZMJSetPos)this.getSet().getPosDict().get(i);
			if(setPos.getHuCardTypes().size()>=34&&isPosOutJin){
				return true;
			}
		}
		return false;
	}
	/**
	 * 操作
	 *
	 * @return
	 */
	public int isOpSize() {
		return  ((QZMJSetOp)this.getmSetOp()).isOpSize();
	}

	/**
	 *
	 */
	public boolean checkSanYou( boolean isPosOutJin){
		boolean doubleJin=false;
		for(int i=0;i<this.getSet().getPlayerNum();i++){
			QZMJSetPos setPos=(QZMJSetPos)this.getSet().getPosDict().get(i);
			List<Integer> outCards=setPos.getOutCardIDs();
			if(outCards.size()>2&&this.getSet().getmJinCardInfo().checkJinExist(outCards.get(outCards.size()-1))&&this.getSet().getmJinCardInfo().checkJinExist(outCards.get(outCards.size()-2))){
				doubleJin=true;
			}
			if(setPos.getHuCardTypes().size()>=34&&isPosOutJin&&doubleJin){
				return true;
			}
		}
		return false;
	}
	/**
	 * 检查是否有杠 暗杆  有的话可以截胡
	 * @param
	 * @return
	 */
	public boolean checkGang(){
		for(OpType type:this.getPosOpRecord().getOpList()){
			if(type.equals(OpType.Gang)||type.equals(OpType.AnGang)){
				return true;
			}
		}
		return false;
	}
	/**
	 * 统计本局分数
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public BaseMJRoom_PosEnd calcPosEnd() {
//		// 玩家当局分数结算  因为一科  放到另外一边去计算
//		this.getCalcPosEnd().calcPosEnd(this);
		// 位置结算信息
		BaseMJRoom_PosEnd ret = this.posEndInfo();
		ret.setEndPoint(this.getCalcPosEnd().getCalcPosEnd());
		return ret;
	}

	/**
	 * 计算总结算信息
	 */
	@Override
	public void calcResults() {
		QZMJResults mResultsInfo = (QZMJResults) this.mResultsInfo();
		if(!HuType.NotHu.equals(this.getQzmjHutype())){
			mResultsInfo.addHuType(this.getQzmjHutype());
		}
		this.setResults(mResultsInfo);
	}

	/**
	 * 检测平胡
	 */
	@Override
	public OpType checkPingHu(int curOpPos, int cardID) {
		// 清空操作牌初始
		this.clearPaoHu();
		if(this.getHuCardTypes().size()>=34) return OpType.Not;
		if (checkOpType(cardID,OpType.JiePao)) {
			this.setmHuOpType(MJHuOpType.JiePao);
			return OpType.JiePao;
		}
		return OpType.Not;
	}

	/**
	 * 计算动作分数
	 * 
	 * @param opType
	 * @param count
	 */
	@Override
	public <T> void calcOpPointType(T opType, int count) {
		this.getCalcPosEnd().calcOpPointType((OpPointEnum) opType, count);
	}

	/**
	 * 新结算
	 */
	@Override
	protected AbsBaseResults newResults() {
		return new QZMJResults();
	}
	public boolean isOutJinCard() {
		return isOutJinCard;
	}

	/**
	 * 抢金胡的操作
	 */
	public void do_TianTing() {
//		this.getSet().getPosDict().get(this.getSet().getDPos()).cleanHandCard();
		this.setPrivateCard(this.qiangJinCards);
		this.cleanHandCard();
		this.setTing(true);
		if(this.getPosID()==this.getSet().getDPos()){
			this.setHandCard(this.getdPosQiangJinOutCard());
		}
	}

	/**
	 * 听游金
	 *
	 * @return
	 */
	public boolean tingYouJin() {

		List<Integer> lists = new ArrayList<Integer>();

		lists = tingYouList(lists, 0);
		List<Integer> jinList = getJinList(lists);
		if (jinList.size() > 0) {
			this.tingYous = jinList;
			return true;
		} else if (lists.size() > 0) {
			if (this.isYouJin) {
				this.tingYous.clear();
				return false;
			}

				int jinNum = getJinNum();
				if (jinNum == 3) {
					this.tingYous.clear();
					return false;
				}

			this.tingYous = lists;
			return true;
		}
		return false;
	}
	/**
	 * 检查手上金的数量
	 * @return
	 */
	public int getJinNum() {
		int jinNum = 0;
		List<MJCard> allMJCard = new ArrayList<MJCard>(this.getPrivateCard());
		if (null != this.getHandCard())
			allMJCard.add(this.getHandCard());

		for (MJCard mCard : allMJCard) {
			if(this.getSet().getmJinCardInfo().checkJinExist(mCard.getCardID())){
				jinNum++;
			}
		}
		return jinNum;
	}
	/**
	 * 获取金列表
	 *
	 * @param lists
	 * @return
	 */
	public List<Integer> getJinList(List<Integer> lists) {
		List<Integer> jinList = new ArrayList<Integer>();
		for (int i = 0, size = lists.size(); i < size; i++) {
			if(this.getSet().getmJinCardInfo().checkJinExist(lists.get(i)))
				jinList.add(lists.get(i));
		}
		return jinList;
	}
	/**
	 * 听游金列表
	 *
	 * @param lists
	 *            列表
	 * @param idx
	 *            下标
	 * @return
	 */
	public List<Integer> tingYouList(List<Integer> lists, int idx) {
		// 获取所有牌
		List<MJCard> allCards = allCards();
		// 如果牌的下标 == 所有牌 -1
		if (allCards.size() == idx)
			return lists;
		// 获取牌ID
		int cardId = allCards.get(idx).cardID;
		// 移除一张牌
		allCards.remove(idx);
		// 听牌
		List<Integer> tingList =MJFactory.getTingCard(TingNormalImpl.class).checkTingCard(this,allCards);
		idx++;
//		 判断听牌数
		if (tingList.size() >= 34) {
			lists.add(cardId);
			return tingYouList(lists, idx);
		}
		return tingYouList(lists, idx);
	}

	public boolean checkKeXuanJin(){
		Integer jinNum=this.getJinNum();
		QZMJRoom qzmjRoom=(QZMJRoom)this.getRoom();
		if(qzmjRoom.RoomCfg(QZMJRoomEnum.QZMJCfg.DanJinNotPingHu)){
			if(jinNum>0) return false;
		}
		if(qzmjRoom.RoomCfg(QZMJRoomEnum.QZMJCfg.ShuangJinNotPingHu)){
			if(jinNum>1) return false;
		}
		return true;
	}
	/**
	 * 新一局中各位置的信息
	 *
	 * @return
	 */
	@Override
	protected QZMJSet_Pos newMJSetPos() {
		return new QZMJSet_Pos();
	}


	/**
	 * 检查抢金
	 * @param
	 * @return
	 */
	public boolean checkTianTing () {
		List<MJCard> qiangJin = MJFactory.getHuCard(QiangJinHuBTJCardImpl.class).qiangJinHuCard(this);
		if (null == qiangJin) {
			return false;
		}
		//如果是庄家的话 把不要的那张牌记录
		if(this.getPosID()==this.getSet().getDPos()){
			List<MJCard> cardsCopy=new ArrayList<>(this.allCards());
			cardsCopy.removeAll(qiangJin);
			if(cardsCopy.size()>0){
				this.setdPosQiangJinOutCard(cardsCopy.get(0));
			}
		}
		this.setQiangJinFlag(true);
		this.qiangJinCards = qiangJin;
		return true;

	}
}
