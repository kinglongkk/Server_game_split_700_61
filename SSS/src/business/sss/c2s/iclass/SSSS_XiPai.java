package business.sss.c2s.iclass;

import cenum.ClassType;
import jsproto.c2s.cclass.BaseSendMsg;

public class SSSS_XiPai extends BaseSendMsg {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public long roomID;
    public long pid;
    public ClassType cType;
    public static SSSS_XiPai make(long roomID, long pid,ClassType cType) {
        SSSS_XiPai ret = new SSSS_XiPai();
        ret.roomID = roomID;
        ret.pid = pid;
        ret.cType = cType;
        return ret;


    }
}