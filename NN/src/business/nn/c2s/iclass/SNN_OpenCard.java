package business.nn.c2s.iclass;

import java.util.List;

import jsproto.c2s.cclass.*;

/**
 * 亮牌
 *
 * @author zaf
 */
public class SNN_OpenCard extends BaseSendMsg {

    public long roomID;
    public int pos;
    public boolean isSelectCard;//是否客户端自己选择牌
    public List<Integer> cardList; //后两张为牛几

    public static SNN_OpenCard make(long roomID, int pos, boolean isSelectCard, List<Integer> cardList) {
        SNN_OpenCard ret = new SNN_OpenCard();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.isSelectCard = isSelectCard;
        ret.cardList = cardList;
        return ret;
    }
}
