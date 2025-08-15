package business.sss.c2s.iclass;


import jsproto.c2s.cclass.BaseSendMsg;

public class CSSS_StartGame extends BaseSendMsg {
    
    public long roomID;


    public static CSSS_StartGame make(long roomID) {
        CSSS_StartGame ret = new CSSS_StartGame();
        ret.roomID = roomID;

        return ret;
    

    }
}