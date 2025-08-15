package business.nn.c2s.iclass;

import cenum.RoomTypeEnum;
import jsproto.c2s.cclass.room.BaseCreateRoom;
import jsproto.c2s.iclass.room.SBase_Config;


@SuppressWarnings("serial")
public class SNN_Config extends SBase_Config {
    public static SNN_Config make(BaseCreateRoom cfg, RoomTypeEnum roomTypeEnum) {
        SNN_Config ret = new SNN_Config();
        ret.setCfg(cfg);
        ret.setRoomType(roomTypeEnum);
        return ret;
    }
}
