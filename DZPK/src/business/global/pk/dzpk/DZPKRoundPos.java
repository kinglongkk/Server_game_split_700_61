package business.global.pk.dzpk;

import business.global.pk.AbsPKRoundPos;
import business.global.pk.AbsPKSetRound;
import business.global.pk.PKOpCard;
import cenum.PKOpType;
import cenum.mj.MJOpCardError;
import cenum.pk.PKOpCardError;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;

import java.util.Objects;


/**
 * 长汀510K 一个round回合中，可能同时等待多个pos进行操作，eg:抢杠胡
 *
 * @author Administrator
 */
public class DZPKRoundPos extends AbsPKRoundPos {
    private DZPKSetPos cSetPos;

    public DZPKRoundPos(AbsPKSetRound round, int opPos) {
        super(round, opPos);
        this.cSetPos = (DZPKSetPos) getPos();
    }

    /**
     * 操作打牌
     *
     * @param request 连接信息
     * @param opType  操作类型
     * @param mOpCard 操作参数
     * @return
     */
    public int opOutCard(WebSocketRequest request, PKOpType opType, PKOpCard mOpCard) {
        // 操作错误		
        if (errorOpType(request, opType) <= 0) {
            return PKOpCardError.ERROR_OP_TYPE.value();
        }
        if (!cSetPos.doOpType(mOpCard, opType)) {
            request.error(ErrorCode.NotAllow, "not find doOpType cardType:{%d},opType:{%s}", mOpCard.getOpValue(), opType.name());
            return MJOpCardError.CHECK_OP_TYPE_ERROR.value();
        }
        cSetPos.setOpType(opType);
        cSetPos.getRoundOpList().set(((DZPKRoomSet) set).getBet_state(), opType.value());
        return exeCardAction(opType);
    }


    @Override
    public int op(WebSocketRequest request, PKOpType opType, PKOpCard mOpCard) {
        if (Objects.nonNull(this.getOpType())) {
            request.error(ErrorCode.NotAllow, "opPos has opered");
            return PKOpCardError.REPEAT_EXECUTE.value();
        }
        int opCardRet = -1;

        switch (opType) {
            case ADD_BET:
            case BET:
                //如果是全部压进去 则为allin
                if (mOpCard.getOpValue() == cSetPos.getDeductPoint()) {
                    opType = PKOpType.ALL_IN;
                    if (!getReceiveOpTypes().contains(PKOpType.ALL_IN)) {
                        getReceiveOpTypes().add(PKOpType.ALL_IN);
                    }
                }
            case PASS_CARD:
            case FALLOW_BET:
            case LET_GO:
            case ALL_IN:
                opCardRet = this.opOutCard(request, opType, mOpCard);
                break;
            default:

        }
        request.response();
        return opCardRet;
    }

    @Override
    protected int getCardOpPos(PKOpType opType, int cardID) {
        return 0;
    }

    @Override
    public boolean isEnd(PKOpType opType, AbsPKSetRound round) {
        ((DZPKRoomSet) set).checkAllOpOver();
        if (((DZPKRoomSet) set).getBet_state() == DZPKRoomEnum.DZPK_BET_STATE.END.ordinal()) {
            round.setSetEnd(true);
            return true;
        }
        return false;
    }
}		
