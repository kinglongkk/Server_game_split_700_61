package business.sss.c2s.iclass;
import business.sss.c2s.cclass.SSSSet_Pos;
import cenum.mj.OpType;
import jsproto.c2s.cclass.BaseSendMsg;


public class SSSS_PosOpCard extends BaseSendMsg {
    
    public long roomID;
    public int pos;
    public SSSSet_Pos set_Pos;
    public OpType opType;
    public int opCard;
    public boolean isFlash;


    public static SSSS_PosOpCard make(long roomID, int pos, SSSSet_Pos set_Pos, OpType opType, int opCard, boolean isFlash) {
        SSSS_PosOpCard ret = new SSSS_PosOpCard();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.set_Pos = set_Pos;
        ret.opType = opType;
        ret.opCard = opCard;
        ret.isFlash = isFlash;

        return ret;
    

    }
}