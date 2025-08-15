package business.dzpk.c2s.iclass;

import jsproto.c2s.iclass.room.SBase_PosLeave;

/**
 * 位置离开通知
 *
 * @author Administrator
 */
@SuppressWarnings("serial")
public class SDZPK_PosLeave extends SBase_PosLeave {

    public static SDZPK_PosLeave make(SBase_PosLeave posLeave) {
        SDZPK_PosLeave ret = new SDZPK_PosLeave();
        ret.setRoomID(posLeave.getRoomID());
        ret.setPos(posLeave.getPos());
        ret.setBeKick(posLeave.isBeKick());
        ret.setOwnerID(posLeave.getOwnerID());
        ret.setKickOutTYpe(posLeave.getKickOutTYpe());
        ret.setMsg(posLeave.getMsg());
        return ret;
    }
}		
