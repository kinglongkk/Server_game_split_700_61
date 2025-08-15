package business.sss.c2s.iclass;


import jsproto.c2s.cclass.BaseSendMsg;

public class CSSS_RoomRecord extends BaseSendMsg {
    public long roomID;


    public static CSSS_RoomRecord make(long roomID) {
    	CSSS_RoomRecord ret = new CSSS_RoomRecord();
    	ret.roomID = roomID;

        return ret;
    

    }
}