package business.dzpk.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 客户端请求加 加积分
 */
public class CDZPK_JiFen extends BaseSendMsg {
    /**
     * 房间
     */
    public long roomID;
    /**
     * 积分
     */
    public int point;

    public static CDZPK_JiFen make(long roomID, int point) {
        CDZPK_JiFen ret = new CDZPK_JiFen();
        ret.roomID = roomID;
        ret.point = point;
        return ret;
    }
}		
