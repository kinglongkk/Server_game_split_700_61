package business.global.shareroom;

import BaseCommon.CommLog;
import business.global.club.Club;
import business.global.config.GameListConfigMgr;
import business.global.room.base.AbsBaseRoom;
import business.global.room.base.RoomImpl;
import business.global.shareclub.ShareClubListMgr;
import business.global.shareunion.ShareUnionListMgr;
import business.global.union.Union;
import business.shareplayer.ShareNode;
import business.shareplayer.SharePlayerMgr;
import cenum.ClassType;
import cenum.RoomTypeEnum;
import cenum.room.RoomState;
import com.ddm.server.common.Config;
import com.ddm.server.common.redis.RedisMap;
import com.ddm.server.common.utils.BeanUtils;
import com.ddm.server.common.utils.CommTime;
import com.ddm.server.common.utils.JsonUtil;
import com.ddm.server.common.utils.PropertiesUtil;
import com.google.gson.Gson;
import core.db.entity.clarkGame.GameTypeBO;
import core.ioc.ContainerMgr;
import jsproto.c2s.cclass.GameType;
import jsproto.c2s.cclass.RoomCfgCount;
import jsproto.c2s.cclass.club.ClubCreateGameSet;
import jsproto.c2s.cclass.room.*;
import jsproto.c2s.cclass.union.UnionCreateGameSet;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author xsj
 * @date 2020/11/5 16:59
 * @description 共享继续房间管理类
 */
public class ShareContinueRoomInfoMgr {
    //房间存储KEy
    private static final String SHARE_CONTINUE_ROOM_INFO_KEY = "shareContinueRoomInfoKey";

    private static ShareContinueRoomInfoMgr instance = new ShareContinueRoomInfoMgr();

    // 获取单例
    public static ShareContinueRoomInfoMgr getInstance() {
        return instance;
    }


    /**
     * 增加共享继续房间
     *
     * @param continueRoomInfo
     */
    public void addShareContinueRoom(ContinueRoomInfo continueRoomInfo) {
        if(continueRoomInfo!=null) {
            RedisMap redisMap = ContainerMgr.get().getRedis().getMap(SHARE_CONTINUE_ROOM_INFO_KEY);
            redisMap.putJson(String.valueOf(continueRoomInfo.getRoomID()), continueRoomInfo);
        }
    }



    /**
     * 获取共享房间信息
     *
     * @param roomId
     * @return
     */
    public ContinueRoomInfo getShareContinueRoom(Long roomId) {
        RedisMap redisMap = ContainerMgr.get().getRedis().getMap(SHARE_CONTINUE_ROOM_INFO_KEY);
        ContinueRoomInfo continueRoomInfo = redisMap.getObject(String.valueOf(roomId), ContinueRoomInfo.class);
        return continueRoomInfo;
    }

    /**
     * 删除共享房间
     *
     * @param roomId
     */
    public void removeShareContinueRoom(Long roomId) {
        RedisMap redisMap = ContainerMgr.get().getRedis().getMap(SHARE_CONTINUE_ROOM_INFO_KEY);
        redisMap.remove(String.valueOf(roomId));
    }

}
