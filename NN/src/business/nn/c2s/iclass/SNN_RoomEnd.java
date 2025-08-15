package business.nn.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.pk.PKRoom_Record;


public class SNN_RoomEnd extends BaseSendMsg {

    public PKRoom_Record record;
    //public List<NNRoom_SetEnd> records;

    public static SNN_RoomEnd make(PKRoom_Record record/*, List<NNRoom_SetEnd> records*/) {
        SNN_RoomEnd ret = new SNN_RoomEnd();
        ret.record = record;
        //ret.records = records;
        return ret;


    }
}
