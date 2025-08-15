package business.nn.c2s.iclass;

import jsproto.c2s.cclass.*;

import java.util.List;


public class SNN_RoomRecord<T> extends BaseSendMsg {

    public List<T> records;

    public static <T> SNN_RoomRecord<T> make(List<T> records) {
        SNN_RoomRecord<T> ret = new SNN_RoomRecord<T>();
        ret.records = records;
        return ret;
    }

}
