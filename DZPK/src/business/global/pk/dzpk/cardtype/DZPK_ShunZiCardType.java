package business.global.pk.dzpk.cardtype;

import business.global.pk.AbsPKSetPos;
import business.global.pk.dzpk.DZPKRoom;
import business.global.pk.dzpk.DZPKRoomEnum;
import business.global.pk.dzpk.base.DZPK_CardTypeImpl;

import java.util.ArrayList;
import java.util.List;

public class DZPK_ShunZiCardType<T> extends DZPK_CardTypeImpl<T> {
    public DZPK_ShunZiCardType<T> clone() {
        return (DZPK_ShunZiCardType<T>) super.clone();
    }
    int cardTypeValue = 0;
    /**
     * @param mSetPos
     * @param privateCardList 公共牌
     * @return
     */
    @Override
    public boolean resultType(AbsPKSetPos mSetPos, List<Integer> privateCardList) {
        ArrayList<ArrayList<Integer>> outList = new ArrayList<>();
        //A在后
        int shunZi = getShunZiEx(outList, (ArrayList<Integer>) privateCardList);
        if (shunZi <= 0) {
            ArrayList<Integer> afirstSunzi = getAatFirst(privateCardList, ((DZPKRoom) mSetPos.getRoom()).isDUAN_PAI());
            shunZi = getShunZiEx(outList, afirstSunzi);
        }
        if (shunZi <= 0) {
            return false;
        }
        addCardTypeResult(outList, mSetPos.getPrivateCards());

        return true;
    }


    @Override
    public DZPKRoomEnum.DZPK_CARD_TYPE cardType() {
        return DZPKRoomEnum.DZPK_CARD_TYPE.SHUN_ZI;
    }

    @Override
    public int cardTypeValue(boolean sanTiaoDa) {
        cardTypeValue = cardType().value();
        if (sanTiaoDa) {
            cardTypeValue = DZPKRoomEnum.DZPK_CARD_TYPE.SAN_TIAO.value();
        }
        return cardTypeValue;
    }

    @Override
    public int cardTypeValue() {
        return cardTypeValue;
    }
}
