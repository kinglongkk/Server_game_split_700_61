package business.sss.c2s.iclass;
import business.sss.c2s.cclass.SSSSet_Pos;
import jsproto.c2s.cclass.BaseSendMsg;


public class SSSS_PosGetCard extends BaseSendMsg {
    
    public long roomID;
    public int pos;
    public int normalMoCnt;
    public int gangMoCnt;
    public SSSSet_Pos set_Pos;


    public static SSSS_PosGetCard make(long roomID, int pos, int normalMoCnt, int gangMoCnt, SSSSet_Pos set_Pos) {
    	SSSS_PosGetCard ret = new SSSS_PosGetCard();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.normalMoCnt = normalMoCnt;
        ret.gangMoCnt = gangMoCnt;
        ret.set_Pos = set_Pos;

        return ret;
    

    }
}