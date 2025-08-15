package business.dzpk.c2s.iclass;

import cenum.RoomTypeEnum;
import jsproto.c2s.cclass.room.BaseCreateRoom;
import jsproto.c2s.iclass.room.SBase_Config;

@SuppressWarnings("serial")
public class SDZPK_Config extends SBase_Config {
    public static SDZPK_Config make(BaseCreateRoom cfg, RoomTypeEnum roomTypeEnum) {
        SDZPK_Config ret = new SDZPK_Config();
        ret.setCfg(cfg);
        ret.setRoomType(roomTypeEnum);
        return ret;
    }
}		
