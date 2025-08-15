package business.global.mj.qzmj.hutype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import business.global.mj.AbsMJSetPos;
import business.global.mj.MJCardInit;
import business.global.mj.hu.BTJHuCardImpl;
import business.global.mj.hu.BaseHuCard;
import business.global.mj.hu.NormalHuCardImpl;
import business.global.mj.manage.MJFactory;
import cenum.mj.MJSpecialEnum;
import cenum.mj.OpType;

/**
 * 莆仙麻将游金
 * @author Huaxing
 *
 */
public class QZMJYouJinImpl extends BaseHuCard {
	
	/**
	 * 检查是否游金
	 * @param setPos 玩家信息
	 * @param youNum 第几游金
	 * @return
	 */
	public boolean isYouJin(AbsMJSetPos setPos,int youNum) {	
		if (!setPos.getOutJinInfo().isOutJinCard()) {
            return false;
        }
		List<Integer> outCardList = new ArrayList<>(setPos.getOutCardIDs()).stream().filter(n->n.intValue()/100<=MJSpecialEnum.NOT_HUA.value()).collect(Collectors.toList());
		//获取打出的中牌数（除花）
		int outCardSize = outCardList.size();
		if (outCardSize <= 0) {
            return false;
        }
		int startOutCard = outCardSize - youNum;
//		if (startOutCard < 0) {
//            return false;
//        }
		int count = 0;
		//起始位置，结束位置
		List<Integer> cardNum = outCardList.subList(startOutCard<0?0:startOutCard,outCardSize);
		Collections.reverse(cardNum);
		for (int cardId : cardNum) {
			if (setPos.getSet().getmJinCardInfo().checkJinExist(cardId)) {
				count++;
			} else {
				break;
			}
		}
		
		if (youNum == 2 && count == 1) {
			setPos.setSpecialOpType(OpType.ShuangYou);
			return true;
		} else if (youNum == 3 && count == 2) {
			setPos.setSpecialOpType(OpType.SanYou);
			return true;
		}
		return false;

	}
		
	
	@Override
	public boolean checkHuCard(AbsMJSetPos mSetPos,MJCardInit mCardInit,int cardType) {
		//听牌Map值列表大小有 >= 34。
//		boolean isPresent = mSetPos.getPosOpNotice().getTingCardMap().values().stream().filter(k->k.size() >=34).findAny().isPresent();
		boolean isPresent=mSetPos.getHuCardTypes().size()==34;
		if((cardType == 1 || cardType == 30) && isPresent) {
			// （cardType == 1 单游 || cardType == 30，三金游）
			// （cardType == 1 单游 ,检查金数量 > 0,cardType == 30，三金游,检查金数量 > 2） 
			if (mSetPos.isJinCard() > (cardType == 1?0:2) && MJFactory.getHuCard(NormalHuCardImpl.class).checkHuCard(mSetPos, mCardInit)) {
				mSetPos.setSpecialOpType(OpType.DanYou);
				return true;
			}
		} else {

			boolean isTingAll = mSetPos.getPosOpNotice().getTingCardMap().values().stream().anyMatch(n->n.size()>= MJSpecialEnum.TING.value());
			if ((isPresent || isTingAll) && this.isYouJin(mSetPos, cardType)) {
				if (mSetPos.isJinCard() > 0 && MJFactory.getHuCard(NormalHuCardImpl.class).checkHuCard(mSetPos, mCardInit)) {
					return true;	
				}
			}
		}
		mSetPos.setSpecialOpType(OpType.Not);
		return false;
	}


}
