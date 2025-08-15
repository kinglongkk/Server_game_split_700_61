package business.global.mj.op;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import business.global.mj.AbsMJSetPos;
import business.global.mj.manage.OpCard;
import business.global.mj.set.LastOpTypeItem;
import cenum.mj.OpType;

public class PengCardJinImpl implements OpCard {

	@Override
	public boolean checkOpCard(AbsMJSetPos mSetPos, int cardID) {		
		int type = cardID/100;
		long count = mSetPos.allCards().stream()
				// 筛选出所有的牌类型
				.map(k->k.getType())
				// 检查不等于金牌
				.filter(k->k == type)
				// 按牌类型分组
				.count();
		return count >= 2L;
	}


	
	@Override
	public boolean doOpCard(AbsMJSetPos mSetPos, int cardID) {		
		int lastOutCard = mSetPos.getSet().getLastOpInfo().getLastOutCard();
		int fromPos = mSetPos.getMJSetCard().getCardByID(lastOutCard).getOwnnerPos();
		int type = lastOutCard / 100;
		List<Integer> publicCard = new ArrayList<>();
		publicCard.add(OpType.Peng.value());
		publicCard.add(fromPos);
		publicCard.add(cardID);
		publicCard.add(cardID);
		// 搜集牌
		List<Integer> tmp = mSetPos.allCards().stream()
				.map(k->k.getCardID()).filter(k->k/100 == type)
				.limit(2).collect(Collectors.toList());
		// 牌 < 2.
		if (tmp.size() < 2) {
			return false;
		}
		
		// 有接杠不接杠选择了碰。
		if (mSetPos.getSet().getLastOpInfo().checkBuGang(type, OpType.JieGang)) {
			// 不能杠了。
			mSetPos.getSet().getLastOpInfo().addBuGang(type, OpType.Not);
		}
		// 增加亮牌
		publicCard.addAll(tmp);
		mSetPos.addPublicCard(publicCard);
		mSetPos.removeAllPrivateCards(tmp);
		mSetPos.privateMoveHandCard();
		mSetPos.getSet().getLastOpInfo().addLastOpItem(OpType.Peng,new LastOpTypeItem(mSetPos.getPosID(),cardID));
		return true;

	}


}
