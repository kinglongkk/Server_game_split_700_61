package business.nn.c2s.iclass;

import java.util.ArrayList;
import java.util.List;

import jsproto.c2s.cclass.*;

/**
 * 亮牌
 *
 * @author zaf
 */
public class CNN_OpenCard extends BaseSendMsg {

    public long roomID;
    public int pos;
    public boolean isSelectCard;//是否客户端自己选择牌
    public ArrayList<Integer> cardList; //后两张为牛几

    public static CNN_OpenCard make(long roomID, int pos, boolean isSelectCard, ArrayList<Integer> cardList) {
        CNN_OpenCard ret = new CNN_OpenCard();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.isSelectCard = isSelectCard;
        ret.cardList = cardList;
        return ret;
    }
}
