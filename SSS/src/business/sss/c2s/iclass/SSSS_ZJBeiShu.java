package business.sss.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

public class SSSS_ZJBeiShu extends BaseSendMsg {
	public long roomID;
	public int pos;
	public int beishu;

	public static SSSS_ZJBeiShu make(long roomID, int pos, int beishu) {
		SSSS_ZJBeiShu ret = new SSSS_ZJBeiShu();
		ret.roomID = roomID;
		ret.pos = pos;
		ret.beishu = beishu;
		return ret;

	}
}