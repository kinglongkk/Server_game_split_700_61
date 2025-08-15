package business.dzpk.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

@SuppressWarnings("serial")
public class SDZPK_SendGift extends BaseSendMsg {
    public long roomID;
    public int sendPos; //发送者	
    public int recivePos; //接受者	
    public long productId;

    public static SDZPK_SendGift make(long roomID, int sendPos, int recivePos, long productId) {
        SDZPK_SendGift ret = new SDZPK_SendGift();
        ret.roomID = roomID;
        ret.sendPos = sendPos;
        ret.recivePos = recivePos;
        ret.productId = productId;
        return ret;
    }
}		
