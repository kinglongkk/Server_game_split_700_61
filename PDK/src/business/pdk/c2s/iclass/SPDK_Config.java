package business.pdk.c2s.iclass;

import cenum.RoomTypeEnum;
import jsproto.c2s.cclass.room.BaseCreateRoom;
import jsproto.c2s.iclass.room.SBase_Config;


@SuppressWarnings("serial")
public class SPDK_Config extends SBase_Config {
    public static SPDK_Config make(BaseCreateRoom cfg,RoomTypeEnum roomTypeEnum) {
    	SPDK_Config ret = new SPDK_Config();
        ret.setCfg(cfg);
        ret.setRoomType(roomTypeEnum);
        return ret;
    }
}
