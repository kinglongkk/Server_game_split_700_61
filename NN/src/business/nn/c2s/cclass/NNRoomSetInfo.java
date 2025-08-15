package business.nn.c2s.cclass;

import business.nn.c2s.iclass.SNN_SetEnd;
import jsproto.c2s.cclass.pk.Victory;
import jsproto.c2s.cclass.room.RoomSetInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 跑得快 当前局游戏信息
 *
 * @author zaf
 */
public class NNRoomSetInfo extends RoomSetInfo {
    public long roomID = 0; // 房间ID
    public int state = NN_define.NN_GameStatus.NN_GAME_STATUS_HOG.value(); // 游戏状态
    public long startTime = 0;
    public int backerPos;// 当前庄家
    public boolean isRandBackerPos;//是否随机庄家
    public int sendCardNumber;
    public int maxBet;//可以推注时最大分数
    public List<NNRoomSet_Pos> posInfo = new ArrayList<>(); // 一局玩家列表
    public List<Victory> callbackerList = new ArrayList<Victory>();        //是否抢过庄
    public SNN_SetEnd setEnd;

}
