package business.pdk.c2s.iclass;

import java.util.List;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 接收客户端数据
 * 明牌
 * @author zaf
 *
 */

public class SPDK_OpenCard extends BaseSendMsg {

	public long roomID;
    public int pos;  //位置
    public int  OpenCard;//是否明牌  0:不明牌 1：明牌
    public List<Integer> cardList;

    public static SPDK_OpenCard make(long roomID,int pos, int  OpenCard, List<Integer> cardList) {
    	SPDK_OpenCard ret = new SPDK_OpenCard();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.OpenCard = OpenCard;
        ret.cardList = cardList;
        return ret;
    }
}
