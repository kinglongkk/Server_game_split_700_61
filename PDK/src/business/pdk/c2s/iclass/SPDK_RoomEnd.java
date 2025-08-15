package business.pdk.c2s.iclass;
import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.pk.PKRoom_Record;


@SuppressWarnings("serial")
public class SPDK_RoomEnd extends BaseSendMsg {

    public PKRoom_Record record;

    public static SPDK_RoomEnd make(PKRoom_Record record) {
        SPDK_RoomEnd ret = new SPDK_RoomEnd();
        ret.record = record;
        //ret.records = records;
        return ret;


    }
}
