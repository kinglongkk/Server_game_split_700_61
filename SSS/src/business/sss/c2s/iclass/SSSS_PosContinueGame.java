package business.sss.c2s.iclass;


import jsproto.c2s.cclass.BaseSendMsg;

public class SSSS_PosContinueGame extends BaseSendMsg {
    
    public long roomID;
    public int pos;


    public static SSSS_PosContinueGame make(long roomID, int pos) {
        SSSS_PosContinueGame ret = new SSSS_PosContinueGame();
        ret.roomID = roomID;
        ret.pos = pos;

        return ret;
    

    }
}