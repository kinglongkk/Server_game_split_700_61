package business.sss.c2s.iclass;

import business.sss.c2s.cclass.SSSS_RankingResult;
import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 自由扑克，对比结算排名
 * @author Huaxing
 *
 */
public class SSSS_Result  extends BaseSendMsg {
	
    public long roomID;
    public SSSS_RankingResult sRankingResult = new SSSS_RankingResult();
    public static SSSS_Result make(long roomID, SSSS_RankingResult sRankingResult) {
    	SSSS_Result ret = new SSSS_Result();
        ret.roomID = roomID;
        ret.sRankingResult = sRankingResult;
        return ret;
    }
}
