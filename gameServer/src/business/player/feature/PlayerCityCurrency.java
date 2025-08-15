package business.player.feature;

import BaseCommon.CommLog;
import business.player.Player;
import business.shareplayer.SharePlayerCurrencyBOMgr;
import cenum.ConstEnum;
import cenum.ItemFlow;
import cenum.PrizeType;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.Config;
import com.ddm.server.common.redis.DistributedRedisLock;
import com.ddm.server.websocket.def.ErrorCode;
import com.google.common.collect.Maps;
import core.config.refdata.RefDataMgr;
import core.config.refdata.ref.RefSelectCity;
import core.db.entity.clarkGame.PlayerCityCurrencyBO;
import core.db.other.Restrictions;
import core.db.service.clarkGame.ClubListBOService;
import core.db.service.clarkGame.PlayerCityCurrencyBOService;
import core.ioc.ContainerMgr;
import core.logger.flow.FlowLogger;
import core.network.http.proto.SData_Result;
import jsproto.c2s.cclass.GameType;
import jsproto.c2s.cclass.PlayerCityCurrencyItem;
import jsproto.c2s.cclass.club.Club_define;
import jsproto.c2s.cclass.union.UnionDefine;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 城市钻石
 */
@Data
public class PlayerCityCurrency extends Feature {
    private Map<Integer, PlayerCityCurrencyBO> cityCurrencyBOMap = Maps.newConcurrentMap();

    public PlayerCityCurrency(Player player) {
        super(player);
    }

    @Override
    public void loadDB() {
    }

    /**
     * 获取玩家城市钻石列表
     *
     * @return
     */
    public List<PlayerCityCurrencyItem> getPlayerCityCurrencyList() {
        // 城市列表
        final List<Integer> cityIdList = getPlayer().getFeature(PlayerFamily.class).getCityIdList();
        // 获取数据库玩家城市钻石列表
        List<PlayerCityCurrencyItem> playerCityCurrencyItems = ContainerMgr.get().getComponent(PlayerCityCurrencyBOService.class).findAllE(Restrictions.eq("pid", getPid()), PlayerCityCurrencyItem.class, PlayerCityCurrencyItem.getItemsName());
        if (CollectionUtils.isEmpty(playerCityCurrencyItems)) {
            return Collections.emptyList();
        }
        return playerCityCurrencyItems.stream().map(k -> {
            PlayerCityCurrencyBO playerCityCurrencyBO = null;
            if(Config.isShare()){
                playerCityCurrencyBO = SharePlayerCurrencyBOMgr.getInstance().get(getPid(), k.getCityId());
            } else {
                playerCityCurrencyBO = getCityCurrencyBOMap().get(k.getCityId());
            }
            if (Objects.nonNull(playerCityCurrencyBO)) {
                return new PlayerCityCurrencyItem(playerCityCurrencyBO.getCityId(), playerCityCurrencyBO.getValue(), cityIdList.contains(k.getCityId()));
            } else {
                return new PlayerCityCurrencyItem(k.getCityId(), k.getValue(), cityIdList.contains(k.getCityId()));
            }
        }).collect(Collectors.toList());
    }

    /**
     * 公共钻石转移到指定城市
     *
     * @return
     */
    public int cityRoomCardChange(int cityId) {
        if (cityId <= 0) {
            CommLog.error("PlayerCityCurrency cityRoomCardChange Pid:{},cityId:{}", getPid(), cityId);
            return Config.DE_DEBUG() ? 1000000:0;
        }
        // 有公共房卡直接转移到当前城市
        if (getPlayer().getPlayerBO().getRoomCard() > 0) {
            // 获取奖励
            this.gainItemFlow(getPlayer().getFeature(PlayerCurrency.class).clearRoomCard(), ItemFlow.PUBLIC_CARD_CHANGE, cityId);
        }
        // 获取指定城市的钻石
        return getPlayerCityCurrencyValue(cityId);
    }


    /**
     * 检查指定城市的钻石是否足够
     *
     * @param cityId 城市Id
     * @param value  消耗值
     * @return
     */
    public boolean check(int value, int cityId) {
        return getPlayerCityCurrencyValue(cityId) >= value;
    }


    /**
     * 检查并消耗房卡 创建房间或加入房间
     *
     * @param count    消耗值
     * @param gameType 游戏类型
     * @param cityId   城市Id
     * @return
     */
    public boolean checkAndConsumeRoom(int count, GameType gameType, int cityId) {
        // 检测充值货币是否足够
        if (this.check(count, cityId)) {
            return consumeCityRoomCard(count, gameType.getId(), ItemFlow.RoomCardRoom, ConstEnum.ResOpType.Lose, cityId);
        }
        return false;
    }

    /**
     * 检查并消耗房卡
     *
     * @param count  消耗值
     * @param reason 选择项
     * @param cityId 城市Id
     * @return
     */
    public boolean checkAndConsumeItemFlow(int count, ItemFlow reason, int cityId) {
        // 检测充值货币是否足够
        if (this.check(count, cityId)) {
            return consumeCityRoomCard(count, -1, reason, ConstEnum.ResOpType.Lose, cityId);
        }
        return false;
    }

    /**
     * 获取奖励
     *
     * @param count  消耗值
     * @param reason 选择项
     * @param cityId 城市Id
     * @return
     */
    public void gainItemFlow(int count, ItemFlow reason, int cityId) {
        this.gainCityRoomCard(count, -1, reason, ConstEnum.ResOpType.Gain, cityId);
    }

    /**
     * 检查并亲友圈消耗房卡 创建亲友圈房间-房卡
     *
     * @param count    消耗值
     * @param gameType 游戏类型
     * @param clubID   亲友圈ID
     * @param status   亲友圈状态
     * @param agentsID 代理ID
     * @param level    代理等级
     * @param cityId   城市Id
     * @return
     */
    public boolean checkAndClubConsumeRoom(int count, GameType gameType, long clubID, Club_define.Club_OperationStatus status, long agentsID, int level, int cityId) {
        // 检测充值货币是否足够
        if (this.check(count, cityId)) {
            return this.consumeCityClubRoomCard(count, gameType.getId(), ItemFlow.RoomCardClubRoom, ConstEnum.ResOpType.Lose, clubID, status, agentsID, level, cityId);
        }
        return false;
    }

    /**
     * 回退亲友圈房卡消耗 创建亲友圈房间-房卡
     *
     * @param gameType 游戏类型
     * @param clubID   亲友圈ID
     * @param status   亲友圈状态
     * @param agentsID 代理ID
     * @param level    代理等级
     * @param cityId   城市Id
     * @return
     */
    public void backClubConsumeRoom(int count, GameType gameType, long clubID, Club_define.Club_OperationStatus status, long agentsID, int level, int cityId) {
        // 消耗值 <= 0
        if (count <= 0) {
            return;
        }
        this.gainCityClubRoomCard(count, gameType.getId(), ItemFlow.RoomCardClubRoom, ConstEnum.ResOpType.Fallback, clubID, status, agentsID, level, cityId);
    }


    /**
     * 检查并赛事消耗房卡 创建赛事房间-房卡
     *
     * @param count    消耗值
     * @param gameType 游戏类型
     * @param unionId  赛事ID
     * @param status   赛事状态
     * @param agentsID 代理ID
     * @param level    代理等级
     * @param cityId   城市Id
     * @return
     */
    public boolean checkAndUnionConsumeRoom(int count, GameType gameType, long unionId, UnionDefine.UNION_OPERATION_STATUS status, long agentsID, int level, int cityId) {
        // 检测充值货币是否足够
        if (this.check(count, cityId)) {
            return this.consumeCityUnionRoomCard(count, gameType.getId(), ItemFlow.RoomCardUnionRoom, ConstEnum.ResOpType.Lose, unionId, status, agentsID, level, cityId);
        }
        return false;
    }

    /**
     * 回退赛事房卡消耗 创建赛事房间-房卡
     *
     * @param count    消耗值
     * @param gameType 游戏类型
     * @param unionId  赛事ID
     * @param status   赛事状态
     * @param agentsID 代理ID
     * @param level    代理等级
     * @param cityId   城市Id
     * @return
     */
    public void backUnionConsumeRoom(int count, GameType gameType, long unionId, UnionDefine.UNION_OPERATION_STATUS status, long agentsID, int level, int cityId) {
        // 消耗值 <= 0
        if (count <= 0) {
            return;
        }
        this.gainCityUnionRoomCard(count, gameType.getId(), ItemFlow.RoomCardUnionRoom, ConstEnum.ResOpType.Fallback, unionId, status, agentsID, level, cityId);
    }


    /**
     * 回退房间消耗 创建房间或加入房间
     *
     * @param count    消耗值
     * @param gameType 游戏类型
     * @param cityId   城市Id
     * @return
     */
    public void backConsumeRoom(int count, GameType gameType, int cityId) {
        this.gainCityRoomCard(count, gameType.getId(), ItemFlow.RoomCardRoom, ConstEnum.ResOpType.Fallback, cityId);
    }

    /**
     * 回退房间消耗 创建房间或加入房间
     *
     * @param count    消耗值
     * @param gameType 游戏类型
     * @param several  大赢家
     * @param cityId   城市Id
     * @return
     */
    public void backConsumeRoom(int count, GameType gameType, int several, int cityId) {
        int consumeCard = count - (int) Math.ceil(count * 1.0 / several);
        backConsumeRoom(consumeCard, gameType, cityId);
    }


    /**
     * 推广奖励
     *
     * @param count 消耗值
     */
    public void roomCardRefererReward(int count) {
        this.gainCityRoomCard(count, -1, ItemFlow.RefererReward, ConstEnum.ResOpType.Gain, getPlayer().getCityId());
    }

    /**
     * 获取指定城市的钻石
     *
     * @param cityId 城市Id
     * @return
     */
    public int getPlayerCityCurrencyValue(int cityId) {
        PlayerCityCurrencyBO playerCityCurrencyBO = this.getPlayerCityCurrencyBO(cityId);
        return Objects.isNull(playerCityCurrencyBO) ? 0 : playerCityCurrencyBO.getValue();
    }


    /**
     * 获取指定城市的钻石信息
     *
     * @param cityId 城市Id
     * @return
     */
    public PlayerCityCurrencyBO getPlayerCityCurrencyBO(int cityId) {
        if (cityId <= 0) {
            return null;
        }
        PlayerCityCurrencyBO currencyBO = null;
        if(Config.isShare()){
            currencyBO = SharePlayerCurrencyBOMgr.getInstance().get(getPid(), cityId);
        } else {
            currencyBO = this.getCityCurrencyBOMap().get(cityId);
        }
        if (Objects.isNull(currencyBO)) {
            // 新创建指定城市
            currencyBO = new PlayerCityCurrencyBO(getPid(), cityId);
            currencyBO.getBaseService().saveIgnoreOrUpDate(currencyBO);
            this.getCityCurrencyBOMap().put(currencyBO.getCityId(), currencyBO);
            //添加共享数据
            if(Config.isShare()){
                SharePlayerCurrencyBOMgr.getInstance().add(currencyBO);
            }
        }
        return currencyBO;
    }


    /**
     * 房卡值
     *
     * @param value     值
     * @param gameId    游戏Id
     * @param reason    操作项
     * @param resOpType 消耗类型
     * @param cityId    城市Id
     */
    public boolean consumeCityRoomCard(int value, int gameId, ItemFlow reason, ConstEnum.ResOpType resOpType, int cityId) {
        if (value <= 0) {
            return false;
        }
        String uuid= UUID.randomUUID().toString();
        try {
            //redis分布式锁
            DistributedRedisLock.acquire("gainCityRoomCard" + this.getPlayer().getPid(), uuid);
            PlayerCityCurrencyBO currencyBO = getPlayerCityCurrencyBO(cityId);
            if (Objects.isNull(currencyBO)) {
                // 没有钻石
                CommLog.error("consumeCityRoomCard pid:{},cityId:{}", getPid(), cityId);
                return false;
            }

            this.lock();
            int before = currencyBO.getValue();
            int finalValue = Math.max(0, before - value);
            currencyBO.saveValue(finalValue);
            if(Config.isShare()){
                SharePlayerCurrencyBOMgr.getInstance().add(currencyBO);
            }
            this.unlock();
            CommLogD.info("消耗consumeCityRoomCard Pid:{},Reason:{},Value:{},FinalValue:{},Before:{},Type:{},FamiliID:{},GameId:{},CityId:{}", player.getPid(), reason.value(), -value, finalValue, before, resOpType.ordinal(), player.getFamiliID(), gameId, cityId);
            if (cityId == getPlayer().getCityId()) {
                getPlayer().pushProperties("roomCard", currencyBO.getValue(), "cityId", cityId);
            }
            FlowLogger.roomCardChargeLog(player.getPid(), reason.value(), -value, finalValue, before, resOpType.ordinal(), player.getFamiliID(), gameId, 0, cityId);
            return true;
        } finally {
            DistributedRedisLock.release("gainCityRoomCard" + this.getPlayer().getPid(), uuid);
        }
    }


    /**
     * 减少亲友圈钻石
     *
     * @param value     值
     * @param gameId    游戏Id
     * @param reason    操作项
     * @param resOpType 消耗类型
     * @param clubID    亲友圈Id
     * @param status    亲友圈状态
     * @param agentsID  代理Id
     * @param level     等级
     * @param cityId    城市id
     * @return
     */
    private boolean consumeCityClubRoomCard(int value, int gameId, ItemFlow reason, ConstEnum.ResOpType resOpType, long clubID, Club_define.Club_OperationStatus status, long agentsID, int level, int cityId) {
        if (value <= 0) {
            return false;
        }
        PlayerCityCurrencyBO currencyBO = getPlayerCityCurrencyBO(cityId);
        if (Objects.isNull(currencyBO)) {
            // 没有钻石
            CommLog.error("consumeCityClubRoomCard pid:{},cityId:{}", getPid(), cityId);
            return false;
        }
        this.lock();
        int before = currencyBO.getValue();
        int finalValue = Math.max(0, before - value);
        currencyBO.saveValue(finalValue);
        if(Config.isShare()){
            SharePlayerCurrencyBOMgr.getInstance().add(currencyBO);
        }
        this.unlock();
        if (cityId == getPlayer().getCityId()) {
            getPlayer().pushProperties("roomCard", currencyBO.getValue(), "cityId", cityId);
        }
        CommLogD.info("消耗consumeCityClubRoomCard Pid:{},Reason:{},Value:{},FinalValue:{},Before:{},Type:{},FamiliID:{},GameId:{},CityId:{}", player.getPid(), reason.value(), -value, finalValue, before, resOpType.ordinal(), player.getFamiliID(), gameId, cityId);
        // 日志:消耗房卡
        FlowLogger.roomCardChargeLog(player.getPid(), reason.value(), -value, finalValue, before, resOpType.ordinal(), player.getFamiliID(), gameId, 0, cityId);
        FlowLogger.clubRoomCardChargeLog(player.getPid(), clubID, status.value(), reason.value(), -value, finalValue, before, resOpType.ordinal(), gameId, 0, agentsID, level, cityId);
        return true;
    }


    /**
     * 减少赛事钻石
     *
     * @param value     值
     * @param gameId    游戏Id
     * @param reason    操作项
     * @param resOpType 消耗类型
     * @param unionId   赛事Id
     * @param status    赛事状态
     * @param agentsID  代理Id
     * @param level     等级
     * @param cityId    城市id
     * @return
     */
    private boolean consumeCityUnionRoomCard(int value, int gameId, ItemFlow reason, ConstEnum.ResOpType resOpType, long unionId, UnionDefine.UNION_OPERATION_STATUS status, long agentsID, int level, int cityId) {
        if (value <= 0) {
            return false;
        }
        PlayerCityCurrencyBO currencyBO = getPlayerCityCurrencyBO(cityId);
        if (Objects.isNull(currencyBO)) {
            // 没有钻石
            CommLog.error("consumeCityUnionRoomCard pid:{},cityId:{}", getPid(), cityId);
            return false;
        }

        this.lock();
        int before = currencyBO.getValue();
        int finalValue = Math.max(0, before - value);
        currencyBO.saveValue(finalValue);
        if(Config.isShare()){
            SharePlayerCurrencyBOMgr.getInstance().add(currencyBO);
        }
        this.unlock();
        if (cityId == getPlayer().getCityId()) {
            getPlayer().pushProperties("roomCard", currencyBO.getValue(), "cityId", cityId);
        }
        CommLogD.info("消耗consumeCityUnionRoomCard Pid:{},Reason:{},Value:{},FinalValue:{},Before:{},Type:{},FamiliID:{},GameId:{},CityId:{}", player.getPid(), reason.value(), -value, finalValue, before, resOpType.ordinal(), player.getFamiliID(), gameId, cityId);
        // 日志:消耗房卡
        FlowLogger.roomCardChargeLog(player.getPid(), reason.value(), -value, finalValue, before, resOpType.ordinal(), player.getFamiliID(), gameId, 0, cityId);
        FlowLogger.unionRoomCardChargeLog(player.getPid(), unionId, status.value(), reason.value(), -value, finalValue, before, resOpType.ordinal(), resOpType.ordinal(), 0, agentsID, level, cityId);
        return true;
    }


    /**
     * 增加个人钻石
     *
     * @param value     值
     * @param gameId    游戏Id
     * @param reason    操作项
     * @param resOpType 消耗类型
     * @param cityId    城市Id
     * @return
     */
    public int gainCityRoomCard(int value, int gameId, ItemFlow reason, ConstEnum.ResOpType resOpType, int cityId) {
        String uuid= UUID.randomUUID().toString();
        try {
            //redis分布式锁
            DistributedRedisLock.acquire("gainCityRoomCard" + this.getPlayer().getPid(), uuid);
            PlayerCityCurrencyBO currencyBO = getPlayerCityCurrencyBO(cityId);
            if (Objects.isNull(currencyBO)) {
                // 没有钻石
                CommLog.error("gainCityRoomCard pid:{},cityId:{}", getPid(), cityId);
                return -1;
            }

            this.lock();
            int before = currencyBO.getValue();
            int finalValue = Math.min(1999999999, before + value);
            currencyBO.saveValue(finalValue);
            if(Config.isShare()){
                SharePlayerCurrencyBOMgr.getInstance().add(currencyBO);
            }
            this.unlock();
            if (cityId == getPlayer().getCityId()) {
                getPlayer().pushProperties("roomCard", currencyBO.getValue(), "cityId", cityId);
            }
            CommLogD.info("获取gainCityRoomCard Pid:{},Reason:{},Value:{},FinalValue:{},Before:{},Type:{},FamiliID:{},GameId:{},CityId:{}", player.getPid(), reason.value(), -value, finalValue, before, resOpType.ordinal(), player.getFamiliID(), gameId, cityId);
            FlowLogger.roomCardChargeLog(player.getPid(), reason.value(), value, finalValue, before, resOpType.ordinal(), player.getFamiliID(), gameId, 0, cityId);
            return finalValue - before;
        } finally {
            DistributedRedisLock.release("gainCityRoomCard" + this.getPlayer().getPid(), uuid);
        }
    }


    /**
     * 增加亲友圈钻石
     *
     * @param value     值
     * @param gameId    游戏Id
     * @param reason    操作项
     * @param resOpType 消耗类型
     * @param clubID    亲友圈Id
     * @param status    亲友圈状态
     * @param agentsID  代理Id
     * @param level     等级
     * @param cityId    城市id
     * @return
     */
    private int gainCityClubRoomCard(int value, int gameId, ItemFlow reason, ConstEnum.ResOpType resOpType, long clubID, Club_define.Club_OperationStatus status, long agentsID, int level, int cityId) {
        String uuid= UUID.randomUUID().toString();
        try {
            //redis分布式锁
            DistributedRedisLock.acquire("gainCityRoomCard" + this.getPlayer().getPid(), uuid);
            PlayerCityCurrencyBO currencyBO = getPlayerCityCurrencyBO(cityId);
            if (Objects.isNull(currencyBO)) {
                // 没有钻石
                CommLog.error("gainCityClubRoomCard pid:{},cityId:{}", getPid(), cityId);
                return -1;
            }

            this.lock();
            int before = currencyBO.getValue();
            int finalValue = Math.min(1999999999, before + value);
            currencyBO.saveValue(finalValue);
            if(Config.isShare()){
                SharePlayerCurrencyBOMgr.getInstance().add(currencyBO);
            }
            this.unlock();
            if (cityId == getPlayer().getCityId()) {
                getPlayer().pushProperties("roomCard", currencyBO.getValue(), "cityId", cityId);
            }
            CommLogD.info("获得gainCityClubRoomCard Pid:{},Reason:{},Value:{},FinalValue:{},Before:{},Type:{},FamiliID:{},GameId:{},CityId:{}", player.getPid(), reason.value(), -value, finalValue, before, resOpType.ordinal(), player.getFamiliID(), gameId, cityId);
            // 日志:消耗房卡
            FlowLogger.roomCardChargeLog(player.getPid(), reason.value(), value, finalValue, before, resOpType.ordinal(), player.getFamiliID(), gameId, 0, cityId);
            FlowLogger.clubRoomCardChargeLog(player.getPid(), clubID, status.value(), reason.value(), value, finalValue, before, resOpType.ordinal(), gameId, 0, agentsID, level, cityId);
            return finalValue - before;
        } finally {
            DistributedRedisLock.release("gainCityRoomCard" + this.getPlayer().getPid(), uuid);
        }
    }


    /**
     * 增加赛事钻石
     *
     * @param value     值
     * @param gameId    游戏Id
     * @param reason    操作项
     * @param resOpType 消耗类型
     * @param unionId   赛事Id
     * @param status    赛事状态
     * @param agentsID  代理Id
     * @param level     等级
     * @param cityId    城市id
     * @return
     */
    private int gainCityUnionRoomCard(int value, int gameId, ItemFlow reason, ConstEnum.ResOpType resOpType, long unionId, UnionDefine.UNION_OPERATION_STATUS status, long agentsID, int level, int cityId) {
        String uuid= UUID.randomUUID().toString();
        try {
            //redis分布式锁
            DistributedRedisLock.acquire("gainCityRoomCard" + this.getPlayer().getPid(), uuid);
            PlayerCityCurrencyBO currencyBO = getPlayerCityCurrencyBO(cityId);
            if (Objects.isNull(currencyBO)) {
                // 没有钻石
                CommLog.error("gainCityUnionRoomCard pid:{},cityId:{}", getPid(), cityId);
                return -1;
            }
            this.lock();
            int before = currencyBO.getValue();
            int finalValue = Math.min(1999999999, before + value);
            currencyBO.saveValue(finalValue);
            if(Config.isShare()){
                SharePlayerCurrencyBOMgr.getInstance().add(currencyBO);
            }
            this.unlock();
            if (cityId == getPlayer().getCityId()) {
                getPlayer().pushProperties("roomCard", currencyBO.getValue(), "cityId", cityId);
            }
            CommLogD.info("获得gainCityUnionRoomCard Pid:{},Reason:{},Value:{},FinalValue:{},Before:{},Type:{},FamiliID:{},GameId:{},CityId:{}", player.getPid(), reason.value(), value, finalValue, before, resOpType.ordinal(), player.getFamiliID(), gameId, cityId);
            // 日志:消耗房卡
            FlowLogger.roomCardChargeLog(player.getPid(), reason.value(), value, finalValue, before, resOpType.ordinal(), player.getFamiliID(), gameId, 0, cityId);
            FlowLogger.unionRoomCardChargeLog(player.getPid(), unionId, status.value(), reason.value(), value, finalValue, before, resOpType.ordinal(), resOpType.ordinal(), 0, agentsID, level, cityId);
            return finalValue - before;
        } finally {
            DistributedRedisLock.release("gainCityRoomCard" + this.getPlayer().getPid(), uuid);
        }
    }

    /**
     * 清空指定城市的钻石
     *
     * @param cityId 城市id
     * @return
     */
    private int clearCityIdRoomCard(int cityId) {
        String uuid= UUID.randomUUID().toString();
        try {
            //redis分布式锁
            DistributedRedisLock.acquire("gainCityRoomCard" + this.getPlayer().getPid(), uuid);
            PlayerCityCurrencyBO currencyBO = getPlayerCityCurrencyBO(cityId);
            if (Objects.isNull(currencyBO)) {
                // 没有钻石
                CommLog.error("consumeCityUnionRoomCard pid:{},cityId:{}", getPid(), cityId);
                return -1;
            }
            this.lock();
            int before = currencyBO.getValue();
            int finalValue = Math.max(0, before - before);
            currencyBO.saveValue(finalValue);
            if(Config.isShare()){
                SharePlayerCurrencyBOMgr.getInstance().add(currencyBO);
            }
            this.unlock();
            if (cityId == getPlayer().getCityId()) {
                getPlayer().pushProperties("roomCard", currencyBO.getValue(), "cityId", cityId);
            }
            CommLogD.info("消耗consumeCityUnionRoomCard Pid:{},Reason:{},Value:{},FinalValue:{},Before:{},Type:{},FamiliID:{},GameId:{},CityId:{}", player.getPid(), ItemFlow.CHANGE_PALYER_CITY_ROOM_CARD.value(), -before, finalValue, before, ConstEnum.ResOpType.Lose.ordinal(), player.getFamiliID(), -1, cityId);
            // 日志:消耗房卡
            FlowLogger.roomCardChargeLog(player.getPid(), ItemFlow.CHANGE_PALYER_CITY_ROOM_CARD.value(), -before, finalValue, before, ConstEnum.ResOpType.Lose.ordinal(), player.getFamiliID(), -1, 0, cityId);
            return before;
        } finally {
            DistributedRedisLock.release("gainCityRoomCard" + this.getPlayer().getPid(), uuid);
        }
    }

    /**
     * 切换指定城市
     * @param doCityId 从：城市id
     * @param toCityId 到：城市id
     */
    public SData_Result changePlayerCityRoomCard(int doCityId ,int toCityId) {
        if (RefSelectCity.checkCityId(doCityId) && RefSelectCity.checkCityId(toCityId)) {
            getPlayer().saveCityId(toCityId);
            // 清空指定城市的钻石
            int value = this.clearCityIdRoomCard(doCityId);
            if (value > 0) {
                // 转移到指定城市
                gainItemFlow(value, ItemFlow.CHANGE_PALYER_CITY_ROOM_CARD, toCityId);
                return SData_Result.make(ErrorCode.Success);
            }
        }
        return SData_Result.make(ErrorCode.CITY_ID_ERROR,"CITY_ID_ERROR");
    }
}
