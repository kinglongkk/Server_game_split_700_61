package business.sss.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

public class SSSS_PosDealVote extends BaseSendMsg{
    
    public long roomID;
    public int pos;
    public boolean agreeDissolve;


    public static SSSS_PosDealVote make(long roomID, int pos, boolean agreeDissolve) {
    	SSSS_PosDealVote ret = new SSSS_PosDealVote();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.agreeDissolve = agreeDissolve;

        return ret;
    

    }
}