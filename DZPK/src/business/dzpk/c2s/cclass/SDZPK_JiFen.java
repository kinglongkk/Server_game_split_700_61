package business.dzpk.c2s.cclass;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 服务端返回 加积分
 */
public class SDZPK_JiFen extends BaseSendMsg {

    /**
     * 房间
     */
    public long roomID;
    /**
     * 位置
     */
    public int pos;
    /**
     * 积分
     */
    public int point;

    public static SDZPK_JiFen make(long roomID, int pos, int jiFen) {
        SDZPK_JiFen ret = new SDZPK_JiFen();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.point = jiFen;

        return ret;


    }
}		
