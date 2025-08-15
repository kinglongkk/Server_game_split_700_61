package business.global.config;

import BaseCommon.CommLog;
import business.global.sharegm.ShareNodeServerMgr;
import business.global.shareroom.ShareRoom;
import business.rocketmq.constant.MqTopic;
import business.shareplayer.ShareNode;
import cenum.GameOpenTypeEnum;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.Config;
import com.ddm.server.common.rocketmq.MqAbsBo;
import com.ddm.server.common.rocketmq.MqProducerMgr;
import com.ddm.server.websocket.def.ErrorCode;
import com.google.gson.Gson;
import core.config.refdata.RefDataMgr;
import core.config.refdata.ref.RefSelectCity;
import core.db.entity.clarkGame.GameTypeBO;
import core.db.service.clarkGame.GameTypeBOService;
import core.ioc.ContainerMgr;
import core.network.http.proto.ZleData_Result;
import jsproto.c2s.cclass.GameTypeUrl;
import jsproto.c2s.cclass.config.CityInfo;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class GameListConfigMgr {


    private static class SingletonHolder {
        public static GameListConfigMgr instance = new GameListConfigMgr();
    }

    public static GameListConfigMgr getInstance() {
        return SingletonHolder.instance;
    }

    private Map<Integer, GameTypeBO> confs = new HashMap<>();

    /**
     * 初始化配置
     */
    public void init() {
        List<GameTypeBO> gameTypeBOs = ContainerMgr.get().getComponent(GameTypeBOService.class).findAll(null);
        if (CollectionUtils.isEmpty(gameTypeBOs)) {
            return;
        }
        for (GameTypeBO gBo : gameTypeBOs) {
            this.confs.put(gBo.getGametype(), gBo);
        }
    }

    /**
     * 获取指定城市信息
     *
     * @param selectCityId
     * @return
     */
    public CityInfo getCity(long selectCityId) {
        if (banCity(selectCityId)) {
            return null;
        }
        RefSelectCity refSelectCity = RefDataMgr.get(RefSelectCity.class, selectCityId);
        if (Objects.nonNull(refSelectCity)) {
            if (refSelectCity.getType() != 3) {
                return null;
            }
        }
        return refSelectCity == null ? null : new CityInfo(refSelectCity.getId(), refSelectCity.getType(), refSelectCity.getAscription(), refSelectCity.getName(), refSelectCity.getPopular(), refSelectCity.getDefaultCity(), refSelectCity.getGame());
    }


    /**
     * 获取指定城市的游戏列表
     *
     * @param selectCityId 选择城市ID
     * @return
     */
    public String findGameIdList(long selectCityId, long unionSign) {
        // 不存在游戏列表配置数据
        CityInfo cityInfo = this.getCity(selectCityId);
        if (Objects.isNull(cityInfo)) {
            return "";
        } else {
            return gameTypeList(cityInfo.getGame(), selectCityId, unionSign);
        }
    }

    /**
     * 解析字符串游戏列表
     *
     * @param haveYouXi
     * @return
     */
    public String gameTypeList(String haveYouXi, long cityId, long unionSign) {
        // 检查工会是否指定游戏
        if (StringUtils.isNotEmpty(haveYouXi)) {
            StringBuilder strBuf = new StringBuilder();
            // 解析游戏列表
            String[] gameTypes = haveYouXi.split(",");
            for (String str : gameTypes) {
                if (StringUtils.isNumeric(str.trim())) {
                    int gameType = Integer.parseInt(str.trim());
                    // 检查游戏类型是否正确
                    if (gameType >= 0 && isOpen(gameType, cityId, unionSign)) {
                        strBuf.append(gameType).append(",");
                    }
                }
            }
            int length = strBuf.length();
            if (length <= 0) {
                return "";
            }
            strBuf.deleteCharAt(length - 1);
            return strBuf.toString();
        }
        return "";
    }

    private boolean isOpen(int gameType, long cityId, long unionSign) {
        GameTypeBO gameTypeBO = confs.get(gameType);
        boolean isOpen = Objects.isNull(gameTypeBO) ? false : gameTypeBO.getTab() == 1;
        if (!isOpen) {
            // 没打开
            return false;
        }
        GameOpenTypeEnum openTypeEnum = GameOpenTypeEnum.valueOf(gameTypeBO.getOpenType());
        if (Objects.isNull(openTypeEnum)) {
            // 开放类型不对
            return false;
        }
        if (GameOpenTypeEnum.GAME_ALL.equals(openTypeEnum)) {
            // 所有游戏
            return true;
        } else if (GameOpenTypeEnum.GAME_CITY.equals(openTypeEnum) && cityId > 0L) {
            // 指定城市
            return gameTypeBO.getOpenContentToList().contains(cityId);
        } else if (GameOpenTypeEnum.GAME_UNION.equals(openTypeEnum) && unionSign > 0L) {
            // 指定赛事
            return gameTypeBO.getOpenContentToList().contains(unionSign);
        }
        return false;
    }

    /**
     * 获取程序数据
     *
     * @param selectCityId 城市id
     * @return Optional<CityInfo>
     */
    public Optional<CityInfo> findCityInfo(long selectCityId) {
        return Optional.ofNullable(this.getCity(selectCityId));
    }

    /**
     * 获取所有列表
     *
     * @return Optional<List < CityInfo>>
     */
    public Optional<List<CityInfo>> getCityInfoList() {
        List<CityInfo> cityInfoList = RefDataMgr.getAll(RefSelectCity.class).values().stream().map(refSelectCity -> refSelectCity == null ? null : new CityInfo((int) refSelectCity.getId(), refSelectCity.getType(), refSelectCity.getAscription(), refSelectCity.getName(), refSelectCity.getPopular(), refSelectCity.getDefaultCity(), refSelectCity.getGame())).filter(k -> null != k).collect(Collectors.toList());
        return cityInfoList == null || cityInfoList.size() <= 0 ? Optional.empty() : Optional.ofNullable(cityInfoList);
    }

    /**
     * 服务端重新更新读取selectCity.json配置表
     */
    public String updateGameListConfig() {
//        // 解析游戏类型配置
//        this.setCityInfoMap(CommFile.GetJsonData("../bin/conf/jsonData/selectCity.json",
//                new TypeToken<Map<Integer, CityInfo>>() {
//                }.getType()));
//        if (null == this.getCityInfoMap() || this.getCityInfoMap().size() <= 0) {
//            CommLog.error("更新城市游戏管理配置,更新结果:{无数据}");
//            return "更新城市游戏管理配置,更新结果:{无数据}！";
//        }
//        CommLog.info("更新城市游戏管理配置,更新结果:{}", this.getCityInfoMap().toString());
        return "更新城市游戏管理配置成功！";
    }


    /**
     * 添加或设置指定的游戏类型
     *
     * @param gameType   游戏类型
     * @param name       名称
     * @param logoico    图标
     * @param barColors  颜色
     * @param gameName   简称:(LYMJ)
     * @param have_xifen 1:有细分游戏,0没有
     * @param tab        0:不显示,1:默认显示,2:禁用
     */
    public String set(Integer gameType, String name, String logoico, String barColors, String gameName, int have_xifen,
                      int tab, String hutypelist, int sort, int classType, String gameServerIP, int gameServerPort, String webSocketUrl, String httpUrl, int openType, List<Long> openContent) {
        GameTypeBO bo = this.confs.get(gameType);
        if (null == bo) {
            bo = new GameTypeBO();
        }
        bo.setGametype(gameType);
        bo.setName(name);
        bo.setLogoico(logoico);
        bo.setBarColors(barColors);
        bo.setGameName(gameName);
        bo.setHave_xifen(have_xifen);
        bo.setTab(tab);
        bo.setHutypelist(hutypelist);
        bo.setSort(sort);
        bo.setClassType(classType);
        bo.setGameServerIP(gameServerIP);
        bo.setGameServerPort(gameServerPort);
        bo.setWebSocketUrl(webSocketUrl);
        bo.setHttpUrl(httpUrl);
        bo.setOpenType(openType);
        bo.setOpenContent(new Gson().toJson(CollectionUtils.isEmpty(openContent) ? Collections.emptyList() : openContent));
        bo.setOpenContentList(CollectionUtils.isEmpty(openContent) ? Collections.emptyList() : openContent);
        bo.getBaseService().saveOrUpDate(bo);
        this.confs.put(gameType, bo);
        return ZleData_Result.make(ErrorCode.Success, "success");
    }


    /**
     * 添加或设置指定的游戏类型
     *
     * @param gameType 游戏类型
     * @param tab      0:不显示,1:默认显示,2:禁用
     */
    public String setTab(int gameType, int tab) {
        GameTypeBO bo = this.confs.get(gameType);
        if (null == bo) {
            return ZleData_Result.make(ErrorCode.NotAllow, "error gameType");
        }
        bo.saveTab(tab);
        return ZleData_Result.make(ErrorCode.Success, "success");
    }

    public boolean setGameHuTypeList(Integer gameType, String hutypelist) {
        GameTypeBO bo = this.confs.get(gameType);
        if (null != bo) {
            bo.saveHutypelist(hutypelist);
            return true;
        }
        return false;
    }

    public boolean setGameHuTypeSort(Integer gameType, int sort) {
        GameTypeBO bo = this.confs.get(gameType);
        if (null != bo) {
            bo.saveSort(sort);
            return true;
        }
        return false;
    }


    public String openTypeAndContent(int gameType, int tab, int openType, List<Long> openContent) {
        GameTypeBO bo = this.confs.get(gameType);
        if (null != bo) {
            bo.openTypeAndContent(tab, openType, openContent);
            return ZleData_Result.make(ErrorCode.Success, "success");
        }
        return ZleData_Result.make(ErrorCode.NotAllow, "error openTypeAndContent");
    }

    /**
     * 更新全部备用节点端口
     */
    public void updateAllPort() {
        if (Config.isStartChangePort()) {
            CommLogD.info("切换端口");
            this.confs.forEach((k, v) -> {
                if (v.getGameServerPort() == Config.backUpNodePort()) {
                    v.saveGameServerPort(Config.nodePort());
                }
            });
            MqProducerMgr.get().send(MqTopic.HTTP_RELOAD_GAME_LIST_CONFIG, new MqAbsBo());
        }

    }


    /**
     * 删除指定游戏类型
     *
     * @param gameType
     */
    public String delete(Integer gameType) {
        GameTypeBO bo = this.confs.get(gameType);
        if (null != bo) {
            this.confs.remove(gameType);
            bo.getBaseService().delete(bo.getId());
            return ZleData_Result.make(ErrorCode.Success, "success");
        }
        return ZleData_Result.make(ErrorCode.NotAllow, "error gameType");
    }

    /**
     * 获取所有配置
     *
     * @return
     */
    public Map<Integer, GameTypeBO> getAllConfig() {
        return new HashMap<>(this.confs);
    }

    /**
     * 获取所有配置列表
     *
     * @return
     */
    public List<GameTypeUrl> getAllList() {
        List<GameTypeUrl> gameTypeBOList = this.confs
                .values()
                .stream()
                .map(k -> getGameTypeUrl(k))
                .collect(Collectors.toList());
        return gameTypeBOList;
    }

    /**
     * 获取一个游戏配置
     *
     * @return
     */
    public GameTypeUrl getByGameType(Integer gameType) {
        GameTypeBO gameTypeBO = this.confs.get(gameType);
        return getGameTypeUrl(gameTypeBO);
    }

    /**
     * 根据房间获取服务节点
     *
     * @return
     */
    public GameTypeUrl getByRoom(ShareRoom shareRoom) {
        ShareNode shareNode = shareRoom.getCurShareNode();
        if (shareRoom.isNoneRoom()) {
            return getByGameType(shareRoom.getGameId());
        } else {
            GameTypeUrl gameTypeUrl = new GameTypeUrl();
            gameTypeUrl.setStart(true);
            gameTypeUrl.setGametype(shareRoom.getGameId());
            gameTypeUrl.setWebSocketUrl(shareNode.getVipAddress());
            gameTypeUrl.setGameServerIP(shareNode.getIp());
            gameTypeUrl.setGameServerPort(shareNode.getPort());
            return gameTypeUrl;
        }
    }

    /**
     * 获取游戏节点
     *
     * @param shareRoom
     * @return
     */
    public ShareNode getShareNodeByRoom(ShareRoom shareRoom) {
        GameTypeUrl gameTypeUrl = getByRoom(shareRoom);
        ShareNode shareNode = new ShareNode("", gameTypeUrl.getWebSocketUrl(), gameTypeUrl.getGameServerIP(), gameTypeUrl.getGameServerPort());
        return shareNode;
    }

    /**
     * 检查游戏所属节点是否存活
     *
     * @param gameType
     * @return
     */
    public boolean checkIsLiveByGameType(Integer gameType) {
        GameTypeBO gameTypeBO = this.confs.get(gameType);
        return ShareNodeServerMgr.getInstance().checkIsLiveByIpPort(gameTypeBO.getGameServerIP(), gameTypeBO.getGameServerPort());
    }

    /**
     * 检查游戏所属节点是否存活
     *
     * @param shareRoom
     * @return
     */
    public boolean checkIsLiveByRoom(ShareRoom shareRoom) {
        ShareNode shareNode = shareRoom.getCurShareNode();
        if (shareRoom.isNoneRoom()) {
            return checkIsLiveByGameType(shareRoom.getGameId());
        } else {
            return ShareNodeServerMgr.getInstance().checkIsLiveByIpPort(shareNode.getIp(), shareNode.getPort());
        }

    }

    private GameTypeUrl getGameTypeUrl(GameTypeBO bo) {
        if (bo == null) {
            return null;
        }
        GameTypeUrl gameTypeUrl = new GameTypeUrl();
        gameTypeUrl.setGametype(bo.getGametype());
        gameTypeUrl.setHttpUrl(bo.getHttpUrl());
        gameTypeUrl.setWebSocketUrl(bo.getWebSocketUrl());
        gameTypeUrl.setGameServerIP(bo.getGameServerIP());
        gameTypeUrl.setGameServerPort(bo.getGameServerPort());
        gameTypeUrl.setStart(ShareNodeServerMgr.getInstance().checkIsLiveByIpPort(bo.getGameServerIP(), bo.getGameServerPort()));
        return gameTypeUrl;
    }

    /**
     * 禁止城市
     *
     * @param cityId
     * @return
     */
    public boolean banCity(long cityId) {
//        return !(1140200L == cityId || 1140201L == cityId || 1101301L == cityId);
        return false;
    }

}
