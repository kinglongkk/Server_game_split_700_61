package business.global.mj.hu;

import java.util.List;

import business.global.mj.AbsMJSetPos;
import business.global.mj.MJCard;
import business.global.mj.manage.MJFactory;
import business.global.mj.ting.TingNormalImpl;

/**
 * 抢金 普通
 * @author Huaxing
 *
 */
public class QiangJinHuCardImpl extends BaseHuCard {

	@Override
	public List<MJCard> qiangJinHuCard (AbsMJSetPos setPos) {
		return MJFactory.getTingCard(TingNormalImpl.class).qangJinTingList(setPos, 0, setPos.getSet().getDPos() == setPos.getPosID());
	}

	@Override
	public boolean doQiangJin(AbsMJSetPos mSetPos, List<MJCard> qiangJinList) {
		mSetPos.setPrivateCard(qiangJinList);
		mSetPos.cleanHandCard();
		mSetPos.getCard(mSetPos.getSet().getmJinCardInfo().getJin(1));
		return true;
	}
}
