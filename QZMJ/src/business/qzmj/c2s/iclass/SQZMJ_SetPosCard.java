package business.qzmj.c2s.iclass;

import java.util.ArrayList;
import java.util.List;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 重新设置玩家手牌
 * @author Huaxing
 * @param <T>
 *
 */
public class SQZMJ_SetPosCard<T> extends BaseSendMsg  {
    public long roomID;
	// 每个玩家的牌面
	public List<T> setPosList = new ArrayList<>();


    public static <T> SQZMJ_SetPosCard make(long roomID, List<T> setPosList) {
    	SQZMJ_SetPosCard ret = new SQZMJ_SetPosCard();
        ret.roomID = roomID;
        ret.setPosList = setPosList;

        return ret;
    

    }
}
