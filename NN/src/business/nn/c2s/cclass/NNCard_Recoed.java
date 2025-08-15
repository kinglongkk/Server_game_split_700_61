package business.nn.c2s.cclass;

import java.util.ArrayList;
import java.util.List;

import jsproto.c2s.cclass.BaseSendMsg;

/*
 * 记录
 * **/

public class NNCard_Recoed extends BaseSendMsg {
    private List<Byte> cardList = new ArrayList<>(); //最终胡牌的列表
    private int point = 0;//积分
    private int cardType = 0;//牛牛类型

    public List<Byte> getCardList() {
        return cardList;
    }

    public void setCardList(List<Byte> cardList) {
        this.cardList = cardList;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }
}
