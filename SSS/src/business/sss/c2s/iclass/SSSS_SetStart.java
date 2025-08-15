package business.sss.c2s.iclass;
import business.sss.c2s.cclass.SSSRoomSetInfo;
import jsproto.c2s.cclass.BaseSendMsg;


public class SSSS_SetStart extends BaseSendMsg {
    
    public long roomID;
    public SSSRoomSetInfo setInfo;


    public static SSSS_SetStart make(long roomID, SSSRoomSetInfo setInfo) {
        SSSS_SetStart ret = new SSSS_SetStart();
        ret.roomID = roomID;
        ret.setInfo = setInfo;
        //打印数组看看
       
        return ret;
    }
}