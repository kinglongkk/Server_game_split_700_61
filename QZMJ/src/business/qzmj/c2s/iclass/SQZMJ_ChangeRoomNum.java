package business.qzmj.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;


public class SQZMJ_ChangeRoomNum extends BaseSendMsg {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public long roomID;
    public String roomKey;
    public int createType;
    public static SQZMJ_ChangeRoomNum make(long roomID, String roomKey, int createType) {
    	SQZMJ_ChangeRoomNum ret = new SQZMJ_ChangeRoomNum();
        ret.roomID = roomID;
        ret.roomKey = roomKey;
        ret.createType = createType;
        return ret;
    }
}