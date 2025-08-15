package business.sss.c2s.iclass;


import jsproto.c2s.cclass.BaseSendMsg;

public class CSSS_ExitRoom extends BaseSendMsg {
    
    public long roomID;
    public int posIndex;

    public static CSSS_ExitRoom make(long roomID, int posIndex) {
        CSSS_ExitRoom ret = new CSSS_ExitRoom();
        ret.roomID = roomID;
        ret.posIndex = posIndex;

        return ret;
    

    }
}