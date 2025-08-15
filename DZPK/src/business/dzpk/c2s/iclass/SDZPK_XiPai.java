package business.dzpk.c2s.iclass;

import cenum.ClassType;
import jsproto.c2s.cclass.BaseSendMsg;


public class SDZPK_XiPai extends BaseSendMsg {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public long roomID;
    public long pid;
    public ClassType cType;

    public static SDZPK_XiPai make(long roomID, long pid, ClassType cType) {
        SDZPK_XiPai ret = new SDZPK_XiPai();
        ret.roomID = roomID;
        ret.pid = pid;
        ret.cType = cType;
        return ret;


    }
}		
