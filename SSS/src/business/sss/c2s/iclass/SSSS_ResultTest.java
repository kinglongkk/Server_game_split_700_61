package business.sss.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;


public class SSSS_ResultTest extends BaseSendMsg {
    public long roomID;

    
    public static SSSS_ResultTest make(long roomID) {
    	SSSS_ResultTest ret = new SSSS_ResultTest();
        ret.roomID = roomID;
     
        return ret;
    }
}
