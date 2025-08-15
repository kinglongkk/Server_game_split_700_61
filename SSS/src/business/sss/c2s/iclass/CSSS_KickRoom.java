package business.sss.c2s.iclass;


import jsproto.c2s.cclass.BaseSendMsg;

public class CSSS_KickRoom extends BaseSendMsg {
    
    public long roomID;
    public int posIndex;


    public static CSSS_KickRoom make(long roomID, int posIndex) {
        CSSS_KickRoom ret = new CSSS_KickRoom();
        ret.roomID = roomID;
        ret.posIndex = posIndex;

        return ret;
    

    }
}