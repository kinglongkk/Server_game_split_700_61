package business.sss.c2s.iclass;


import jsproto.c2s.cclass.BaseSendMsg;

public class CSSS_ReadyRoom extends BaseSendMsg {
    
    public long roomID;
    public int posIndex;


    public static CSSS_ReadyRoom make(long roomID, int posIndex) {
        CSSS_ReadyRoom ret = new CSSS_ReadyRoom();
        ret.roomID = roomID;
        ret.posIndex = posIndex;

        return ret;
    

    }
}