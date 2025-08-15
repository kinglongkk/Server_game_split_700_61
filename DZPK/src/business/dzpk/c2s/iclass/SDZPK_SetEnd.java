package business.dzpk.c2s.iclass;

import jsproto.c2s.cclass.*;


@SuppressWarnings("serial")
public class SDZPK_SetEnd<T> extends BaseSendMsg {

    public long roomID;
    public T setEnd;


    public static <T> SDZPK_SetEnd<T> make(long roomID, T setEnd) {
        SDZPK_SetEnd<T> ret = new SDZPK_SetEnd<T>();
        ret.roomID = roomID;
        ret.setEnd = setEnd;
        return ret;


    }
}		
