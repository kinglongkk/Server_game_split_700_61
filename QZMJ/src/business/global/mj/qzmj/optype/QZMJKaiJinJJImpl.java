package business.global.mj.qzmj.optype;

import business.global.mj.AbsMJSetPos;
import business.global.mj.MJCard;
import business.global.mj.op.KaiJinImpl;
import business.global.mj.op.KaiJinJJ1Impl;
import business.global.mj.qzmj.QZMJSetCard;
import cenum.mj.MJSpecialEnum;

/**
 * 开金
 * 进金
 * 开到白板，白板金
 * @author Huaxing
 *
 */
public class QZMJKaiJinJJImpl extends KaiJinImpl {


	


	
	/**
	 * 开金
	 * @param mSetPos 玩家信息
	 */
	public void kaiJinCard (AbsMJSetPos mSetPos) {

		// 摸牌开金
		MJCard card = ((QZMJSetCard)mSetPos.getSet().getMJSetCard()).popKaiJin();
		if(null == card) {
			// 没有牌
			return;
		}
		// 检查摸到牌的类型是否 < 50 , > 50 花
		if (card.getType() < MJSpecialEnum.NOT_HUA.value()) {
			// 添加金牌
			if (mSetPos.getSet().getmJinCardInfo().addJinCard(card)) {
				// 金牌添加成功
				return;
			} else {
				// 添加金失败，重新开金
				kaiJinCard(mSetPos);
			}
		} else {
			// 开金开到花牌，通知补花，并且重新开金
			kaiJinApplique(mSetPos,card);
			// 重新开金
			kaiJinCard(mSetPos);
		}
	}

	

	
	public MJCard jinJin (MJCard card) {
		if (card.type == MJSpecialEnum.BAIBAN.value()) {
            return card;
        }
		//牌类型
		int type = card.type /10;
		//牌大小
		int size = (card.cardID % 1000)/100;
		if (size >= 9) {
			size = 1;
		} else {
			size++;
		}
		int cardId = (type * 10+size)*100 + 1;
		return new MJCard(cardId);
	}	
	
	public MJCard backJinJin (MJCard card) {
		if (card.type == MJSpecialEnum.BAIBAN.value()) {
            return card;
        }
		//牌类型
		int type = card.type /10;
		//牌大小
		int size = (card.cardID % 1000)/100;
		
		if (size <= 1) {
			size = 9;
		} else {
			size--;
		}
		int cardId = (type * 10+size)*100 + 1;
		return new MJCard(cardId);
	}

	/**
	 * 开金补花通知
	 * @param mSetPos 玩家信息
	 * @param mCard 开出的牌
	 */
	private void kaiJinApplique (AbsMJSetPos mSetPos,MJCard mCard) {
		// 添加打出的牌
		mSetPos.addOutCardIDs(mCard.getCardID());
		// 添加花
		mSetPos.getPosOpRecord().addHua(mCard.getCardID());
		// 通知补花,补花位置。
		mSetPos.getSet().MJApplique(mSetPos.getPosID());
	}
}
