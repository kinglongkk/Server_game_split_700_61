package business.pdk.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 接收客户端数据
 * 明牌
 * @author zaf
 *
 */

public class CPDK_OpenCard extends BaseSendMsg {

	public long roomID;
    public int pos;  //位置
    public int  OpenCard;//是否明牌  0:不明牌 1：明牌

    public static CPDK_OpenCard make(long roomID,int pos, int OpenCard) {
    	CPDK_OpenCard ret = new CPDK_OpenCard();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.OpenCard = OpenCard;
        return ret;
    }
}
