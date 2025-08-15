package business.global.pk.dzpk;

import business.dzpk.c2s.iclass.CDZPK_CreateRoom;
import business.global.pk.PKRoom;
import cenum.PKOpType;
import com.ddm.server.common.utils.Random;

public class DZPKRoomSet_SNG extends DZPKRoomSet {


    /**
     * @param setID
     * @param room
     * @param dPos
     */
    public DZPKRoomSet_SNG(int setID, PKRoom room, int dPos) {
        super(setID, room, dPos);
        if (setID == 1) {
            DZPKRoom room1 = (DZPKRoom) room;
            room1.startTimer();
        }
    }

    public int initPosPint() {
        int initJifen = 0;
        if (((DZPKRoom) getRoom()).isQUICK_SPORT()) {
            if (getRealPlayNum() < 5) {
                initJifen = 3000;
            } else {
                initJifen = 2000;
            }
        } else {
            if (getRealPlayNum() < 5) {
                initJifen = 5000;
            } else {
                initJifen = 2000;

            }
        }

        return initJifen;
    }

    private int getRealPlayNum() {
        return (int) getRoom().getRoomPosMgr().getPosList().stream().filter(absPKSetPos -> absPKSetPos.getPid() > 0).count();
    }


    /**
     * 随机出大盲
     */
    @Override
    protected void randomDaMang() {
        this.setRandomDaMang(Random.nextInt(posDict.size()));
        doMang();
    }

    /**
     * 随机出大盲
     */

    protected void doMang() {
        DZPKRoomEnum.DZPK_SNGDaXiaoMangEnum mangEnum = DZPKRoomEnum.DZPK_SNGDaXiaoMangEnum.getMang(getMangCount());
        int daMang = mangEnum.xiaoMang * 2;
        deducted(daMang, PKOpType.Not, getRandomDaMang());
        deducted(mangEnum.xiaoMang, PKOpType.Not, xiaoMang());
        setLowerBet(daMang);

    }


    /**
     * @return
     */
    private int getMangCount() {
        DZPKRoom room = (DZPKRoom) getRoom();
        long gameTime = System.currentTimeMillis() - room.getCreateTime();
        return (int) (gameTime / timeDes());

    }

    public long timeDes() {
        CDZPK_CreateRoom cfg = getRoom().getCfg();
        return cfg.getWanfa() == DZPKRoomEnum.DZPKWanFaEnum.QUICK_SPORT.ordinal() ? 3 * 60 * 1000 : 5 * 60 * 1000;
    }

    @Override
    public int getDaMangPoint() {
        DZPKRoomEnum.DZPK_SNGDaXiaoMangEnum mangEnum = DZPKRoomEnum.DZPK_SNGDaXiaoMangEnum.getMang(getMangCount());
        return mangEnum.xiaoMang * 2;
    }

    @Override
    public int getXiaoMangPoint() {
        DZPKRoomEnum.DZPK_SNGDaXiaoMangEnum mangEnum = DZPKRoomEnum.DZPK_SNGDaXiaoMangEnum.getMang(getMangCount());
        return mangEnum.xiaoMang;
    }


}
