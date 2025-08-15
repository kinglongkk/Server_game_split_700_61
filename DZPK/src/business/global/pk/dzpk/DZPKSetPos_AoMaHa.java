package business.global.pk.dzpk;

import business.global.pk.AbsPKSetRoom;
import business.global.room.base.AbsRoomPos;
import cenum.PKOpType;
import com.ddm.server.common.utils.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * 每一局每个位置信息
 *
 * @author Huaxing
 */
public class DZPKSetPos_AoMaHa extends DZPKSetPos {

    public DZPKSetPos_AoMaHa(int posID, AbsRoomPos roomPos, AbsPKSetRoom set) {
        super(posID, roomPos, set);
    }

    @Override
    public List<PKOpType> receiveOpTypes() {
        getBetOptions().clear();
        if (opType.equals(PKOpType.PASS_CARD)) {
            return new ArrayList<>();
        }

        boolean firstBet = getRoomSet().isFirstBet();
        List<PKOpType> opTypes = Lists.newArrayList();
        opTypes.add(PKOpType.PASS_CARD);
        if (getRoomSet().getLowerBet() > getDeductPoint()) {
            opTypes.add(PKOpType.ALL_IN);
        }
        int lowerBet = getRoomSet().getLowerBet();
        resetOptions(!firstBet);
        if (firstBet) {
            if (getDeductPoint() >= lowerBet) {
                opTypes.add(PKOpType.BET);
                opTypes.add(PKOpType.LET_GO);
            }
        } else {
            long count = getRoomSet().getPosDict().values().stream().filter(absPKSetPos -> ((DZPKSetPos) absPKSetPos).getOpType().equals(PKOpType.ADD_BET)).count();
            long allinCount = getRoomSet().getPosDict().values().stream().filter(absPKSetPos -> ((DZPKSetPos) absPKSetPos).getOpType().equals(PKOpType.ALL_IN)).count();
            if (getDeductPoint() > lowerBet && count + allinCount <= 0) {
                opTypes.add(PKOpType.ADD_BET);
            }
            if (getDeductPoint() >= lowerBet) {
                opTypes.add(PKOpType.FALLOW_BET);
            }
        }
        return opTypes;
    }

    @Override
    public void resetOptions(boolean addBet) {
        int lowerBet = getRoomSet().getLowerBet() + (addBet ? 1 : 0);
        int maxBet = getRoomSet().getTotalBet() + getRoomSet().getDaMangPoint();
        maxBet = getDeductPoint() > maxBet ? maxBet : getDeductPoint();
        if (lowerBet > maxBet) {
            getBetOptions().add(maxBet);
        } else if (maxBet - lowerBet < 4) {
            for (int i = lowerBet; i <= maxBet; i++) {
                getBetOptions().add(i);
            }
        } else {
            int index = (maxBet - lowerBet) / 4;
            for (int i = 0; i < 5; i++) {
                if (i < 4) {
                    getBetOptions().add(lowerBet + i * index);
                } else {
                    getBetOptions().add(maxBet);
                }
            }
        }
    }
}
