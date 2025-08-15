package business.qzmj.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

public class SQZMJ_Jin extends BaseSendMsg {
	public long roomID;
	public int jin;
	public int jin2;
	public int jinJin;
	public int normalMoCnt = 0; // 普通摸牌数量
	public int gangMoCnt = 0; // 杠后摸牌数量
	public static SQZMJ_Jin make(long roomID, int jin, int jin2, int jinJin, int normalMoCnt, int gangMoCnt) {
		SQZMJ_Jin ret = new SQZMJ_Jin();
    	ret.roomID = roomID;
    	ret.jin = jin;
    	ret.jin2 = jin2;
    	ret.jinJin = jinJin;
    	ret.normalMoCnt = normalMoCnt;
    	ret.gangMoCnt = gangMoCnt;

        return ret;
	    
	}
}
