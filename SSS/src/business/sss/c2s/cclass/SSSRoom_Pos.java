package business.sss.c2s.cclass;


/**
 * 房间玩家信息
 * @author Huaxing
 *
 */
public class SSSRoom_Pos{
		public int pos;
		public long pid;
		public String name;
		public String headImageUrl;
		public int sex;
		public boolean isLostConnect;
		public boolean roomReady; // 房间准备开始游戏
		public boolean gameReady; // 继续游戏准备
		
		public boolean isCardReady;//已经准备好牌序
		
		public int point; //积分
		public int flashCnt; // 闪电出牌
//		public int curSetFlashCnt; // 闪电出牌
		
		public int paiPin; // 牌品值
		public int up; // 顶
		public int down; // 踩
	}
