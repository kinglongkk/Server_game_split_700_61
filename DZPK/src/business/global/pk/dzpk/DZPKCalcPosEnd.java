package business.global.pk.dzpk;

import business.global.pk.AbsPKCalcPosEnd;

public class DZPKCalcPosEnd extends AbsPKCalcPosEnd {
    /**
     * 玩家信息
     */
    private DZPKSetPos setPos;
    /**
     * 当局信息
     */
    private DZPKRoomSet roomSet;


    public DZPKCalcPosEnd(DZPKSetPos setPos, DZPKRoomSet roomSet) {
        this.setPos = setPos;
        this.roomSet = roomSet;
    }

}
