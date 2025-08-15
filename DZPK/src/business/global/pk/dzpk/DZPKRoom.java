package business.global.pk.dzpk;

import business.dzpk.c2s.cclass.*;
import business.dzpk.c2s.iclass.*;
import business.global.pk.AbsPKSetRoom;
import business.global.pk.PKFactory;
import business.global.pk.PKRoom;
import business.global.pk.dzpk.base.DZPK_CardTypeImpl;
import business.global.room.base.AbsRoomPos;
import business.global.room.base.AbsRoomPosMgr;
import business.global.room.base.DissolveRoom;
import cenum.ChatType;
import cenum.ClassType;
import cenum.RoomTypeEnum;
import cenum.room.GaoJiTypeEnum;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.task.ScheduledExecutorServiceMgr;
import com.ddm.server.common.utils.CommFile;
import com.ddm.server.websocket.def.ErrorCode;
import com.google.gson.Gson;
import core.db.persistence.BaseDao;
import core.network.http.proto.SData_Result;
import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.RoomEndResult;
import jsproto.c2s.cclass.room.BaseRoomConfigure;
import jsproto.c2s.cclass.room.GetRoomInfo;
import jsproto.c2s.cclass.room.RoomPosInfo;
import jsproto.c2s.iclass.room.SBase_Dissolve;
import jsproto.c2s.iclass.room.SBase_PosLeave;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

/**
 * 长汀510K游戏房间
 *
 * @author Administrator
 */
@Setter
@Getter
public class DZPKRoom extends PKRoom {
    // 房间配置		
    private CDZPK_CreateRoom cfg = null;
    public static List<Class<?>> OP_TYPE_CLASSES;
    public List<DZPK_CardTypeImpl> cardTypes;
    public static String OP_TYPE_PATH = "business.global.pk.dzpk.cardtype";
    private DZPKRoom_SetEnd lasSeteEnd;
    private long createTime;
    /**
     * 任务触发器
     */
    @SuppressWarnings("rawtypes")
    private ScheduledFuture scheduledFuture;

    static {
        OP_TYPE_CLASSES = CommFile.getClasses(OP_TYPE_PATH);
    }

    @Override
    public RoomEndResult getRoomEndResult() {
        RoomEndResult sRoomEndResult = super.getRoomEndResult();
        List<DZPKResults> resultsList = sRoomEndResult.getResultsList();
        int max1 = resultsList.stream().mapToInt(k -> k.getPoint()).max().getAsInt();
        for (DZPKResults results : resultsList) {
            results.setWinner(results.getPoint() == max1);
        }
        return sRoomEndResult;
    }

    public List<DZPK_CardTypeImpl> getCardTypes() {
        if (cardTypes == null) {
            CDZPK_CreateRoom cfg = getCfg();
            cardTypes = new ArrayList<>();
            for (Class clz : OP_TYPE_CLASSES) {
                //内部类要去掉
                if (clz.getName().contains("$")) {
                    continue;
                }
                DZPK_CardTypeImpl baseCardType = (DZPK_CardTypeImpl) PKFactory.getCardType(clz);
                cardTypes.add(baseCardType);
            }
            cardTypes.sort(new Comparator<DZPK_CardTypeImpl>() {
                @Override
                public int compare(DZPK_CardTypeImpl o1, DZPK_CardTypeImpl o2) {
                    return o1.cardTypeValue(cfg.getSantiaoshunzi() == DZPKRoomEnum.DZPKSANTIAN_SHUNZIEnum.SAN_TIAO_BIG.ordinal()) < o2.cardTypeValue(cfg.getSantiaoshunzi() == DZPKRoomEnum.DZPKSANTIAN_SHUNZIEnum.SAN_TIAO_BIG.ordinal()) ? 1 : -1;
                }
            });
        }
        return cardTypes;
    }


    protected DZPKRoom(BaseRoomConfigure<CDZPK_CreateRoom> baseRoomConfigure, String roomKey, long ownerID) {
        super(baseRoomConfigure, roomKey, ownerID);
        initShareBaseCreateRoom(CDZPK_CreateRoom.class, baseRoomConfigure);
        this.cfg = (CDZPK_CreateRoom) baseRoomConfigure.getBaseCreateRoom();
        this.createTime = System.currentTimeMillis();
    }

    /**
     * 开启定时器
     */
    public void startTimer() {
        this.setScheduledFuture(ScheduledExecutorServiceMgr.getInstance().getScheduledFuture(() -> {
            try {
                if (getCurSet() != null) {
                    DZPKRoomEnum.DZPK_SNGDaXiaoMangEnum mangEnum = DZPKRoomEnum.DZPK_SNGDaXiaoMangEnum.getMang(getMangCount());
                    DZPKRoomEnum.DZPK_SNGDaXiaoMangEnum nextMang = DZPKRoomEnum.DZPK_SNGDaXiaoMangEnum.getMang(getMangCount() + 1);
                    DZPKShengMang shengMang = new DZPKShengMang();
                    shengMang.setDaMang(mangEnum.xiaoMang * 2);
                    shengMang.setXiaoMang(mangEnum.xiaoMang);
                    shengMang.setNextDaMang(mangEnum.xiaoMang * 2);
                    shengMang.setNextXiaoMang(mangEnum.xiaoMang);
                    ((DZPKRoomSet) getCurSet()).getRoomPlayBack().playBack2All(shengMang);
                }
            } catch (Exception e) {
                BaseDao.stackTrace("startTimer", e);
                CommLogD.error("[setScheduledFuture]:[{}] roomId:{} error:{}", getBaseRoomConfigure().getGameType().toString(), getRoomID(), e.getMessage(), e);
            }

        }, 0, timeDes()));
    }

    /**
     * 自动准备游戏 玩家加入房间时，自动进行准备。
     */
    @Override
    public boolean autoReadyGame() {
        return this.getBaseRoomConfigure().getBaseCreateRoom().getFangjian().contains(DZPKRoomEnum.DZPKGameRoomConfigEnum.ZiDongZhunBei.ordinal());
    }

    /**
     * 房主需要准备
     *
     * @return T:不准备,F:默认准备
     */
    public boolean ownerNeedReady() {
        return !this.getBaseRoomConfigure().getBaseCreateRoom().getFangjian().contains(DZPKRoomEnum.DZPKGameRoomConfigEnum.ZiDongZhunBei.ordinal());
    }


    /**
     * 清除记录。
     */
    @Override
    public void clearEndRoom() {
        super.clear();
        this.cfg = null;
    }

    @Override
    public int getPlayingCount() {
        return (int) this.getRoomPosMgr().posList.stream().filter(k -> k.getPid() > 0).count();
    }


    @Override
    public int getWanfa() {
        return cfg.getWanfa();
    }

    /**
     * 获取房间配置
     *
     * @return
     */
    public CDZPK_CreateRoom getRoomCfg() {
        if (this.cfg == null) {
            initShareBaseCreateRoom(CDZPK_CreateRoom.class, getBaseRoomConfigure());
            return (CDZPK_CreateRoom) getBaseRoomConfigure().getBaseCreateRoom();
        }
        return this.cfg;
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

    @Override
    public AbsRoomPosMgr initRoomPosMgr() {
        return new DZPKRoomPosMgr(this);
    }

    @Override
    public void startNewSet() {
        this.setCurSetID(this.getCurSetID() + 1);
        // 每个位置，清空准备状态		
        this.getRoomPosMgr().clearGameReady();
        // 通知局数变化		
        this.getRoomTyepImpl().roomSetIDChange();
        this.setAutoDismiss(false);
        this.setCurSet(this.newPKRoomSet(this.getCurSetID(), this, this.getDPos()));
    }

    @Override
    protected AbsPKSetRoom newPKRoomSet(int curSetID, PKRoom room, int dPos) {
        if (isSNG()) {
            return new DZPKRoomSet_SNG(curSetID, room, dPos);
        } else if (isDUAN_PAI()) {
            return new DZPKRoomSet_DuanPai(curSetID, room, dPos);
        } else if (isAO_HA_MA()) {
            return new DZPKRoomSet_AoMaHA(curSetID, room, dPos);
        } else {
            return new DZPKRoomSet(curSetID, room, dPos);
        }
    }


    @Override
    public <E> boolean RoomCfg(E m) {
        return false;
    }


    @SuppressWarnings("rawtypes")
    @Override
    public GetRoomInfo getRoomInfo(long pid) {
        S_DZPKGetRoomInfo ret = new S_DZPKGetRoomInfo();
        // 设置房间公共信息		
        this.getBaseRoomInfo(ret);
        if (Objects.nonNull(this.getCurSet())) {
            ret.setSet(this.getCurSet().getNotify_set(pid));
        } else {
            ret.setSet(new DZPKRoomSetInfo());
        }
        ret.setLocalTime(System.currentTimeMillis());
        ret.setShengMangtime(System.currentTimeMillis() - createTime);
        return ret;
    }

    @Override
    public DissolveRoom initDissolveRoom(int posID, int WaitSec) {
        DissolveRoom dissolveRoom = new DissolveRoom(this, posID, WaitSec);
        for (int i = 0; i < getPlayerNum(); i++) {
            if (getRoomPosMgr().getPosByPosID(i) == null) {
                dissolveRoom.getPosAgreeList().set(i, 1);
            }
        }
        return dissolveRoom;
    }

    @Override
    public BaseSendMsg Trusteeship(long roomID, long pid, int pos, boolean trusteeship) {
        return SDZPK_Trusteeship.make(roomID, pid, pos, trusteeship);
    }


    @Override
    public BaseSendMsg PosLeave(SBase_PosLeave posLeave) {
        return SDZPK_PosLeave.make(posLeave);
    }


    @Override
    public BaseSendMsg LostConnect(long roomID, long pid, boolean isLostConnect, boolean isShowLeave) {
        return SDZPK_LostConnect.make(roomID, pid, isLostConnect, isShowLeave);
    }

    @Override
    public BaseSendMsg PosContinueGame(long roomID, int pos) {
        return SDZPK_PosContinueGame.make(roomID, pos);
    }

    @Override
    public BaseSendMsg PosUpdate(long roomID, int pos, RoomPosInfo posInfo, int custom) {
        return SDZPK_PosUpdate.make(roomID, pos, posInfo, custom);
    }

    @Override
    public BaseSendMsg PosReadyChg(long roomID, int pos, boolean isReady) {
        return SDZPK_PosReadyChg.make(roomID, pos, isReady);
    }

    @Override
    public BaseSendMsg Dissolve(SBase_Dissolve dissolve) {
        return SDZPK_Dissolve.make(dissolve);
    }

    @Override
    public BaseSendMsg StartVoteDissolve(long roomID, int createPos, int endSec) {
        return SDZPK_StartVoteDissolve.make(roomID, createPos, endSec);
    }

    @Override
    public BaseSendMsg PosDealVote(long roomID, int pos, boolean agreeDissolve, int endSec) {
        return SDZPK_PosDealVote.make(roomID, pos, agreeDissolve);
    }

    @Override
    public BaseSendMsg Voice(long roomID, int pos, String url) {
        return SDZPK_Voice.make(roomID, pos, url);
    }

    @Override
    public <T> BaseSendMsg RoomRecord(List<T> records) {
        return SDZPK_RoomRecord.make(records);
    }

    @Override
    public <T> BaseSendMsg RoomEnd(T record, RoomEndResult<?> sRoomEndResult) {
        return SDZPK_RoomEnd.make(record, getRoomEndResult());
    }

    @Override
    public BaseSendMsg XiPai(long roomID, long pid, ClassType cType) {
        return SDZPK_XiPai.make(roomID, pid, cType);
    }

    @Override
    public BaseSendMsg ChatMessage(long pid, String name, String content, ChatType type, long toCId, int quickID) {
        return SDZPK_ChatMessage.make(pid, name, content, type, toCId, quickID);
    }

    @Override
    public BaseSendMsg ChangePlayerNum(long roomID, int createPos, int endSec, int playerNum) {
        return SDZPK_ChangePlayerNum.make(roomID, createPos, endSec, playerNum);
    }

    @Override
    public BaseSendMsg ChangePlayerNumAgree(long roomID, int pos, boolean agreeChange) {
        return SDZPK_ChangePlayerNumAgree.make(roomID, pos, agreeChange);
    }

    @Override
    public BaseSendMsg ChangeRoomNum(long roomID, String roomKey, int createType) {
        return SDZPK_ChangeRoomNum.make(roomID, roomKey, createType);
    }

    /**
     * 30秒未准备自动退出
     *
     * @return
     */
    @Override
    public boolean is30SencondTimeOut() {
        return this.getRoomCfg().getGaoji().contains(GaoJiTypeEnum.SECOND_TIMEOUT_30.ordinal());
    }

    /**
     * 是否禁止语音
     *
     * @return
     */
    @Override
    public boolean isDisAbleVoice() {
        return checkGaoJiXuanXiang(GaoJiTypeEnum.DISABLE_VOICE);
    }


    @Override
    public void roomTrusteeship(int pos) {
        RobotDeal(pos);
    }

    @Override
    public void cancelTrusteeship(AbsRoomPos pos) {

    }

    @Override
    public boolean isCanChangePlayerNum() {
        return this.getBaseRoomConfigure().getBaseCreateRoom().getFangjian().contains(DZPKRoomEnum.DZPKGameRoomConfigEnum.CHANGE_PLAYER_NUM.ordinal());
    }

    /**
     * 机器人处理
     */
    @Override
    public void RobotDeal(int pos) {
        if (this.getCurSet() != null) {
            AbsPKSetRoom mSetRoom = (AbsPKSetRoom) this.getCurSet();
            if (null != mSetRoom.getCurRound()) {
                mSetRoom.getCurRound().RobothandCrad(pos);
            }
        }
    }

    public boolean isDE_ZHOU() {
        return getRoomCfg().getMoshi() == DZPKRoomEnum.DZPK_MoShi.DE_ZHOU.value();
    }

    public boolean isAO_HA_MA() {
        return getRoomCfg().getMoshi() == DZPKRoomEnum.DZPK_MoShi.AO_HA_MA.value();
    }

    public boolean isDUAN_PAI() {
        return getRoomCfg().getMoshi() == DZPKRoomEnum.DZPK_MoShi.DUAN_PAI.value();
    }

    public boolean isSNG() {
        return getRoomCfg().getMoshi() == DZPKRoomEnum.DZPK_MoShi.SNG.value();
    }


    public boolean isQUICK_SPORT() {
        return cfg.getWanfa() == DZPKRoomEnum.DZPKWanFaEnum.QUICK_SPORT.ordinal();
    }


    public boolean isJingShai() {
        return RoomTypeEnum.UNION.equals(getRoomTypeEnum());
    }

    public SData_Result getLastSetInfo(long pid) {

        try {
            lock();
            AbsRoomPos roomPos = this.getRoomPosMgr().getPosByPid(pid);
            if (roomPos == null) {
                // 找不到指定的位置信息
                return SData_Result.make(ErrorCode.NotAllow, "not in pos");
            }
            if (lasSeteEnd == null) {
                // 找不到指定的位置信息
                return SData_Result.make(ErrorCode.NotAllow, "not lasSetEndInfo");
            }
            this.getRoomPosMgr().notify2Pos(roomPos.getPosID(), SDZPK_LastSetBetInfo.make(getRoomID(), lasSeteEnd));
            return SData_Result.make(ErrorCode.Success);
        } finally {
            unlock();
        }

    }

    /**
     * 判断是否到了升盲的时间
     *
     * @return
     */
    public boolean isShengMangTime() {
        long gameTime = System.currentTimeMillis() - getCreateTime();
        return gameTime % timeDes() == 0;
    }

    public long timeDes() {
        CDZPK_CreateRoom cfg = getCfg();
        return cfg.getWanfa() == DZPKRoomEnum.DZPKWanFaEnum.QUICK_SPORT.ordinal() ? 3 * 60 * 1000 : 5 * 60 * 1000;
    }

    /**
     * @return
     */
    private int getMangCount() {
        long gameTime = System.currentTimeMillis() - getCreateTime();
        return (int) (gameTime / timeDes());

    }
}
