package business.sss.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.room.RoomSetEndInfo;

import java.util.List;


public class SSSS_RoomRecord<T> extends BaseSendMsg {
    
    public List<T> records;


    public static <T> SSSS_RoomRecord make(List<T> records) {
        SSSS_RoomRecord<T> ret = new SSSS_RoomRecord<T>();
        ret.records = records;

        return ret;
    

    }
}