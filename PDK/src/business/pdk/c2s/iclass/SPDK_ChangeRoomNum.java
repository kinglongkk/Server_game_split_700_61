package business.pdk.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;


public class SPDK_ChangeRoomNum extends BaseSendMsg {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public long roomID;
    public String roomKey;
    public int createType;
    public static SPDK_ChangeRoomNum make(long roomID, String roomKey,int createType) {
    	SPDK_ChangeRoomNum ret = new SPDK_ChangeRoomNum();
        ret.roomID = roomID;
        ret.roomKey = roomKey;
        ret.createType = createType;
        return ret;
    }
}
