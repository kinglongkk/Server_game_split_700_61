package business.nn.c2s.iclass;

import jsproto.c2s.cclass.*;

/**
 * 叫庄
 *
 * @author zaf
 */
public class CNN_CallBacker extends BaseSendMsg {

    public long roomID;
    public int pos;
    public int callBackerNum;//抢庄倍数

    public static CNN_CallBacker make(long roomID, int pos, int callBackerNum) {
        CNN_CallBacker ret = new CNN_CallBacker();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.callBackerNum = callBackerNum;
        return ret;


    }
}
