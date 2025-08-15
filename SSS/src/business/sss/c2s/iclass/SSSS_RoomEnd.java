package business.sss.c2s.iclass;
import business.sss.c2s.cclass.SSSRoom_Record;
import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.pk.PKRoom_Record;


public class SSSS_RoomEnd extends BaseSendMsg {
    
    public SSSRoom_Record record;


    public static SSSS_RoomEnd make(SSSRoom_Record record) {
        SSSS_RoomEnd ret = new SSSS_RoomEnd();
        ret.record = record;

        return ret;
    

    }
}