package business.nn.c2s.iclass;

import jsproto.c2s.cclass.*;

/**
 * 叫庄
 *
 * @author zaf
 */
public class SNN_CallBacker extends BaseSendMsg {

    public long roomID;
    public int pos;
    public int callBackerNum;//抢庄倍数

    public static SNN_CallBacker make(long roomID, int pos, int callBackerNum) {
        SNN_CallBacker ret = new SNN_CallBacker();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.callBackerNum = callBackerNum;
        return ret;
    }
}
