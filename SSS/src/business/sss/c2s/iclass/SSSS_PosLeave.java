package business.sss.c2s.iclass;

import jsproto.c2s.iclass.room.SBase_PosLeave;

public class SSSS_PosLeave extends SBase_PosLeave {

    public static SSSS_PosLeave make(SBase_PosLeave posLeave) {
        SSSS_PosLeave ret = new SSSS_PosLeave();
        ret.setRoomID(posLeave.getRoomID());
        ret.setPos(posLeave.getPos());
        ret.setBeKick(posLeave.isBeKick());
        ret.setOwnerID(posLeave.getOwnerID());
        ret.setKickOutTYpe(posLeave.getKickOutTYpe());
        ret.setMsg(posLeave.getMsg());
        return ret;
    }
}