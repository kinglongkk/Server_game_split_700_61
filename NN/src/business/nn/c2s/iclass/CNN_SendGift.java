package business.nn.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

@SuppressWarnings("serial")
public class CNN_SendGift extends BaseSendMsg {
    public long roomID;
    public int pos;
    public long productId;

    public static CNN_SendGift make(long roomID, int pos, long productId) {
        CNN_SendGift ret = new CNN_SendGift();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.productId = productId;
        return ret;
    }
}
