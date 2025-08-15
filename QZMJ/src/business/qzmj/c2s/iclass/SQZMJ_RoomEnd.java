package business.qzmj.c2s.iclass;
import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.RoomEndResult;


@SuppressWarnings("serial")
public class SQZMJ_RoomEnd<T> extends BaseSendMsg {
    
    public T record;
	public RoomEndResult<?> sRoomEndResult;

    public static <T> SQZMJ_RoomEnd<T> make(T record, RoomEndResult<?> sRoomEndResult) {
    	SQZMJ_RoomEnd<T> ret = new SQZMJ_RoomEnd<T>();
        ret.record = record;
        ret.sRoomEndResult = sRoomEndResult;
        return ret;
    

    }
}