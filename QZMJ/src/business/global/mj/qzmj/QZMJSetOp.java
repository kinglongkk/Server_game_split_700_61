package business.global.mj.qzmj;

import java.util.ArrayList;
import java.util.List;

import business.global.mj.AbsMJSetOp;
import business.global.mj.AbsMJSetPos;
import business.global.mj.MJCard;
import business.global.mj.hu.BTJHuCardImpl;
import business.global.mj.hu.NormalHuCardImpl;
import business.global.mj.hu.QiangGangHuCardImpl;
import business.global.mj.hu.QiangJinHuBTJCardImpl;
import business.global.mj.manage.MJFactory;
import business.global.mj.op.*;
import business.global.mj.qzmj.hutype.QZMJTingImpl;
import business.global.mj.qzmj.hutype.QZMJYouJinImpl;
import business.global.mj.qzmj.QZMJRoomEnum.QZMJOpPoint;
import cenum.mj.MJHuOpType;
import cenum.mj.OpType;

public class QZMJSetOp extends AbsMJSetOp {
	// 玩家信息
	private QZMJSetPos mSetPos;
	private List<MJCard> qiangJinList = new ArrayList<MJCard>();
	// 操作
	private List<OpType> opTypes = new ArrayList<>();

	public QZMJSetOp(QZMJSetPos mSetPos) {
		super();
		this.mSetPos = mSetPos;
	}

	@Override
    public boolean doOpType(int cardID, OpType opType) {
		boolean doOpType = false;
		switch (opType) {
		case AnGang:
			doOpType = MJFactory.getOpCard(AnGangCardImpl.class).doOpCard(mSetPos, cardID);
			this.addOp(mSetPos, doOpType, OpType.AnGang);
			break;
		case Gang:
			doOpType = MJFactory.getOpCard(GangCardImpl.class).doOpCard(mSetPos, cardID);
			this.addOp(mSetPos, doOpType, OpType.Gang);
			break;
		case Chi:
			doOpType = MJFactory.getOpCard(ChiCardNormalImpl.class).doOpCard(mSetPos, cardID);
			break;
		case JieGang:
			doOpType = MJFactory.getOpCard(JieGangCardImpl.class).doOpCard(mSetPos, cardID);
			this.addOp(mSetPos, doOpType, OpType.JieGang);
			break;
		case Peng:
			doOpType = MJFactory.getOpCard(PengCardImpl.class).doOpCard(mSetPos, cardID);
			break;
		case JiePao:
		case Hu:
			if (null != this.qiangJinList && this.qiangJinList.size() > 0&&mSetPos.isQiangJinFlag()) {
				doOpType = MJFactory.getHuCard(QiangGangHuCardImpl.class).doQiangJin(mSetPos, this.qiangJinList);
			} else {
				doOpType = doPingHu();
			}
			break;
		case QiangGangHu:
			doOpType = MJFactory.getHuCard(QiangGangHuCardImpl.class).checkHuCard(mSetPos.getMJSetPos());
			if (doOpType){
				mSetPos.setmHuOpType(MJHuOpType.QGHu);
			}
			break;
		case DanYou:
		case SiJinDao:
		case SanYou:
		case ShuangYou:
		case SanJinYou:
		case TianHu:
		case SanJinDao:
			doOpType = true;
			break;
		case TianTing:
			// 抢金胡
			mSetPos.do_TianTing();
			doOpType = true;
			break;
		default:
			break;
		}
		return doOpType;
	}



	@Override
	public boolean checkOpType(int cardID, OpType opType) {
		int cardType = cardID / 100;
		boolean isOpType = false;
		switch (opType) {
		case AnGang:
			if(this.mSetPos.getSet().getSetCard().getRandomCard().getLeftCards().size()==16) return false;
			isOpType = MJFactory.getOpCard(AnGangCardImpl.class).checkOpCard(mSetPos, 0);
			break;
		case Gang:
			if(this.mSetPos.getSet().getSetCard().getRandomCard().getLeftCards().size()==16) return false;
			isOpType = MJFactory.getOpCard(GangCardImpl.class).checkOpCard(mSetPos, cardID);
			break;
		case JieGang:
			if(this.mSetPos.getSet().getSetCard().getRandomCard().getLeftCards().size()==16) return false;
			isOpType = MJFactory.getOpCard(JieGangCardImpl.class).checkOpCard(mSetPos, cardID);
			break;
		case Peng:
			isOpType = MJFactory.getOpCard(PengCardImpl.class).checkOpCard(mSetPos, cardID);
			break;
		case Chi:
			isOpType = MJFactory.getOpCard(ChiCardNormalImpl.class).checkOpCard(mSetPos, cardID);
			break;
		case Ting:
			isOpType = MJFactory.getTingCard(QZMJTingImpl.class).checkTingList(mSetPos);
			break;
		case TianTing:
				if(checkTianTing(mSetPos)){
					isOpType=true;
				}
			break;
		case JiePao:
			if(MJFactory.getHuCard(NormalHuCardImpl.class).checkHuCard(mSetPos, mSetPos.mCardInit(cardType, true))){
				mSetPos.getPosOpRecord().addOpHuList(QZMJRoomEnum.QZMJOpPoint.Hu);
				return true;
			}
			break;
		case SanYou:
			// 莆仙游金三游
			if (MJFactory.getHuCard(QZMJYouJinImpl.class).checkHuCard(mSetPos, mSetPos.mCardInit(cardType, true),3)) {
					mSetPos.getPosOpRecord().addOpHuList(QZMJOpPoint.SanYou);
			return true;
			}
			break;
		case ShuangYou:
			// 莆仙游金双游
			if (MJFactory.getHuCard(QZMJYouJinImpl.class).checkHuCard(mSetPos, mSetPos.mCardInit(cardType, true),2)) {
				mSetPos.getPosOpRecord().addOpHuList(QZMJOpPoint.ShuangYou);
				return true;
			}
			break;
		case DanYou:
			// 莆仙游金单游
			if (MJFactory.getHuCard(QZMJYouJinImpl.class).checkHuCard(mSetPos, mSetPos.mCardInit(cardType, true),1)) {
				mSetPos.getPosOpRecord().addOpHuList(QZMJOpPoint.DanYou);
				return true;
			}
			break;
		case TianHu:
		case Hu:
		case QiangGangHu:
			// 普通胡牌
			if(MJFactory.getHuCard(NormalHuCardImpl.class).checkHuCard(mSetPos, mSetPos.mCardInit(cardType, true))) {
				if(opType==OpType.QiangGangHu){
					mSetPos.getPosOpRecord().addOpHuList(QZMJOpPoint.QGH);
				}else {
					if(opType==OpType.Hu&&cardID!=0) mSetPos.getPosOpRecord().addOpHuList(QZMJOpPoint.Hu);
				}
				return true;
			}
			return false;
		case SanJinDao:
			return mSetPos.isJinCard() == 3;
		case TingYouJin:
			isOpType=mSetPos.tingYouJin();
			break;
		default:
			break;
		}
		return isOpType;
	}

	/**
	 * 检查抢金
	 * @param mSetPos
	 * @return
	 */
	private boolean checkTianTing (QZMJSetPos mSetPos) {
		List<MJCard> qiangJin = MJFactory.getHuCard(QiangJinHuBTJCardImpl.class).qiangJinHuCard(mSetPos);
		if (null == qiangJin) {
			return false;
		}
		//如果是庄家的话 把不要的那张牌记录
		if(mSetPos.getPosID()==mSetPos.getSet().getDPos()){
			List<MJCard> cardsCopy=new ArrayList<>(mSetPos.allCards());
			cardsCopy.removeAll(qiangJin);
			if(cardsCopy.size()>0){
				mSetPos.setdPosQiangJinOutCard(cardsCopy.get(0));
			}
		}
		mSetPos.setQiangJinFlag(true);
		mSetPos.qiangJinCards = qiangJin;
		return true;

	}

	public boolean doPingHu() {
		if (MJHuOpType.JiePao.equals(mSetPos.getmHuOpType())) {
			int lastOutCard = mSetPos.getSet().getLastOpInfo().getLastOutCard();
			if (lastOutCard > 0) {
				mSetPos.setHandCard(new MJCard(lastOutCard));
			}
		}
		return true;
	}


	@Override
	public void clear() {
		this.mSetPos = null;
		this.qiangJinList = null;
	}
	/**
	 * 检查动作
	 *
	 * @return
	 */
	public int isOpSize() {
		return this.opTypes.size();
	}

	/**
	 * 清除所有动作
	 */
	public void cleanOp() {
		this.opTypes.clear();
	}
	/**
	 * 添加动作
	 *
	 * @param doOpType 是否操作成功
	 * @param opType   动作类型
	 */
	public void addOp(AbsMJSetPos mSetPos, boolean doOpType, OpType opType) {
		if (doOpType) {
			this.opTypes.add(opType);
		}
	}
}
