package business.nn.c2s.iclass;

import jsproto.c2s.cclass.*;

/**
 * 下注
 *
 * @author zaf
 */
public class SNN_AddBet extends BaseSendMsg {

    public long roomID;
    public int pos;
    public int addBet;

    public static SNN_AddBet make(long roomID, int pos, int addBet) {
        SNN_AddBet ret = new SNN_AddBet();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.addBet = addBet;
        return ret;


    }
}
