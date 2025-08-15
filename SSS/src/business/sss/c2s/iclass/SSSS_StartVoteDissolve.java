package business.sss.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

public class SSSS_StartVoteDissolve extends BaseSendMsg {
    public long roomID;
    public int createPos;
    public int endSec;


    public static SSSS_StartVoteDissolve make(long roomID, int createPos, int endSec) {
    	SSSS_StartVoteDissolve ret = new SSSS_StartVoteDissolve();
        ret.roomID = roomID;
        ret.createPos = createPos;
        ret.endSec = endSec;

        return ret;
    

    }
}
