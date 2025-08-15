package business.global.mj.qzmj.hutype;

import business.global.mj.AbsMJSetPos;
import business.global.mj.MJCard;
import business.global.mj.MJCardInit;
import business.global.mj.manage.MJFactory;
import business.global.mj.ting.AbsTing;
import business.global.mj.util.HuUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 镇江麻将
 *
 * @author Huaxing
 */
public class QZMJTingImpl extends AbsTing {

    @Override
    public boolean tingHu(AbsMJSetPos mSetPos, MJCardInit mCardInit) {
        if (MJFactory.getHuCard(QZMJHuImpl.class).checkHuCard(mSetPos, mCardInit)) {
            return true;
        }
        return false;
    }

    /**
     * 检查听到的牌
     *
     * @param mSetPos
     * @param allCardList
     * @return
     */
    public List<Integer> absCheckTingCard(AbsMJSetPos mSetPos, List<MJCard> allCardList) {
        List<Integer> ret = new ArrayList<>();
        if (CollectionUtils.isEmpty(allCardList))
            return ret;
        boolean isHu;
        // 遍历其他牌
        MJCardInit mCardInit;
        for (int type : HuUtil.CheckTypes) {
            mCardInit = mSetPos.mCardInit(allCardList, type, true);

            isHu = tingHu(mSetPos, mCardInit);
            if (isHu) {
                if (!ret.contains(type)) {
                    ret.add(type);
                }
            }
        }
        return ret;
    }

}
