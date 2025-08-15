package business.global.pk.dzpk.cardtype;

import business.global.pk.AbsPKSetPos;
import business.global.pk.dzpk.DZPKRoomEnum;
import business.global.pk.dzpk.base.DZPK_CardTypeImpl;
import jsproto.c2s.cclass.pk.BasePockerLogic;

import java.util.ArrayList;
import java.util.List;

/**
 * 高牌 走到这 说明已经没有其他的牌型了
 */
public class DZPK_GaoPaiCardType<T> extends DZPK_CardTypeImpl<T> {
    /**
     * @param mSetPos
     * @param privateCardList 公共牌
     * @return
     */
    @Override
    public boolean resultType(AbsPKSetPos mSetPos, List<Integer> privateCardList) {
        //取最大三张牌
        ArrayList<Integer> sortList = BasePockerLogic.sort(new ArrayList<>(privateCardList), false);
        int size = sortList.size();
        List<Integer> subList = sortList.subList(size - 5, size);
        setCardTypeResult(subList, mSetPos.getPrivateCards());
        return true;
    }

    @Override
    public DZPKRoomEnum.DZPK_CARD_TYPE cardType() {
        return DZPKRoomEnum.DZPK_CARD_TYPE.GAO_PAI;
    }

    public DZPK_GaoPaiCardType<T> clone() {
        return (DZPK_GaoPaiCardType<T>) super.clone();
    }
}
