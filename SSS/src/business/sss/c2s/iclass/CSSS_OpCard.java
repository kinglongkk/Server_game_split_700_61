package business.sss.c2s.iclass;


import jsproto.c2s.cclass.BaseSendMsg;

public class CSSS_OpCard extends BaseSendMsg {
    
    public long roomID;
    public int setID;
    public int roundID;
    public int cardID;
    public int opType;


    public static CSSS_OpCard make(long roomID, int setID, int roundID, int cardID, int opType) {
        CSSS_OpCard ret = new CSSS_OpCard();
        ret.roomID = roomID;
        ret.setID = setID;
        ret.roundID = roundID;
        ret.cardID = cardID;
        ret.opType = opType;

        return ret;
    

    }
}