package business.global.pk.dzpk;

import business.dzpk.c2s.cclass.DZPKFaPai;
import business.dzpk.c2s.cclass.DZPKRoomSetInfo;
import business.dzpk.c2s.cclass.DZPKRoom_SetEnd;
import business.dzpk.c2s.iclass.*;
import business.global.pk.*;
import business.global.room.base.RoomPlayBack;
import cenum.PKOpType;
import cenum.PrizeType;
import cenum.mj.MJSpecialEnum;
import cenum.room.SetState;
import cenum.room.TrusteeshipState;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.utils.CommMath;
import com.ddm.server.common.utils.CommTime;
import com.ddm.server.common.utils.Random;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.pk.base.BasePKRoom_SetEnd;
import jsproto.c2s.cclass.pk.base.BasePKSet_Pos;
import jsproto.c2s.cclass.room.RoomSetInfo;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 德州扑克 一局游戏逻辑
 *
 * @author Huaxing
 */

public class DZPKRoomSet extends AbsPKSetRoom {
    /**
     * 大盲位置
     */
    private int randomDaMang;
    /**
     * 下注列表
     */
    private List<Integer> betList = new ArrayList<>();
    /**
     * 总注
     */
    private int totalBet;
    /**
     * 每轮
     */
    private int roundBet;

    /**
     * 第几次下注
     */
    private int bet_state = DZPKRoomEnum.DZPK_BET_STATE.DI_PAI.ordinal();
    /**
     * 新下注开始
     */
    private boolean firstBet = true;
    /**
     * 是否 有人加注/下注的位置
     */
    private int addBetPos = -1;
    /**
     * 让牌位置
     */
    private int letGoPos = -1;
    /**
     * 上家下注金额  最低下注
     */
    private int lowerBet;

    /**
     * @param setID
     * @param room
     * @param dPos
     */

    public DZPKRoomSet(int setID, PKRoom room, int dPos) {
        super(setID, room, dPos);
        // 回放记录添加游戏配置
        this.addGameConfig();
        this.setStartMS(CommTime.nowMS());
        // 初始化位置
        this.initSetPos();
        // 洗底牌
        this.absPKSetCard();
        // 初始化本局位置管理器
        this.setSetPosMgr(this.absPKSetPosMgr());
        //检查所有玩家积分 够不够 不够哦通知 加分
        posDict.values().forEach(absPKSetPos -> ((DZPKSetPos) absPKSetPos).checkJiFen(qianZhu()));
    }

    public int qianZhu() {
        CDZPK_CreateRoom cfg = getRoom().getCfg();
        return DZPKRoomEnum.DZPK_Ante.getValue(cfg.getQianzhu());
    }

    /**
     * 初始化位置
     */
    protected void initSetPos() {
        DZPKRoomPos roomPos;
        for (int posId = 0; posId < this.getRoom().getPlayerNum(); posId++) {
            roomPos = (DZPKRoomPos) this.room.getRoomPosMgr().getPosByPosID(posId);
            // 检查位置上是否有玩家
            if (null == roomPos || roomPos.getPid() <= 0L) {
                continue;
            }
            this.getPosDict().put(posId, this.absPKSetPos(posId));
        }
    }

    /**
     * 开始发牌
     */
    public void startSet() {
        CommLogD.info("startSet id:{}", getSetID());
        this.setInitTime(5000);
        // 初始化pai
        this.initPosPrivateCard();
        //前注
        this.ante();
        //随机一个玩家为大盲，自动下大盲注；
        this.randomDaMang();
        // 通知本局开始
        this.notify2SetStart();
        this.getRoom().getTrusteeship().setTrusteeshipState(TrusteeshipState.Normal);
    }

    /**
     * 初始化玩家手上的牌
     */
    protected final void initPosPrivateCard() {
        DZPKSetCard setCard = (DZPKSetCard) getSetCard();
        if (getGodInfo().isGodCardMode()) {
            // 神牌模式下：（只允许内测时开启）
            this.initGodPosCard();
            List<Integer> publicCard = this.getGodInfo().getPConfigMgr().getPublicCard();
            if (CollectionUtils.isNotEmpty(publicCard)) {
                setCard.setPublicCard(new ArrayList<>(publicCard));
            }
        } else {
            posDict.values().forEach(absPKSetPos -> absPKSetPos.init(this.getSetCard().popList(this.cardSize())));
        }
        setCard.initPublicCard();
    }


    private void ante() {
        if (isSNG()) {
            return;
        }
        DZPKSetPos setPos;
        DZPKSetCard pkSetCard = (DZPKSetCard) getPKSetCard();
        int ante = qianZhu();
        for (AbsPKSetPos absPKSetPos : posDict.values()) {
            setPos = (DZPKSetPos) absPKSetPos;
            //够扣前注不够 则移除底牌
            if (!deducted(ante, PKOpType.Not, setPos.getPosID()) && ante > 0) {
                pkSetCard.getLeftCards().addAll(setPos.getPrivateCards());
                setPos.getPrivateCards().clear();
                setPos.setOpType(PKOpType.PASS_CARD);
            }
        }
        if (checkAllOpOver()) {
            endSet();
        }
    }

    /**
     * @param betPoint
     */
    public boolean deducted(int betPoint, PKOpType opType, int pos) {
        DZPKSetPos pkSetPos = (DZPKSetPos) getPKSetPos(pos);
        if (pkSetPos == null) {
            return false;
        }
        if (betPoint == 0 || betPoint > pkSetPos.getDeductPoint()) {
            return false;
        }
        if (!opType.equals(PKOpType.ALL_IN) && betPoint < getLowerBet()) {
            return false;
        }
        pkSetPos.deducted(betPoint, opType);
        if (opType.equals(PKOpType.ALL_IN) || opType.equals(PKOpType.ADD_BET) || opType.equals(PKOpType.BET)) {
            setLowerBet(betPoint);
            setAddBetPos(pos);
            DZPKSetPos yPos = null;
            int nextOpPos = 0;
            for (int i = 1; i < getRoom().getPlayerNum(); i++) {
                nextOpPos = (i + pos) % this.getRoom().getPlayerNum();
                yPos = (DZPKSetPos) this.getPosDict().get(nextOpPos);
                // 获取玩家位置信息
                if (null == yPos) {
                    continue;
                }
                if (yPos.getOpType().equals(PKOpType.PASS_CARD) || yPos.getOpType().equals(PKOpType.ALL_IN)) {
                    continue;
                }
                yPos.setOpType(PKOpType.Not);
            }
        }
        betList.add(betPoint);
        totalBet += betPoint;
        roundBet += betPoint;

        return true;
    }

    /**
     * 初始化神牌玩家身上的牌
     */
    protected void initGodPosCard() {
        // 神牌模式
        this.getPosDict().values().stream().forEach(k -> this.getGodInfo().isGodCard(k, k.getPosID()));
        if (this.getGodInfo().getPConfigMgr().getGodCard() == MJSpecialEnum.GOD_CARD.value()) {
            this.getPosDict().values().stream().forEach(mSetPos -> {
                if (mSetPos.sizePrivateCard() == cardSize() || !((PKRoom) mSetPos.getRoom()).isPlaying(mSetPos.getPosID())) {
                    return;
                }
                int podIdx = cardSize() - mSetPos.sizePrivateCard();
                List<Integer> privateList = getPKSetCard().popList(podIdx);
                mSetPos.forcePopCard(privateList);
            });
        }
    }


    /**
     * 随机出大盲
     */
    protected void randomDaMang() {
        this.randomDaMang = Random.nextInt(posDict.size());
        CommLogD.debug("randomDaMang" + randomDaMang);
        CDZPK_CreateRoom cfg = getRoom().getCfg();
        DZPKRoomEnum.DZPK_DaXiaoMangEnum mangEnum = DZPKRoomEnum.DZPK_DaXiaoMangEnum.getMang(cfg.getDaxiaomangjibie());
        deducted(mangEnum.daMang, PKOpType.Not, randomDaMang);
        deducted(mangEnum.xiaoMang, PKOpType.Not, xiaoMang());
        this.lowerBet = getDaMangPoint();
    }

    protected int xiaoMang() {
        return (randomDaMang + posDict.size() - 1) % posDict.size();
    }


    @Override
    public int cardSize() {
        return 2;
    }

    @Override
    protected <T> BaseSendMsg setEnd(long roomID, T setEnd) {
        ((DZPKRoom) getRoom()).setLasSeteEnd((DZPKRoom_SetEnd) setEnd);
        return SDZPK_SetEnd.make(roomID, setEnd);
    }

    @Override
    protected AbsPKSetPos absPKSetPos(int posID) {
        return new DZPKSetPos(posID, this.getRoom().getRoomPosMgr().getPosByPosID(posID), this);
    }

    @Override
    protected void absPKSetCard() {
        // 设置当局牌
        this.setSetCard(new DZPKSetCard(this));
    }

    @Override
    protected AbsPKSetPosMgr absPKSetPosMgr() {
        return new DZPKSetPosMgr(this);
    }

    @Override
    protected <T> BaseSendMsg setStart(long roomID, T setInfo) {
        return SDZPK_SetStart.make(roomID, setInfo);
    }

    @Override
    protected AbsPKSetRound nextSetRound(int roundID) {
        return new DZPKSetRound(this, roundID);
    }

    @Override
    protected void calcCurSetPosPoint() {

        DZPKSetPos setPos;
        int win = 0;
        int count = Math.toIntExact(getPosDict().values().stream().filter(absPKSetPos -> ((DZPKSetPos) absPKSetPos).isWin()).count());
        if (count == 0) {
            return;
        }
        List<DZPKSetPos> posList = new ArrayList<>();
        for (AbsPKSetPos absPKSetPos : posDict.values()) {
            setPos = (DZPKSetPos) absPKSetPos;
            if (setPos.isWin()) {
                win = totalBet / count;
                posList.add(setPos);
                setPos.setDeductPoint(setPos.getDeductPoint() + win);
            } else {
                win = 0;
            }
            setPos.setWinPint(-setPos.getBetPoint() + win);
            setPos.setEndPoint(setPos.getDeductPoint());
            CommLogD.debug("房间" + getRoom().getRoomID() + ",第几局" + getSetID() + "玩家"
                    + setPos.getPosID() + "输赢" + setPos.win + "，赢得" + win + "，剩余" + setPos.getDeductPoint());
        }

        //除不尽的情况下
        int left = totalBet % count;
        if (count > 0 && left > 0) {
            setPos = posList.get(CommMath.randomInt(posList.size() - 1));
            setPos.setDeductPoint(setPos.getDeductPoint() + left);
            setPos.setEndPoint(setPos.getDeductPoint());
            setPos.setWinPint(left + setPos.getWinPint());
            CommLogD.debug("房间" + getRoom().getRoomID() + ",第几局" + getSetID() + "玩家"
                    + setPos.getPosID() + "除不尽的情况下，赢得" + win + "，剩余" + setPos.getDeductPoint());
        }
        //计算本剧输赢多少
        posDict.values().forEach(absPKSetPos -> {
            DZPKSetPos pos = (DZPKSetPos) absPKSetPos;
            DZPKRoomPos posByPosID = (DZPKRoomPos) getRoom().getRoomPosMgr().getPosByPosID(pos.getPosID());
            posByPosID.setTotoalWinPoint(posByPosID.getTotoalWinPoint() + pos.getWinPint());
        });

    }

    @Override
    public boolean checkExistPrizeType(PrizeType prizeType) {
        return prizeType.equals(this.room.getBaseRoomConfigure().getPrizeType());
    }

    /**
     * MANG_ZHU,//选择盲注级别+几排分
     * DI_PAI,//发底牌
     * FAN_PAI,//翻牌
     * ZHUAN_PAI,//转牌
     * HE_PAI,//河牌
     * END,
     *
     * @param sec 秒
     * @return
     */
    @Override
    public boolean update(int sec) {
        boolean isClose = false;
        switch (this.getState()) {
            case Init://选分
                if (checkAllDaiFen()) {
                    startSet();
                    setState(SetState.Playing);
                    if (!this.startNewRound()) {
                        endSet();
                    }
                }
                break;
            case Playing://翻牌
                updatePlaying(sec);
                break;
            case End://结算
                this.clearEndSetRoom();
                isClose = true;
                break;
            default://其他
                break;
        }
        return isClose;
    }

    public void updatePlaying(int sec) {
        boolean isRoundClosed = this.getCurRound().update(sec);
        if (isRoundClosed) {
            if (getCurRound().isSetEnd() || !this.startNewRound()) {
                this.endSet();
            }
        }
    }

    /**
     * 本轮是否全部操作完了
     *
     * @return
     */
    public boolean checkAllOpOver() {
        DZPKSetPos setPos;
        int qipaiCunt = Math.toIntExact(posDict.values().stream().filter(absPKSetPos -> ((DZPKSetPos) absPKSetPos).getOpType() == PKOpType.PASS_CARD || absPKSetPos.getPrivateCards().size() == 0).count());
        DZPKSetCard dzpkSetCard = (DZPKSetCard) this.setCard;
        int allInCount = Math.toIntExact(posDict.values().stream().filter(absPKSetPos -> ((DZPKSetPos) absPKSetPos).getOpType() == PKOpType.ALL_IN).count());
        int allLetGo = Math.toIntExact(posDict.values().stream().filter(absPKSetPos -> ((DZPKSetPos) absPKSetPos).getOpType() == PKOpType.LET_GO).count());
        boolean allOp = (posDict.values().stream().allMatch(absPKSetPos -> ((DZPKSetPos) absPKSetPos).getOpType() != PKOpType.Not
                && ((DZPKSetPos) absPKSetPos).getOpType() != PKOpType.LET_GO));

        if (posDict.size() - qipaiCunt <= 1 || (allInCount > 0 && allOp)) {
            bet_state = DZPKRoomEnum.DZPK_BET_STATE.END.ordinal();
            dzpkSetCard.dealCard(true);
        } else if (allLetGo + qipaiCunt == posDict.size()) {
            //全部让牌
            bet_state++;
            dzpkSetCard.dealCard(false);
        } else {
            //有下注/加注 或者都让牌
            if (addBetPos < 0) {
                return false;
            }
            if (!allOp) {
                return false;
            }
            bet_state++;
            dzpkSetCard.dealCard(false);
        }
        CommLogD.debug("bet_state" + bet_state);
        //通知发牌
        getRoomPlayBack().playBack2All(new DZPKFaPai(dzpkSetCard.getCurDealCard(), dzpkSetCard.getCommonCard()));
        setFirstBet(true);
        roundBet = 0;
        for (AbsPKSetPos absPKSetPos : getPosDict().values()) {
            setPos = (DZPKSetPos) absPKSetPos;
            if (!setPos.getOpType().equals(PKOpType.PASS_CARD)) {
                setPos.setOpType(PKOpType.Not);
            }
        }
        return true;
    }


    /**
     * 判断是否带分结束
     *
     * @return
     */
    private boolean checkAllDaiFen() {
        int i = 0;
        for (AbsPKSetPos absMJSetPos : this.getPosDict().values()) {
            DZPKSetPos setPos = (DZPKSetPos) absMJSetPos;
            if (!setPos.isDaifenFlag()) {
                return false;
            }
        }
        return true;
    }

    public void setState(SetState state) {
        this.state = state;
        // 通知游戏阶段管理状态改变
        this.getRoom().getRoomPosMgr().notify2All(SDZPK_ChangeStatus.make(this.getRoom().getRoomID(), state));
    }

    @Override
    public void clearBo() {
        this.setBo(null);
    }

    @Override
    public RoomSetInfo getNotify_set(long pid) {
        DZPKRoomSetInfo ret = new DZPKRoomSetInfo();
        // 庄家位置
        ret.setDPos(this.getDPos());
        // 第几局
        ret.setSetID(this.getSetID());
        // 当前操作时间
        ret.setSetCurrentTime(CommTime.nowSecond());
        // 状态
        ret.setState(this.getState());
        // 初始手牌
        ret.setSetPosList(setPosList(pid));
        // 如果是等待状态： waiting；
        if (SetState.Playing.equals(this.getState()) && Objects.nonNull(getCurRound())) {
            // 当前等待信息
            ret.setSetRound(this.getCurRound().getNotify_RoundInfo(this.getRoom().getRoomPosMgr().getPosByPid(pid).getPosID()));
        }
        // 结束状态
        if (SetState.End.equals(this.getState())) {
            ret.setSetEnd(this.getNotify_setEnd());
        } else {
            ret.setSetEnd(this.newPKRoomSetEnd());
        }
        //大小盲
        ret.setDaMangPos(randomDaMang);
        ret.setXiaoMangPos(xiaoMang());
        ret.setDaManggPoint(getDaMangPoint());
        ret.setXiaoManggPoint(getXiaoMangPoint());
        ret.setTotalBetList(betList);
        ret.setTotalBetPoint(totalBet);
        ret.setRoundBetPoint(roundBet);
        if (!getState().equals(SetState.Init)) {
            ret.setPublicCardList(((DZPKSetCard) setCard).getCommonCard());
        }
        return ret;
    }

    /**
     * 一局结束的信息
     *
     * @return
     */
    @Override
    public BasePKRoom_SetEnd getNotify_setEnd() {
        DZPKRoom_SetEnd setEnd = (DZPKRoom_SetEnd) this.mRoomSetEnd();
        setEnd.setPublicCardList(((DZPKSetCard) setCard).getCommonCard());
        // 获取房间当局结束数据
        return this.mRoomSetEnd();
    }

    /**
     * 扑克当局结算
     *
     * @return
     */
    @Override
    protected BasePKRoom_SetEnd newPKRoomSetEnd() {
        return new DZPKRoom_SetEnd();
    }

    /**
     * 设置每个操作位列表
     *
     * @param pid 玩家Pid
     * @return
     */
    public List<BasePKSet_Pos> setPosList(long pid) {
        DZPKSetPos set_pos;
        List<BasePKSet_Pos> pos = new ArrayList<>();
        for (int i = 0; i < this.getRoom().getPlayerNum(); i++) {
            set_pos = (DZPKSetPos) this.getPosDict().get(i);
            // 获取玩家位置信息
            if (null == set_pos) {
                continue;
            }
            pos.add(this.getPosDict().get(i).getNotify(pid));
        }
        return pos;
    }


    @Override
    public void addDissolveRoom(BaseSendMsg baseSendMsg) {
        if (SetState.End.equals(this.getState()) || null == this.getSetPosMgr()) {
            return;
        }
        this.getRoomPlayBack().addPlaybackList(baseSendMsg, this.getSetPosMgr().getPKAllPlayBackNotify());
    }

    @Override
    public void addGameConfig() {
        this.getRoomPlayBack().addPlaybackList(SDZPK_Config.make(this.getRoom().getCfg(),
                this.getRoom().getRoomTyepImpl().getRoomTypeEnum()), null);
    }

    @Override
    public int getTabId() {
        return this.getRoom().getTabId();
    }

    /**
     * 获取下一位操作者位置
     *
     * @param opPos
     * @return
     */
    public int nextOpPos(int opPos) {
        DZPKSetPos yPos = null;
        int nextOpPos = -1;
        for (int i = 1; i < this.getRoom().getPlayerNum(); i++) {
            nextOpPos = (i + opPos) % this.getRoom().getPlayerNum();
            yPos = (DZPKSetPos) this.getPosDict().get(nextOpPos);
            // 获取玩家位置信息
            if (null == yPos) {
                continue;
            }
            if (!yPos.opType.equals(PKOpType.Not)) {
                continue;
            }
            // 下家出牌 
            return nextOpPos;
        }
        for (int i = 1; i < this.getRoom().getPlayerNum(); i++) {
            nextOpPos = (i + opPos) % this.getRoom().getPlayerNum();
            yPos = (DZPKSetPos) this.getPosDict().get(nextOpPos);
            // 获取玩家位置信息
            if (null == yPos) {
                continue;
            }
            if (yPos.opType.equals(PKOpType.LET_GO)) {
                return nextOpPos;
            }
        }

        return nextOpPos;
    }

    @Override
    public void endSet() {
        CommLogD.info("endSet id:{}", getSetID());
        //正常计算
        if (SetState.End.equals(this.getState())) {
            return;
        }
        this.setState(SetState.End);
        this.setEnd(true);
        //比牌
        this.compareCardList();
        // 结算算分
        this.calcPoint();

        // 广播
        this.getRoomPlayBack().playBack2All(this.setEnd(getRoom().getRoomID(), this.getNotify_setEnd()));
        //保存本局下注情况

        // 记录回放码
        this.roomPlayBack();
    }


    /**
     * 开始比牌
     */
    private void compareCardList() {
        if (setCard == null) {
            return;
        }
        ArrayList<Integer> commonCard = ((DZPKSetCard) setCard).getCommonCard();
        //初始话牌型
        posDict.values().forEach(absPKSetPos -> ((DZPKSetPos) absPKSetPos).initCardType(commonCard));
        //比大小
        Long max = posDict.values().stream().filter(absPKSetPos -> ((DZPKSetPos) absPKSetPos).getCardTypeInfo() != null).
                map(absPKSetPos -> ((DZPKSetPos) absPKSetPos).getCardTypeInfo().getValue()).max((o1, o2) -> o1.compareTo(o2)).get();
        //判断输赢
        CommLogD.debug("牌型最大值" + max);
        Long diPaiMax = posDict.values().stream().filter(absPKSetPos -> ((DZPKSetPos) absPKSetPos).getCardTypeInfo().getValue()
                == max).map(absPKSetPos -> ((DZPKSetPos) absPKSetPos).getCardTypeInfo().getDiPaiValue()).max((o1, o2) -> o1.compareTo(o2)).get();
        posDict.values().forEach(absPKSetPos -> {
            DZPKSetPos setPos = (DZPKSetPos) absPKSetPos;

            if (setPos.getCardTypeInfo().getValue() == max && setPos.getCardTypeInfo().getDiPaiValue() == diPaiMax) {
                setPos.setWin(true);
            } else {
                setPos.setWin(false);
            }
            CommLogD.debug("座位" + setPos.getPosID() + "输赢" + setPos.win + "牌值" + setPos.getCardTypeInfo().getValue());
        });
    }

    /**
     * 配置文件是否需要游戏名
     *
     * @return T:需要,F:不需要
     */
    @Override
    public boolean isConfigName() {
        return true;
    }

    @Override
    public RoomPlayBack newRoomPlayBackImpl() {
        return new DZPKRoomPlayBackImpl(this.getRoom());
    }


    /**
     * 初始化积分
     *
     * @param request
     * @param pos
     * @param jiFen
     */
    public void doInitJifenPai(WebSocketRequest request, int pos, int jiFen) {
        if (null == request) {
            return;
        }
        if (!SetState.Init.equals(this.getState())) {
            request.error(ErrorCode.NotAllow, "jiFen error:state is not  can   state:" + this.getState());
            return;
        }
        DZPKSetPos setPos = (DZPKSetPos) this.getPKSetPos(pos);
        if (setPos == null) {
            request.error(ErrorCode.NotAllow, "setPos is null posID:" + pos);
            return;
        }
        CDZPK_CreateRoom cfg = getRoom().getCfg();
        if (jiFen < getJifenMin() || jiFen > getJiFenMax()) {
            request.error(ErrorCode.NotAllow, "jiFen not allow:" + jiFen);
            return;
        }
        if (setPos.isDaifenFlag()) {
            request.error(ErrorCode.NotAllow, "jifen  has enough:" + room.getRoomID());
            return;
        }
        setPos.setJuNeiPoint(jiFen, false);

        if (null != request) {
            request.response();
        }
    }

    public int initPosPint() {
        return getXiaoMangPoint() * 200;
    }

    public int getFirstPos() {
        //小盲玩家先下注
        if (letGoPos >= 0) {
            int firstPos = (letGoPos + 1) % posDict.size();
            this.letGoPos = -1;
            return firstPos;
        }
        if (bet_state > DZPKRoomEnum.DZPK_BET_STATE.FAN_PAI.ordinal()) {
            return xiaoMang();
        }
        return (randomDaMang + 1) % posDict.size();
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof DZPKRoomSet) {
            if (getSetID() == ((DZPKRoomSet) o).getSetID()) {
                return true;
            }
        }
        return false;
    }

    public boolean isDE_ZHOU() {
        return ((DZPKRoom) room).isDE_ZHOU();
    }

    public boolean isAO_HA_MA() {
        return ((DZPKRoom) room).isAO_HA_MA();
    }

    public boolean isDUAN_PAI() {
        return ((DZPKRoom) room).isDUAN_PAI();
    }

    public boolean isSNG() {
        return ((DZPKRoom) room).isSNG();
    }

    public int getDaMangPoint() {
        return getXiaoMangPoint() * 2;
    }

    public int getXiaoMangPoint() {
        CDZPK_CreateRoom cfg = getRoom().getCfg();
        DZPKRoomEnum.DZPK_DaXiaoMangEnum mangEnum = DZPKRoomEnum.DZPK_DaXiaoMangEnum.getMang(cfg.getDaxiaomangjibie());
        return mangEnum.xiaoMang;
    }

    public int getJifenMin() {
        return getXiaoMangPoint() * 200;
    }

    public int getJiFenMax() {
        return getJifenMin() * 5;
    }

    public int getRandomDaMang() {
        return randomDaMang;
    }

    public void setRandomDaMang(int randomDaMang) {
        this.randomDaMang = randomDaMang;
    }

    public List<Integer> getBetList() {
        return betList;
    }

    public void setBetList(List<Integer> betList) {
        this.betList = betList;
    }

    public int getTotalBet() {
        return totalBet;
    }

    public void setTotalBet(int totalBet) {
        this.totalBet = totalBet;
    }

    public int getBet_state() {
        return bet_state;
    }

    public void setBet_state(int bet_state) {
        this.bet_state = bet_state;
    }

    public boolean isFirstBet() {
        return firstBet;
    }

    public void setFirstBet(boolean firstBet) {
        this.firstBet = firstBet;
    }

    public int getAddBetPos() {
        return addBetPos;
    }

    public void setAddBetPos(int addBetPos) {
        this.addBetPos = addBetPos;
    }

    public int getLowerBet() {
        return lowerBet;
    }

    public void setLowerBet(int lowerBet) {
        this.lowerBet = lowerBet;
    }

    public int getLetGoPos() {
        return letGoPos;
    }

    public void setLetGoPos(int letGoPos) {
        this.letGoPos = letGoPos;
    }

    public int getRoundBet() {
        return roundBet;
    }

    public void setRoundBet(int roundBet) {
        this.roundBet = roundBet;
    }
}
