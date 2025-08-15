package business.sss.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

public class SSSS_LostConnect extends BaseSendMsg {
    
    public long roomID;
    public long pid;
    public boolean isLostConnect;
    public boolean isShowLeave;

    public static SSSS_LostConnect make(long roomID, long pid, boolean isLostConnect,boolean isShowLeave) {
    	SSSS_LostConnect ret = new SSSS_LostConnect();
        ret.roomID = roomID;
        ret.pid = pid;
        ret.isLostConnect = isLostConnect;
        ret.isShowLeave = isShowLeave;

        return ret;
    

    }
}