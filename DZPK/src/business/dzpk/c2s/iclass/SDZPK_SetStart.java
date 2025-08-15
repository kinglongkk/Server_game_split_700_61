package business.dzpk.c2s.iclass;

import jsproto.c2s.cclass.*;


@SuppressWarnings("serial")
public class SDZPK_SetStart<T> extends BaseSendMsg {

    public long roomID;
    public T setInfo;


    public static <T> SDZPK_SetStart<T> make(long roomID, T setInfo) {
        SDZPK_SetStart<T> ret = new SDZPK_SetStart<T>();
        ret.roomID = roomID;
        ret.setInfo = setInfo;
        //打印数组看看		

        return ret;
    }
}		
