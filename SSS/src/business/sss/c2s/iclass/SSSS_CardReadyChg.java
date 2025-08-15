package business.sss.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

public class SSSS_CardReadyChg  extends BaseSendMsg {
    public long roomID;
    public int pos;
    public boolean isReady;


    public static SSSS_CardReadyChg make(long roomID, int pos, boolean isReady) {
    	SSSS_CardReadyChg ret = new SSSS_CardReadyChg();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.isReady = isReady;

        return ret;
    

    }
}
