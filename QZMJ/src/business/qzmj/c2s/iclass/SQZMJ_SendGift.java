package business.qzmj.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

@SuppressWarnings("serial")
public class SQZMJ_SendGift extends BaseSendMsg {
	public long roomID;
	public int  sendPos; //发送者
	public int 	recivePos; //接受者
	public long productId;

    public static SQZMJ_SendGift make(long roomID, int sendPos, int recivePos, long productId) {
    	SQZMJ_SendGift ret = new SQZMJ_SendGift();
        ret.roomID = roomID;
        ret.sendPos = sendPos;
        ret.recivePos = recivePos;
        ret.productId = productId;
        return ret;
    }
}