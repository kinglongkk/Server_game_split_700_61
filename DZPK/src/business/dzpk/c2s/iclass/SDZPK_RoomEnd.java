package business.dzpk.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.RoomEndResult;


@SuppressWarnings("serial")
public class SDZPK_RoomEnd<T> extends BaseSendMsg {

    public T record;
    public RoomEndResult sRoomEndResult;

    public static <T> SDZPK_RoomEnd<T> make(T record, RoomEndResult sRoomEndResult) {
        SDZPK_RoomEnd<T> ret = new SDZPK_RoomEnd<T>();
        ret.record = record;
        ret.sRoomEndResult = sRoomEndResult;
        return ret;


    }
}		
