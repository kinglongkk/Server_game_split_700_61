package business.nn.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

@SuppressWarnings("serial")
public class CNN_FaPaiJieShu extends BaseSendMsg {
    public long roomID;
    public int pos;

    public static CNN_FaPaiJieShu make(long roomID, int pos) {
        CNN_FaPaiJieShu ret = new CNN_FaPaiJieShu();
        ret.roomID = roomID;
        ret.pos = pos;
        return ret;
    }
}
