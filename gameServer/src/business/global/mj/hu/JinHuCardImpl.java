package business.global.mj.hu;

import business.global.mj.AbsMJSetPos;
import business.global.mj.MJCardInit;
import business.global.mj.util.HuDuiUtil;

/**
 * 金倒
 * @author Huaxing
 *
 */
public class JinHuCardImpl extends BaseHuCard {

	@Override
	public boolean checkHuCard(AbsMJSetPos mSetPos, int jin) {
		MJCardInit mInit = mSetPos.mCardInit(0,true);
		if (null != mInit) {
			return mInit.sizeJin() >= jin;
		}
		return false;
	}

	@Override
	public boolean checkHuCard(AbsMJSetPos mSetPos,MJCardInit mInit, int jin) {
		if (null != mInit) {
			return mInit.sizeJin() >= jin;
		}
		return false;
	}
}
