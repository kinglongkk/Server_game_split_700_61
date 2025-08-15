package business.nn.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 接收客户端数据
 * 加倍
 *
 * @author zaf
 */

public class CNN_AddDouble extends BaseSendMsg {

    public long roomID;
    public int pos;  //位置
    public int addDouble;


    public static CNN_AddDouble make(long roomID, int pos, int addDouble) {
        CNN_AddDouble ret = new CNN_AddDouble();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.addDouble = addDouble;
        return ret;
    }
}
