package business.sss.c2s.iclass;


import jsproto.c2s.cclass.BaseSendMsg;

public class CSSS_RoomSetRecord extends BaseSendMsg {
    
    public long roomID;


    public static CSSS_RoomSetRecord make(long roomID) {
    	CSSS_RoomSetRecord ret = new CSSS_RoomSetRecord();
        ret.roomID = roomID;

        return ret;
    

    }
}