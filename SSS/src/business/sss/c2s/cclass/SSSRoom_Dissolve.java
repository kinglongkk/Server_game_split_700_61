package business.sss.c2s.cclass;

import java.util.ArrayList;
import java.util.List;

/**
 * 红中麻将 解散
 * @author Clark
 *
 */
	
	// 房间玩家信息
	public class SSSRoom_Dissolve{
		public int endSec = 0;
		public int createPos = 0;
		public List<Integer> posAgreeList = new ArrayList<>(); // 0未表态 1支持 2拒绝
	}
