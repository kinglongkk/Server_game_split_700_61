package business.sss.c2s.iclass;


import jsproto.c2s.cclass.BaseSendMsg;

public class CSSS_UnReadyRoom extends BaseSendMsg {
    
    public long roomID;
    public int posIndex;


    public static CSSS_UnReadyRoom make(long roomID, int posIndex) {
        CSSS_UnReadyRoom ret = new CSSS_UnReadyRoom();
        ret.roomID = roomID;
        ret.posIndex = posIndex;

        return ret;
    

    }
}