package business.sss.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

public class CSSS_RoomBeiShu extends BaseSendMsg {
	public long roomID;
	public int posIndex;
	public int beishu;

	public static CSSS_RoomBeiShu make(long roomID, int posIndex, int beishu) {
		CSSS_RoomBeiShu ret = new CSSS_RoomBeiShu();
		ret.roomID = roomID;
		ret.posIndex = posIndex;
		ret.beishu = beishu;
		return ret;
	}
}