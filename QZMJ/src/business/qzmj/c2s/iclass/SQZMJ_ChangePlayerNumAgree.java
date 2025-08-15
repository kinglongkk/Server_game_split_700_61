package business.qzmj.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;


public class SQZMJ_ChangePlayerNumAgree extends BaseSendMsg {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public long roomID;
    public int pos;
    public boolean agreeChange;
    public static SQZMJ_ChangePlayerNumAgree make(long roomID, int pos, boolean agreeChange) {
    	SQZMJ_ChangePlayerNumAgree ret = new SQZMJ_ChangePlayerNumAgree();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.agreeChange = agreeChange;
        return ret;
    

    }
}