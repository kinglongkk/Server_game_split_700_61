package business.nn.c2s.iclass;

import java.util.ArrayList;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 接收客户端数据
 * 加倍
 *
 * @author zaf
 */

public class CNN_OpCard extends BaseSendMsg {

    public long roomID;
    public int pos;  //位置
    public int opCardType;  //PDK_CARD_TYPE 操作类型及牌的类型
    public ArrayList<Integer> cardList;
    public int daiNum;//带几张牌
    public boolean isFlash = false;//是否自动打牌

    public static CNN_OpCard make(long roomID, int pos, int opCardType, ArrayList<Integer> cardList, int daiNum, boolean isFlash) {
        CNN_OpCard ret = new CNN_OpCard();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.opCardType = opCardType;
        ret.cardList = cardList;
        ret.daiNum = daiNum;
        ret.isFlash = isFlash;
        return ret;
    }
}
