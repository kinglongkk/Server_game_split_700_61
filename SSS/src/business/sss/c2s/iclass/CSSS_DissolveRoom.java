package business.sss.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 自由扑克 接收客户端数据 解散房间
 * 
 * @author Huaxing
 *
 */
public class CSSS_DissolveRoom extends BaseSendMsg {

	public long roomID;

	public static CSSS_DissolveRoom make(long roomID) {
		CSSS_DissolveRoom ret = new CSSS_DissolveRoom();
		ret.roomID = roomID;
		return ret;

	}
}