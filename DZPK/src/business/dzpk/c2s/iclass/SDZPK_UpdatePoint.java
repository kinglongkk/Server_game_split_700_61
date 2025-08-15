package business.dzpk.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

import java.util.List;

public class SDZPK_UpdatePoint<T> extends BaseSendMsg {
    /**
     * 房间id
     */
    public long roomID;
    public List<T> setPosList;
    /**
     * 是否顺便清空510K 公共牌
     */
    public boolean isClear;

    public static <T> SDZPK_UpdatePoint<T> make(long roomID, List<T> setPosList, boolean isClear) {
        SDZPK_UpdatePoint ret = new SDZPK_UpdatePoint();
        ret.setRoomID(roomID);
        ret.setSetPosList(setPosList);
        ret.setClear(isClear);
        return ret;

    }

    public long getRoomID() {
        return roomID;
    }

    public void setRoomID(long roomID) {
        this.roomID = roomID;
    }

    public List<T> getSetPosList() {
        return setPosList;
    }

    public void setSetPosList(List<T> setPosList) {
        this.setPosList = setPosList;
    }

    public boolean isClear() {
        return isClear;
    }

    public void setClear(boolean clear) {
        isClear = clear;
    }
}		
		
