package business.sss.c2s.cclass;

import jsproto.c2s.cclass.room.RoomSetEndInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 红中麻将 配置
 * @author Clark
 *
 */

	
	// 一局结束的信息
	public class SSSRoom_SetEnd extends RoomSetEndInfo {
		public int endTime = 0;
		public List<SSSRoom_PosEnd> posHuList = new ArrayList<>(); // 每个玩家的结算
	}
	
