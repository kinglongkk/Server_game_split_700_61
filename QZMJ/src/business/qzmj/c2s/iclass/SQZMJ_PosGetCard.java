package business.qzmj.c2s.iclass;
import jsproto.c2s.cclass.*;


@SuppressWarnings("serial")
public class SQZMJ_PosGetCard<T> extends BaseSendMsg {
    
    public long roomID;
    public int pos;
    public int normalMoCnt;
    public int gangMoCnt;
    public T set_Pos;
    public int cardRestSize;


    public static <T> SQZMJ_PosGetCard<T> make(long roomID, int pos, int normalMoCnt, int gangMoCnt, T set_Pos, int cardRestSize ) {
    	SQZMJ_PosGetCard<T> ret = new SQZMJ_PosGetCard<T>();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.normalMoCnt = normalMoCnt;
        ret.gangMoCnt = gangMoCnt;
        ret.set_Pos = set_Pos;
        ret.cardRestSize = cardRestSize;

        return ret;
    

    }
}