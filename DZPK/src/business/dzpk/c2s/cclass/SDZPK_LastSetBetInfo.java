package business.dzpk.c2s.cclass;

import jsproto.c2s.cclass.BaseSendMsg;
import lombok.Data;

/**
 * 上局下注情况
 *
 * @author Administrator
 */
@Data
public class SDZPK_LastSetBetInfo<T> extends BaseSendMsg {
    public long roomID;
    public T setEnd;


    public static <T> SDZPK_LastSetBetInfo<T> make(long roomID, T setEnd) {
        SDZPK_LastSetBetInfo<T> ret = new SDZPK_LastSetBetInfo<T>();
        ret.roomID = roomID;
        ret.setEnd = setEnd;
        return ret;


    }


}