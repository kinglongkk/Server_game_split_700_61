package business.nn.c2s.cclass;

import java.util.ArrayList;
import java.util.List;

/**
 * 一局中每个位置信息
 *
 * @author zaf
 */
public class NNRoomSet_Pos {

    public int posID = 0;                    // 座号ID
    public long pid = 0;                    // 账号
    public List<Integer> cards = new ArrayList<>();    //牌
    public int addBet = 0;                    //下注分数
    public boolean checkCard = false;            //亮牌玩家
    public boolean openCard = false;            //开牌玩家
    public boolean isPlaying = false;            //当前局是否在玩
    public int crawType;                    //牛牛类型
    public int point;                        //积分
    /**
     * 竞技点
     */
    public Double sportsPoint;

    public NNRoomSet_Pos(int posID, long pid, List<Integer> cards, int addBet, boolean checkCard, boolean openCard, boolean isPlaying, int crawType, int point) {
        super();
        this.posID = posID;
        this.pid = pid;
        this.cards = cards;
        this.addBet = addBet;
        this.checkCard = checkCard;
        this.openCard = openCard;
        this.isPlaying = isPlaying;
        this.crawType = crawType;
        this.point = point;
    }

    public NNRoomSet_Pos() {
    }

}
