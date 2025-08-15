package business.nn.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;


public class SNN_ChangePlayerNumAgree extends BaseSendMsg {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public long roomID;
    public int pos;
    public boolean agreeChange;

    public static SNN_ChangePlayerNumAgree make(long roomID, int pos, boolean agreeChange) {
        SNN_ChangePlayerNumAgree ret = new SNN_ChangePlayerNumAgree();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.agreeChange = agreeChange;
        return ret;


    }
}
