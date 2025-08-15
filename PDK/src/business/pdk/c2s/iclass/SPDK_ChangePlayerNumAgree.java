package business.pdk.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;


public class SPDK_ChangePlayerNumAgree extends BaseSendMsg {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public long roomID;
    public int pos;
    public boolean agreeChange;
    public static SPDK_ChangePlayerNumAgree make(long roomID, int pos, boolean agreeChange) {
    	SPDK_ChangePlayerNumAgree ret = new SPDK_ChangePlayerNumAgree();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.agreeChange = agreeChange;
        return ret;
    

    }
}
