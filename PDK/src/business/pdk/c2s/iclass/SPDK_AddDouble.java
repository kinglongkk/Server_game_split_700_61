package business.pdk.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 接收客户端数据
 * 加倍
 * @author zaf
 *
 */

@SuppressWarnings("serial")
public class SPDK_AddDouble extends BaseSendMsg {

	public long roomID;
    public int pos;  //位置
    public int addDouble;


    public static SPDK_AddDouble make(long roomID,int pos,int addDouble) {
    	SPDK_AddDouble ret = new SPDK_AddDouble();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.addDouble = addDouble;
        return ret;
    }
}
