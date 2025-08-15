package business.dzpk.c2s.cclass;

import cenum.PKOpType;
import jsproto.c2s.cclass.pk.base.BasePKSet_Pos;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * 位置信息
 */
@Data
public class DZPKSet_Pos extends BasePKSet_Pos {
    /**
     * 本剧剩余积分
     */
    public int point;
    /**
     * 总下注
     */
    public int betPoint;//总下注
    /**
     * 最近一次下注
     */
    public int curBetPoint;//最近一次下注
    /**
     * 下注列表，每次下了多少
     */
    private List<Integer> betList = new ArrayList<>();//下注列表，每次下了多少
    /**
     * 最近一次操作类型
     */
    private PKOpType opType = PKOpType.Not;
    /**
     * 是否需要带分
     */
    private DZPKStartDaiFen daifen;
    /**
     * 是否第一名
     */
    private boolean firstWinner;
    /**
     * 当前第几名
     */
    private int mingci;
}
