package business.dzpk.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;


public class SDZPK_ChangePlayerNumAgree extends BaseSendMsg {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public long roomID;
    public int pos;
    public boolean agreeChange;

    public static SDZPK_ChangePlayerNumAgree make(long roomID, int pos, boolean agreeChange) {
        SDZPK_ChangePlayerNumAgree ret = new SDZPK_ChangePlayerNumAgree();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.agreeChange = agreeChange;
        return ret;


    }
}		
