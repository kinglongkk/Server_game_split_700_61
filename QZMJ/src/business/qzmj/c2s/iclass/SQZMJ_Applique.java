package business.qzmj.c2s.iclass;

import cenum.mj.OpType;
import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 补花
 * @author Huaxing
 * @param <T>
 *
 */
@SuppressWarnings("serial")
public class SQZMJ_Applique<T> extends BaseSendMsg {
    public long roomID;
    public int pos;
    public OpType opType;
    public int opCard;
    public boolean isFlash;
    public T set_Pos;

    
    @SuppressWarnings("rawtypes")
	public static <T> SQZMJ_Applique make(long roomID, int pos, OpType opType, int opCard, boolean isFlash, T set_Pos) {
    	SQZMJ_Applique ret = new SQZMJ_Applique();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.opType = opType;
        ret.opCard = opCard;
        ret.isFlash = isFlash;
        ret.set_Pos = set_Pos;
        return ret;
    

    }
}
