package business.global.mj.qzmj;

import cenum.mj.*;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;

import business.global.mj.AbsMJRoundPos;
import business.global.mj.AbsMJSetRound;
import business.global.mj.MJCard;
import business.global.mj.set.MJOpCard;

/**
 * 一个round回合中，可能同时等待多个pos进行操作，eg:抢杠胡
 *
 * @author Administrator
 */
public class QZMJRoundPos extends AbsMJRoundPos {
    public QZMJRoundPos(AbsMJSetRound round, int opPos) {
        super(round, opPos);
    }



    /**
     * 打牌
     *
     * @param request 连接请求
     * @param opType  动作类型
     * @param cardID  牌值
     * @return
     */
    public int opOutCard(WebSocketRequest request, OpType opType, int cardID) {
        // 操作错误
        if (errorOpType(request, opType) <= 0) {
            return MJOpCardError.ERROR_OP_TYPE.value();
        }
        // 检查牌是否存在
        MJCard card = getCardByID(cardID);
        if (null == card) {
            request.error(ErrorCode.NotAllow, "1not find cardID:" + cardID);
            return MJOpCardError.CHECK_OP_TYPE_ERROR.value();
        }
        // 是不是自己身上的牌
        if (!outCard(card)) {
            request.error(ErrorCode.NotAllow, "2not find cardID:" + cardID);
            return MJOpCardError.CHECK_OP_TYPE_ERROR.value();
        }
        ((QZMJSetPos)this.pos).setQiangJinFlag(false);
        // =====================================
        // 记录当前回合操作的牌
        this.setOpCard(cardID);

        ((QZMJSetPos)this.pos).cleanOp();
        // 执行动作
        return this.exeCardAction(opType);
    }
    /**
     * 手上有门牌的操作。
     *
     * @param cardID
     */
    @Override
    protected int getCardOpPos(OpType opType, int cardID) {
        if (OpType.Hu.equals(opType)) {
            // 操作动作
            if (!doOpType(cardID, opType)) {
                return MJOpCardError.ERROR_EXEC_OP_TYPE.value();
            }
        } else {
            return MJOpCardError.NONE.value();
        }
        // 记录操作的动作，并且尝试结束本回合
        this.opTypeTryEndRound(this.opPos, opType, MJCEnum.OpHuType(opType), TryEndRoundEnum.ALL_WAIT);
        return this.opPos;
    }


    // 暂未实现 TODO
    public int op_Default() {
        this.tryEndRound(false);
        return 0;
    }

    @Override
    public int op(WebSocketRequest request, OpType opType, MJOpCard mOpCard) {
        int opCardRet = -1;
        if (this.getOpType() != null) {
            request.error(ErrorCode.NotAllow, "opPos has opered");
            return MJOpCardError.REPEAT_EXECUTE.value();
        }
        switch (opType) {
            case Out:
                opCardRet = opOutCard(request, opType, mOpCard.getOpCard());
                break;
            case AnGang:
                opCardRet = opAnGang(request, opType, mOpCard.getOpCard());
                break;
            case JieGang:
                opCardRet = opJieGang(request, opType);
                break;
            case Gang:
                opCardRet = opGang(request, opType, mOpCard.getOpCard());
                break;
            case Pass:
                opCardRet = opPass(request, opType);
                break;
            case Peng:
                opCardRet = opPeng(request, opType);
                break;
            case Chi:
                opCardRet = opChi(request, opType, mOpCard.getOpCard());
                break;
            case QiangJin:
                opCardRet =op_QiangJin(request, opType,mOpCard.getOpCard());
                break;
            case TianHu:
                opCardRet = op_TianHu(request, opType,mOpCard.getOpCard());
                break;
            case SanJinDao:
                opCardRet = op_SanJinDao(request, opType,mOpCard.getOpCard());
                break;
            case DanYou:
                opCardRet = op_DanYou(request, opType,mOpCard.getOpCard());
                break;

            case SanYou:
                opCardRet = op_SanYou(request, opType,mOpCard.getOpCard());
                break;
            case ShuangYou:
                opCardRet = op_ShuangYou(request, opType,mOpCard.getOpCard());
                break;
            // 自摸.
            case Hu:
            case QiangGangHu:
            case JiePao:
                opCardRet = opHuType(request, opType);
                break;
            case TianTing:
                opCardRet = op_TianTing(request, opType);
                break;
            default:
                break;
        }
        request.response();
        return opCardRet;
    }
    // 3.2报听
    public int op_TianTing(WebSocketRequest request, OpType opType) {
        // 操作错误
        if (errorOpType(request, opType) <= 0) {
            return -1;
        }
        // 执行操作
        int ret = this.opReturn(opType, 0, TryEndRoundEnum.ALL_WAIT);
        if (ret < 0) {
            request.error(ErrorCode.NotAllow, "op :{%s},ret :{%d}", opType.toString(), ret);
            return -1;
        }
        return ret;

    }
    // 天胡
    public int op_TianHu(WebSocketRequest request, OpType opType, int cardID) {
        // 操作错误
        if (errorOpType(request, opType) <= 0) {
            return -1;
        }
        this.setOpType(OpType.TianHu);
        // 记录操作的牌ID
        this.setOpCard(cardID);
        // 执行操作
        int ret = this.opReturn(opType,0,TryEndRoundEnum.ALL_AT_ONCE);
        if(ret < 0) {
            request.error(ErrorCode.NotAllow,"op :{%s},ret :{%d}",opType.toString(),ret);
            return -1;
        }
        QZMJSetPos bSetPos =((QZMJSetPos)this.pos);
        bSetPos.setQzmjHutype(HuType.TianHu);
        return ret;
    }
    // 抢金
    public int op_QiangJin(WebSocketRequest request, OpType opType, int cardID) {
        // 操作错误
        if (errorOpType(request, opType) <= 0) {
            return -1;
        }


        // 记录操作的牌ID
        this.setOpCard(cardID);
        // 执行操作
        int ret = this.opReturn(opType,0,TryEndRoundEnum.ALL_AT_ONCE);
        if(ret < 0) {
            request.error(ErrorCode.NotAllow,"op :{%s},ret :{%d}",opType.toString(),ret);
            return -1;
        }
        //抢金当自摸算
        this.pos.setHuType(HuType.ZiMo);
        QZMJSetPos bSetPos =((QZMJSetPos)this.pos);
        bSetPos.setQzmjHutype(HuType.QiangJin);
        return ret;
    }


    // 四金倒
    public int op_SanJinDao(WebSocketRequest request, OpType opType, int cardID) {
        QZMJSetPos bSetPos =((QZMJSetPos)this.pos);
        // 操作错误
        if (errorOpType(request, opType) <= 0) {
            return -1;
        }
        // 记录操作的牌ID
        this.setOpCard(cardID);
        if(bSetPos.getHandCard()!=null){
            this.set.getMHuInfo().setHuPos(bSetPos.getPosID());
            bSetPos.setHuType(HuType.SanJinDao);
            this.set.getMHuInfo().addHuPos(this.getRound().getRoundID(),bSetPos.getPosID());
            this.opNotHu(this.opPos, opType, TryEndRoundEnum.ALL_AT_ONCE);
            return this.opPos;
        }
        // 执行操作
        int ret = this.opReturn(opType,0,TryEndRoundEnum.ALL_AT_ONCE);
        if(ret < 0) {
            request.error(ErrorCode.NotAllow,"op :{%s},ret :{%d}",opType.toString(),ret);
            return -1;
        }
        bSetPos.setQzmjHutype(HuType.SanJinDao);
        return ret;
    }
    // 单游
    public int op_SanYou(WebSocketRequest request, OpType opType, int cardID) {
        QZMJSetPos bSetPos =((QZMJSetPos)this.pos);
        // 操作错误
        if (errorOpType(request, opType) <= 0) {
            return -1;
        }
        bSetPos.setHuType(HuType.ZiMo);
        this.setOpType(OpType.SanYou);
        this.set.getMHuInfo().setHuPos(bSetPos.getPosID());
        this.set.getMHuInfo().addHuPos(this.getRound().getRoundID(),bSetPos.getPosID());
        // 记录操作的牌ID
        this.setOpCard(cardID);
        // 记录操作的动作，并且尝试结束本回合
        this.opNotHu(this.opPos, opType, TryEndRoundEnum.ALL_AT_ONCE);
        bSetPos.setQzmjHutype(HuType.SanYou);
        return this.opPos;
    }
    // 单游
    public int op_DanYou(WebSocketRequest request, OpType opType, int cardID) {
        QZMJSetPos bSetPos =((QZMJSetPos)this.pos);
        // 操作错误
        if (errorOpType(request, opType) <= 0) {
            return -1;
        }
        bSetPos.setHuType(HuType.ZiMo);
        this.setOpType(OpType.DanYou);
        this.set.getMHuInfo().setHuPos(bSetPos.getPosID());
        this.set.getMHuInfo().addHuPos(this.getRound().getRoundID(),bSetPos.getPosID());
        // 记录操作的牌ID
        this.setOpCard(cardID);
        // 记录操作的动作，并且尝试结束本回合
        this.opNotHu(this.opPos, opType, TryEndRoundEnum.ALL_AT_ONCE);
        bSetPos.setQzmjHutype(HuType.DanYou);
        return this.opPos;
    }
    // 双游
    public int op_ShuangYou(WebSocketRequest request, OpType opType, int cardID) {
        // 操作错误
        if (errorOpType(request, opType) <= 0) {
            return -1;
        }
        QZMJSetPos bSetPos =((QZMJSetPos)this.pos);
        bSetPos.setHuType(HuType.ZiMo);
        this.set.getMHuInfo().setHuPos(bSetPos.getPosID());
        this.set.getMHuInfo().addHuPos(this.getRound().getRoundID(),bSetPos.getPosID());
        // 记录操作的牌ID
        this.setOpCard(cardID);
        // 记录操作的动作，并且尝试结束本回合
        this.opNotHu(this.opPos, opType, TryEndRoundEnum.ALL_AT_ONCE);
        bSetPos.setQzmjHutype(HuType.ShuangYou);
        return this.opPos;
    }

}
