package business.nn.c2s.iclass;

import jsproto.c2s.cclass.*;

/**
 * 亮牌
 *
 * @author zaf
 */
public class CNN_CheckCard extends BaseSendMsg {

    public long roomID;
    public int pos;

    public static CNN_CheckCard make(long roomID, int pos) {
        CNN_CheckCard ret = new CNN_CheckCard();
        ret.roomID = roomID;
        ret.pos = pos;
        return ret;
    }
}
