package business.nn.c2s.iclass;

import cenum.ClassType;
import jsproto.c2s.cclass.BaseSendMsg;


public class SNN_XiPai extends BaseSendMsg {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public long roomID;
    public long pid;
    public ClassType cType;

    public static SNN_XiPai make(long roomID, long pid, ClassType cType) {
        SNN_XiPai ret = new SNN_XiPai();
        ret.roomID = roomID;
        ret.pid = pid;
        ret.cType = cType;
        return ret;


    }
}
