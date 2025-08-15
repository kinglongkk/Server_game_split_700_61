package business.qzmj.c2s.iclass;
import jsproto.c2s.cclass.*;


@SuppressWarnings("serial")
public class SQZMJ_SetStart<T> extends BaseSendMsg {
    
    public long roomID;
    public T setInfo;


    public static <T> SQZMJ_SetStart<T> make(long roomID, T setInfo) {
    	SQZMJ_SetStart<T> ret = new SQZMJ_SetStart<T>();
        ret.roomID = roomID;
        ret.setInfo = setInfo;
        //打印数组看看
       
        return ret;
    }
}