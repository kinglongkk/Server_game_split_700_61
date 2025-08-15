package business.pdk.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

@SuppressWarnings("serial")
public class CPDK_SendGift extends BaseSendMsg {
	public long roomID;
	public int pos;
	public long productId;

    public static CPDK_SendGift make(long roomID, int pos, long productId) {
    	CPDK_SendGift ret = new CPDK_SendGift();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.productId = productId;
        return ret;
    }
}
