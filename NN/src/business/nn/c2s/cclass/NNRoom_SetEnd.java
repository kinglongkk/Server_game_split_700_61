package business.nn.c2s.cclass;

import jsproto.c2s.cclass.room.RoomSetEndInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 牛牛 配置
 * @author Clark
 *
 */

	
// 一局结束的信息
public class NNRoom_SetEnd extends RoomSetEndInfo {
	public int endTime = 0;
	public List<NNRoom_PosEnd> posResultList = new ArrayList<>(); // 每个玩家的结算
}
	
