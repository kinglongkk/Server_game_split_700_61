package business.sss.c2s.cclass;

import business.sss.c2s.cclass.entity.PlayerResult;
import jsproto.c2s.cclass.room.ArenaRoomConfig;
import jsproto.c2s.cclass.room.RoomSetEndInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 红中麻将 配置
 * @author Clark
 *
 */
// 一局中各位置的信息
public class SSSRoomSet_End extends RoomSetEndInfo {
	public int endTime = 0;
	public List<PlayerResult> pResults = new ArrayList<PlayerResult>();
	public ArenaRoomConfig aRoomCfg;
}
	
