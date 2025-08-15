package business.sss.c2s.iclass;


import jsproto.c2s.cclass.BaseSendMsg;

public class CSSS_RoomRecordDetail extends BaseSendMsg {
    
    public long roomID;


    public static CSSS_RoomRecordDetail make(long roomID) {
        CSSS_RoomRecordDetail ret = new CSSS_RoomRecordDetail();
        ret.roomID = roomID;

        return ret;
    

    }
}