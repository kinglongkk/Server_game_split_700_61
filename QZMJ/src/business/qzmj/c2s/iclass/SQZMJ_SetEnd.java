package business.qzmj.c2s.iclass;
import jsproto.c2s.cclass.*;


@SuppressWarnings("serial")
public class SQZMJ_SetEnd<T> extends BaseSendMsg {
    
    public long roomID;
    public T setEnd;


    public static <T> SQZMJ_SetEnd<T> make(long roomID, T setEnd) {
    	SQZMJ_SetEnd<T> ret = new SQZMJ_SetEnd<T>();
        ret.roomID = roomID;
        ret.setEnd = setEnd;

        return ret;
    

    }
}