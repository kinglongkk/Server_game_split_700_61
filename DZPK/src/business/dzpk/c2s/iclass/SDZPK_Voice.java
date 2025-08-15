package business.dzpk.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 语音通知
 *
 * @author Administrator
 */
@SuppressWarnings("serial")
public class SDZPK_Voice extends BaseSendMsg {
    // 房间ID	
    private long roomID;
    // 位置	
    private int pos;
    // 语音地址	
    private String url;

    public static SDZPK_Voice make(long roomID, int pos, String url) {
        SDZPK_Voice ret = new SDZPK_Voice();
        ret.setRoomID(roomID);
        ret.setPos(pos);
        ret.setUrl(url);
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}		
