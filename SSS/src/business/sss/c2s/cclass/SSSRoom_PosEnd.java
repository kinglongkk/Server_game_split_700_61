package business.sss.c2s.cclass;

import java.util.ArrayList;
import java.util.List;

/**
 * 红中麻将 配置
 * @author Clark
 *
 */
	
	// 位置结束的信息
	public class SSSRoom_PosEnd{
		public int pos = 0; //位置
//		public HuType huType = HuType.NotHu; // 每个玩家的胡牌类型 0不胡 ；1自摸；2抢杠胡
		public int point = 0; // 本局积分变更
//		public int flashCnt = 0; // 本局获得闪电卷
		public List<String> shouCard = new ArrayList<>(); //
		public String handCard; //
//		public int zhongMa = 0; // 中码
//		public List<String> maCardList = new ArrayList<>(); // 扎码的牌
	}
	

