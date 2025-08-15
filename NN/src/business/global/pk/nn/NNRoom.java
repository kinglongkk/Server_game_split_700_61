package business.global.pk.nn;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import business.global.room.base.AbsRoomPos;
import business.global.room.base.AbsRoomPosMgr;
import business.global.room.base.DissolveRoom;
import business.global.room.pk.PockerRoom;
import business.nn.c2s.cclass.NNRoomSetInfo;
import business.nn.c2s.cclass.NN_define;
import business.nn.c2s.iclass.*;
import cenum.*;
import cenum.room.GameRoomConfigEnum;
import cenum.room.RoomState;
import com.google.gson.Gson;

import business.global.room.RoomRecordMgr;
import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.pk.PKRoom_Record;
import jsproto.c2s.cclass.pk.PKRoom_RecordPosInfo;
import jsproto.c2s.cclass.room.BaseRoomConfigure;
import business.nn.c2s.cclass.NN_define.NN_GameType;
import jsproto.c2s.cclass.room.GetRoomInfo;
import jsproto.c2s.cclass.room.RoomPosInfo;
import jsproto.c2s.iclass.S_GetRoomInfo;
import jsproto.c2s.iclass.room.SBase_Dissolve;
import jsproto.c2s.iclass.room.SBase_PosLeave;

public class NNRoom extends PockerRoom {
    public CNN_CreateRoom roomCfg; //创建配置
    public final int maxCardCount = 5;
    public final int endPointList[] = {2, 4, 8};
    public final int backerPointList[] = {0, 100, 150, 200};
    private int callbacker = -1; //庄家备份 -1:标识没有庄家
    private NNConfigMgr configMgr = new NNConfigMgr();


    protected NNRoom(BaseRoomConfigure<CNN_CreateRoom> baseRoomConfigure, String roomKey, long ownerID) {
        super(baseRoomConfigure, roomKey, ownerID);
        initShareBaseCreateRoom(CNN_CreateRoom.class, baseRoomConfigure);
        this.roomCfg = (CNN_CreateRoom) baseRoomConfigure.getBaseCreateRoom();
        if (PrizeType.RoomCard == getBaseRoomConfigure().getPrizeType() && roomCfg.getSign() == NN_GameType.NN_GDZJ.value() && roomCfg.shangzhuangfenshu > 0)
            this.getRoomPosMgr().getPosByPosID(0).setPoint(this.backerPointList[this.roomCfg.shangzhuangfenshu]);
    }

    /**
     * 房间内每个位置信息 管理器
     */
    @Override
    public AbsRoomPosMgr initRoomPosMgr() {
        return new NNRoomPosMgr(this);
    }

    /**
     * 获取房间配置
     *
     * @return
     */
    public CNN_CreateRoom getRoomCfg() {
        if (this.roomCfg == null) {
            initShareBaseCreateRoom(CNN_CreateRoom.class, getBaseRoomConfigure());
            return (CNN_CreateRoom) getBaseRoomConfigure().getBaseCreateRoom();
        }
        return this.roomCfg;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCfg() {
        return (T) getRoomCfg();
    }

    @Override
    public String dataJsonCfg() {
        // 获取房间配置
        return new Gson().toJson(this.getRoomCfg());
    }

    /**
     * 清除记录。
     */
    @Override
    public void clearEndRoom() {
        super.clear();
        this.roomCfg = null;
    }


    @Override
    public boolean getCurSetUpdate(int sec) {
        return getCurSet().update(sec);
    }


    @Override
    public void startNewSet() {
        this.setCurSetID(this.getCurSetID() + 1);
        this.createSet();
        // 每个位置，清空准备状态
        this.getRoomPosMgr().clearGameReady();
    }

    //创建set
    public void createSet() {
        if (null != this.getCurSet()) {
            this.getCurSet().clear();
            this.setCurSet(null);
        }
        if (PrizeType.RoomCard == this.getBaseRoomConfigure().getPrizeType()) {
            if (roomCfg.getSign() == NN_GameType.NN_ZYQZ.value()) {
                this.setCurSet(new NNRoomSet_ZYQZ(this));
            } else if (this.roomCfg.getSign() == NN_GameType.NN_MPQZ.value()) {
                this.setCurSet(new NNRoomSet_MPQZ(this));
            } else if (this.roomCfg.getSign() == NN_GameType.NN_NNSZ.value()) {
                this.setCurSet(new NNRoomSet_NNSZ(this));
            } else if (this.roomCfg.getSign() == NN_GameType.NN_GDZJ.value()) {
                this.setCurSet(new NNRoomSet_GDZJ(this));
            } else if (this.roomCfg.getSign() == NN_GameType.NN_TBNN.value()) {
                this.setCurSet(new NNRoomSet_TBNN(this));
            } else if (NN_GameType.NN_LZNN.value() == this.roomCfg.getSign()) {
                this.setCurSet(new NNRoomSet_LZNN(this));
            }
        }
        this.getRoomTyepImpl().roomSetIDChange();
    }

    @Override
    public void setEndRoom() {
        if (null != this.getCurSet()) {
            if (getHistorySet().size() > 0) {
                // 增加房局记录
                RoomRecordMgr.getInstance().add(this);
                this.getRoomPosMgr().notify2All(SNN_RoomEnd.make(this.getPKRoomRecordInfo()));
                refererReceiveList();
            }
        }
    }

    /*
     * 主动离开房间的其他条件 条件不满足不退出
     * */
    @Override
    public boolean exitRoomOtherCondition(long pid) {
        // 游戏已经开始，不能自由离开 防止一人加入一人退出时bug
        if (this.getCurSet() != null && this.getCurSet().getSetID() == 0 && !RoomState.Init.equals(this.getRoomState())) {
            return false;
        }
        // 玩家玩过游戏就不能离开
        NNRoomPos pos = (NNRoomPos) this.getRoomPosMgr().getPosByPid(pid);
        if (pos != null && pos.isPlayTheGame()) {
            return false;
        }
        return true;
    }

    /**
     * 构建房间回放返回给客户端
     *
     * @return 通知结构体
     */
    public PKRoom_Record getPKRoomRecordInfo() {
        PKRoom_Record pkRoom_record = new PKRoom_Record();
        pkRoom_record.setCnt = this.getHistorySetSize();
        pkRoom_record.recordPosInfosList = this.getRecordPosInfoList();
        pkRoom_record.roomID = this.getRoomID();
        pkRoom_record.endSec = this.getGameRoomBO().getEndTime();
        return pkRoom_record;
    }

    @Override
    public void calcEnd() {
        if (PrizeType.RoomCard == this.getBaseRoomConfigure().getPrizeType() && this.roomCfg.getSign() == NN_GameType.NN_GDZJ.value() && this.roomCfg.shangzhuangfenshu > 0) {
            int sourcePoint = this.getRoomPosMgr().getPosByPosID(0).getPoint();
            this.getRoomPosMgr().getPosByPosID(0).setPoint(sourcePoint - this.backerPointList[this.roomCfg.shangzhuangfenshu]);
        }
        super.calcEnd();
    }

    //获取最大玩家数
    public int getMaxPlayerNum() {
        return ShareDefine.MAXPLAYERNUM_NN;
    }

    //获取庄家
    public int getCallBacker() {
        return this.callbacker;
    }

    //设置庄家
    public void setCallBacker(int callBacker) {
        this.callbacker = callBacker;
    }

    protected List<PKRoom_RecordPosInfo> getRecordPosInfoList() {
        List<PKRoom_RecordPosInfo> sRecord = new ArrayList<PKRoom_RecordPosInfo>();
        for (int i = 0; i < this.getMaxPlayerNum(); i++) {
            PKRoom_RecordPosInfo posInfo = new PKRoom_RecordPosInfo();

            NNRoomPos roomPos = (NNRoomPos) this.getRoomPosMgr().getPosByPosID(i);
            posInfo.flatCount = roomPos.getFlat();
            posInfo.loseCount = roomPos.getLose();
            posInfo.winCount = roomPos.getWin();

            posInfo.point = roomPos.getPoint();
            posInfo.pos = i;
            posInfo.pid = roomPos.getPid();
            posInfo.setSportsPoint(roomPos.sportsPoint());
            sRecord.add(posInfo);
        }
        return sRecord;
    }

    /**
     * 加入房间的其他条件 条件不满足不进入
     */
    @Override
    public boolean enterRoomOtherCondition(long pid) {
        if (this.getRoomCfg().gaojixuanxiang.contains(0) && this.getRoomState() != RoomState.Init) {
            return false;
        }
        return true;
    }

    @Override
    public boolean autoStartGame() {
        //亲友圈 大联盟2-8人自动开始游戏
        if (RoomTypeEnum.checkUnionOrClub(this.getRoomTyepImpl().getRoomTypeEnum())) {
            return true;
        }
        return false;
    }

    //当前几个人在玩
    public int getPlayingCount() {
        int count = 0;
        if (this.getCurSet() != null) {
            count = ((NNRoomSet) this.getCurSet()).getPlayingCount();
        } else {
            count = this.getMaxPlayerNum();
        }
        return count;
    }

    @Override
    public DissolveRoom initDissolveRoom(int posID, int WaitSec) {
        return new NNDissolveRoom(this, posID, WaitSec);
    }

    @Override
    public void roomTrusteeship(int pos) {
        ((NNRoomSet) this.getCurSet()).roomTrusteeship(pos);
    }

    @Override
    public void RobotDeal(int pos) {
        ((NNRoomSet) this.getCurSet()).roomTrusteeship(pos);
    }

    /**
     * @return configMgr
     */
    public NNConfigMgr getConfigMgr() {
        return configMgr;
    }

    @Override
    public void cancelTrusteeship(AbsRoomPos pos) {
        ((NNRoomSet) this.getCurSet()).roomTrusteeship(pos.getPosID());
    }

    @Override
    public boolean isGodCard() {
        // TODO 自动生成的方法存根
        return this.getConfigMgr().isGodCard();
    }

    @Override
    public BaseSendMsg XiPai(long roomID, long pid, ClassType cType) {
        return SNN_XiPai.make(roomID, pid, cType);
    }

    @Override
    public BaseSendMsg ChatMessage(long pid, String name, String content, ChatType type, long toCId, int quickID) {
        return SNN_ChatMessage.make(pid, name, content, type, toCId, quickID);
    }

    @Override
    public boolean isCanChangePlayerNum() {
        return this.getBaseRoomConfigure().getBaseCreateRoom().getFangjian().contains(GameRoomConfigEnum.FangJianQieHuanRenShu.ordinal());
    }

    @Override
    public BaseSendMsg ChangePlayerNum(long roomID, int createPos, int endSec, int playerNum) {
        return SNN_ChangePlayerNum.make(roomID, createPos, endSec, playerNum);
    }

    @Override
    public BaseSendMsg ChangePlayerNumAgree(long roomID, int pos, boolean agreeChange) {
        return SNN_ChangePlayerNumAgree.make(roomID, pos, agreeChange);
    }

    @Override
    public BaseSendMsg ChangeRoomNum(long roomID, String roomKey, int createType) {
        return SNN_ChangeRoomNum.make(roomID, roomKey, createType);
    }

    @Override
    public GetRoomInfo getRoomInfo(long pid) {
        S_GetRoomInfo ret = new S_GetRoomInfo();
        // 设置房间公共信息
        this.getBaseRoomInfo(ret);
        if (Objects.nonNull(this.getCurSet())) {
            ret.setSet(this.getCurSet().getNotify_set(pid));
        } else {
            ret.setSet(new NNRoomSetInfo());
        }
        return ret;
    }

    /**
     * 神牌消息
     *
     * @param msg
     * @param pid
     */
    @Override
    public void godCardMsg(String msg, long pid) {

    }

    @Override
    public BaseSendMsg Trusteeship(long roomID, long pid, int pos, boolean trusteeship) {
        return SNN_Trusteeship.make(roomID, pid, pos, trusteeship);
    }


    @Override
    public BaseSendMsg PosLeave(SBase_PosLeave posLeave) {
        return SNN_PosLeave.make(posLeave);
    }

    @Override
    public BaseSendMsg LostConnect(long roomID, long pid, boolean isLostConnect, boolean isShowLeave) {
        return SNN_LostConnect.make(roomID, pid, isLostConnect, isShowLeave);
    }

    @Override
    public BaseSendMsg PosContinueGame(long roomID, int pos) {
        return SNN_PosContinueGame.make(roomID, pos);
    }

    @Override
    public BaseSendMsg PosUpdate(long roomID, int pos, RoomPosInfo posInfo, int custom) {
        return SNN_PosUpdate.make(roomID, pos, posInfo, custom);
    }

    @Override
    public BaseSendMsg PosReadyChg(long roomID, int pos, boolean isReady) {
        return SNN_PosReadyChg.make(roomID, pos, isReady);
    }

    @Override
    public BaseSendMsg Dissolve(SBase_Dissolve dissolve) {
        return SNN_Dissolve.make(dissolve);
    }

    @Override
    public BaseSendMsg StartVoteDissolve(long roomID, int createPos, int endSec) {
        return SNN_StartVoteDissolve.make(roomID, createPos, endSec);
    }

    @Override
    public BaseSendMsg PosDealVote(long roomID, int pos, boolean agreeDissolve, int endSec) {
        return SNN_PosDealVote.make(roomID, pos, agreeDissolve);
    }

    @Override
    public BaseSendMsg Voice(long roomID, int pos, String url) {
        return SNN_Voice.make(roomID, pos, url);
    }

    @Override
    public <T> BaseSendMsg RoomRecord(List<T> records) {
        return SNN_RoomRecord.make(records);
    }

    @Override
    public int getTimerTime() {
        return 200;
    }

    /**
     * 房主是否需要准备
     *
     * @return
     */
    @Override
    public boolean ownerNeedReady() {
        return true;
    }

    /**
     * 自动准备游戏 玩家加入房间时，自动进行准备。
     */
    @Override
    public boolean autoReadyGame() {
        return getRoomCfg().getKexuanwanfa().contains(NN_define.KeXuanWanFa.ZiDong.getType());
    }


}
