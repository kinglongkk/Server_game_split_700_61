package business.sss.c2s.cclass;

import jsproto.c2s.cclass.Player.ShortPlayer;
import jsproto.c2s.cclass.pk.PKRoom_RecordPosInfo;

import java.util.ArrayList;
import java.util.List;

//房间战绩
public class SSSRoom_Record {
	
	public long roomID;//房间ID
	public String roomName;//房间名
	public String key;
	public int endSec;//房间结束的秒
	public int setCnt;//总局数
	public int posCnt;//最大人数
	
	public ShortPlayer ownerInfo;//房主个人信息
	
	public List<ShortPlayer> players = new ArrayList<>(); // 玩家信息
	public List<Integer> point = new ArrayList<>(); // 积分变化 
	public List<SSSResults> recordPosInfosList = new ArrayList<>();//胜利次数
	//参与玩家的最终结算信息
//	public List<SSSRoom_RecordPlayer> players = new ArrayList<>(); // 玩家信息
	

}
