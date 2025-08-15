package business.sss.c2s.iclass;
import cenum.room.SetState;
import jsproto.c2s.cclass.BaseSendMsg;


public class SSSS_SetEnd extends BaseSendMsg {
    
    public long roomID;
    public SetState setState;
    public String reason;

    public static SSSS_SetEnd make(long roomID,SetState setState) {
    	SSSS_SetEnd ret = new SSSS_SetEnd();
        ret.roomID = roomID;
        ret.setState = setState;
        return ret;
    

    }
}