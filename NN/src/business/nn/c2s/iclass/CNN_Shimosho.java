package business.nn.c2s.iclass;

import jsproto.c2s.cclass.*;

/**
 * 下庄
 *
 * @author zaf
 */
public class CNN_Shimosho extends BaseSendMsg {

    public long roomID;
    public int pos;

    public static CNN_Shimosho make(long roomID, int pos) {
        CNN_Shimosho ret = new CNN_Shimosho();
        ret.roomID = roomID;
        ret.pos = pos;
        return ret;
    }
}
