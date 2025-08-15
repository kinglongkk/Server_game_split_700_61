package business.global.pk.dzpk.cardtype;

import business.global.pk.AbsPKSetPos;
import business.global.pk.dzpk.DZPKRoom;
import business.global.pk.dzpk.DZPKRoomEnum;
import business.global.pk.dzpk.base.DZPK_CardTypeImpl;

import java.util.ArrayList;
import java.util.List;

public class DZPK_TongHuaShunCardType<T> extends DZPK_CardTypeImpl<T> {
    public DZPK_TongHuaShunCardType<T> clone() {
        return (DZPK_TongHuaShunCardType<T>) super.clone();
    }

    /**
     * @param mSetPos
     * @param privateCardList 公共牌
     * @return
     */
    @Override
    public boolean resultType(AbsPKSetPos mSetPos, List<Integer> privateCardList) {
        ArrayList<ArrayList<Integer>> outList = new ArrayList<>();
        int count = getTongHuaShun(outList, new ArrayList<>(privateCardList));

        if (count <= 0) {
            ArrayList<Integer> afirstSunzi = getAatFirst(privateCardList, ((DZPKRoom) mSetPos.getRoom()).isDUAN_PAI());
            count = getTongHuaShun(outList, afirstSunzi);
        }
        if (count <= 0) {
            return false;
        }
        addCardTypeResult(outList, mSetPos.getPrivateCards());

        return true;
    }

    @Override
    public DZPKRoomEnum.DZPK_CARD_TYPE cardType() {
        return DZPKRoomEnum.DZPK_CARD_TYPE.TONG_HUA_SHUN;
    }

}
