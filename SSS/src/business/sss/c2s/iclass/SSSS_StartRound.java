package business.sss.c2s.iclass;
import business.sss.c2s.cclass.SSSRoom_SetRound;
import jsproto.c2s.cclass.BaseSendMsg;


public class SSSS_StartRound extends BaseSendMsg {
    
    public long roomID;
    public SSSRoom_SetRound room_SetWait;


    public static SSSS_StartRound make(long roomID, SSSRoom_SetRound room_SetWait) {
        SSSS_StartRound ret = new SSSS_StartRound();
        ret.roomID = roomID;
        ret.room_SetWait = room_SetWait;

        return ret;
    

    }
}