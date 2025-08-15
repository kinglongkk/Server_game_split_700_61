package business.sss.c2s.iclass;


import jsproto.c2s.cclass.BaseSendMsg;

public class SSSS_Reward extends BaseSendMsg {
    
    public long roomID;
    public long rpid;


    public static SSSS_Reward make(long roomID, long rpid) {
        SSSS_Reward ret = new SSSS_Reward();
        ret.roomID = roomID;
        ret.rpid = rpid;

        return ret;
    

    }
}