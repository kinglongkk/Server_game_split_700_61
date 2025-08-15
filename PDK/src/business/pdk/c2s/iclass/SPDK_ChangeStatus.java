package business.pdk.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 接收客户端数据
 * 明牌
 * @author zaf
 *
 */

@SuppressWarnings("serial")
public class SPDK_ChangeStatus extends BaseSendMsg {

	public long roomID;
    public int state;  //位置
    public int opPos;//操作位
    public boolean isRobCloseSuccess;//是否是抢关门成功

    public static SPDK_ChangeStatus make(long roomID,int state, int opPos, boolean isRobCloseSuccess) {
    	SPDK_ChangeStatus ret = new SPDK_ChangeStatus();
        ret.roomID = roomID;
        ret.state = state;
        ret.opPos = opPos;
        ret.isRobCloseSuccess = isRobCloseSuccess;
        return ret;
    }
}
