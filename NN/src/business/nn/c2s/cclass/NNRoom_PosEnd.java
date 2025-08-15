package business.nn.c2s.cclass;

import java.util.ArrayList;
import java.util.List;

/**
 * 牛牛 配置
 *
 * @author zaf
 */

// 位置结束的信息
public class NNRoom_PosEnd {
    public int pos = 0; //位置
    public long pid = 0;//玩家pid
    public int point = 0; // 本局积分变更
    public int addBet = 0;//押注
    public boolean isCallBacker = false;//是否是庄家
    public int callBackerNum = 0;
    public int crawType = 0;//牛牛类型
    public boolean isPlaying = false; //是否参加当前游戏
    public List<Integer> cardList = new ArrayList<Integer>();
    /**
     * 竞技点
     */
    public Double sportsPoint;
}


