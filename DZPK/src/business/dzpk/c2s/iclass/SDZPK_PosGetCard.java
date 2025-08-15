package business.dzpk.c2s.iclass;

import jsproto.c2s.cclass.*;


@SuppressWarnings("serial")
public class SDZPK_PosGetCard<T> extends BaseSendMsg {

    public long roomID;
    public int pos;
    public int normalMoCnt;
    public int gangMoCnt;
    public T set_Pos;


    public static <T> SDZPK_PosGetCard<T> make(long roomID, int pos, int normalMoCnt, int gangMoCnt, T set_Pos) {
        SDZPK_PosGetCard<T> ret = new SDZPK_PosGetCard<T>();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.normalMoCnt = normalMoCnt;
        ret.gangMoCnt = gangMoCnt;
        ret.set_Pos = set_Pos;

        return ret;


    }
}		
