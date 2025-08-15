package business.global.pk.dzpk;

import business.global.pk.AbsPKSetPos;
import business.global.pk.PKRoom;

public class DZPKRoomSet_AoMaHA extends DZPKRoomSet {


    /**
     * @param setID
     * @param room
     * @param dPos
     */
    public DZPKRoomSet_AoMaHA(int setID, PKRoom room, int dPos) {
        super(setID, room, dPos);
    }

    @Override
    protected AbsPKSetPos absPKSetPos(int posID) {
        return new DZPKSetPos_AoMaHa(posID, this.getRoom().getRoomPosMgr().getPosByPosID(posID), this);
    }
    public int qianZhu() {
        return 0;
    }
}
