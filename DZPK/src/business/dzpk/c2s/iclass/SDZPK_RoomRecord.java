package business.dzpk.c2s.iclass;

import jsproto.c2s.cclass.*;

import java.util.List;

@SuppressWarnings("serial")
public class SDZPK_RoomRecord<T> extends BaseSendMsg {

    public List<T> records;

    public static <T> SDZPK_RoomRecord<T> make(List<T> records) {
        SDZPK_RoomRecord<T> ret = new SDZPK_RoomRecord<T>();
        ret.records = records;

        return ret;

    }
}		
