package business.global.mj.qzmj;

import business.global.mj.manage.MJFactory;
import business.global.mj.qzmj.optype.QZMJBuHuaImpl;
import business.qzmj.c2s.iclass.*;
import cenum.mj.FlowerEnum;
import cenum.mj.OpType;
import com.ddm.server.common.utils.CommTime;

import business.global.mj.AbsMJSetPos;
import business.global.mj.AbsMJSetPosMgr;
import business.global.mj.AbsMJSetRoom;
import business.global.mj.AbsMJSetRound;
import business.global.mj.MJCard;
import business.global.mj.qzmj.QZMJRoomEnum.QZMJDiFen;
import business.global.room.mj.MJRoomPos;
import business.qzmj.c2s.cclass.QZMJRoomSetEnd;
import business.qzmj.c2s.cclass.QZMJRoomSetInfo;
import business.qzmj.c2s.cclass.QZMJSetRoonCfg;
import cenum.PrizeType;
import cenum.mj.MJSpecialEnum;
import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.mj.BaseMJSet_Pos;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 仙游麻将 一局游戏逻辑
 *
 * @author Huaxing
 */
public class QZMJRoomSet extends AbsMJSetRoom {
    // 设置局数配置
    private QZMJSetRoonCfg qSetRoonCfg = new QZMJSetRoonCfg();
    private boolean tianTingFlag=false;//是有人可以进行天听状态
    private boolean tianTingCheckFlag=false;//天听是否检查过
    public QZMJRoomSet(int setID, QZMJRoom room, int dPos) {
        super(setID, room, dPos);
        this.startMS = CommTime.nowMS();
        // 回放记录添加游戏配置
        this.addGameConfig();
        this.startSet();
    }


    public boolean isTianTingCheckFlag() {
        return tianTingCheckFlag;
    }

    public void setTianTingCheckFlag(boolean tianTingCheckFlag) {
        this.tianTingCheckFlag = tianTingCheckFlag;
    }

    public boolean isTianTingFlag() {
        return tianTingFlag;
    }

    public void setTianTingFlag(boolean tianTingFlag) {
        this.tianTingFlag = tianTingFlag;
    }

    /**
     * 连庄
     *
     * @return
     */
    public int lianZhuang() {
        return getRoom().getEvenDpos();
    }

    /**
     * 庄家
     *
     * @return
     */
    public int getZhuang() {
        if (PrizeType.Gold.equals(this.getRoom().getBaseRoomConfigure().getPrizeType())) {
            return ((getRoom().getBaseMark()));
        } else {
            return QZMJDiFen.diFen.value();
        }
    }
    public int getLimitScore() {
        return 100;
    }
    /**
     * 闲家
     *
     * @return
     */
    public int getXian() {
        if (PrizeType.Gold.equals(this.getRoom().getBaseRoomConfigure().getPrizeType())) {
            return getRoom().getBaseMark();
        } else {
            return QZMJDiFen.diFen.value();
        }
    }

    /**
     * 本局结算， 计算下一局的坐庄信息
     * 第一局由坐庄由房主为庄家，此后谁胡谁坐庄；
     * 	臭庄时由上局庄家继续做庄；
     * 	臭庄也算连庄；
     */
    @Override
    public int calcNextDPos() {
        // 庄家胡牌、流局，庄家坐庄
        if (this.getMHuInfo().isHuNotEmpty()) {
            if (this.getMHuInfo().getHuPos() == this.dPos) {
                this.room.setEvenDpos(this.room.getEvenDpos() + 1);
                return this.dPos;
            } else {
                this.room.setEvenDpos(1);
                return this.getMHuInfo().getHuPos();
            }
        } else {
            this.room.setEvenDpos(this.room.getEvenDpos() + 1);
            return this.dPos;
        }
    }

    /**
     * 麻将当局结算
     *
     * @return
     */
    @Override
    protected QZMJRoomSetEnd newMJRoomSetEnd() {
        return new QZMJRoomSetEnd();
    }

    /**
     * 一局结束的信息
     */
    @Override
    public QZMJRoomSetEnd getNotify_setEnd() {
        QZMJRoomSetEnd setEndInfo = (QZMJRoomSetEnd) this.mRoomSetEnd();
        setEndInfo.setJin(this.getmJinCardInfo().getJin(1).getCardID());
        return setEndInfo;
    }

    /**
     * 开金通知
     */
    @Override
    public void kaiJinNotify(MJCard jinCard, MJCard jinCard2) {
        // 开金通知
        setJinJin(jinCard2.getCardID());
        getRoomPlayBack().playBack2All(SQZMJ_Jin.make(getRoom().getRoomID(), jinCard.getCardID(), 0,
                jinCard2.getCardID(), getMJSetCard().getRandomCard().getNormalMoCnt(),
                getMJSetCard().getRandomCard().getGangMoCnt()));
    }

    /**
     * 设置开金原牌
     *
     * @param jin
     */
    public void setJinJin(int jin) {
        if (this.qSetRoonCfg.getJinJin() == jin) {
            return;
        }
        this.qSetRoonCfg.setJinJin(jin);
    }

    /**
     * 游戏技术开金配置
     *
     * @return
     */
    private QZMJSetRoonCfg getRoomSetCfg() {
        return this.qSetRoonCfg;
    }

    /**
     * 麻将补花
     */
    @Override
    public void MJApplique(int pos) {
        // 福州麻将没有补花，所有无需使用。
        AbsMJSetPos setPos = posDict.get(pos);
        BaseMJSet_Pos posInfoOther = setPos.getNotify(false);
        BaseMJSet_Pos posInfoSelf = setPos.getNotify(true);
        getRoomPlayBack().playBack2Pos(pos,
                SQZMJ_Applique.make(getRoom().getRoomID(), pos, OpType.Out, 0, false, posInfoSelf), null);
        this.room.getRoomPosMgr().notify2ExcludePosID(pos,
                SQZMJ_Applique.make(getRoom().getRoomID(), pos, OpType.Out, 0, false, posInfoOther));
    }

    /**
     * 发送设置位置的牌
     */
    @Override
    public void sendSetPosCard() {
        for (int i = 0; i < room.getPlayerNum(); i++) {
            AbsMJSetPos setPos = posDict.get(i);
            setPos.sortCards();
        }
        for (int i = 0; i < room.getPlayerNum(); i++) {
            long pid = this.getRoom().getRoomPosMgr().getPosByPosID(i).getPid();
            if (i == 0) {
                this.getRoomPlayBack().playBack2Pos(i,
                        SQZMJ_SetPosCard.make(this.room.getRoomID(), this.setPosCard(pid)), null);
            } else {
                this.room.getRoomPosMgr().notify2Pos(i,
                        SQZMJ_SetPosCard.make(this.room.getRoomID(), this.setPosCard(pid)));
            }
        }
    }

    /**
     * 摸牌消息
     */
    @Override
    protected <T> BaseSendMsg posGetCard(long roomID, int pos, int normalMoCnt, int gangMoCnt, T set_Pos) {
        return SQZMJ_PosGetCard.make(roomID, pos, normalMoCnt, gangMoCnt, set_Pos,this.setCard.getRandomCard().getSize());
    }

    /**
     * 下回合操作位置
     */
    @Override
    protected AbsMJSetRound nextSetRound(int roundID) {
        return new QZMJSetRound(this, roundID);
    }

    /**
     * 小局结算消息
     */
    @Override
    protected <T> BaseSendMsg setEnd(long roomID, T setEnd) {
        return SQZMJ_SetEnd.make(roomID, setEnd);
    }

    /**
     * 玩家位置信息
     */
    @Override
    protected AbsMJSetPos absMJSetPos(int posID) {
        return new QZMJSetPos(posID, (MJRoomPos) this.room.getRoomPosMgr().getPosByPosID(posID), this);
    }

    /**
     * 本局牌管理
     */
    @Override
    protected void absMJSetCard() {
        // 设置当局牌
        this.setSetCard(new QZMJSetCard(this));
    }

    /**
     * 牌局开始消息通知
     */
    @Override
    protected <T> BaseSendMsg setStart(long roomID, T setInfo) {
        return SQZMJ_SetStart.make(roomID, setInfo);
    }

    /**
     * 本局玩家操作管理
     */
    @Override
    protected AbsMJSetPosMgr absMJSetPosMgr() {
        return new QZMJSetPosMgr(this);
    }

    /**
     * 计算当局每个pos位置的分数。
     */
    @Override
    protected void calcCurSetPosPoint() {
        // 计算位置小局分数
        this.getPosDict().values().forEach(k -> k.calcPosPoint());
        //一课分数结算
        calYiKaoPoint(((QZMJRoom)this.room).isYiKe(),this.getLimitScore());
        for (int index = 0; index < room.getPlayerNum(); index++) {
            AbsMJSetPos pos = posDict.get(index);
            ((QZMJCalcPosEnd) pos.getCalcPosEnd()).calcPosPoint();
        }
        // 其他特殊结算 连庄记录
        this.calcOtherPoint();
        
    }
    /**
     * 其他特殊结算
     */
    public void calcOtherPoint() {
        // 庄家胡牌、流局，庄家坐庄
        long count = 0;
        if (((QZMJRoom) getRoom()).isYiKe()) {
            count = posDict.values().stream().filter(m -> m.getEndPoint() + m.getRoomPos().getPoint() + getLimitScore() <= 0).count();
        }
        if (this.room.getCurSetID() == this.room.getCount() || count > 0) {
            ((QZMJRoom) room).isEnd = true;
            return;
        }
        int evenDposCount = 0;
        if (this.getMHuInfo().getHuPos() != -1) {
            if (this.getMHuInfo().getHuPos() == this.dPos) {
                evenDposCount = this.room.getEvenDpos() + 1;
                addLianZhuang(this.dPos, evenDposCount);
            }
        } else {
            evenDposCount = this.room.getEvenDpos() + 1;
            addLianZhuang(this.dPos, evenDposCount);
        }
    }
    /**
     * 牌数
     */
    @Override
    public int cardSize() {
        return MJSpecialEnum.SIZE_16.value();
    }

    /**
     * 清空数据
     */
    @Override
    public void clear() {
        super.clear();
    }

    /**
     * 清空BO数据
     */
    @Override
    public void clearBo() {
        super.clearBo();
    }

    /**
     * 创建新的当局麻将信息
     */
    @Override
    protected QZMJRoomSetInfo newMJRoomSetInfo() {
        return new QZMJRoomSetInfo();
    }

    /**
     * 获取通知当局信息
     */
    @Override
    public QZMJRoomSetInfo getNotify_set(long pid) {
        QZMJRoomSetInfo ret = (QZMJRoomSetInfo) this.getMJRoomSetInfo(pid);
        // 金
        ret.setJin(this.getmJinCardInfo().getJin(1).getCardID());
        ret.setRoomSetCfg(this.getRoomSetCfg());
        return ret;
    }

    /**
     * 开金数
     */
    @Override
    public int kaiJinNum() {
        return 1;
    }

    /**
     * 是否白板替金
     */
    @Override
    public boolean isBaiBanTiJin() {
        return false;
    }

    /**
     * 回放记录添加游戏配置
     */
    @Override
    public void addGameConfig() {
        this.getRoomPlayBack().addPlaybackList(SQZMJ_Config.make(this.getRoom().getCfg(),this.getRoom().getRoomTyepImpl().getRoomTypeEnum()), null);
    }
    /**
     * 配置文件是否需要游戏名
     *
     * @return T:需要,F:不需要
     */
    public boolean isConfigName() {
        return true;
    }
    /**
     * 摸牌
     *
     * @param opPos
     * @param isNormalMo
     * @return
     */
    public MJCard getCard(int opPos, boolean isNormalMo) {
        QZMJSetPos setPos = (QZMJSetPos)this.posDict.get(opPos);
        // 随机摸牌
        MJCard card = this.setCard.pop(isNormalMo, this.getGodInfo().godHandCard(setPos));
        if (Objects.isNull(card)) {
            // 黄庄位置
            this.getMHuInfo().setHuangPos(opPos);
            return null;
        }
        setPos.setQiangJinFlag(false);
        // 设置牌
        setPos.getCard(card);
        // 通知房间内的所有玩家，指定玩家摸牌了。
        this.notify2GetCard(setPos);
        //摸到花牌 进行补花
        if (card.type >= MJSpecialEnum.FENG.value()) {
            MJFactory.getOpCard(QZMJBuHuaImpl.class).checkOpCard(setPos, FlowerEnum.HAND_CARD.ordinal());
        }
        return card;
    }

}
