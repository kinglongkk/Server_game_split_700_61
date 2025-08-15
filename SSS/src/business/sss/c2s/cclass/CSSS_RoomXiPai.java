package business.sss.c2s.cclass;

import jsproto.c2s.cclass.BaseSendMsg;

public class CSSS_RoomXiPai extends BaseSendMsg {
    public long roomID;
    //public long pid;

    public static CSSS_RoomXiPai make(long roomID/*, long pid*/) {
        CSSS_RoomXiPai ret = new CSSS_RoomXiPai();
        ret.roomID = roomID;
        //ret.pid = pid;
        return ret;
    }
}
