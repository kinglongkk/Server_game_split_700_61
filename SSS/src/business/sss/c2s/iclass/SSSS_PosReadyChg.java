package business.sss.c2s.iclass;


import jsproto.c2s.cclass.BaseSendMsg;

public class SSSS_PosReadyChg extends BaseSendMsg {
    
    public long roomID;
    public int pos;
    public boolean isReady;


    public static SSSS_PosReadyChg make(long roomID, int pos, boolean isReady) {
        SSSS_PosReadyChg ret = new SSSS_PosReadyChg();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.isReady = isReady;

        return ret;
    

    }
}