package business.nn.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 位置准备通知
 *
 * @author Administrator
 */
@SuppressWarnings("serial")
public class SNN_PosReadyChg extends BaseSendMsg {
    // 房间ID
    private long roomID;
    // 位置
    private int pos;
    // T:准备，F:取消准备
    private boolean isReady;

    public static SNN_PosReadyChg make(long roomID, int pos, boolean isReady) {
        SNN_PosReadyChg ret = new SNN_PosReadyChg();
        ret.setRoomID(roomID);
        ret.setPos(pos);
        ret.setReady(isReady);
        return ret;
    }

    public long getRoomID() {
        return roomID;
    }

    public void setRoomID(long roomID) {
        this.roomID = roomID;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }

}
