package business.nn.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;


public class SNN_ChangeRoomNum extends BaseSendMsg {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public long roomID;
    public String roomKey;
    public int createType;

    public static SNN_ChangeRoomNum make(long roomID, String roomKey, int createType) {
        SNN_ChangeRoomNum ret = new SNN_ChangeRoomNum();
        ret.roomID = roomID;
        ret.roomKey = roomKey;
        ret.createType = createType;
        return ret;
    }
}
