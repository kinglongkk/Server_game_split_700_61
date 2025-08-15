package business.sss.c2s.iclass;


import jsproto.c2s.cclass.BaseSendMsg;

public class CSSS_Reward extends BaseSendMsg {
    
    public long roomID;


    public static CSSS_Reward make(long roomID) {
        CSSS_Reward ret = new CSSS_Reward();
        ret.roomID = roomID;

        return ret;
    

    }
}