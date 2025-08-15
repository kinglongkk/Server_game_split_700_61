package business.pdk.c2s.iclass;

import cenum.ClassType;
import jsproto.c2s.cclass.BaseSendMsg;


public class SPDK_XiPai extends BaseSendMsg {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public long roomID;
    public long pid;
    public ClassType cType;
    public static SPDK_XiPai make(long roomID, long pid,ClassType cType) {
    	SPDK_XiPai ret = new SPDK_XiPai();
        ret.roomID = roomID;
        ret.pid = pid;
        ret.cType = cType;
        return ret;
    

    }
}
