package business.nn.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 接收客户端数据
 * 抢关门
 *
 * @author zaf
 */

public class CNN_RobClose extends BaseSendMsg {

    public long roomID;
    public int pos;  //位置
    public int robClose;//是否抢关门  0:否 1：是

    public static CNN_RobClose make(long roomID, int pos, int robClose) {
        CNN_RobClose ret = new CNN_RobClose();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.robClose = robClose;
        return ret;
    }
}
