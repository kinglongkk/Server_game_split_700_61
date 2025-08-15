package business.dzpk.c2s.iclass;


import cenum.room.SetState;
import jsproto.c2s.cclass.BaseSendMsg;

public class SDZPK_ChangeStatus extends BaseSendMsg {
    public long roomID;
    public SetState state;

    public static SDZPK_ChangeStatus make(long roomID, SetState state) {
        SDZPK_ChangeStatus ret = new SDZPK_ChangeStatus();
        ret.roomID = roomID;
        ret.state = state;
        return ret;
    }
}	
