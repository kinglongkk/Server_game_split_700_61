package business.qzmj.c2s.iclass;

import cenum.ClassType;
import jsproto.c2s.cclass.BaseSendMsg;


public class SQZMJ_XiPai extends BaseSendMsg {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public long roomID;
    public long pid;
    public ClassType cType;
    public static SQZMJ_XiPai make(long roomID, long pid, ClassType cType) {
    	SQZMJ_XiPai ret = new SQZMJ_XiPai();
        ret.roomID = roomID;
        ret.pid = pid;
        ret.cType = cType;
        return ret;
    

    }
}