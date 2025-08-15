package business.pdk.c2s.iclass;

import java.util.Map;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 接收客户端数据
 * 记牌器
 * @author zaf
 *
 */

@SuppressWarnings("serial")
public class SPDK_CardNumber extends BaseSendMsg {

	public long roomID;
	public Map<Integer, Integer> cardNumMap;

    public static SPDK_CardNumber make(long roomID, Map<Integer, Integer> cardNumMap) {
    	SPDK_CardNumber ret = new SPDK_CardNumber();
        ret.cardNumMap = cardNumMap;
        ret.roomID = roomID;
        return ret;
    }
}
