package business.dzpk.c2s.iclass;


import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.room.RoomPosInfo;

import java.util.ArrayList;
import java.util.List;

public class SDZPK_ChangePosList extends BaseSendMsg {
    public long roomID;
    // 房间玩家信息列表	
    private List<RoomPosInfo> posList = new ArrayList<>();

    public static SDZPK_ChangePosList make(long roomID, List<RoomPosInfo> posList) {
        SDZPK_ChangePosList ret = new SDZPK_ChangePosList();
        ret.roomID = roomID;
        ret.posList = posList;
        return ret;
    }
}	
