package business.global.pk.dzpk;

import business.dzpk.c2s.cclass.DZPK_RoundPos;
import business.dzpk.c2s.iclass.SDZPK_PosOpCard;
import business.dzpk.c2s.iclass.SDZPK_StartRound;
import business.global.pk.*;
import cenum.PKOpType;
import cenum.mj.MJOpCardError;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.utils.CommTime;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.pk.base.BasePKRoom_SetRound;
import jsproto.c2s.cclass.pk.base.BasePKSet_Pos;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * 长汀510K 回合逻辑 每一次等待操作，都是一个round
 *
 * @author Huaxing
 */
@Setter
@Getter
public class DZPKSetRound extends AbsPKSetRound {
    /**
     * 当前局
     */
    private DZPKRoomSet roomSet;

    public DZPKSetRound(AbsPKSetRoom set, int roundID) {
        super(set, roundID);
        this.roomSet = (DZPKRoomSet) set;
    }


    @Override
    protected boolean autoOutCard(int sec) {
        return false;
    }

    @Override
    protected <T> BaseSendMsg startRound(long roomID, T room_SetWait) {
        return SDZPK_StartRound.make(roomID, room_SetWait);
    }

    @Override
    protected AbsPKRoundPos nextRoundPos(int pos) {
        return new DZPKRoundPos(this, pos);
    }

    @Override
    public synchronized int opCard(WebSocketRequest request, int opPos, PKOpType opType, PKOpCard mOpCard) {
        if (this.getEndTime() > 0){
            request.error(ErrorCode.NotAllow, "end Time opPos has no round power");
            return MJOpCardError.ERROR_OP_TYPE.value();
        }
        AbsPKRoundPos pos = this.getRoundPosDict().get(opPos);
        if (Objects.nonNull(pos)) {
            int opCardRet = pos.op(request, opType, mOpCard);
            if (opCardRet >= 0) {
                this.posOpCardRet(opCardRet, mOpCard, false);
            }
            return opCardRet;
        }
        request.error(ErrorCode.NotAllow, "opPos has no round power");
        return MJOpCardError.ROUND_POS_ERROR.value();
    }


    protected <T> BaseSendMsg posOpCard(long roomID, int pos, T set_Pos, PKOpType opType, PKOpCard mOpCard,
                                        boolean isFlash) {
        SDZPK_PosOpCard ret = new SDZPK_PosOpCard();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.set_Pos = set_Pos;
        ret.opType = opType;
        ret.cardType = mOpCard.getOpValue();
        ret.opCards = mOpCard.getCardList();
        ret.isFlash = isFlash;
        ret.setTotalBetPoint(getRoomSet().getTotalBet());
        ret.setRoundBetPoint(getRoomSet().getRoundBet());
        return ret;
    }


    /**
     * 尝试开始回合, 如果失败，则set结束
     *
     * @return
     */
    @Override
    public boolean tryStartRound() {
        // 前一个可参考的操作round		
        AbsPKSetRound preRound = getPreRound();
        if (Objects.isNull(preRound) || roomSet.isFirstBet()) {
            int posId = (((DZPKRoomSet) set).getFirstPos());
            if (!startWithGetCard(posId)) {
                return false;
            }
            this.notifyStart();
            return true;
        } else {
            int opPos = preRound.getExeOpPos();
            opPos = this.roomSet.nextOpPos(opPos);
            if (opPos == -1) {
                return false;
            }
            if (!getOpTypeList(opPos)) {
                return false;
            }
            notifyStart();
            return true;
        }
    }

    /**
     * 当局第一回合
     *
     * @param pos 回合位置
     * @return
     */
    public boolean startWithGetCard(int pos) {
        //重置所有玩家操作
        DZPKSetPos setPos;
        setPos = (DZPKSetPos) this.getSet().getPosDict().get(pos);
        if (Objects.isNull(setPos)) {
            CommLogD.error("DZPKSetRound startWithGetCard Pos:{}", pos);
            return false;
        }
        return getOpTypeList(pos);
    }

    /**
     * 获取本轮信息
     *
     * @param pos 位置
     * @return
     */
    @Override
    public BasePKRoom_SetRound getNotify_RoundInfo(int pos) {
        ret = new BasePKRoom_SetRound();
        ret.setWaitID(this.roundID);
        ret.setStartWaitSec(this.startTime);
        for (AbsPKRoundPos roundPos : this.roundPosDict.values()) {
            if (Objects.nonNull(roundPos.getOpType())) {
                continue;
            }
            boolean isSelf = pos == roundPos.getOpPos();
            // 自己 或 公开		
            if (isSelf || roundPos.isPublicWait()) {
                DZPK_RoundPos data = new DZPK_RoundPos();
                data.setOpList(roundPos.getReceiveOpTypes());
                data.setWaitOpPos(roundPos.getOpPos());
                DZPKSetPos setPos = (DZPKSetPos) set.getPKSetPos(pos);
                data.setBetOptions(setPos.getBetOptions());
                ret.addOpPosList(data);
                // 重新记录打牌时间		
                roundPos.getPos().getRoomPos().setLatelyOutCardTime(CommTime.nowMS());
                // 设置最后操作时间		
                this.set.setLastShotTime(CommTime.nowSecond());

            }
        }
        return ret;
    }

    @Deprecated
    @Override
    protected <T> BaseSendMsg posOpCard(long roomID, int pos, T set_Pos, PKOpType opType, int opCard, boolean isFlash) {
        return null;
    }

    /**
     * 位置操作牌
     *
     * @param opPosRet 操作位置
     * @param isFlash  是否动画
     */
    protected void posOpCardRet(int opPosRet, PKOpCard mOpCard, boolean isFlash) {
        AbsPKSetPos sPos = this.set.getPKSetPos(opPosRet);
        this.setExeOpPos(opPosRet);
        BasePKSet_Pos posInfoOther = sPos.getNotify(false);
        BasePKSet_Pos posInfoSelf = sPos.getNotify(true);
        this.set.getRoomPlayBack().playBack2Pos(opPosRet, this.posOpCard(this.room.getRoomID(), opPosRet, posInfoSelf
                , this.getOpType(), mOpCard, isFlash), set.getSetPosMgr().getPKAllPlayBackNotify());
        this.set.getRoom().getRoomPosMgr().notify2ExcludePosID(opPosRet, this.posOpCard(this.room.getRoomID(),
                opPosRet, posInfoOther, this.getOpType(), mOpCard, isFlash));
    }

    /**
     * 机器人操作
     *
     * @param posID
     */
    @Override
    public void RobothandCrad(int posID) {
        if (this.getEndTime() > 0) {
            return;
        }
        if (this.getRoundPosDict().containsKey(posID)) {
            new DZPKRobotOpCard(this).RobothandCrad(posID);
        }
    }

    /**
     * 通知回合开始
     */
    @Override
    public void notifyStart() {
        BaseSendMsg other = this.startRound(this.set.getRoom().getRoomID(), this.getNotify_RoundInfo(-1));
        for (int posID = 0; posID < this.room.getPlayerNum(); posID++) {
            if (this.roundPosDict.containsKey(posID)) {
                this.set.getRoomPlayBack().playBack2Pos(posID, this.startRound(this.set.getRoom().getRoomID(),
                        this.getNotify_RoundInfo(posID)), set.getSetPosMgr().getPKAllPlayBackNotify());
            } else {
                this.set.getRoom().getRoomPosMgr().notify2PosClearLatelyOutCardTime(posID, other);
            }
        }
    }
}		
