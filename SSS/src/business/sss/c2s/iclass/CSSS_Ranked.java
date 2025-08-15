package business.sss.c2s.iclass;

import business.sss.c2s.cclass.CSSS_PlayerRanked;
import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 确认好牌的位置
 * 
 * @author Huaxing
 *
 */
public class CSSS_Ranked extends BaseSendMsg {
	public long roomID;
	public long pid;
	public int posIdx;
	public boolean isSpecial;
	public int special = -1;
	public CSSS_PlayerRanked dunPos = new CSSS_PlayerRanked();

	public CSSS_Ranked() {
		super();
	}

	public CSSS_Ranked(long roomID, long pid, int posIdx, CSSS_PlayerRanked dun) {
		super();
		this.roomID = roomID;
		this.pid = pid;
		this.posIdx = posIdx;
		this.dunPos = dun;
	}

	public static CSSS_Ranked make(long roomID, long pid, int posIndex, CSSS_PlayerRanked dun, boolean isSpecial, int special) {
		CSSS_Ranked ret = new CSSS_Ranked();
		ret.roomID = roomID;
		ret.pid = pid;
		ret.posIdx = posIndex;
		ret.dunPos = dun;
		ret.isSpecial = isSpecial;
		ret.special = special;
		return ret;
	}

	@Override
	public String toString() {
		return "CSSS_Ranked [roomID=" + roomID + ", pid=" + pid + ", posIdx=" + posIdx + ", dunPos=" + dunPos + "]\n";
	}
}
