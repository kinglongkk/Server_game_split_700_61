package business.pdk.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

@SuppressWarnings("serial")
public class CPDK_FaPaiJieShu extends BaseSendMsg {
	public long roomID;
	public int pos;

    public static CPDK_FaPaiJieShu make(long roomID, int pos) {
        CPDK_FaPaiJieShu ret = new CPDK_FaPaiJieShu();
        ret.roomID = roomID;
        ret.pos = pos;
        return ret;
    }
}