package business.nn.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

@SuppressWarnings("serial")
public class CNN_ContinueRoom extends BaseSendMsg {
    public long roomID;
    public int continueType;

    public static CNN_ContinueRoom make(long roomID, int continueType) {
        CNN_ContinueRoom ret = new CNN_ContinueRoom();
        ret.roomID = roomID;
        ret.continueType = continueType;
        return ret;
    }
}
