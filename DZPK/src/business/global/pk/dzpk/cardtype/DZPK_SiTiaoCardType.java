package business.global.pk.dzpk.cardtype;

import business.global.pk.AbsPKSetPos;
import business.global.pk.dzpk.DZPKRoomEnum;
import business.global.pk.dzpk.base.DZPK_CardTypeImpl;

import java.util.List;

public class DZPK_SiTiaoCardType<T> extends DZPK_CardTypeImpl<T> {
    public DZPK_SiTiaoCardType<T> clone() {
        return (DZPK_SiTiaoCardType<T>) super.clone();
    }

    /**
     * @param mSetPos
     * @param privateCardList 公共牌
     * @return
     */
    @Override
    public boolean resultType(AbsPKSetPos mSetPos, List<Integer> privateCardList) {

        return checkCardType(privateCardList, 4, mSetPos.getPrivateCards());
    }

    @Override
    public DZPKRoomEnum.DZPK_CARD_TYPE cardType() {
        return DZPKRoomEnum.DZPK_CARD_TYPE.SI_TIAO;
    }


}
