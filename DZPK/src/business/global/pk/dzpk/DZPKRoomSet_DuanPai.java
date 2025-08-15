package business.global.pk.dzpk;

import business.global.pk.PKRoom;
import com.ddm.server.common.utils.Random;

public class DZPKRoomSet_DuanPai extends DZPKRoomSet {


    /**
     * @param setID
     * @param room
     * @param dPos
     */
    public DZPKRoomSet_DuanPai(int setID, PKRoom room, int dPos) {
        super(setID, room, dPos);
    }

    /**
     * 随机出大盲
     */
    protected void randomDaMang() {
        this.setRandomDaMang(Random.nextInt(posDict.size()));
        setLowerBet(qianZhu());
    }

    @Override
    public int getJifenMin() {

        return qianZhu() * 100;
    }

    @Override
    public int getJiFenMax() {
        return getJifenMin() * 5;
    }
}
