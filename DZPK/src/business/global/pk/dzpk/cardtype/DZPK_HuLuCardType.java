package business.global.pk.dzpk.cardtype;

import business.global.pk.AbsPKSetPos;
import business.global.pk.dzpk.DZPKRoomEnum;
import business.global.pk.dzpk.base.DZPK_CardTypeImpl;
import jsproto.c2s.cclass.pk.BasePockerLogic;

import java.util.*;
import java.util.stream.Collectors;

public class DZPK_HuLuCardType<T> extends DZPK_CardTypeImpl<T> {
    public DZPK_HuLuCardType<T> clone() {
        return (DZPK_HuLuCardType<T>) super.clone();
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
        ArrayList<Integer> key2s = new ArrayList<>();
        ArrayList<Integer> key3s = new ArrayList<>();
        ArrayList<Integer> newList = new ArrayList<>();
        for (int key : valueMap.keySet()) {
            List<Integer> list = valueMap.get(key);
            if (list.size() == 3) {
                key3s.add(key);
            } else if (list.size() == 2) {
                key2s.add(key);
            }
        }
        if (key2s.size() < 1 || key3s.size() < 1) {
            return false;
        }
        Collections.sort(key3s, new Comparator<Integer>() {

            @Override
            public int compare(Integer o1, Integer o2) {
                // 返回值为int类型，大于0表示正序，小于0表示逆序
                return o2 - o1;
            }
        });

        Integer Value3 = key3s.remove(0);
        newList.addAll(valueMap.get(Value3));
        if (key3s.size() > 0) {
            key2s.addAll(key3s);
        }
        Collections.sort(key2s, new Comparator<Integer>() {

            @Override
            public int compare(Integer o1, Integer o2) {
                // 返回值为int类型，大于0表示正序，小于0表示逆序
                return o2 - o1;
            }
        });
        List<Integer> list2 = valueMap.get(key2s.get(0));
        newList.addAll(list2.subList(0, 2));
        setCardTypeResult(newList, mSetPos.getPrivateCards());
        return true;
    }

    @Override
    public DZPKRoomEnum.DZPK_CARD_TYPE cardType() {
        return DZPKRoomEnum.DZPK_CARD_TYPE.HU_LU;
    }


}
