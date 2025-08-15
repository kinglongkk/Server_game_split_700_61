package business.dzpk.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

import java.util.ArrayList;
import java.util.List;

/**
 * 重新设置玩家手牌
 *
 * @param <T>
 * @author Huaxing
 */
public class SDZPK_SetPosCard<T> extends BaseSendMsg {
    public long roomID;
    // 每个玩家的牌面	
    public List<T> setPosList = new ArrayList<>();


    public static <T> SDZPK_SetPosCard make(long roomID, List<T> setPosList) {
        SDZPK_SetPosCard ret = new SDZPK_SetPosCard();
        ret.roomID = roomID;
        ret.setPosList = setPosList;

        return ret;


    }
}		
