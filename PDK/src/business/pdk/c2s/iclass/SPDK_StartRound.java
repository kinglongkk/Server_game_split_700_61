package business.pdk.c2s.iclass;
import jsproto.c2s.cclass.*;


@SuppressWarnings("serial")
public class SPDK_StartRound<T> extends BaseSendMsg {
    
    public long roomID;
    public T room_SetWait;


    public static <T>SPDK_StartRound<T> make(long roomID, T room_SetWait) {
    	SPDK_StartRound<T> ret = new SPDK_StartRound<T>();
        ret.roomID = roomID;
        ret.room_SetWait = room_SetWait;

        return ret;
    

    }
}
