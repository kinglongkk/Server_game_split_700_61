package business.global.pk.nn;

import java.util.*;

import business.global.room.base.AbsRoomPos;
import business.global.room.base.AbsRoomSet;
import business.global.room.base.RoomPlayBack;
import business.nn.c2s.cclass.*;
import business.nn.c2s.iclass.*;
import cenum.PrizeType;

import cenum.RoomTypeEnum;
import cenum.room.TrusteeshipState;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.utils.CommTime;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import business.nn.c2s.cclass.NN_define.NN_GameStatus;
import core.db.entity.clarkGame.GameSetBO;
import core.db.service.clarkGame.GameSetBOService;
import core.ioc.ContainerMgr;
import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.pk.Victory;
import jsproto.c2s.cclass.room.RoomPosInfo;
import org.apache.commons.collections.CollectionUtils;

/**
 * 牛牛一局游戏逻辑
 *
 * @author zaf
 */

public abstract class NNRoomSet extends AbsRoomSet {

    public NNRoom room = null;
    public long startMS = 0;
    public GameSetBO bo = null;
    protected NN_GameStatus status = NN_GameStatus.NN_GAME_STATUS_HOG;
    public NNSetCard setCard = null;
    public NNRoom_SetEnd setEnd = new NNRoom_SetEnd();
    private int m_backerPos = -1; //庄家  -1:标识没有庄家
    public boolean m_isRandBackPos = false;//是否随机庄家
    public ArrayList<Double> sportsPointList = null;

    public List<Integer> betList; //下注分数
    public List<Boolean> checkCardList;    //亮牌玩家
    public List<Boolean> openCardList;    //开牌玩家
    public List<Boolean> playingList;        //每局玩家
    public List<Victory> callbackerList;        //是否抢过庄   victory：pos:抢庄位置 num 抢庄陪数
    public List<Integer> crawTypeList;//牛牛类型
    public ArrayList<Integer> pointList; //得分
    HashMap<Integer, List<Integer>> hMap = new HashMap<Integer, List<Integer>>();
    protected static final int TUIZHUSETID = 3;
    //	protected static final int MAXBETNUM = 10;//推注为底分的最大陪数
    protected static final int WAITTRUSTEESHIPTIME = 3000;//托管延迟2s
    protected static final int TUIZHULIST[] = {0, 5, 10};
    protected RoomPlayBack roomPlayBack; // 回放

    @SuppressWarnings("rawtypes")
    public NNRoomSet(NNRoom room) {
        super(room.getCurSetID());
        this.room = room;
        this.checkCardList = new ArrayList<>(Collections.nCopies(this.room.getMaxPlayerNum(), false));
        this.openCardList = new ArrayList<>(Collections.nCopies(this.room.getMaxPlayerNum(), false));
        this.playingList = new ArrayList<>(Collections.nCopies(this.room.getMaxPlayerNum(), false));
        this.callbackerList = new ArrayList<>();
        this.betList = new ArrayList<>(Collections.nCopies(this.room.getMaxPlayerNum(), 0));
        this.crawTypeList = new ArrayList<>(Collections.nCopies(this.room.getMaxPlayerNum(), 0));
        this.pointList = new ArrayList<>(Collections.nCopies(this.room.getMaxPlayerNum(), 0));
        this.initSportsPointList();
        // 回放记录添加游戏配置
        this.addGameConfig();
        this.startSet();
    }

    private void initSportsPointList() {
        if (RoomTypeEnum.UNION.equals(this.room.getRoomTypeEnum())) {
            this.sportsPointList = new ArrayList<>(Collections.nCopies(this.room.getPlayerNum(), 0D));
        }
    }


    /**
     * 回放记录添加游戏配置
     */
    @Override
    public void addGameConfig() {
        this.getRoomPlayBack().addPlaybackList(SNN_Config.make(room.getRoomCfg(), this.room.getRoomTyepImpl().getRoomTypeEnum()), null);
    }

    /**
     * 获取房间回放记录
     *
     * @return
     */
    public RoomPlayBack getRoomPlayBack() {
        if (null == this.roomPlayBack) {
            this.roomPlayBack = new NNRoomPlayBackImpl(this.room);
        }
        return this.roomPlayBack;
    }

    /**
     * 每200ms更新1次   秒
     *
     * @param sec
     * @return T 是 F 否
     */
    public abstract boolean update(int sec);

    /*
     * 开始状态
     * **/
    public abstract NN_GameStatus getStartStatus();

    /*
     * 设置默认庄家位置
     * **/
    public abstract void setDefeault();

    /**
     * 结算
     */
    public abstract void resultCalc();

    //获取发牌的数目
    public abstract int getSendCardNumber();

    //叫庄
    public void onCallBacker(WebSocketRequest request, CNN_CallBacker Backer) {
        if (this.getStatus() != NN_GameStatus.NN_GAME_STATUS_HOG) {
            request.error(ErrorCode.NotAllow, "onAddScore is not NN_GameStatus.NN_GAME_STATUS_HOG error " + this.getStatus());
            return;
        }

        if (this.getCallbackerNum(Backer.pos) >= 0) {
            request.error(ErrorCode.NotAllow, "onAddScore is alary callbacker error " + Backer.pos);
            return;
        }
        request.response();
        this.setCallBacker(Backer.pos, Backer.callBackerNum);

        this.room.getRoomPosMgr().notify2All(SNN_CallBacker.make(Backer.roomID, Backer.pos, Backer.callBackerNum));

        if (this.getPlayingCount() == this.callbackerList.size()) {
            this.onHogEnd();
        }
    }

    //下注
    public void onAddBet(WebSocketRequest request, CNN_AddBet addBet) {
        if (this.getStatus() != NN_GameStatus.NN_GAME_STATUS_BET) {
            request.error(ErrorCode.NotAllow, "onAddScore is not NN_GameStatus.NN_GAME_STATUS_BET error " + this.getStatus());
            return;
        }
        if (this.getBackerPos() == addBet.pos) {
            request.error(ErrorCode.NotAllow, "onAddScore callbacker do not addbet  " + this.getBackerPos());
            return;
        }
        NNRoomPos roomPos = (NNRoomPos) this.room.getRoomPosMgr().getPosByPosID(addBet.pos);
        int endPoint = this.room.endPointList[this.room.getRoomCfg().difen];
        ArrayList<Integer> scoreList = new ArrayList<Integer>();
        scoreList.add(endPoint / 2);
        scoreList.add(endPoint);
        int point = this.getTuZhuPoint(roomPos.getPid());
        if (point > 0) scoreList.add(point);

        if (!scoreList.contains(addBet.addBet)) {
            request.error(ErrorCode.NotAllow, "onAddScore not find shangzhuangfenshu error " + addBet.addBet);
            return;
        }

        if (this.betList.get(addBet.pos) > 0) {
            request.error(ErrorCode.NotAllow, "onAddScore is alary exeit error " + addBet.pos);
            return;
        }
        if (endPoint != addBet.addBet && endPoint / 2 != addBet.addBet)
            roomPos.setTuiZhu(this.room.getCurSetID() - 1, true);
        request.response();
        this.betList.set(addBet.pos, addBet.addBet);

        this.room.getRoomPosMgr().notify2All(SNN_AddBet.make(this.room.getRoomID(), addBet.pos, addBet.addBet));

        if (this.cheakAllBet()) {
            this.onBetEnd();
        }
    }

    //下庄
    public void onShimosho(WebSocketRequest request, CNN_Shimosho addBet) {
        request.error(ErrorCode.NotAllow, "onShimosho error do not ");
    }


    //抢庄结束
    public abstract void onHogEnd();

    /*
     * 设置状态
     * */
    public void setStatus(NN_GameStatus state) {
        if (this.status == state) return;
        this.status = state;
        this.startMS = CommTime.nowMS();
    }

    /*
     * 获取状态
     * */
    public NN_define.NN_GameStatus getStatus() {
        return this.status;
    }


    /**
     * 开始设置
     */
    public void startSet() {
        //设置参与游戏的玩家
        for (AbsRoomPos pos : this.room.getRoomPosMgr().posList) {
            NNRoomPos roomPos = (NNRoomPos) pos;
            if ((pos.isReady() && this.room.getCurSetID() == 1) || (this.room.getCurSetID() > 1 && pos.getPid() != 0)) {
                this.setPosPlaying(pos.getPosID(), true);
                roomPos.setPlayTheGame(true);
            }
        }

        // 洗底牌
        this.setCard = new NNSetCard(this);
        // 是否开启神牌模式
        if (room.isGodCard()) {
            godCard();
        }
        for (int i = 0; i < this.room.getXiPaiList().size(); i++) {
            this.setCard.onXiPai();
        }
        this.room.getXiPaiList().clear();

        //对每个位置的人设置牌
        int index = this.room.getCurSetID();
        for (int i = 0; i < this.room.getPlayerNum(); i++) {
            index = (index + 1) % this.room.getPlayerNum();
//			if(!this.playingList.get(index)) continue;
            NNRoomPos roomPos = (NNRoomPos) this.room.getRoomPosMgr().getPosByPosID(index);
            // 如果是DEBUG模式发送神牌
            if (room.isGodCard()) {
                roomPos.init(hMap.get(index), null, 0);
            } else {
                roomPos.init(this.setCard.popList(this.room.maxCardCount));
            }
        }

        this.status = getStartStatus();
        this.startMS = CommTime.nowMS();
        this.setDefeault();

        //开始发牌
        for (int i = 0; i < this.room.getPlayerNum(); i++) {
            long pid = this.room.getRoomPosMgr().getPosByPosID(i).getPid();
            this.room.getRoomPosMgr().notify2Pos(i, SNN_SetStart.make(this.room.getRoomID(), this.getNotify_set(pid)));
        }
        this.room.getRoomPosMgr().setAllLatelyOutCardTime();
        this.room.getTrusteeship().setTrusteeshipState(TrusteeshipState.Normal);
    }


    /**
     * 局结束
     */
    public void endSet() {

        if (this.status == NN_GameStatus.NN_GAME_STATUS_RESULT)
            return;

        this.setStatus(NN_GameStatus.NN_GAME_STATUS_RESULT);
        setEnd(true);
        this.calcPoint();

        ArrayList<ArrayList<Integer>> cards = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < this.room.getPlayerNum(); i++) {

            NNRoomPos roomPos = (NNRoomPos) this.room.getRoomPosMgr().getPosByPosID(i);
            cards.add(roomPos.getNotifyCard(roomPos.getPid()));

        }
        // 广播
        this.room.getRoomPosMgr().notify2All(SNN_SetEnd.make(this.room.getRoomID(), this.status.value(), this.startMS, this.crawTypeList, this.pointList, cards, this.sportsPointList));
    }


    //设置所有玩家都准备进行下一场游戏
    public void setAllGameReady(boolean flag) {
        if (this.room.getCurSetID() >= this.room.getRoomCfg().getSetCount()) return;
        for (AbsRoomPos pos : this.room.getRoomPosMgr().posList) {
            if (pos.getPid() != 0) {
                pos.setGameReady(flag);
                if (flag) pos.setLatelyOutCardTime(0);
            }
        }
    }

    /**
     * 结算积分
     */
    public void calcPoint() {
        GameSetBO gameSetBO = ContainerMgr.get().getComponent(GameSetBOService.class).findOne(room.getRoomID(), this.room.getCurSetID());
        this.bo = gameSetBO == null ? new GameSetBO() : gameSetBO;
        if (gameSetBO == null) {
            bo.setRoomID(room.getRoomID());
            bo.setSetID(this.room.getCurSetID());
            bo.setTabId(this.room.getTabId());
        }

        this.resultCalc();

        for (int i = 0; i < this.room.getMaxPlayerNum(); i++) {
            NNRoomPos roomPos = (NNRoomPos) this.room.getRoomPosMgr().getPosByPosID(i);

            NNRoom_PosEnd posEnd = roomPos.calcPosEnd();
            if (CollectionUtils.isNotEmpty(this.sportsPointList)) {
                this.sportsPointList.set(posEnd.pos, Objects.isNull(posEnd.sportsPoint) ? 0D : posEnd.sportsPoint);
            }
            this.setEnd.posResultList.add(posEnd);
        }
        room.getRoomPosMgr().setAllLatelyOutCardTime();
        this.setEnd.endTime = CommTime.nowSecond();

        NNRoom_SetEnd lSetEnd = this.getNotify_setEnd();
        String gsonSetEnd = new Gson().toJson(lSetEnd);
        bo.setDataJsonRes(gsonSetEnd);
        bo.setEndTime(setEnd.endTime);
        bo.setPlayBackCode(getPlayBackDateTimeInfo().getPlayBackCode());
        bo.getBaseService().saveOrUpDate(bo);
    }

    /**
     * 获取通知设置
     *
     * @param pid 用户ID
     * @return
     */
    public NNRoomSetInfo getNotify_set(long pid) {
        NNRoomSetInfo ret = new NNRoomSetInfo();
        ret.setSetID(this.room.getCurSetID());
        ret.startTime = this.startMS;
        ret.sendCardNumber = this.getSendCardNumber();
        // 每个玩家的牌面
        ret.posInfo = new ArrayList<>();
        for (int i = 0; i < this.room.getPlayerNum(); i++) {
            NNRoomPos roomPos = (NNRoomPos) this.room.getRoomPosMgr().getPosByPosID(i);
            RoomPosInfo room_Pos = this.room.getRoomPosMgr().getNotify_PosList().get(i);
            NNRoomSet_Pos roomSet_Pos = new NNRoomSet_Pos();
            int setPoint = this.pointList.get(i);
            roomSet_Pos.posID = room_Pos.getPos();
            roomSet_Pos.pid = room_Pos.getPid();
            roomSet_Pos.cards = roomPos.getNotifyCard(pid);
            roomSet_Pos.checkCard = this.checkCardList.get(i);
            roomSet_Pos.openCard = this.openCardList.get(i);
            roomSet_Pos.addBet = this.betList.get(i);
            roomSet_Pos.isPlaying = this.playingList.get(i);
            roomSet_Pos.crawType = this.crawTypeList.get(i);
            roomSet_Pos.point = this.pointList.get(i);
            roomSet_Pos.sportsPoint = roomPos.setSportsPoint(setPoint);

            ret.posInfo.add(roomSet_Pos);
        }
        ret.maxBet = this.getTuZhuPoint(pid);
        ret.backerPos = this.getBackerPos();
        ret.isRandBackerPos = this.isRandBackPos();
        ret.callbackerList = this.callbackerList;
        ret.state = this.status.value();
        if (this.status == NN_GameStatus.NN_GAME_STATUS_RESULT) {
            ArrayList<ArrayList<Integer>> cards = new ArrayList<>();
            for (int i = 0; i < this.room.getPlayerNum(); i++) {
                NNRoomPos roomPos = (NNRoomPos) this.room.getRoomPosMgr().getPosByPosID(i);
                cards.add(roomPos.getNotifyCard(roomPos.getPid()));
            }
            SNN_SetEnd end = SNN_SetEnd.make(this.room.getRoomID(), this.status.value(), this.startMS, this.crawTypeList, this.pointList, cards, this.sportsPointList);
            ret.setEnd = end;
        }
        return ret;
    }

    /**
     * 获取通知设置结束
     *
     * @return
     */
    public NNRoom_SetEnd getNotify_setEnd() {
        return setEnd;
    }


    //设置那些玩家在游戏中
    public void setPosPlaying(int pos, boolean flag) {
        playingList.set(pos, flag);
    }

    //托管
    public void roomTrusteeship(int pos) {

        if (!((NNRoomPosMgr) this.room.getRoomPosMgr()).notAllTrusteeship() && CommTime.nowMS() - this.startMS >= WAITTRUSTEESHIPTIME)
            return;

        if (!this.playingList.get(pos)) return;

        NNRoomPos roomPos = (NNRoomPos) this.room.getRoomPosMgr().getPosByPosID(pos);
        if (!(roomPos.isTrusteeship() || roomPos.isRobot())) return;

        switch (this.status) {
            case NN_GAME_STATUS_HOG:

                if (this.getCallbackerNum(pos) < 0) {
                    int callBackNum = 0;
                    if (roomPos.isRobot() && this.room.getBaseRoomConfigure().getPrizeType() == PrizeType.Gold)
                        callBackNum = (int) ((this.room.roomCfg.zuidaqiangzhuang + 2) * Math.random());
                    this.setCallBacker(pos, callBackNum);
                    this.room.getRoomPosMgr().notify2All(SNN_CallBacker.make(this.room.getRoomID(), pos, callBackNum));
                }


                if (this.getPlayingCount() == this.callbackerList.size()) {
                    this.onHogEnd();
                }
                break;
            case NN_GAME_STATUS_BET:
                int endPoint = this.room.endPointList[this.room.roomCfg.difen];

                if (this.getBackerPos() == pos) break;
                if (this.betList.get(pos) > 0) break;

                int addBet = endPoint / 2;

                if (roomPos.isRobot() && this.room.getBaseRoomConfigure().getPrizeType() == PrizeType.Gold)
                    addBet = Math.random() > 0.5f ? endPoint : endPoint / 2;


                this.betList.set(pos, addBet);

                this.room.getRoomPosMgr().notify2All(SNN_AddBet.make(this.room.getRoomID(), pos, addBet));

                if (this.cheakAllBet()) {
                    this.onBetEnd();
                }
                break;
            case NN_GAME_STATUS_SENDCARD_SECOND:

                if (!this.checkCardList.get(pos)) this.checkCardList.set(pos, true);
                if (!this.openCardList.get(pos)) {
                    this.openCardList.set(pos, true);
                    this.room.getRoomPosMgr().notify2All(SNN_OpenCard.make(this.room.getRoomID(), pos, false, new ArrayList<Integer>()));
                }

                if (this.cheakAllOpenCard()) {
                    this.onSendCardEnd();
                }
                break;
            case NN_GAME_STATUS_RESULT:

                if (!roomPos.isGameReady()) {
                    roomPos.setGameReady(true);
                    roomPos.setLatelyOutCardTime(0);
                }

                break;
            default:
                break;
        }
    }


    //翻牌
    public void onOpenCard(WebSocketRequest request, CNN_OpenCard openCard) {
        if (this.getStatus() != NN_GameStatus.NN_GAME_STATUS_SENDCARD_SECOND) {
            request.error(ErrorCode.NotAllow, "onAddScore is not NN_GameStatus.NN_GAME_STATUS_SendCard error " + this.getStatus());
            return;
        }

        if (this.openCardList.get(openCard.pos)) {
            request.error(ErrorCode.NotAllow, "onAddScore is alary opend  error " + openCard.pos);
            return;
        }

        request.response();
        this.openCardList.set(openCard.pos, true);

        if (openCard.isSelectCard) {
            NNRoomPos roomPos = (NNRoomPos) this.room.getRoomPosMgr().getPosByPosID(openCard.pos);
            if (this.checkCardList(openCard.pos, openCard.cardList))
                roomPos.init(openCard.cardList);
        }

        this.room.getRoomPosMgr().notify2All(SNN_OpenCard.make(this.room.getRoomID(), openCard.pos, openCard.isSelectCard, openCard.cardList));
        if (this.cheakAllOpenCard()) {
            this.onSendCardEnd();
        }
    }

    //翻牌
    public void onCheckCard(WebSocketRequest request, CNN_CheckCard checkCard) {
        if (this.getStatus() != NN_GameStatus.NN_GAME_STATUS_SENDCARD_SECOND) {
            request.error(ErrorCode.NotAllow, "onAddScore is not NN_GameStatus.NN_GAME_STATUS_SendCard error " + this.getStatus());
            return;
        }

        if (this.checkCardList.get(checkCard.pos)) {
            request.error(ErrorCode.NotAllow, "onAddScore is alary checkCard  error " + checkCard.pos);
            return;
        }
        request.response();
        this.checkCardList.set(checkCard.pos, true);
    }

    //当前几个人在玩
    public int getPlayingCount() {
        int count = 0;
        for (boolean flag : this.playingList) {
            if (flag) count++;
        }
        return count;
    }

    //是否所有玩家都下注
    public boolean cheakAllBet() {
        for (int i = 0; i < this.room.getMaxPlayerNum(); i++) {
            if (i == this.getBackerPos()) continue;
            if (this.playingList.get(i) && this.betList.get(i) <= 0) {
                return false;
            }
        }
        return true;
    }

    //是否所有玩家都开牌
    public boolean cheakAllOpenCard() {
        for (int i = 0; i < this.room.getMaxPlayerNum(); i++) {
            if (this.playingList.get(i) && !this.openCardList.get(i)) {
                return false;
            }
        }
        return true;
    }

    /*
     * 设置是否抢庄
     * */
    public void setCallBacker(int pos, int callBackerNum) {
        boolean flag = false;
        try {
            if (this.callbackerList.size() > 0) {
                for (Victory vic : this.callbackerList) {
                    if (null == vic) continue;
                    if (pos == vic.getPos()) {
                        vic.setNum(callBackerNum);
                        flag = true;
                    }
                }
            }
            if (!flag) {
                if (this.callbackerList == null) this.callbackerList = new ArrayList<Victory>();
                Victory vic = new Victory(pos, callBackerNum);
                this.callbackerList.add(vic);
            }
        } catch (Exception e) {
            CommLogD.error("getCallbackerNum error:", e);
        }
        return;
    }

    /*
     * 抢庄倍数
     * */
    @SuppressWarnings("finally")
    public int getCallbackerNum(int pos) {
        int flag = -1;
        try {
            if (this.callbackerList.size() <= 0) return flag;
            for (Victory vic : this.callbackerList) {
                if (null == vic) continue;
                if (vic.getPos() == pos) {
                    flag = vic.getNum();
                    break;
                }
            }
//			CommLogD.info("getCallbackerNum num:%s ,pos:%s", flag, pos);
//			if(this.callbackerList.size() > 0) {
//				CommLogD.info("this.callbackerList.toString():%s", this.callbackerList.toString());
//			}
        } catch (Exception e) {
            CommLogD.error("getCallbackerNum error:", e);
        } finally {
            return flag;
        }
    }

    //有没有抢庄的
    @SuppressWarnings("finally")
    public int getCallBackerCount() {
        int count = 0;
        try {
            if (this.callbackerList.size() <= 0) return count;
            for (Victory vic : this.callbackerList) {
                if (null == vic) continue;
                if (vic.getNum() >= 1) {
                    count++;
                }
            }
        } catch (Exception e) {
            CommLogD.error("getCallBackerCount error:", e);
        } finally {
            return count;
        }
    }

    //获取阶段时间
    public int getWaitTimeByStatus() {
        int waitTime = 0;
        switch (this.status) {
            case NN_GAME_STATUS_SENDCARD_ONE:
                waitTime = 10000;
                break;
            case NN_GAME_STATUS_HOG:
                waitTime = 6000;
                break;
            case NN_GAME_STATUS_ONSURECALLBACKER:
                waitTime = 5000;
                break;
            case NN_GAME_STATUS_BET:
                waitTime = 5000;
                break;
            case NN_GAME_STATUS_SENDCARD_SECOND:
                waitTime = 15000;
                break;
            case NN_GAME_STATUS_RESULT:
                waitTime = 0;
                break;
            default:
                break;
        }
        return waitTime;
    }

    /**
     * @return backerPos
     */
    public int getBackerPos() {
        return this.m_backerPos;
    }


    /**
     * @param backerPos 要设置的 backerPos
     */
    public void setBackerPos(int backerPos, boolean isRandBackerPos) {
        this.m_backerPos = backerPos;
        this.m_isRandBackPos = isRandBackerPos;

        if (this.getCallbackerNum(backerPos) <= 0)
            this.setCallBacker(backerPos, 1);
    }

    /*
     * 牌验证
     * **/
    public boolean checkCardList(int pos, ArrayList<Integer> cardList) {
        NNRoomPos roomPos = (NNRoomPos) this.room.getRoomPosMgr().getPosByPosID(pos);
        for (Integer byte1 : cardList) {
            if (!roomPos.privateCards.contains(byte1)) {
                return false;
            }
        }
        return true;
    }


    //庄家确认结束
    public void onSureCallbacker() {
        this.setStatus(NN_GameStatus.NN_GAME_STATUS_BET);
        this.sendTuZhuPoint();
    }

    //下注结束
    public void onBetEnd() {
        for (int i = 0; i < this.room.getMaxPlayerNum(); i++) {
            if (!this.playingList.get(i)) continue;
            if (this.getBackerPos() == i) continue;

            if (this.playingList.get(i) && this.betList.get(i) == 0) {
                int bet = this.room.endPointList[this.room.roomCfg.difen] / 2;
                this.betList.set(i, bet);
                this.room.getRoomPosMgr().notify2All(SNN_AddBet.make(this.room.getRoomID(), i, bet));
            }
        }
        this.setStatus(NN_GameStatus.NN_GAME_STATUS_SENDCARD_SECOND);

        this.room.getRoomPosMgr().notify2All(SNN_StatusChange.make(this.room.getRoomID(), this.getStatus().value(), this.startMS, this.getSendCardNumber(), this.getBackerPos(), this.isRandBackPos(), 0, this.callbackerList));
    }

    //发牌结束
    public void onSendCardEnd() {
        for (int i = 0; i < this.room.getMaxPlayerNum(); i++) {
            if (this.playingList.get(i) && this.openCardList.get(i) == false) {
                this.room.getRoomPosMgr().notify2All(SNN_OpenCard.make(this.room.getRoomID(), i, false, new ArrayList<Integer>()));
            }
        }
        this.endSet();
    }

    //在玩家中随机一个玩家位置出来
    public int getRandPos() {
        int randPos = (int) (Math.random() * this.room.getPlayingCount());
        if (!this.playingList.get(randPos)) {
            for (int i = 0; i < this.room.getMaxPlayerNum(); i++) {
                randPos = (randPos + 1) % this.room.getMaxPlayerNum();
                if (this.playingList.get(randPos)) {
                    break;
                }
            }
        }
        return randPos;
    }

    //下发推注分数
    public void sendTuZhuPoint() {
        for (int i = 0; i < this.room.getMaxPlayerNum(); i++) {
            NNRoomPos roomPos = (NNRoomPos) this.room.getRoomPosMgr().getPosByPosID(i);
            if (roomPos == null || roomPos.getPid() == 0)
                continue;

            int point = this.getTuZhuPoint(roomPos.getPid());

            this.room.getRoomPosMgr().notify2Pos(roomPos.getPosID(), SNN_StatusChange.make(this.room.getRoomID(), this.getStatus().value(), this.startMS, 0, this.getBackerPos(), this.isRandBackPos(), point, this.callbackerList));
        }
    }

    //获取推注分数
    public int getTuZhuPoint(long pid) {
        int point = 0;
        NNRoomPos roomPos = (NNRoomPos) this.room.getRoomPosMgr().getPosByPid(pid);
        int curSetID = this.room.getCurSetID();
//		CommLogD.info("this.room.roomCfg.isXianJiaTuiZhu:%s  this.getBackerPos():%d roomPos.posID:%d roomPos.point:%d  "
//				+ "!roomPos.isTuiZhu(curSetID - 1):%s curSetID:%d\n", this.room.roomCfg.isXianJiaTuiZhu, this.getBackerPos() , roomPos.posID , 
//				roomPos.point , !roomPos.isTuiZhu(curSetID - 1) , curSetID );
        if (this.room.roomCfg.isXianJiaTuiZhu > 0 && this.getBackerPos() != roomPos.getPosID() && roomPos.getPoint() > 0 && curSetID > TUIZHUSETID
                && !roomPos.isTuiZhu(curSetID - 1 - 1)) {
            if (this.isCanTuiZhu(roomPos.getPosID())) {
                int bet = this.room.endPointList[this.room.roomCfg.difen];
                point = Math.min(roomPos.getPoint(), bet * TUIZHULIST[this.room.roomCfg.isXianJiaTuiZhu]);
                if (point == bet || point == bet / 2) {
                    point = 0;
                }
            }
        }
        return point;
    }


    /*
     * 获取上一句是否是闲家 且是否赢了
     * */
    public boolean isCanTuiZhu(int pos) {
        boolean flag = false;
        NNRoomSet set = this.getHisSet(this.room.getCurSetID() - 1 - 1);
        if (null == set) {
            return flag;
        }
        int lastPoint = set.pointList.get(pos);
        if (set.m_backerPos != pos && lastPoint > 0) {
            flag = true;
        }
        return flag;
    }

    //获取历史set
    public NNRoomSet getHisSet(int setID) {
        if (room.getHistorySetSize() > setID)
            return (NNRoomSet) room.getHistorySet(setID);
        return null;
    }


    /**
     * 设置神牌
     */
    private void godCard() {
        if (!room.isGodCard()) {
            return;
        }
        boolean flag1 = BasePockerLogic.deleteCard(this.setCard.getLeftCards(), room.getConfigMgr().getPrivate_Card1());
        boolean flag2 = BasePockerLogic.deleteCard(this.setCard.getLeftCards(), room.getConfigMgr().getPrivate_Card2());
        boolean flag3 = BasePockerLogic.deleteCard(this.setCard.getLeftCards(), room.getConfigMgr().getPrivate_Card3());
        boolean flag4 = BasePockerLogic.deleteCard(this.setCard.getLeftCards(), room.getConfigMgr().getPrivate_Card4());
        boolean flag5 = BasePockerLogic.deleteCard(this.setCard.getLeftCards(), room.getConfigMgr().getPrivate_Card5());
        boolean flag6 = BasePockerLogic.deleteCard(this.setCard.getLeftCards(), room.getConfigMgr().getPrivate_Card6());
        boolean flag7 = BasePockerLogic.deleteCard(this.setCard.getLeftCards(), room.getConfigMgr().getPrivate_Card7());
        boolean flag8 = BasePockerLogic.deleteCard(this.setCard.getLeftCards(), room.getConfigMgr().getPrivate_Card8());
        if (flag1 && flag2 && flag3 && flag4 && flag5 && flag6 && flag7 && flag8) {
            ArrayList<Integer> card1 = getGodCard(room.getConfigMgr().getPrivate_Card1());
            ArrayList<Integer> card2 = getGodCard(room.getConfigMgr().getPrivate_Card2());
            ArrayList<Integer> card3 = getGodCard(room.getConfigMgr().getPrivate_Card3());
            ArrayList<Integer> card4 = getGodCard(room.getConfigMgr().getPrivate_Card4());
            ArrayList<Integer> card5 = getGodCard(room.getConfigMgr().getPrivate_Card5());
            ArrayList<Integer> card6 = getGodCard(room.getConfigMgr().getPrivate_Card6());
            ArrayList<Integer> card7 = getGodCard(room.getConfigMgr().getPrivate_Card7());
            ArrayList<Integer> card8 = getGodCard(room.getConfigMgr().getPrivate_Card8());
            hMap.put(0, card1);
            hMap.put(1, card2);
            hMap.put(2, card3);
            hMap.put(3, card4);
            hMap.put(4, card5);
            hMap.put(5, card6);
            hMap.put(6, card7);
            hMap.put(7, card8);
        } else {
            int cardNum = this.room.maxCardCount;
            hMap.put(0, this.setCard.popList(cardNum));
            hMap.put(1, this.setCard.popList(cardNum));
            hMap.put(2, this.setCard.popList(cardNum));
            hMap.put(3, this.setCard.popList(cardNum));
            hMap.put(4, this.setCard.popList(cardNum));
            hMap.put(5, this.setCard.popList(cardNum));
            hMap.put(6, this.setCard.popList(cardNum));
            hMap.put(7, this.setCard.popList(cardNum));
        }
    }

    /**
     * 插入牌 并在牌堆里面删除
     */
    public ArrayList<Integer> getGodCard(ArrayList<Integer> list) {
        if (!room.isGodCard()) {
            return new ArrayList<>();
        }
        int cardNum = this.room.maxCardCount;
        ArrayList<Integer> cardList = new ArrayList<Integer>(cardNum);
        cardList.addAll(list);
        int count = cardNum - cardList.size();
        ArrayList<Integer> tempList = this.setCard.popList(count);
        BasePockerLogic.deleteCard(this.setCard.getLeftCards(), tempList);
        cardList.addAll(tempList);
        return cardList;
    }

    /**
     * @return m_isRandBackPos
     */
    public boolean isRandBackPos() {
        return m_isRandBackPos;
    }

    @Override
    public void clear() {
        this.room = null;
        if (null != this.setCard) {
            this.setCard = null;
        }
//        this.pointList = null;
        this.openCardList = null;
        this.setEnd = null;
        this.hMap = null;
    }

    @Override
    public void addDissolveRoom(BaseSendMsg baseSendMsg) {
        if (this.status == NN_define.NN_GameStatus.NN_GAME_STATUS_RESULT) {
            return;
        }
        NNRoomPosMgr roomPosMgr = (NNRoomPosMgr) this.room.getRoomPosMgr();
        this.getRoomPlayBack().addPlaybackList(baseSendMsg, roomPosMgr.getAllPlayBackNotify());
    }

    @Override
    public boolean checkExistPrizeType(PrizeType prizeType) {
        return prizeType.equals(this.room.getBaseRoomConfigure().getPrizeType());
    }

    @Override
    public void clearBo() {
        this.bo = null;
    }

    /**
     * 标识Id
     *
     * @return
     */
    @Override
    public int getTabId() {
        return this.room.getTabId();
    }
}
