package business.dzpk.c2s.iclass;

import jsproto.c2s.cclass.*;


@SuppressWarnings("serial")
public class SDZPK_StartRound<T> extends BaseSendMsg {

    public long roomID;
    public T room_SetWait;


    public static <T> SDZPK_StartRound<T> make(long roomID, T room_SetWait) {
        SDZPK_StartRound<T> ret = new SDZPK_StartRound<T>();
        ret.roomID = roomID;
        ret.room_SetWait = room_SetWait;

        return ret;


    }
}		
