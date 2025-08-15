package business.sss.c2s.cclass;

import java.util.ArrayList;
import java.util.List;

/**
 * 红中麻将 配置
 * @author Clark
 *
 */
	
	public class SSSRoom_SetRound{
		// 本次等待
		public int waitID = 0; // 当前第几次等待操作
		public int startWaitSec = 0; //开始等待时间
		public List<SSSRoom_RoundPos> opPosList = new ArrayList<>();

	}
	
