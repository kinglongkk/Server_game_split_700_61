package business.dzpk.c2s.cclass;

import cenum.room.SetState;
import jsproto.c2s.cclass.pk.base.BasePKRoom_SetEnd;
import jsproto.c2s.cclass.pk.base.BasePKRoom_SetRound;
import jsproto.c2s.cclass.pk.base.BasePKSet_Pos;
import jsproto.c2s.cclass.room.RoomSetInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 当局信息
 */
@Data
public class DZPKRoomSetInfo extends RoomSetInfo {
    private int dPos;
    /**
     * 小盲位置
     */
    private int daMangPos = 0;
    /**
     * 大盲位置
     */
    private int xiaoMangPos = 0;
    /**
     * 小盲注
     */
    private int xiaoManggPoint = 0;
    /**
     * 大盲注
     */
    private int daManggPoint = 0;
    /**
     * 总下注（底池）
     */
    private int totalBetPoint;
    /**
     * 每轮总下注（底池）
     */
    private int roundBetPoint;
    /**
     * 总下注列表
     */
    private List<Integer> totalBetList;
    /***
     * 公共牌
     */
    private List<Integer> publicCardList;

    /**
     * 最后操作时间
     */
    private int lastShotTime = 0;
    /**
     * 当前时间
     */
    private int setCurrentTime;
    /**
     * 当前局状态 Init；Wait；End； Playing不需要信息
     */
    private SetState state = SetState.Init;
    /**
     * 当前等待信息 Wait
     */
    private BasePKRoom_SetRound setRound = new BasePKRoom_SetRound();
    /**
     * 结束状态
     */
    private BasePKRoom_SetEnd setEnd = new BasePKRoom_SetEnd();
    /**
     * 每个玩家的牌面
     */
    private List<BasePKSet_Pos> setPosList = new ArrayList<>();

}		
