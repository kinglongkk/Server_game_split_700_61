package business.pdk.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

@SuppressWarnings("serial")
public class CPDK_ContinueRoom extends BaseSendMsg {
	public long roomID;
	public int continueType;

    public static CPDK_ContinueRoom make(long roomID, int continueType) {
        CPDK_ContinueRoom ret = new CPDK_ContinueRoom();
        ret.roomID = roomID;
        ret.continueType = continueType;
        return ret;
    }
}
