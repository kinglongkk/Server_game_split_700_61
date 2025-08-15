package business.global.pk.dzpk.cardtype;

import business.global.pk.AbsPKSetPos;
import business.global.pk.dzpk.DZPKRoomEnum;
import business.global.pk.dzpk.base.DZPK_CardTypeImpl;

import java.util.List;

public class DZPK_YiDuiCardType<T> extends DZPK_CardTypeImpl<T> {
    public DZPK_YiDuiCardType<T> clone() {
        return (DZPK_YiDuiCardType<T>) super.clone();
    }

    /**
     * @param mSetPos
     * @param privateCardList 公共牌
     * @return
     */
    @Override
    public boolean resultType(AbsPKSetPos mSetPos, List<Integer> privateCardList) {
        return checkCardType(privateCardList, 2, mSetPos.getPrivateCards());
    }


    @Override
    public DZPKRoomEnum.DZPK_CARD_TYPE cardType() {
        return DZPKRoomEnum.DZPK_CARD_TYPE.DAN_DUI;
    }


}
