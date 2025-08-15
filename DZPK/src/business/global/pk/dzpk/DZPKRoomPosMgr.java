package business.global.pk.dzpk;

import business.global.room.base.AbsBaseRoom;
import business.global.room.base.AbsRoomPosMgr;
import com.ddm.server.common.utils.CommTime;

public class DZPKRoomPosMgr extends AbsRoomPosMgr {
    public DZPKRoomPosMgr(AbsBaseRoom room) {
        super(room);
    }

    @Override
    protected void initPosList() {

        for (int posID = 0; posID < this.getPlayerNum(); posID++) {
            this.posList.add(new DZPKRoomPos(posID, this.getRoom()));
        }
    }

    /**
     * 是否所有玩家继续下一局
     *
     * @return
     */
    @Override
    public boolean isAllContinue() {
        if (null == this.getPosList() || this.getPosList().size() <= 1) {
            // 玩家信息列表没数据
            return false;
        }
        if (this.room.getBaseRoomConfigure().getBaseCreateRoom().getFangjian().contains(DZPKRoomEnum.DZPKGameRoomConfigEnum.XIAO_JU_10S_AOTO_READY.ordinal())) {
            this.getPosList().stream().forEach(k -> {
                if (k.getPid() > 0 && !k.isGameReady() && k.getTimeSec() > 0 && CommTime.nowSecond() - k.getTimeSec() >= 10) {
                    getRoom().continueGame(k.getPid());
                }
            });
        }
        // 玩家在游戏中并且没有准备。
        return this.getPosList().stream().allMatch(k -> k.getPid() <= 0L || (k.getPid() > 0L && k.isGameReady()));
    }

    /**
     * 是否所有玩家准备
     *
     * @return
     */
    @Override
    public boolean isAllReady() {
        if (null == this.getPosList() || this.getPosList().size() <= 1) {
            // 玩家信息列表没数据
            return false;
        }

        boolean playNum2_9 = room.getBaseRoomConfigure().getBaseCreateRoom().getPlayerMinNum() == 2 &&
                room.getBaseRoomConfigure().getBaseCreateRoom().getPlayerNum() == 9;
        if (!playNum2_9) {
            return this.getPosList().stream().allMatch(k -> k.isReady());
        } else {
            long readyCount = this.getPosList().stream().filter(k -> k.getPid() > 0L && k.isReady()).count();
            long realPlayerNum = this.getPosList().stream().filter(k -> k.getPid() > 0L).count();
            return readyCount == realPlayerNum && realPlayerNum >= 2;
        }
    }

    public boolean isFirstWinner(int posID) {
        int max1 = getPosList().stream().mapToInt(k -> ((DZPKRoomPos) k).getTotoalWinPoint()).max().getAsInt();
        return ((DZPKRoomPos) getPosByPosID(posID)).getTotoalWinPoint() == max1;

    }

    public int getMingCi(int posID) {
        DZPKRoomPos posByPosID = (DZPKRoomPos) getPosByPosID(posID);
        int totalWin = posByPosID.getTotoalWinPoint();
        int count = (int) getPosList().stream().filter(absRoomPos -> absRoomPos.getPid() > 0 && ((DZPKRoomPos) absRoomPos).getTotoalWinPoint() > totalWin).count();
        return count + 1;
    }
}
