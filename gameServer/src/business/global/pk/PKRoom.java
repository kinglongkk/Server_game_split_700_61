package business.global.pk;

import business.global.room.RoomRecordMgr;
import business.global.room.base.AbsBaseRoom;
import business.global.room.base.AbsRoomPos;
import business.global.room.base.AbsRoomPosMgr;
import cenum.PKOpType;
import cenum.room.GameRoomConfigEnum;
import cenum.room.RoomState;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import core.network.http.proto.SData_Result;
import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.RoomEndResult;
import jsproto.c2s.cclass.pk.base.BasePKRoom_Record;
import jsproto.c2s.cclass.room.BaseRoomConfigure;

import java.util.stream.Collectors;

/**
 * 扑克公共父类
 *
 * @author Administrator
 */
public abstract class PKRoom extends AbsBaseRoom {
    // 头家或者庄位
    private int dPos = 0;

    /**
     * 扑克公共父类构造函数
     *
     * @param baseRoomConfigure 公共配置
     * @param roomKey           房间key
     * @param ownerID           房主ID
     */
    @SuppressWarnings("rawtypes")
    protected PKRoom(BaseRoomConfigure baseRoomConfigure, String roomKey, long ownerID) {
        super(baseRoomConfigure, roomKey, ownerID);
    }

    /**
     * 初始化创建者
     */
    @Override
    public void initCreatePos() {
        if (this.getOwnerID() <= 0L) {
            return;
        }
        // 进入房间
        this.enterRoom(this.getOwnerID(), 0, false, null);
        this.getRoomPosMgr().getPosByPid(this.getOwnerID()).setReady(!ownerNeedReady());
    }

    /**
     * 获取庄家位置
     *
     * @return
     */
    public int getDPos() {
        return this.dPos;
    }

    /**
     * 设置庄家位置
     *
     * @param dPos 庄位置
     */
    public void setDPos(int dPos) {
        this.dPos = dPos;
    }

    /**
     * 可选玩法
     *
     * @param m
     * @return
     */
    public abstract <E> boolean RoomCfg(E m);

    /**
     * 玩法
     *
     * @return
     */
    public abstract int getWanfa();

    /**
     * 房间内每个位置信息 管理器
     */
    @Override
    public AbsRoomPosMgr initRoomPosMgr() {
        return new PKRoomPosMgr(this);
    }

    /**
     * 加入房间的其他条件 条件不满足不进入
     */
    @Override
    public boolean enterRoomOtherCondition(long pid) {
        return RoomState.Init.equals(this.getRoomState());
    }

    /**
     * 主动离开房间的其他条件 条件不满足不退出
     */
    @Override
    public boolean exitRoomOtherCondition(long pid) {
        return RoomState.Init.equals(this.getRoomState());
    }

    /**
     * 获取扑克房间记录信息
     *
     * @return
     */
    public BasePKRoom_Record getPKRoomRecordInfo() {
        BasePKRoom_Record recordInfo = new BasePKRoom_Record();
        recordInfo.setEndSec(this.getGameRoomBO().getEndTime());
        recordInfo.setPlayers(this.getRoomPosMgr().getPosList().stream().filter(k -> k.getPid() > 0L && k.isPlayTheGame()).map(k -> k.getShortPlayer()).collect(Collectors.toList()));
        recordInfo.setRoomID(this.getRoomID());
        recordInfo.setSetCnt(this.getGameRoomBO().getSetCount());
        return recordInfo;
    }

    /**
     * 结算
     */
    @Override
    public void calcEnd() {
        // 更新游戏房间BO和更新玩家个人游戏记录BO
        this.updateGameRoomBOAndPlayerRoomAloneBO();
    }

    /**
     * 新一局
     */
    @Override
    public void startNewSet() {
        this.setCurSetID(this.getCurSetID() + 1);
        // / 计算庄位
        if (this.getCurSetID() == 1) {
            setDPos(0);
        } else if (this.getCurSet() != null) {
            AbsPKSetRoom mRoomSet = (AbsPKSetRoom) this.getCurSet();
            // 根据上一局计算下一局庄家
            setDPos(0);
            mRoomSet.clear();
        }
        // 每个位置，清空准备状态
        this.getRoomPosMgr().clearGameReady();
        // 通知局数变化
        this.getRoomTyepImpl().roomSetIDChange();
        this.setCurSet(this.newPKRoomSet(this.getCurSetID(), this, this.getDPos()));
    }

    /**
     * 启动定时器
     */
    @Override
    public void startTrusteeShipTime() {
        this.getTrusteeship().startTrusteeShipTime();
    }

    /**
     * 扑克小局
     *
     * @return
     */
    protected abstract AbsPKSetRoom newPKRoomSet(int curSetID, PKRoom room, int dPos);

    /**
     * 清空结束房间
     */
    @Override
    public void clearEndRoom() {
        super.clear();
    }

    /**
     * 设置定时器时间
     */
    @Override
    public int getTimerTime() {
        return 200;
    }

    /**
     * 游戏阶段人数
     */
    @Override
    public int getPlayingCount() {
        return this.getBaseRoomConfigure().getBaseCreateRoom().getPlayerNum();
    }

    /**
     * 打牌操作
     *
     * @param pid     用户ID
     * @param setID   局数
     * @param roundID 回合
     * @param opType  打牌类型
     * @param mOpCard 牌
     */
    @SuppressWarnings("rawtypes")
    public SData_Result opCard(WebSocketRequest request, long pid, int setID, int roundID, PKOpType opType, PKOpCard mOpCard) {
        try {
            lock();
            AbsPKSetRoom mCurSet = (AbsPKSetRoom) this.getCurSet();
            if (null == mCurSet || mCurSet.getSetID() != setID) {
                return SData_Result.make(ErrorCode.NotAllow,
                        "not current setID:" + (mCurSet != null ? mCurSet.getSetID() : 0));
            }
            if (mCurSet.getCurRound() == null || mCurSet.getCurRound().getRoundID() != roundID) {
                return SData_Result.make(ErrorCode.NotAllow, "not current roundID:"
                        + (mCurSet.getCurRound() != null ? mCurSet.getCurRound().getRoundID() : 0));
            }
            AbsRoomPos roomPos = this.getRoomPosMgr().getPosByPid(pid);
            if (null == roomPos) {
                return SData_Result.make(ErrorCode.NotAllow, "not find your pos:");
            }
            // 重置时间
            if (mCurSet.getCurRound().opCard(request, roomPos.getPosID(), opType, mOpCard) >= 0) {
                roomPos.setLatelyOutCardTime(0L);
            }
            return SData_Result.make(ErrorCode.Success);
        } finally {
            unlock();
        }
    }

    /**
     * 房间结算
     *
     * @param record         结算信息
     * @param sRoomEndResult 总结算记录
     * @return
     */
    public abstract <T> BaseSendMsg RoomEnd(T record, RoomEndResult<?> sRoomEndResult);

    /**
     * 神牌消息
     *
     * @param msg
     * @param pid
     */
    @Override
    public void godCardMsg(String msg, long pid) {
        if (null == this.getCurSet()) {
            return;
        }
        AbsPKSetRoom setRoom = (AbsPKSetRoom) this.getCurSet();
        setRoom.getGodInfo().addGodCardMap(pid, msg);
    }

    /**
     * 机器人处理
     */
    @Override
    public void RobotDeal(int pos) {
        if (this.getCurSet() != null) {
            AbsPKSetRoom mSetRoom = (AbsPKSetRoom) this.getCurSet();
            if (null != mSetRoom.getCurRound()) {
//				mSetRoom.getCurRound().RobothandCrad(pos);
            }
        }
    }

    @Override
    public void roomTrusteeship(int pos) {
        RobotDeal(pos);
    }

    @Override
    public long getTrusteeshipUpdateTime() {
        return 3500;
    }

    @Override
    public void cancelTrusteeship(AbsRoomPos pos) {

    }

    @Override
    public void setEndRoom() {
        if (null != this.getCurSet()) {
            // 增加房局记录
            this.getRoomPosMgr().notify2All(this.RoomEnd(this.getPKRoomRecordInfo(), this.getRoomEndResult()));
            if (getHistorySet().size() > 0) {
                RoomRecordMgr.getInstance().add(this);
                refererReceiveList();
            }
        }
    }

    @Override
    public boolean isCanChangePlayerNum() {
        return this.getBaseRoomConfigure().getBaseCreateRoom().getFangjian().contains(GameRoomConfigEnum.FangJianQieHuanRenShu.ordinal());
    }

    /**
     * 自动开始游戏 所有玩家准备好自动开始。
     */
    @Override
    public boolean autoStartGame() {
        return true;
    }

    /**
     * 自动准备游戏 玩家加入房间时，自动进行准备。
     */
    @Override
    public boolean autoReadyGame() {
        return true;
    }

    /**
     * 中途解散保存战绩
     *
     * @return T:保存战绩,F:不保存
     */
    @Override
    public boolean isMidwayDisbandmentPreservation() {
        return true;
    }

    /**
     * 房主是否需要准备
     *
     * @return
     */
    public boolean ownerNeedReady() {
        return false;
    }

    /**
     * 开始条件
     *
     * @param pid
     * @return
     */
    @Override
    public boolean startGameOtherCondition(long pid) {
        return this.getRoomPosMgr().isAllReady();
    }

    /**
     * 是否在玩
     * @param i
     * @return
     */
    public boolean isPlaying(int i){
        AbsRoomPos roomPos = getRoomPosMgr().getPosByPosID(i);
        if(roomPos!=null){
            return roomPos.isPlayTheGame();
        }
        return false;
    }

    /**
     * 是否在玩
     * @param pid
     * @return
     */
    public boolean isPlayingByPid(long pid){
        AbsRoomPos roomPos = getRoomPosMgr().getPosByPid(pid);
        if(roomPos!=null){
            return roomPos.isPlayTheGame();
        }
        return false;
    }

    @Override
    protected void continueRoom() {

    }
}
