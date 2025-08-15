package business.dzpk.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

public class SDZPK_CleanPosCard extends BaseSendMsg {

    public long roomID;
    public int pos;


    public static SDZPK_CleanPosCard make(long roomID, int pos) {
        SDZPK_CleanPosCard ret = new SDZPK_CleanPosCard();
        ret.roomID = roomID;
        ret.pos = pos;

        return ret;


    }
}		
