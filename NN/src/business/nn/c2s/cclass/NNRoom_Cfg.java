package business.nn.c2s.cclass;

import java.util.ArrayList;
import java.util.List;

import jsproto.c2s.cclass.room.BaseCreateRoom;

/*
 * 创建房间配置
 * */
public class NNRoom_Cfg extends BaseCreateRoom {
    public int endPoints = 0;                //底分
    public int fanbeiguize = 0;        //翻倍规则
    public List<Integer> teshupaixing = new ArrayList<Integer>();    //特殊牌型
    public int isXianJiaTuiZhu = 0;        //闲家推注
    public boolean isKeptOutAfterStartGame = false;//游戏开始后禁止进入
    public boolean isProhibitToRubCard = false;    //禁止搓牌
    public int score = 0;                    //上庄分数
    public int maxNum = 3;                    //最大倍数
    public int sign = NN_define.NN_GameType.NN_MPQZ.value();            //牛牛类型
}
