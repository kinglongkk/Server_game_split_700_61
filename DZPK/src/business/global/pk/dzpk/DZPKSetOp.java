package business.global.pk.dzpk;

import business.global.pk.AbsPKSetOp;
import business.global.pk.PKOpCard;
import cenum.PKOpType;

/**
 * 当局操作动作
 *
 * @author
 */
public class DZPKSetOp extends AbsPKSetOp {
    /**
     * 玩家信息
     */
    private DZPKSetPos mSetPos;
    /**
     * 当前局信息
     */
    private DZPKRoomSet roomSet;

    public DZPKSetOp(DZPKSetPos mSetPos) {
        super();
        this.mSetPos = mSetPos;
        this.roomSet = (DZPKRoomSet) mSetPos.getSet();
    }


    @Override
    public boolean doOpType(PKOpCard opCard, PKOpType opType) {
        boolean opResult = false;
        switch (opType) {
            case PASS_CARD:
                if (roomSet.isFirstBet()) {
                    roomSet.setLetGoPos(mSetPos.getPosID());
                }
                opResult = true;
                break;
            case LET_GO:
                roomSet.setLetGoPos(mSetPos.getPosID());
                opResult = true;
                break;
            case BET:
                if (mSetPos.getBetOptions().contains(opCard.getOpValue())) {
                    opResult = roomSet.deducted(opCard.getOpValue(), opType, mSetPos.getPosID());
                }
                if (opResult) {
                    //重置下注选项
                    roomSet.setFirstBet(false);
                }
                break;
            case FALLOW_BET:
                opResult = roomSet.deducted(roomSet.getLowerBet(), opType, mSetPos.getPosID());

                break;
            case ADD_BET:
                //在筹码充足的情况：加注的下注额不能超过底池总额+大盲。
                //筹码不足的情况：加注的下注额不超过all in。
                if (roomSet.isAO_HA_MA()) {
                    if (opCard.getOpValue() > roomSet.getTotalBet() + roomSet.getDaMangPoint()) {
                        break;
                    }
                }
                if (mSetPos.getBetOptions().contains(opCard.getOpValue()) && opCard.getOpValue() > roomSet.getLowerBet()) {
                    opResult = roomSet.deducted(opCard.getOpValue(), opType, mSetPos.getPosID());
                }
                break;
            case ALL_IN:
                opResult = roomSet.deducted(mSetPos.getDeductPoint(), opType, mSetPos.getPosID());
                if (opResult && getRoomSet().isFirstBet()) {
                    //重置下注选项
                    roomSet.setFirstBet(false);
                }
                break;
        }
        return opResult;
    }

    /**
     * 检查出牌类型
     *
     * @param opCard 牌ID
     * @param opType 动作类型
     * @return
     */
    @Override
    public boolean checkOpType(PKOpCard opCard, PKOpType opType) {

        return false;
    }

    public DZPKSetPos getmSetPos() {
        return mSetPos;
    }

    public DZPKRoomSet getRoomSet() {
        return roomSet;
    }

    @Override
    public void clear() {

    }
}		
