package business.global.pk.dzpk.cardtype;

import business.global.pk.AbsPKSetPos;
import business.global.pk.dzpk.DZPKRoomEnum;

import java.util.List;

public class DZPK_KingShunCardType<T> extends DZPK_TongHuaShunCardType<T> {
    public DZPK_KingShunCardType<T> clone() {
        return (DZPK_KingShunCardType<T>) super.clone();
    }

    /**
     * @param mSetPos
     * @param privateCardList 公共牌
     * @return
     */
    @Override
    public boolean resultType(AbsPKSetPos mSetPos, List<Integer> privateCardList) {
        if (super.resultType(mSetPos, privateCardList)) {
            if (getValue() == kingValue) {
                return true;
            }
        }
        return false;
    }

    @Override
    public DZPKRoomEnum.DZPK_CARD_TYPE cardType() {
        return DZPKRoomEnum.DZPK_CARD_TYPE.KING_SHUN;
    }


}
