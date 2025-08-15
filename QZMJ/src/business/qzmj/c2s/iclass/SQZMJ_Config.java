package business.qzmj.c2s.iclass;

import cenum.RoomTypeEnum;
import jsproto.c2s.cclass.room.BaseCreateRoom;
import jsproto.c2s.iclass.room.SBase_Config;

@SuppressWarnings("serial")
public class SQZMJ_Config extends SBase_Config {
	public static SQZMJ_Config make(BaseCreateRoom cfg, RoomTypeEnum roomTypeEnum) {
		SQZMJ_Config ret = new SQZMJ_Config();
		ret.setCfg(cfg);
		ret.setRoomType(roomTypeEnum);
		return ret;
	}
}