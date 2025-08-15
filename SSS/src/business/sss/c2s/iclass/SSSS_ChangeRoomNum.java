package business.sss.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

public class SSSS_ChangeRoomNum extends BaseSendMsg {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public long roomID;
    public String roomKey;
    public int createType;
    public static SSSS_ChangeRoomNum make(long roomID, String roomKey,int createType) {
        SSSS_ChangeRoomNum ret = new SSSS_ChangeRoomNum();
        ret.roomID = roomID;
        ret.roomKey = roomKey;
        ret.createType = createType;
        return ret;
    }
}
