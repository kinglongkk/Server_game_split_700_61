package business.global.pk.dzpk.cardtype;

import business.global.pk.AbsPKSetPos;
import business.global.pk.dzpk.DZPKRoomEnum;
import business.global.pk.dzpk.base.DZPK_CardTypeImpl;
import jsproto.c2s.cclass.pk.BasePockerLogic;

import java.util.*;
import java.util.stream.Collectors;

public class DZPK_LiangDuiCardType<T> extends DZPK_CardTypeImpl<T> {
    public DZPK_LiangDuiCardType<T> clone() {
        return (DZPK_LiangDuiCardType<T>) super.clone();
    }

    /**
     * @param mSetPos
     * @param privateCardList 公共牌
     * @return
     */
    @Override
    public boolean resultType(AbsPKSetPos mSetPos, List<Integer> privateCardList) {
        Map<Integer, List<Integer>> valueMap =
                privateCardList.stream().collect(Collectors.groupingBy(k -> BasePockerLogic.getCardValue(k)));
        ArrayList<Integer> keys = new ArrayList<>();
        ArrayList<Integer> newSets = new ArrayList<>();
        List<Integer> newList = new ArrayList<>();
        for (int key : valueMap.keySet()) {
            List<Integer> list = valueMap.get(key);
            if (list.size() == 2) {
                newSets.add(key);
            } else if (list.size() == 1) {
                keys.addAll(list);
            } else {
                return false;
            }
        }
        if (newSets.size() < 2) {
            return false;
        }
        //从中找出两对
        ArrayList<Integer> sortSet = BasePockerLogic.sort(newSets, false);
        Collections.sort(sortSet, new Comparator<Integer>() {

            @Override
            public int compare(Integer o1, Integer o2) {
                // 返回值为int类型，大于0表示正序，小于0表示逆序
                return o2 - o1;
            }
        });
        for (int key : sortSet) {
            newList.addAll(valueMap.get(key));
            if (newList.size() == 4) {
                break;
            }
        }

        ArrayList<Integer> sort = BasePockerLogic.sort(keys, false);
        newList.addAll(sort.subList(0, 1));
        if (newList.size() != 5) {
            return false;
        }
        setCardTypeResult(newList, mSetPos.getPrivateCards());
        return true;
    }

    @Override
    public DZPKRoomEnum.DZPK_CARD_TYPE cardType() {
        return DZPKRoomEnum.DZPK_CARD_TYPE.LIANG_DUI;
    }


}
