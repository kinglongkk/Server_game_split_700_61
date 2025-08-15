package business.sss.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

public class SSSS_ChangePlayerNumAgree extends BaseSendMsg {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public long roomID;
    public int pos;
    public boolean agreeChange;
    public static SSSS_ChangePlayerNumAgree make(long roomID, int pos, boolean agreeChange) {
        SSSS_ChangePlayerNumAgree ret = new SSSS_ChangePlayerNumAgree();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.agreeChange = agreeChange;
        return ret;


    }
}