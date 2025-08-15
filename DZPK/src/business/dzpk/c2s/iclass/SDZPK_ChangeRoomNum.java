package business.dzpk.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;


public class SDZPK_ChangeRoomNum extends BaseSendMsg {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public long roomID;
    public String roomKey;
    public int createType;

    public static SDZPK_ChangeRoomNum make(long roomID, String roomKey, int createType) {
        SDZPK_ChangeRoomNum ret = new SDZPK_ChangeRoomNum();
        ret.roomID = roomID;
        ret.roomKey = roomKey;
        ret.createType = createType;
        return ret;
    }
}		
