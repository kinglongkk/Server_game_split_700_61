package business.sss.c2s.cclass;

import cenum.mj.OpType;

import java.util.ArrayList;
import java.util.List;

/**
 * 红中麻将 配置
 * @author Clark
 *
 */
	
	public class SSSRoom_RoundPos{
		// 本次等待
		public int waitOpPos = -1; //当前等待操作的人   暗操作，填-1
		public List<OpType> opList = new ArrayList<>(); // 可执行者独享，可操作列表

		public OpType opType = OpType.Pass;
		public int opCard = 0;
	}
	
