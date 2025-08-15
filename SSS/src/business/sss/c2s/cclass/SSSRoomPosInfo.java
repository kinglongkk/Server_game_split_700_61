package business.sss.c2s.cclass;

import jsproto.c2s.cclass.room.RoomPosInfo;

public class SSSRoomPosInfo extends RoomPosInfo {
    public boolean isCardReady;// 已经准备好牌序

    public boolean isCardReady() {
        return isCardReady;
    }

    public void setCardReady(boolean cardReady) {
        isCardReady = cardReady;
    }
}
