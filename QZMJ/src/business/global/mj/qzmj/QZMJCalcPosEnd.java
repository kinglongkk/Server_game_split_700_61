package business.global.mj.qzmj;

import java.util.*;
import java.util.stream.Collectors;

import cenum.PrizeType;
import business.global.mj.AbsCalcPosEnd;
import business.global.mj.AbsMJSetPos;
import business.global.mj.qzmj.QZMJRoomEnum.QZMJCfg;
import business.global.mj.qzmj.QZMJRoomEnum.QZMJOpPoint;
import cenum.mj.*;
import com.ddm.server.common.CommLogD;

public class QZMJCalcPosEnd extends AbsCalcPosEnd {
	// 初始值
	private    int ZHUANG_DIFEN = 10;
	private    int XIAN_DIFEN = 5;
	protected Map<QZMJOpPoint, Integer> huTypeMap = new HashMap<>();
	// 春夏秋冬 梅兰竹菊
	private static  final List<Integer> CXQD = Arrays.asList(5101,5201,5301,5401);
	private static  final List<Integer> MLZJ = Arrays.asList(5501,5601,5701,5801);
	private QZMJRoomSet set;
	private QZMJRoom room;
	private int lianZhuang = 0;
	private int diFenPoint = 0;
	private int fanTotal=0;
	private boolean isHuPos = false;//是不是胡牌


	public QZMJCalcPosEnd(AbsMJSetPos mSetPos) {
		super(mSetPos);
		this.set = (QZMJRoomSet) mSetPos.getSet();
		this.room = (QZMJRoom) mSetPos.getRoom();
		if (mSetPos.getRoom().RoomCfg(QZMJCfg.LianZhuang)) {
			this.lianZhuang = (this.set.lianZhuang() - 1) <= 0 ? 0 : (this.set.lianZhuang() - 1);
			if (this.lianZhuang > 0) {
				ZHUANG_DIFEN+=lianZhuang*5;
			}
		}
		// 庄家
		if (this.set.getDPos() == mSetPos.getPosID()) {
			if (this.lianZhuang > 0) {
				this.calcOpPointType(QZMJOpPoint.LianZhuang, this.lianZhuang);

			}
		}
	}

	@Override
	public int calcPoint(boolean isZhuang, Object... params) {
		return 0;
	}

	@Override
	public void calcPosEnd(AbsMJSetPos mSetPos) {
		mSetPos.setEndPoint(mSetPos.getEndPoint() + mSetPos.getDeductPoint());
	}

	@Override
	public void calcPosPoint(AbsMJSetPos mSetPos) {
		// 臭庄则当局不计分。
		if (this.set.getMHuInfo().isHuEmpty()) {
            return;
        }
		//计算番
		this.calcFan();
		//计算底分
		this.calDiFen();
		// 计算胡牌
		this.calcHu();
	}

	public void calcPosPoint() {
		this.getMSetPos().setEndPoint(this.getMSetPos().getEndPoint() + this.getMSetPos().getDeductPoint());
	}
	/**
	 * 计算底分
	 */
	private void calDiFen() {
		diFenPoint = this.set.getDPos() == this.getMSetPos().getPosID()?ZHUANG_DIFEN:XIAN_DIFEN;
		this.calcOpPointType(QZMJOpPoint.DiFen, diFenPoint);
	}
	/**
	 * 低番 盘数
	 */
	private void calcFan() {
		List<List<Integer>> pLists = new ArrayList<>();
		pLists.addAll(this.getMSetPos().getPublicCardList());
		int pengFan=0;//碰番
		int gangFan=0;//杠番
		int huaFan=0;//花番
		int jinFan=0;//金番
		// 遍历吃碰杠操作
		for (List<Integer> publicCards : pLists) {
			int type = publicCards.get(0);
			int cardType = publicCards.get(2) / 100;
			if (type == OpType.JieGang.value() || type == OpType.Gang.value()) {
				if (cardType >= 40) {
					gangFan += 3;
				} else {
					gangFan += 2;
				}
			}else if (type == OpType.AnGang.value()) {
				if (cardType >= 40) {
					gangFan += 4;
				} else {
					gangFan += 3;
				}
			} else if (type == OpType.Peng.value()) {

				if (cardType >= 40) {
					pengFan += 1;
				}
			}
		}
//		手牌暗刻  策划确认
		Map<Integer, Long> map = this.getMSetPos().allCards().stream()
				// 筛选出所有的牌类型
				.map(k->k.getType())
				// 检查等于金牌 或者 不是花牌
				.filter(k->this.checkFilter(this.getMSetPos(),k))
				// 按牌类型分组
				.collect(Collectors.groupingBy(p -> p, Collectors.counting()));
		// 遍历出相同类型 >= 4.
		for(Map.Entry<Integer,Long> con:map.entrySet()){
			if (con.getValue() >= 3) {
				if(con.getKey()>40){
					pengFan+=2;
				}else {
					pengFan++;
				}
			}
		}
		List<Integer> huaList=new ArrayList<>(this.getMSetPos().getPosOpRecord().getHuaList());
		if(huaList.size()==8){
			huaFan=16;
		}else if(huaList.containsAll(MLZJ)||huaList.containsAll(CXQD)){
			huaFan=8+huaList.size()-4;
		}else {
			huaFan=huaList.size();
		}
		jinFan=((QZMJSetPos)this.getMSetPos()).getJinNum();
		this.fanTotal=gangFan+pengFan+huaFan+jinFan;
		if(gangFan>0){
			this.calcOpPointType(QZMJOpPoint.GangFan, gangFan);
		}
		if(pengFan>0){
			this.calcOpPointType(QZMJOpPoint.PengFan, pengFan);
		}
		if(huaFan>0){
			this.calcOpPointType(QZMJOpPoint.HuaFan, huaFan);
		}
		if(jinFan>0){
			this.calcOpPointType(QZMJOpPoint.JinFan, jinFan);
		}

	}

	/**
	 * 检查过滤器
	 */
	protected boolean checkFilter(AbsMJSetPos mSetPos, int type) {
		return !mSetPos.getSet().getmJinCardInfo().checkJinExist(type) && type < MJSpecialEnum.NOT_HUA.value();
	}

	/**
	 * 动作分数
	 * @param oEnum
	 * @return
	 */
	private int OpValue(QZMJOpPoint oEnum) {
		QZMJRoom xRoom = (QZMJRoom) getMSetPos().getSet().getRoom();
		switch (oEnum) {
		case Hu:
		case ZiMo:
		case QGH:
		case SanJinDao:
		case TianHu:
		case TianTing:
		case ShuangYou:
		case SanYou:
		case QiangJin:
			return oEnum.value();
		case DanYou:
				return xRoom.getYouJinBeiShu();
		default:
			break;
		}
		return 0;
	}

	/**
	 * 计算胡
	 * 
	 * @param 
	 */
	private void calcHu() {
		isHuPos = this.set.getMHuInfo().getHuPos() == this.getMSetPos().getPosID();
		QZMJOpPoint maxOp = null;
		int huPointValue=0;
		if(isHuPos){//只计算胡牌玩家的胡list

			for(Object ob: this.getMSetPos().getPosOpRecord().getOpHuList()){
				QZMJOpPoint temp = ((QZMJOpPoint)ob);
				if(maxOp==null||maxOp.value()<temp.value()){
					maxOp = temp;
				}
			}

		}
		if(maxOp!=null){
			// 胡牌型倍数
			huPointValue= OpValue(maxOp);
			if(((QZMJSetPos)this.getMSetPos()).isTing()){
				huPointValue*=OpValue(QZMJOpPoint.TianTing);
				this.getMSetPos().getPosOpRecord().addOpHuList(QZMJOpPoint.TianTing);
				this.calcOpPointType(QZMJOpPoint.TianTing, OpValue(QZMJOpPoint.TianTing));
			}
			this.calcOpPointType(maxOp, OpValue(maxOp));
		}
		int calPoint=0;

		if (huPointValue!=0 && isHuPos) {//赢家
			calPoint = (fanTotal+diFenPoint)*huPointValue;//结算分数
		}else{
			calPoint = fanTotal;//输家比较水数
		}

		//先看是否有人点炮
		for(Map.Entry<Integer,AbsMJSetPos> con:set.getPosDict().entrySet()){
			if(con.getValue().getmHuOpType()==null)continue;
			if(con.getValue().getmHuOpType().equals(MJHuOpType.JiePao)||con.getValue().getmHuOpType().equals(MJHuOpType.QGHu)){
				setDianPao();
			}
		}

		//放胡单家陪
		if(room.getJieSuan(QZMJRoomEnum.QZMJJieSuan.FHDJP)){
			// 点炮和没胡的玩家 在放胡当家给中 不算分
			if(HuType.NotHu.equals(this.getMSetPos().getHuType())||HuType.DianPao.equals(this.getMSetPos().getHuType())){
				return;
			}
			if(MJHuOpType.JiePao.equals(this.getMSetPos().getmHuOpType())||MJHuOpType.QGHu.equals(this.getMSetPos().getmHuOpType())){
				calc1V1Op(this.getMSetPos().getSet().getLastOpInfo().getLastOpPos(),calPoint*(room.getPlayerNum()-1));
			}else{//自摸
				this.calc1V3Op(calPoint);
			}
		}else{
			this.calc1V3Op(calPoint);
		}


	}




	private class CalcPosEnd {
		@SuppressWarnings("unused")
		private Map<QZMJOpPoint, Integer> huTypeMap = new HashMap<>();

		public CalcPosEnd(Map<QZMJOpPoint, Integer> huTypeMap) {
			this.huTypeMap = huTypeMap;
		}
	}

	@Override
	public <T> void calcOpPointType(T opType, int count) {
		QZMJOpPoint opPoint = (QZMJOpPoint) opType;
		switch (opPoint) {
		case Not:
			break;
		default:
			this.addhuType(opPoint, count, MJEndType.PLUS);
			break;
		}
	}

	/**
	 * 添加胡类型
	 *
	 * @param opPoint
	 * @param point
	 */
	protected void addhuType(QZMJOpPoint opPoint, int point, MJEndType bEndType) {
		if (this.huTypeMap.containsKey(opPoint)) {
			// 累计
			int calcPoint = point;
			if (MJEndType.PLUS.equals(bEndType)) {
				calcPoint = this.huTypeMap.get(opPoint) + point;
			} else if (MJEndType.MULTIPLY.equals(bEndType)) {
				calcPoint = this.huTypeMap.get(opPoint) * point;
			}
			this.huTypeMap.put(opPoint, calcPoint);
		} else {
			this.huTypeMap.put(opPoint, point);
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCalcPosEnd() {
		return (T) new CalcPosEnd(this.huTypeMap);
	}


	/**
	 * 动作分数
	 * 1V3扣分。
	 * 1人加分，3人扣分
	 *
	 * @param value 分数
	 */
	private void calc1V3Op(int value) {

		// 其他玩家信息
		AbsMJSetPos mOSetPos;
		for (int i = 0; i < this.getMSetPos().getPlayerNum(); i++) {
			int tempValue = value;
			int nextPos = (this.set.getMHuInfo().getHuPos() + i) % this.getMSetPos().getPlayerNum();
			// 遍历玩家
			mOSetPos = this.getMSetPos().getMJSetPos(nextPos);
			if (mOSetPos == null || set.getMHuInfo().getHuPos() == mOSetPos.getPosID() || mOSetPos.getPid() == this.getMSetPos().getPid()) {
				// 找不到玩家或者胡牌玩家或者自己直接跳过。（不能赢胡牌玩家的分数）
				continue;
			}
			// 赢家和其他玩家算钱
			if(isHuPos){
				int opPoint = this.getMSetPos().getPosOpRecord().getOpHuList().stream().mapToInt(k ->OpValue ((QZMJOpPoint) k)).max().orElse(0);
				if(set.getDPos() == mOSetPos.getPosID()){//输的那家是庄家的话
					tempValue += (ZHUANG_DIFEN - XIAN_DIFEN) * opPoint;
				}
			}
//			//remark 一考分数上限控制
//			if (PrizeType.RoomCard.equals(set.getRoom().getBaseRoomConfigure().getPrizeType()) && ((QZMJRoom) set.getRoom()).isYiKe()) {
//				//remark 分数达到上限
//				if (mOSetPos.getRoomPos().getTempPoint() + set.getLimitScore() - tempValue < 0) {
//					tempValue = mOSetPos.getRoomPos().getTempPoint() + set.getLimitScore();
//				}
//			}
			// remark 更新临时分数
//			mOSetPos.getRoomPos().setTempPoint(mOSetPos.getRoomPos().getTempPoint() - tempValue);
//			getMSetPos().getRoomPos().setTempPoint(getMSetPos().getRoomPos().getTempPoint() + tempValue);

			getMSetPos().setDeductPoint(getMSetPos().getDeductPoint() + tempValue);
			mOSetPos.setDeductPoint(mOSetPos.getDeductPoint() - tempValue);
		}
	}

	/**
	 * 动作分数 1V1扣分。
	 *
	 * @param lastOpPos 输分玩家的位置
	 * @param calcPoint 分数
	 */
	public void calc1V1Op(int lastOpPos, int calcPoint) {
		// 输分玩家的位置信息
		AbsMJSetPos fromPos = this.getMSetPos().getMJSetPos(lastOpPos);
		if (null == fromPos) {
			// 没找到
			CommLogD.error("calc1V1Op lastOpPos :{}", lastOpPos);
			return;
		}
		int tempValue = calcPoint;
//		//remark 一考分数上限控制
//		if (PrizeType.RoomCard.equals(set.getRoom().getBaseRoomConfigure().getPrizeType()) && ((QZMJRoom) set.getRoom()).isYiKe()) {
//			//remark 分数达到上限
//			if (fromPos.getRoomPos().getTempPoint() + set.getLimitScore() - tempValue < 0) {
//				tempValue = fromPos.getRoomPos().getTempPoint() + set.getLimitScore();
//			}
//		}
		// 赢的分数计算。
		this.getMSetPos().setDeductPoint(this.getMSetPos().getDeductPoint() + tempValue);
		// 输的分数计算。
		fromPos.setDeductPoint(fromPos.getDeductPoint() - tempValue);
	}
}
