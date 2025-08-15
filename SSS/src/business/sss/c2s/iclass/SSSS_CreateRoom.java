package business.sss.c2s.iclass;


import jsproto.c2s.cclass.BaseSendMsg;

public class SSSS_CreateRoom extends BaseSendMsg {
    
    public long roomID;
    public String roomKey;


    public static SSSS_CreateRoom make(long roomID, String roomKey) {
        SSSS_CreateRoom ret = new SSSS_CreateRoom();
        ret.roomID = roomID;
        ret.roomKey = roomKey;

        return ret;
    

    }
}