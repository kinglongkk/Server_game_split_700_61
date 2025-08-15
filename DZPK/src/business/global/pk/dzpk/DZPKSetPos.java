package business.global.pk.dzpk;

import business.dzpk.c2s.cclass.*;
import business.global.pk.AbsPKSetPos;
import business.global.pk.AbsPKSetRoom;
import business.global.pk.PKOpCard;
import business.global.pk.dzpk.base.DZPK_CardTypeImpl;
import business.global.pk.dzpk.cardtype.DZPK_GaoPaiCardType;
import business.global.room.base.AbsRoomPos;
import cenum.PKOpType;
import cenum.RoomTypeEnum;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.utils.Lists;
import jsproto.c2s.cclass.pk.base.BasePKRoom_PosEnd;
import jsproto.c2s.cclass.pk.base.BasePKSet_Pos;
import jsproto.c2s.cclass.room.AbsBaseResults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 每一局每个位置信息
 *
 * @author Huaxing
 */
public class DZPKSetPos extends AbsPKSetPos {
    /**
     * 当局信息
     */
    private DZPKRoomSet roomSet;
    /**
     * 下注列表
     */
    private List<Integer> betList = new ArrayList<>();
    public boolean daifenFlag = true; // 积分是否加完了
    public int betPoint;//总下注
    public int curBetPoint;//最近下注
    public boolean win = true;
    private DZPK_CardTypeImpl cardTypeInfo = new DZPK_GaoPaiCardType();
    public PKOpType opType = PKOpType.Not;//最后一次操作
    /**
     * 可下注选项
     */
    private List<Integer> betOptions = new ArrayList<>();
    /**
     * m每阶段下了多少
     */
    private List<Integer> roundBetList = new ArrayList<>();
    private List<Integer> roundOpList = new ArrayList<>();
    /**
     * 赢分
     */
    private int winPint;


    /**
     * @param posID
     * @param roomPos
     * @param set
     */

    public DZPKSetPos(int posID, AbsRoomPos roomPos, AbsPKSetRoom set) {
        super(posID, roomPos, set);
        this.roomSet = (DZPKRoomSet) set;
        this.setMSetOp(new DZPKSetOp(this));
        this.setCalcPosEnd(new DZPKCalcPosEnd(this, this.getRoomSet()));
        setJuNeiPoint();
        roundBetList.add(0);//底牌
        roundBetList.add(0);//翻
        roundBetList.add(0);//转
        roundBetList.add(0);//河

        roundOpList.add(PKOpType.Not.value());//底牌
        roundOpList.add(PKOpType.Not.value());//翻
        roundOpList.add(PKOpType.Not.value());//转
        roundOpList.add(PKOpType.Not.value());//河
    }

    private void setJuNeiPoint() {
        int initpoint = getRoomPos().getPoint();
        if (roomSet.getSetID() == 1) {
            initpoint = roomSet.initPosPint();
        }
        setJuNeiPoint(initpoint, true);
    }


    @Override
    protected AbsBaseResults newResults() {
        return new DZPKResults();
    }

    /**
     * 新一局中各位置的信息
     *
     * @return
     */
    @Override
    protected BasePKSet_Pos newPKSetPos() {
        return new DZPKSet_Pos();
    }


    @Override
    public DZPKSet_Pos getNotify(boolean isSelf) {
        isSelf = isRevealCard() ? true : isSelf;
        DZPKSet_Pos ret = (DZPKSet_Pos) newPKSetPos();
        ret.setPid(this.getPid());
        ret.setPosID(this.getPosID());
        ret.setShouCard(isSelf ? this.getPrivateCards() : Collections.emptyList());
        ret.setIsLostConnect(this.getRoomPos().isLostConnect());
        ret.setBetPoint(betPoint);
        ret.setBetList(betList);
        ret.setPoint(getDeductPoint());
        ret.setCurBetPoint(curBetPoint);
        ret.setOpType(opType);
        ret.setTrusteeship(this.getRoomPos().isTrusteeship());
        if (!isDaifenFlag()) {
            ret.setDaifen(new DZPKStartDaiFen(roomSet.getJifenMin(), roomSet.getJiFenMax(), getPosID(), getDeductPoint()));
        }
        boolean firstWinner = ((DZPKRoomPosMgr) getRoom().getRoomPosMgr()).isFirstWinner(getPosID());
        int mingCi = ((DZPKRoomPosMgr) getRoom().getRoomPosMgr()).getMingCi(getPosID());
        ret.setFirstWinner(firstWinner);
        ret.setMingci(mingCi);
        return ret;
    }

    public void setJuNeiPoint(int jiFenPai, boolean initState) {
        int initPoint = jiFenPai;
        if (RoomTypeEnum.UNION.equals(this.getRoom().getRoomTypeEnum())) {
            initPoint = initPoint > getRoomPos().getRoomSportsPointValue() ? (int) getRoomPos().getRoomSportsPointValue() : initPoint;
            getRoomPos().setRoomSportsPoint(getRoomPos().getRoomSportsPointValue() - initPoint);
        }
        this.setDeductPoint(this.getDeductPoint() + initPoint);
        CommLogD.info("座位" + getPosID() + "带入积分" + jiFenPai + "实际积分" + initPoint + "最终积分" + getDeductPoint());
        if (!initState) {
            this.setDaifenFlag(true);
            getRoomSet().getRoomPlayBack().playBack2All(SDZPK_JiFen.make(getRoom().getRoomID(), getPosID(), jiFenPai));
        } else {
            getRoomPos().setPoint(0);
        }
    }

    @Override
    public DZPKSet_Pos getPlayBackNotify() {

        return getNotify(true);
    }

    /**
     * 通知通知带分开始
     *
     * @param ante
     */
    public boolean checkJiFen(int ante) {
        DZPKRoom room = (DZPKRoom) getRoom();
        CommLogD.info("初始化积分" + getDeductPoint());
        //钱不够下注
        if (getDeductPoint() <= ante) {
            int playerPoint = roomSet.initPosPint();
            if (room.isJingShai()) {
                AbsRoomPos posByPosID = getRoom().getRoomPosMgr().getPosByPosID(getPosID());
                playerPoint = (int) posByPosID.getRoomSportsPointValue();
            }
            //分数不够扣 不用通知
            if (playerPoint <= ante) {
                return true;
            }
            CommLogD.info("初始化积分不够" + getDeductPoint() + "前注" + ante);
            setDaifenFlag(false);
            this.getRoomSet().getRoomPlayBack().playBack2All(new DZPKStartDaiFen(roomSet.getJifenMin(), roomSet.getJiFenMax(), getPosID(), playerPoint));
            return false;
        }
        return true;
    }

    @Override
    public List<PKOpType> receiveOpTypes() {
        betOptions.clear();
        if (opType.equals(PKOpType.PASS_CARD)) {
            return new ArrayList<>();
        }

        boolean firstBet = roomSet.isFirstBet();
        List<PKOpType> opTypes = Lists.newArrayList();
        opTypes.add(PKOpType.PASS_CARD);
        opTypes.add(PKOpType.ALL_IN);
        int lowerBet = roomSet.getLowerBet();
        resetOptions(!firstBet);
        if (firstBet) {
            if (getDeductPoint() >= lowerBet) {
                opTypes.add(PKOpType.BET);
                opTypes.add(PKOpType.LET_GO);
            }
        } else {
            long count = roomSet.getPosDict().values().stream().filter(absPKSetPos -> ((DZPKSetPos) absPKSetPos).getOpType().equals(PKOpType.ADD_BET)).count();
            long allinCount = roomSet.getPosDict().values().stream().filter(absPKSetPos -> ((DZPKSetPos) absPKSetPos).getOpType().equals(PKOpType.ALL_IN)).count();
            if (getDeductPoint() > lowerBet && count + allinCount <= 0) {
                opTypes.add(PKOpType.ADD_BET);
            }
            if (getDeductPoint() >= lowerBet) {
                opTypes.add(PKOpType.FALLOW_BET);
            }
        }
        return opTypes;
    }

    public void resetOptions(boolean addBet) {
        int lowerBet = roomSet.getLowerBet() + (addBet ? 1 : 0);
        if (lowerBet > getDeductPoint()) {
            betOptions.add(getDeductPoint());
        } else if (getDeductPoint() - lowerBet < 4) {
            for (int i = lowerBet; i <= getDeductPoint(); i++) {
                betOptions.add(i);
            }
        } else {
            int index = (getDeductPoint() - lowerBet) / 5;
            for (int i = 0; i < 5; i++) {
                if (i < 4) {
                    betOptions.add(lowerBet + i * index);
                } else {
                    betOptions.add(getDeductPoint());

                }
            }
        }
    }


    @Override
    public boolean doOpType(PKOpCard opCard, PKOpType opType) {
        return getMSetOp().doOpType(opCard, opType);
    }

    /**
     * 统计本局分数
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public DZPKRoom_PosEnd calcPosEnd() {
        // 位置结算信息
        DZPKRoom_PosEnd ret = (DZPKRoom_PosEnd) this.posEndInfo();
        // 获取位置结算信息		
        ret.setShouCard(this.getPrivateCards());
        //本局输赢	
        ret.setPoint(getDeductPoint());
        //总输赢
        ret.setRoomPoint(getEndPoint());
        ret.setNewCardList(cardTypeInfo.getCardList());
        int count = Math.toIntExact(roomSet.getPosDict().values().stream().filter(absPKSetPos -> ((DZPKSetPos) absPKSetPos).isWin()).count());

        int winPoint = roomSet.getTotalBet() / count;
        ret.setDiChiPoint(win ? winPoint : 0);
        ret.setCardType(cardTypeInfo.cardType().value());
        ret.setCardType(cardTypeInfo.cardType().value());
        DZPKRoom_PosEnd.DZPKRoom_PosRoundInfo room_posRoundInfo = new DZPKRoom_PosEnd.DZPKRoom_PosRoundInfo();
        room_posRoundInfo.setBetList(roundBetList);
        room_posRoundInfo.setOpList(roundOpList);
        List<List<Integer>> cardLsit = new ArrayList<>();
        cardLsit.add(getPrivateCards());
        DZPKSetCard setCard = (DZPKSetCard) getSet().getSetCard();
        if (setCard.getCommonCard().size() == 5) {
            cardLsit.add(setCard.getCommonCard().subList(0, 3));
            cardLsit.add(setCard.getCommonCard().subList(3, 4));
            cardLsit.add(setCard.getCommonCard().subList(4, 5));
        }
        room_posRoundInfo.setCardList(cardLsit);
        ret.setRoundInfo(room_posRoundInfo);
        ret.setWinPoint(this.winPint);
        return ret;
    }

    /**
     * 新位置结算信息
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    @Override
    protected BasePKRoom_PosEnd newPKSetPosEnd() {
        return new DZPKRoom_PosEnd();
    }

    /**
     * 结算信息
     *
     * @return
     */
    @Override
    protected DZPKResults mResultsInfo() {
        DZPKResults mResults = (DZPKResults) this.getResults();
        if (Objects.isNull(mResults)) {
            // new 总结算		
            mResults = (DZPKResults) this.newResults();
            // 用户PID		
            mResults.setPid(this.getPid());
            // 位置		
            mResults.setPosId(this.getPosID());
            // 是否房主		
            mResults.setOwner(this.getPid() == this.getRoom().getOwnerID());
        }
        // 总分数		
        mResults.setPoint(this.pidSumPointEnd());
        if (betPoint > 0) {
            mResults.setWinCountPoint(mResults.getWinCountPoint() + 1);
        }
        return mResults;
    }

    @Override
    public DZPKSetPosRobot newPKSetPosRobot() {
        return new DZPKSetPosRobot(this);
    }


    /**
     * 获取当局信息
     *
     * @return
     */
    public DZPKRoomSet getRoomSet() {
        return roomSet;
    }

    /**
     * @param betPoint
     */
    public boolean deducted(int betPoint, PKOpType opType) {

        setDeductPoint(getDeductPoint() - betPoint);
        betList.add(betPoint);
        curBetPoint = betPoint;
        this.betPoint += betPoint;
        int roundBet = roundBetList.get(roomSet.getBet_state()) + betPoint;
        roundBetList.set(roomSet.getBet_state(), roundBet);
        CommLogD.info("房间" + roomSet.getRoom().getRoomID() + ",第几局" +
                roomSet.getSetID() + "玩家" + getPosID() + ",下注" + opType.name() + ":" + betPoint + "，剩余" + getDeductPoint());
        return true;
    }

    /**
     * 找出最佳牌型
     *
     * @param commonCard
     */
    public void initCardType(List<Integer> commonCard) {
        List<Integer> cards = new ArrayList<>(commonCard);
        if (!opType.equals(PKOpType.PASS_CARD)) {
            cards.addAll(getPrivateCards());
        }
        DZPKRoom room = (DZPKRoom) getRoom();
        if (cards.size() != 7) {
            return;
        }

        for (DZPK_CardTypeImpl baseCardType : room.getCardTypes()) {
            if (baseCardType.resultType(this, cards)) {
                CommLogD.info("选中\t" + baseCardType.toString() + "," + baseCardType.getClass().getSimpleName() + "," + baseCardType.cardType());
                this.cardTypeInfo = baseCardType.clone();
                break;
            }
        }
    }

    public void setRoomSet(DZPKRoomSet roomSet) {
        this.roomSet = roomSet;
    }

    public List<Integer> getBetList() {
        return betList;
    }

    public void setBetList(List<Integer> betList) {
        this.betList = betList;
    }

    public boolean isDaifenFlag() {
        return daifenFlag;
    }

    public void setDaifenFlag(boolean daifenFlag) {
        this.daifenFlag = daifenFlag;
    }

    public int getBetPoint() {
        return betPoint;
    }

    public void setBetPoint(int betPoint) {
        this.betPoint = betPoint;
    }

    public int getCurBetPoint() {
        return curBetPoint;
    }

    public void setCurBetPoint(int curBetPoint) {
        this.curBetPoint = curBetPoint;
    }

    public PKOpType getOpType() {
        return opType;
    }

    public void setOpType(PKOpType opType) {

        this.opType = opType;
    }

    public boolean isWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public DZPK_CardTypeImpl getCardTypeInfo() {
        return cardTypeInfo;
    }

    public void setCardTypeInfo(DZPK_CardTypeImpl cardTypeInfo) {
        this.cardTypeInfo = cardTypeInfo;
    }

    public List<Integer> getBetOptions() {
        return betOptions;
    }

    public void setBetOptions(List<Integer> betOptions) {
        this.betOptions = betOptions;
    }

    public List<Integer> getStateBetList() {
        return roundBetList;
    }

    public List<Integer> getRoundOpList() {
        return roundOpList;
    }

    public void setRoundOpList(List<Integer> roundOpList) {
        this.roundOpList = roundOpList;
    }

    public void setStateBetList(List<Integer> stateBetList) {
        this.roundBetList = stateBetList;
    }

    public List<Integer> getRoundBetList() {
        return roundBetList;
    }

    public void setRoundBetList(List<Integer> roundBetList) {
        this.roundBetList = roundBetList;
    }

    public int getWinPint() {
        return winPint;
    }

    public void setWinPint(int winPint) {
        this.winPint = winPint;
    }

}
