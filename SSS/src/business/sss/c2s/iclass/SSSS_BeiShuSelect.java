package business.sss.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

public class SSSS_BeiShuSelect extends BaseSendMsg {
	public long roomID;

	public static SSSS_BeiShuSelect make(long roomID) {
		SSSS_BeiShuSelect ret = new SSSS_BeiShuSelect();
		ret.roomID = roomID;
		return ret;
	}
}
