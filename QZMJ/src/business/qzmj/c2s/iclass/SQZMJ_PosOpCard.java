package business.qzmj.c2s.iclass;
import cenum.mj.OpType;
import jsproto.c2s.cclass.BaseSendMsg;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("serial")
public class SQZMJ_PosOpCard<T> extends BaseSendMsg {
    
    public long roomID;
    public int pos;
    public T set_Pos;
    public OpType opType;
    public int opCard;
    public boolean isFlash;
    public List<T> set_PosList = new ArrayList<>();


    public static <T> SQZMJ_PosOpCard<T> make(long roomID, int pos, T set_Pos, OpType opType, int opCard, boolean isFlash, List<T> set_PosList) {
    	SQZMJ_PosOpCard<T> ret = new SQZMJ_PosOpCard<T>();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.set_Pos = set_Pos;
        ret.opType = opType;
        ret.opCard = opCard;
        ret.isFlash = isFlash;
        ret.set_PosList = set_PosList;

        return ret;
    

    }
}