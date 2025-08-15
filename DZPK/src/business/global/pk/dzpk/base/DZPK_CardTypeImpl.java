package business.global.pk.dzpk.base;

import business.global.pk.AbsPKSetPos;
import business.global.pk.CardType;
import business.global.pk.PKCurOutCardInfo;
import business.global.pk.PKOpCard;
import business.global.pk.dzpk.DZPKRoom;
import business.global.pk.dzpk.DZPKRoomEnum;
import com.ddm.server.common.CommLogD;
import jsproto.c2s.cclass.pk.BasePockerLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Data
public abstract class DZPK_CardTypeImpl<T> implements CardType<T>, Cloneable, Serializable {
    /**
     * 权值  用于比大小
     */
    protected long value;

    protected long diPaiValue;
    protected static long kingValue = 1001413121110L;
    ;
    /**
     * 重组后的牌
     */
    protected List<Integer> cardList = new ArrayList<>();
    /**
     * 重组后拿到的公共牌
     */
    protected List<Integer> myCommonCardList = new ArrayList<>();


    public int cardListSize = 5;

    /**
     * 对象之间的浅克隆【只负责copy对象本身，不负责深度copy其内嵌的成员对象】
     *
     * @return
     */

    public DZPK_CardTypeImpl<T> clone() {
        try {
            return (DZPK_CardTypeImpl<T>) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 最佳牌型
     *
     * @return
     */
    public abstract DZPKRoomEnum.DZPK_CARD_TYPE cardType();

    public int cardTypeValue() {
        return cardType().value();
    }

    public int cardTypeValue(boolean sanTiaoDa) {
        return cardType().value();
    }


    @Override
    public boolean resultType(AbsPKSetPos mSetPos, List<Integer> privateCardList) {
        return false;
    }

    @Override
    public boolean resultType(AbsPKSetPos mSetPos, List<Integer> privateCardList, PKOpCard opCard) {
        return false;
    }

    @Override
    public PKOpCard robotResultType(AbsPKSetPos mSetPos, PKCurOutCardInfo curOutCardInfo, T item) {
        return null;
    }

    /**
     * @param newCardList
     */
    public void setCardTypeResult(List<Integer> newCardList, List<Integer> privateCardLis) {
        setCardTypeResult(newCardList, 0, privateCardLis);
    }

    /**
     * @param newCardList
     * @param changeCardValue 0 不换 1 ：A-1 2：A-5
     */
    public void setCardTypeResult(List<Integer> newCardList, int changeCardValue, List<Integer> privateCardList) {
        if (newCardList.size() != 5) {
            CommLogD.error("setCardTypeResult error " + newCardList);
            return;
        }
        this.cardList.clear();
        if (changeCardValue > 0) {
            newCardList = resetAatFirst(newCardList, changeCardValue == 2);
        }
        this.cardList.addAll(newCardList);
        setValue(getValue(newCardList, false));
        newCardList.removeAll(privateCardList);
        setMyCommonCardList(newCardList);
        setDiPaiValue(getValue(privateCardList, true));
    }

    protected long getValue(List<Integer> newCardList, boolean dipai) {

        Collections.sort(newCardList, new Comparator<Integer>() {

            @Override
            public int compare(Integer o1, Integer o2) {
                // 返回值为int类型，大于0表示正序，小于0表示逆序
                return BasePockerLogic.getCardValue(o2) - BasePockerLogic.getCardValue(o1);
            }
        });
        long reValue = 0;
        //六位数 牌型值 + 牌值
        long multiple = dipai ? 0 : 100000000000L;
        reValue = cardTypeValue() * multiple;
        for (int card : newCardList) {
            multiple /= 100;
            reValue += BasePockerLogic.getCardValue(card) * multiple / 10;
        }
        return reValue;
    }

    /**
     * newCardList
     *
     * @param allCardList
     * @param privateCards
     */
    public boolean addCardTypeResult(ArrayList<ArrayList<Integer>> allCardList, ArrayList<Integer> privateCards) {

        List<Integer> biggerList = new ArrayList<>();
        long maxValue = 0;
        for (List<Integer> cardList : allCardList) {
            long cardVale = getValue(cardList, false);
            if (biggerList.isEmpty()) {
                biggerList = cardList;
                maxValue = cardVale;
            } else if (maxValue < cardVale) {
                maxValue = cardVale;
                biggerList = cardList;
            }
        }
        if (maxValue > 0) {
            setCardTypeResult(biggerList, privateCards);
            return true;
        }
        return false;
    }

    public boolean checkCardType(
            List<Integer> privateCardList, int cardCount, ArrayList<Integer> privateCards) {
        Map<Integer, List<Integer>> valueMap =
                privateCardList.stream().collect(Collectors.groupingBy(k -> BasePockerLogic.getCardValue(k)));
        ArrayList<Integer> keys = new ArrayList<>();
        ArrayList<Integer> newSets = new ArrayList<>();
        for (int key : valueMap.keySet()) {
            List<Integer> list = valueMap.get(key);
            if (list.size() == cardCount) {
                newSets.add(key);
            } else if (list.size() == 1) {
                keys.addAll(list);
            } else {
                return false;
            }
        }

        if (newSets.size() < 1) {
            return false;
        }
        ArrayList<Integer> newList = new ArrayList<>();
        //从中找出两对
        ArrayList<Integer> sortSet = BasePockerLogic.sort(newSets, false);
        Collections.sort(sortSet, new Comparator<Integer>() {

            @Override
            public int compare(Integer o1, Integer o2) {
                // 返回值为int类型，大于0表示正序，小于0表示逆序
                return o2 - o1;
            }
        });
        Integer remove = newSets.remove(0);
        newList.addAll(valueMap.get(remove));
        for (int key : newSets) {
            keys.addAll(valueMap.get(key));
        }
        ArrayList<Integer> sort = BasePockerLogic.sort(keys, false);
        newList.addAll(sort.subList(0, 5 - newList.size()));
        if (newList.size() != 5) {
            return false;
        }
        setCardTypeResult(newList, privateCards);
        return true;
    }

    /**
     * @param privateCardList
     * @param duan_pai
     * @return
     */
    protected ArrayList<Integer> getAatFirst(List<Integer> privateCardList, boolean duan_pai) {
        //A在后
        ArrayList<Integer> newCardList = new ArrayList<>();
        boolean have = false;
        int newCard = 0;
        for (int card : privateCardList) {
            if (BasePockerLogic.getCardValue(card) == 14) {
                newCard = duan_pai ? card - 9 : card - 13;
                newCardList.add(newCard);
                have = true;
            } else {
                newCardList.add(card);
            }
        }
        if (have) {
            return newCardList;
        }
        return (ArrayList<Integer>) privateCardList;
    }

    /**
     * @param privateCardList
     * @param duan_pai
     * @return
     */
    protected ArrayList<Integer> resetAatFirst(List<Integer> privateCardList, boolean duan_pai) {
        //A在后
        ArrayList<Integer> newCardList = new ArrayList<>();
        ArrayList<ArrayList<Integer>> outList = new ArrayList<>();
        boolean have = false;
        int newCard = 0;
        int v = duan_pai ? 5 : 1;
        int key = duan_pai ? 9 : 13;
        for (int card : privateCardList) {
            if (BasePockerLogic.getCardValue(card) == v) {
                newCard = card + key;
                newCardList.add(newCard);
                have = true;
            } else {
                newCardList.add(card);
            }
        }
        if (have) {
            return newCardList;
        }
        return null;
    }

    protected ArrayList<Integer> findAfirstSunzi(AbsPKSetPos mSetPos, List<Integer> privateCardList) {
        ArrayList<ArrayList<Integer>> outList = new ArrayList<>();
        int shunZi = 0;
        List<Integer> aatFirst = getAatFirst(privateCardList, ((DZPKRoom) mSetPos.getRoom()).isDUAN_PAI());
        if (aatFirst != null) {
            shunZi = BasePockerLogic.getShunZiEx(outList, new ArrayList<>(aatFirst), 5);
        }
        if (shunZi > 0) {
            return outList.get(0);
        }
        return new ArrayList<>();
    }

    /**
     * 获取同花顺子
     * inList 传入数组
     * outList 返回的对子数组
     * return 数量
     **/
    public int getTongHuaShun(ArrayList<ArrayList<Integer>> outList, ArrayList<Integer> inList) {
        ArrayList<ArrayList<Integer>> lists = new ArrayList<>();
        int shunZiEx = getShunZiEx(lists, inList);
        if (shunZiEx > 0) {
            for (List<Integer> list : lists) {
                getTongHua(outList, new ArrayList<>(list));
            }
        }
        return outList.size();
    }

    /**
     * 获取同花
     * inList 传入数组
     * outList 返回的对子数组
     * return 数量
     **/
    public int getTongHua(ArrayList<ArrayList<Integer>> outList, ArrayList<Integer> inList) {
        Map<Integer, List<Integer>> colorMap = inList.stream().collect(Collectors.groupingBy(k -> BasePockerLogic.getCardColor(k)));
        for (List<Integer> tonghuaList : colorMap.values()) {
            if (tonghuaList.size() >= 5) {
                ArrayList<Integer> valueList = BasePockerLogic.sort((ArrayList<Integer>) tonghuaList, true);
                CommLogD.info(valueList.toString());
                int size = valueList.size();
                List<Integer> list = valueList.subList(size - 5, size);
                outList.add(new ArrayList<>(list));
            }
        }
        return outList.size();
    }

    /**
     * 获取顺子  不重复 固定长度
     * inList 传入数组
     * outList 返回的对子数组
     * return 数量
     **/
    public int getShunZiEx(ArrayList<ArrayList<Integer>> outList, ArrayList<Integer> inList) {

        ArrayList<Integer> valueList = BasePockerLogic.sort(inList, false);
        if (valueList.size() < 5) {
            return 0;
        }
        int cardvalue = 0;
        for (int i = 0; i <= valueList.size() - cardListSize; i++) {
            ArrayList<Integer> temp = new ArrayList<Integer>(cardListSize);
            temp.add(valueList.get(i));
            cardvalue = getCardValue(temp.get(0));
            for (int j = i; j < valueList.size(); j++) {
                if (getCardValue(valueList.get(j)) - cardvalue != 1) {
                    continue;
                }
                temp.add(valueList.get(j));
                cardvalue++;
                if (temp.size() == cardListSize) {
                    break;
                }
            }
            if (temp.size() != cardListSize) {
                continue;
            }
            outList.add(temp);

        }
        return outList.size();
    }

    protected static int getCardValue(Integer card) {
        return BasePockerLogic.getCardValue(card);
    }

    protected static int getCardColor(Integer card) {
        return BasePockerLogic.getCardColor(card);
    }
}
