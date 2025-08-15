package business.sss.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

public class SSSS_SendGift extends BaseSendMsg {
    public long roomID;
    public int  sendPos; //发送者
    public int 	recivePos; //接受者
    public long productId;

    public static SSSS_SendGift make(long roomID, int sendPos, int recivePos, long productId) {
        SSSS_SendGift ret = new SSSS_SendGift();
        ret.roomID = roomID;
        ret.sendPos = sendPos;
        ret.recivePos = recivePos;
        ret.productId = productId;
        return ret;
    }
}