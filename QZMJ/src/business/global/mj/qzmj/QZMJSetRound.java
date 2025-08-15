package business.global.mj.qzmj;

import business.global.mj.AbsMJSetPos;
import business.global.mj.qzmj.optype.QZMJKaiJinJJImpl;
import cenum.mj.MJOpCardError;
import com.ddm.server.common.utils.CommTime;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;

import business.global.mj.AbsMJRoundPos;
import business.global.mj.AbsMJSetRoom;
import business.global.mj.AbsMJSetRound;
import business.global.mj.manage.MJFactory;
import business.global.mj.set.MJOpCard;
import business.qzmj.c2s.iclass.SQZMJ_PosOpCard;
import business.qzmj.c2s.iclass.SQZMJ_StartRound;
import cenum.mj.OpType;
import com.ddm.server.websocket.handler.requset.WebSocketRequestDelegate;
import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.mj.BaseMJRoom_RoundPos;
import jsproto.c2s.cclass.mj.BaseMJRoom_SetRound;
import jsproto.c2s.cclass.mj.BaseMJSet_Pos;
import jsproto.c2s.cclass.mj.NextOpType;

import java.util.Arrays;
import java.util.List;

/**
 * 麻将 回合逻辑 每一次等待操作，都是一个round
 *
 * @author Huaxing
 */

public class QZMJSetRound extends AbsMJSetRound {

    public QZMJSetRound(AbsMJSetRoom set, int roundID) {
        super(set, roundID);
    }

    @Override
    public synchronized int opCard(WebSocketRequest request, int opPos, OpType opType, MJOpCard mOpCard) {
        if (this.getEndTime() > 0){
            request.error(ErrorCode.NotAllow, "end Time opPos has no round power");
            return MJOpCardError.ERROR_OP_TYPE.value();
        }
        AbsMJRoundPos pos = this.roundPosDict.get(opPos);
        if (null == pos) {
            request.error(ErrorCode.NotAllow, "opPos has no round power");
            return MJOpCardError.ROUND_POS_ERROR.value();
        }
        int opCardRet = pos.op(request, opType, mOpCard);
        if (opCardRet >= 0) {
            // 位置操作牌
            this.posOpCardRet(opCardRet, false);
        }
        return opCardRet;
    }

    // / ==================================================
    @Override
    public AbsMJRoundPos nextPosOpType(AbsMJRoundPos nextPos) {
        if (nextPos.getPos().checkOpType(0, OpType.TingYouJin)) {
            nextPos.addOpType(OpType.TingYouJin);
        } else {
            if (nextPos.getPos().checkOpType(0, OpType.Ting)) {
                nextPos.addOpType(OpType.Ting);
            }
        }
//        if (nextPos.getPos().checkOpType(0, OpType.Gang)) {
//            nextPos.addOpType(OpType.Gang);
//        }
//        if (nextPos.getPos().checkOpType(0, OpType.AnGang)) {
//            nextPos.addOpType(OpType.AnGang);
//        }
        nextPos.addOpType(OpType.Out);
        return nextPos;
    }

    /**
     * 开始本回合,并摸牌
     *
     * @param pos
     * @param isNormalMo
     * @return
     */
    @Override
    public boolean startWithGetCard(int pos, boolean isNormalMo) {
        // 抓牌
        // 作弊情况下，已经有手牌
        if (null == this.set.getMJSetPos(pos).getHandCard()) {
            if (null == this.set.getCard(pos, isNormalMo)) {
                return false;
            }
        }
        if (this.set.isAtFirstHu()) {
            this.set.setAtFirstHu(false);
            this.set.getSetPosMgr().startSetApplique();
            MJFactory.getOpCard(QZMJKaiJinJJImpl.class).checkOpCard(this.set.getMJSetPos(this.set.getDPos()), 0);
            this.set.sendSetPosCard();
            this.set.getSetPosMgr().checkOpType(this.set.getDPos(), 0, OpType.TianHu);
            NextOpType nOpType = this.set.getSetPosMgr().exeCardAction(OpType.TianHu);
            if (null != nOpType) {
                for (int posID : nOpType.getPosOpTypeListMap().keySet()) {
                    AbsMJRoundPos nextPos = this.nextRoundPos(posID);
                    if (nOpType.getPosOpTypeListMap().containsKey(posID)) {
                        nextPos.addOpType(nOpType.getPosOpTypeListMap().get(posID));
                    }
                    nextPos.addOpType(OpType.Pass);
                    this.roundPosDict.put(nextPos.getOpPos(), nextPos);
                }
                return true;
            }

            ((QZMJRoomSet)this.set).setTianTingCheckFlag(true);
            this.set.getSetPosMgr().checkOpType(this.set.getDPos(), 0, OpType.TianTing);
            NextOpType nOpType2 = this.set.getSetPosMgr().exeCardAction(OpType.TianTing);
            if (null != nOpType2) {
                for (int posID : nOpType2.getPosOpTypeListMap().keySet()) {
                    AbsMJRoundPos nextPos = this.nextRoundPos(posID);
                    if (nOpType2.getPosOpTypeListMap().containsKey(posID)) {
                        nextPos.addOpType(nOpType2.getPosOpTypeListMap().get(posID));
                    }
                    nextPos.addOpType(OpType.Pass);
                    this.roundPosDict.put(nextPos.getOpPos(), nextPos);
                }
                return true;
            }

        }
        return MJRoundPos(pos);
    }

    /**
     * 天听
     *
     * @param pos
     * @return
     */
    private void tianTing(int pos) {
        AbsMJRoundPos tmPos = this.nextRoundPos(pos);
        QZMJSetPos setPos=(QZMJSetPos) this.getSet().getPosDict().get(pos);
        if(setPos.checkTianTing()){
            ((QZMJRoomSet)this.set).setTianTingFlag(true);
            tmPos.addOpType(Arrays.asList(OpType.TianTing, OpType.Pass));
            this.roundPosDict.put(tmPos.getOpPos(), tmPos);
        }
    }
    /**
     * 位置操作牌
     */
    @Override
    public <T> BaseSendMsg posOpCard(long roomID, int pos, T set_Pos, OpType opType, int opCard, boolean isFlash) {
       QZMJSetPosMgr setPosMgr = (QZMJSetPosMgr) set.getSetPosMgr();
        return SQZMJ_PosOpCard.make(roomID, pos, set_Pos, opType, opCard, isFlash, (List<T>) setPosMgr.getAllNotify(pos));
    }

    @Override
    protected <T> BaseSendMsg startRound(long roomID, T room_SetWait) {
        return SQZMJ_StartRound.make(roomID, room_SetWait);
    }



    @Override
    protected AbsMJRoundPos nextRoundPos(int pos) {
        return new QZMJRoundPos(this, pos);
    }

    @Override
    protected boolean tryStartRoundOther(AbsMJSetRound preRound) {
        // 点完天听进入下一个回合  由庄家的下一家 摸牌打牌
        if (OpType.TianTing.equals(preRound.getOpType())) {
            return tryStartRoundTianTing(preRound);

        }
        return false;
    }
    /**
     * 天听打牌  由庄家开始打牌
     *
     * @param preRound
     * @return
     */
    protected boolean tryStartRoundTianTing(AbsMJSetRound preRound) {
        int opPos = this.getSet().getDPos();
        // 出牌对应的接手操作
        if (preRound.checkExistNextRoundOp()) {
            // 检查下回合操作位置
            return this.checkNextRoundOpPos(preRound);
        }
        // 无人接手
        else {
            MJRoundPos(opPos);
            notifyStart();
            return true;
        }
    }
    /**
     * 过  天听的时候点过进入  此时没有人有手牌 由庄家的下一家继续摸牌打牌
     *
     * @param preRound
     * @return
     */
    protected boolean tryStartRoundPass(AbsMJSetRound preRound) {
        QZMJRoomSet roomSet=(QZMJRoomSet)this.getSet();
        if (preRound.getWaitDealRound() != null) {
            preRound = preRound.getWaitDealRound();
        }
        // 上次的出牌，需要继续处理
        if (preRound.checkExistNextRoundOp()) {
            // 检查下回合操作位置
            return this.checkNextRoundOpPos(preRound);
        } else {
            //其他操作
            if(passOther()){
                return true;
            }
            //判断是否进入过天听 没有进入的话 进入天听
            if(!roomSet.isTianTingCheckFlag()){
                roomSet.setTianTingCheckFlag(true);
                this.set.getSetPosMgr().checkOpType(this.set.getDPos(), 0, OpType.TianTing);
                NextOpType nOpType2 = this.set.getSetPosMgr().exeCardAction(OpType.TianTing);
                if (null != nOpType2) {
                    for (int posID : nOpType2.getPosOpTypeListMap().keySet()) {
                        AbsMJRoundPos nextPos = this.nextRoundPos(posID);
                        if (nOpType2.getPosOpTypeListMap().containsKey(posID)) {
                            nextPos.addOpType(nOpType2.getPosOpTypeListMap().get(posID));
                        }
                        nextPos.addOpType(OpType.Pass);
                        this.roundPosDict.put(nextPos.getOpPos(), nextPos);
                    }
                    notifyStart();
                    return true;
                }
            }
            // 检查是否直接过
            if (this.checkPass()) {
                return true;
            } else if (preRound.getOpType() == OpType.Out) {
                // 无法再处理了，下家抓牌
                // 获取用户位置ID
                return checkQtherPing();
            } else if (preRound.getOpType() == OpType.Gang) {
                return checkQtherQiang();
            }
            return true;
        }
    }
    /**
     * 检查天胡 庄家下架抓牌开始
     */
    public boolean checkQtherTian() {
        // 继续抓牌
        int opPos = (this.set.getDPos() + 1) % this.room.getPlayerNum();
        if (!startWithGetCard(opPos, false)) {
            return false;
        }
        notifyStart();
        return true;
    }
    /**
     * T：同一圈内，没过手（过手是摸牌，吃，碰杠都算），就不能碰漏掉的那张牌；(过圈)。
     */
    @Override
    protected boolean checkExistClearPass() {
        return true;
    }

    /**
     * 获取本轮信息
     *
     * @param pos 位置
     * @return
     */

    public BaseMJRoom_SetRound getNotify_RoundInfo(int pos) {
        ret = new BaseMJRoom_SetRound();
        ret.setWaitID(this.roundID);
        ret.setStartWaitSec(this.startTime);
        for (AbsMJRoundPos roundPos : this.roundPosDict.values()) {
            if (roundPos.getOpType() != null) {
                continue;
            }
            // 自己 或 公开
            if (pos == roundPos.getOpPos() || roundPos.isPublicWait()) {
                BaseMJRoom_RoundPos data = new BaseMJRoom_RoundPos();
                boolean isSelf = pos == roundPos.getOpPos();
                data.setOpList(roundPos.getRecieveOpTypes());
                data.setChiList(roundPos.getPos().getPosOpNotice().getChiList());
                data.setLastOpCard(roundPos.getLastOutCard());
                data.setWaitOpPos(roundPos.getOpPos());
                data.setTingCardMap(isSelf ? roundPos.getPos().getPosOpNotice().getTingCardMap() : null);
                if (this.isBuChiFuDaFu()) {
                    data.setBuChuList(isSelf ? roundPos.getPos().getPosOpNotice().getBuNengChuList() : null);
                }
                data.setTingYouList(((QZMJSetPos)roundPos.getPos()).tingYous);
                ret.addOpPosList(data);
                // 设置动作列表
                roundPos.getPos().getPosOpRecord().setOpList(data.getOpList());
                // 重新记录打牌时间
                roundPos.getPos().getRoomPos().setLatelyOutCardTime(CommTime.nowMS());
                // 设置最后操作时间
                this.set.getLastOpInfo().setLastShotTime(CommTime.nowSecond());

            }
        }
        return ret;
    }

    /**
     * 自动打牌
     *
     * @param sec
     * @return
     */
    @Override
    protected boolean autoOutCard(int sec) {
        if (sec - this.startTime < 1) {
            return false;
        }
        QZMJRoundPos roundPos;
        QZMJSetPos sPos;
        for (int posID = 0; posID < this.room.getPlayerNum(); posID++) {
            roundPos = (QZMJRoundPos) this.roundPosDict.get(posID);
            if (null == roundPos) {
                continue;
            }
            sPos = (QZMJSetPos) roundPos.getPos();
            if (null == sPos || sPos.getRoomPos().isTrusteeship()) {
                continue;
            }

            List<OpType> opList = roundPos.getRecieveOpTypes();
            if (null == opList || opList.size() <= 0) {
                continue;
            }
            if (sPos.isTing()) {
                int cardID = 0;
                if (opList.contains(OpType.Hu)) {
                    this.opCard(new WebSocketRequestDelegate(), roundPos.getOpPos(), OpType.Hu, MJOpCard.OpCard(cardID));
                } else if (opList.contains(OpType.JiePao)) {
                    this.opCard(new WebSocketRequestDelegate(), roundPos.getOpPos(), OpType.JiePao, MJOpCard.OpCard(cardID));
                }
//                else if (opList.contains(OpType.AnGang) && sPos.getHandCard() != null) {
//                    this.opCard(new WebSocketRequestDelegate(), roundPos.getOpPos(), OpType.AnGang,
//                            MJOpCard.OpCard(sPos.getHandCard().getCardID()));
//                } else if (opList.contains(OpType.JieGang)) {
//                    this.opCard(new WebSocketRequestDelegate(), roundPos.getOpPos(), OpType.JieGang, MJOpCard.OpCard(0));
//                }
                else if (opList.contains(OpType.Out)) {
                    if (null != sPos.getHandCard()) {
                        cardID = sPos.getHandCard().cardID;
                    } else {
                        continue;
                    }
                    this.opCard(new WebSocketRequestDelegate(), roundPos.getOpPos(), OpType.Out, MJOpCard.OpCard(cardID));
                }else {
                    this.opCard(new WebSocketRequestDelegate(), roundPos.getOpPos(), OpType.Pass, MJOpCard.OpCard(cardID));
                }
            }
        }
        return false;
    }
    /**
     * 检查是否直接过
     *
     * @return
     */
    protected boolean checkPass() {
        for (int i = 0; i < this.room.getPlayerNum(); i++) {
            if (set.getMJSetPos(i).getHandCard() != null) {
                QZMJSetPos setPos=(QZMJSetPos)set.getMJSetPos(i);
                AbsMJRoundPos nextPos = this.nextRoundPos(i);
                if (setPos.checkOpType(0, OpType.TingYouJin)) {
                    nextPos.addOpType(OpType.TingYouJin);
                }
                nextPos.addOpType(OpType.Out);
                this.roundPosDict.put(nextPos.getOpPos(), nextPos);
                notifyStart();
                return true;
            }
        }
        return false;
    }
}

