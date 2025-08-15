package business.nn.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 接收客户端数据
 * 加倍
 *
 * @author zaf
 */

@SuppressWarnings("serial")
public class SNN_AddDouble extends BaseSendMsg {

    public long roomID;
    public int pos;  //位置
    public int addDouble;


    public static SNN_AddDouble make(long roomID, int pos, int addDouble) {
        SNN_AddDouble ret = new SNN_AddDouble();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.addDouble = addDouble;
        return ret;
    }
}
