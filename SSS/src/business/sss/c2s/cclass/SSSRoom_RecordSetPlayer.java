package business.sss.c2s.cclass;

import jsproto.c2s.cclass.Player.ShortPlayer;

import java.util.ArrayList;
import java.util.List;

public class SSSRoom_RecordSetPlayer {
	
	public int posID = 0; // 作为ID
	public ShortPlayer player; // 玩家信息
	public int setPoint = 0; // 局输赢积分 
	public List<Integer> cards = new ArrayList<>(); //牌列表
}
