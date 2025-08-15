package business.dzpk.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 上局下注情况
 *
 * @author Administrator
 */
public class CDZPK_LastBetInfo extends BaseSendMsg {
    // 房间ID
    private long roomID;

    public static CDZPK_LastBetInfo make(long roomID) {
        CDZPK_LastBetInfo ret = new CDZPK_LastBetInfo();
        ret.setRoomID(roomID);
        return ret;
    }

    public long getRoomID() {
        return roomID;
    }

    public void setRoomID(long roomID) {
        this.roomID = roomID;
    }


}