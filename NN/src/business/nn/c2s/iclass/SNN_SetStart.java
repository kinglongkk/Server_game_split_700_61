package business.nn.c2s.iclass;

import business.nn.c2s.cclass.NNRoomSetInfo;
import core.db.persistence.BaseDao;
import jsproto.c2s.cclass.*;

/**
 * 一局游戏开始
 *
 * @author zaf
 */
public class SNN_SetStart extends BaseSendMsg {

    public long roomID;
    public NNRoomSetInfo setInfo;

    public static SNN_SetStart make(long roomID, NNRoomSetInfo setInfo) {
        SNN_SetStart ret = new SNN_SetStart();
        ret.roomID = roomID;
        ret.setInfo = setInfo;
        return ret;
    }
}
