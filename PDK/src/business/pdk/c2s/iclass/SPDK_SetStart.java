package business.pdk.c2s.iclass;
import business.pdk.c2s.cclass.PDKRoomSetInfo;
import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 一局游戏开始
 * @author zaf
 * */
@SuppressWarnings("serial")
public class SPDK_SetStart extends BaseSendMsg {

    public long roomID;
    public PDKRoomSetInfo setInfo;

    public static SPDK_SetStart make(long roomID, PDKRoomSetInfo setInfo) {
        SPDK_SetStart ret = new SPDK_SetStart();
        ret.roomID = roomID;
        ret.setInfo = setInfo;
        return ret;
    }
}
