package business.dzpk.c2s.cclass;

import jsproto.c2s.cclass.BaseSendMsg;
import lombok.Data;

/**
 *
 */
@Data
public class DZPKStartDaiFen extends BaseSendMsg {
    /**
     * 下限
     */
    private int min;//下限
    /**
     * 上限
     */
    private int max;//上限
    /**
     * 需要带分玩家位置
     */
    private int pos;//需要带分玩家位置
    /**
     * 最多可以带入的积分（玩家身上的）
     */
    private int point;//需要带分玩家现有的  为带入的积分

    public DZPKStartDaiFen(int min, int max, int pos, int point) {
        this.min = min;
        this.max = max;
        this.pos = pos;
        this.point = point;
    }
}
