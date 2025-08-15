package business.global.pk.dzpk.cardtype;

import business.global.pk.AbsPKSetPos;
import business.global.pk.dzpk.DZPKRoomEnum;
import business.global.pk.dzpk.base.DZPK_CardTypeImpl;

import java.util.ArrayList;
import java.util.List;

public class DZPK_TongHuaCardType<T> extends DZPK_CardTypeImpl<T> {
    public DZPK_TongHuaCardType<T> clone() {
        return (DZPK_TongHuaCardType<T>) super.clone();
    }

    /**
     * @param mSetPos
     * @param privateCardList 公共牌
     * @return
     */
    @Override
    public boolean resultType(AbsPKSetPos mSetPos, List<Integer> privateCardList) {
        ArrayList<ArrayList<Integer>> outList = new ArrayList<>();
        int count = getTongHua(outList, new ArrayList<>(privateCardList));
        if (count <= 0) {
            return false;
        }
        return addCardTypeResult(outList, mSetPos.getPrivateCards());

    }

    @Override
    public DZPKRoomEnum.DZPK_CARD_TYPE cardType() {
        return DZPKRoomEnum.DZPK_CARD_TYPE.TONG_HUA;
    }

}
