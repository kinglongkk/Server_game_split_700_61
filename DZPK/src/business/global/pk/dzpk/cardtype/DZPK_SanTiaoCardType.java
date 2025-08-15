package business.global.pk.dzpk.cardtype;

import business.global.pk.AbsPKSetPos;
import business.global.pk.dzpk.DZPKRoomEnum;
import business.global.pk.dzpk.base.DZPK_CardTypeImpl;

import java.util.List;

public class DZPK_SanTiaoCardType<T> extends DZPK_CardTypeImpl<T> {
    int cardTypeValue = 0;

    public DZPK_SanTiaoCardType<T> clone() {
        return (DZPK_SanTiaoCardType<T>) super.clone();
    }

    /**
     * @param mSetPos
     * @param privateCardList 公共牌
     * @return
     */
    @Override
    public boolean resultType(AbsPKSetPos mSetPos, List<Integer> privateCardList) {
        return checkCardType(privateCardList, 3, mSetPos.getPrivateCards());
    }

    @Override
    public DZPKRoomEnum.DZPK_CARD_TYPE cardType() {
        return DZPKRoomEnum.DZPK_CARD_TYPE.SAN_TIAO;
    }

    @Override
    public int cardTypeValue(boolean sanTiaoDa) {
        cardTypeValue = cardType().value();
        if (sanTiaoDa) {
            cardTypeValue = DZPKRoomEnum.DZPK_CARD_TYPE.SHUN_ZI.value();
        }
        return cardType().value();
    }

    @Override
    public int cardTypeValue() {
        return cardTypeValue;
    }
}
