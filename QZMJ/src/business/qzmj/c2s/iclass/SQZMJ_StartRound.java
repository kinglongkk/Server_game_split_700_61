package business.qzmj.c2s.iclass;
import jsproto.c2s.cclass.*;


@SuppressWarnings("serial")
public class SQZMJ_StartRound<T> extends BaseSendMsg {
    
    public long roomID;
    public T room_SetWait;


    public static <T> SQZMJ_StartRound<T> make(long roomID, T room_SetWait) {
    	SQZMJ_StartRound<T> ret = new SQZMJ_StartRound<T>();
        ret.roomID = roomID;
        ret.room_SetWait = room_SetWait;

        return ret;
    

    }
}