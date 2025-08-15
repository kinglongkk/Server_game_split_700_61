package business.global.mj.op;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import business.global.mj.AbsMJSetPos;
import business.global.mj.manage.OpCard;
import business.global.mj.set.LastOpTypeItem;
import cenum.mj.OpType;

public class JieGangCardImpl implements OpCard {

	@Override
	public boolean checkOpCard(AbsMJSetPos mSetPos, int cardID) {		
		int type = cardID/100;
		if(mSetPos.getSet().getmJinCardInfo().checkJinExist(type)) {
			return false;
		}
		long count = mSetPos.allCards().stream()
				// 筛选出所有的牌类型
				.map(k->k.getType())
				// 检查不等于金牌
				.filter(k->k == type)
				// 按牌类型分组
				.count();
		return count >= 3L;
	}

	@Override
	public boolean doOpCard(AbsMJSetPos mSetPos, int cardID) {		
		int lastOutCard = mSetPos.getSet().getLastOpInfo().getLastOutCard();
		int fromPos = mSetPos.getMJSetCard().getCardByID(lastOutCard).getOwnnerPos();
		int type = lastOutCard / 100;
		if (mSetPos.getSet().getmJinCardInfo().checkJinExist(type)) {
			// 金牌或者花牌。
			return false;
		}
		List<Integer> publicCard = new ArrayList<>();
		publicCard.add(OpType.JieGang.value());
		publicCard.add(fromPos);
		publicCard.add(cardID);
		publicCard.add(cardID);
		// 搜集牌
		List<Integer> tmp = mSetPos.allCards().stream()
				.map(k->k.getCardID()).filter(k->k/100 == type)
				.collect(Collectors.toList());
		// 牌 < 3.
		if (tmp.size() < 3) {
			return false;
		}
		// 增加亮牌
		publicCard.addAll(tmp);
		mSetPos.addPublicCard(publicCard);
		mSetPos.removeAllPrivateCards(tmp);
		mSetPos.getSet().getLastOpInfo().addLastOpItem(OpType.JieGang,new LastOpTypeItem(mSetPos.getPosID(),cardID));
		mSetPos.getSet().getLastOpInfo().getLastOpGangItem().setLastOpGangItem(mSetPos.getPosID(),cardID,fromPos,OpType.JieGang);
		return true;

	}

}
