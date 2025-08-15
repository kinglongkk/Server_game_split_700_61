package business.nn.c2s.iclass;

import jsproto.c2s.cclass.*;


@SuppressWarnings("serial")
public class SNN_StartRound<T> extends BaseSendMsg {

    public long roomID;
    public T room_SetWait;


    public static <T> SNN_StartRound<T> make(long roomID, T room_SetWait) {
        SNN_StartRound<T> ret = new SNN_StartRound<T>();
        ret.roomID = roomID;
        ret.room_SetWait = room_SetWait;

        return ret;


    }
}
