package business.sss.c2s.cclass;

import business.sss.c2s.cclass.entity.PlayerResult;
import business.sss.c2s.iclass.CSSS_Ranked;
import cenum.room.SetState;
import jsproto.c2s.cclass.room.RoomSetInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 自由扑克 当前局游戏信息
 * @author Clark
 *
 */
public class SSSRoomSetInfo extends RoomSetInfo {
	
	public long roomID = 0; // 房间ID
	public SetState state = SetState.Init; // 游戏状态
	public long setStartTime = 0;
	public long setCurrentTime = 0;
	public String mapai = "";
	public long backerPos =0;
	public  int beishu=0;
	public boolean isXiPai = false;
	public boolean isPlaying = false;
	public List<SSSRoomSet_Pos> posInfo = new ArrayList<SSSRoomSet_Pos>(); // 一局玩家列表
	public List<SSSRoomSet_End> posEndInfo = new ArrayList<>(); // 一局玩家列表
	public List<SSSSet_Pos> setPosList = new ArrayList<>();
	public SSSRoomSet_End setEnd = new SSSRoomSet_End();


	

	
}
