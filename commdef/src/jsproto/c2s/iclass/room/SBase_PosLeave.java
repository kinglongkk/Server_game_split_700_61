package jsproto.c2s.iclass.room;

import jsproto.c2s.cclass.BaseSendMsg;
import lombok.Data;

@Data
public class SBase_PosLeave extends BaseSendMsg {

    // 房间ID
    private long roomID;
    // 位置
    private int pos;
    // 是否踢出
    private boolean beKick;
    // 房主
    private long ownerID;
    // 房主或系统操作
    private int kickOutTYpe;
    private String msg;
    public static SBase_PosLeave make(long roomID, int pos, boolean beKick, long ownerID, int kickOutTYpe,String msg) {
        SBase_PosLeave ret = new SBase_PosLeave();
        ret.setRoomID(roomID);
        ret.setPos(pos);
        ret.setBeKick(beKick);
        ret.setOwnerID(ownerID);
        ret.setKickOutTYpe(kickOutTYpe);
        ret.setMsg(msg);
        return ret;
    }


}
