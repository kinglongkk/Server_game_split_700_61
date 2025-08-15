package business.dzpk.c2s.cclass;

import jsproto.c2s.cclass.room.AbsBaseResults;
import lombok.Data;

/**
 * 总结算信息
 *
 * @author Huaxing
 */
@Data
public class DZPKResults extends AbsBaseResults {

    /**
     * 胜局数
     */
    private int winCountPoint;
    /**
     * 大赢家
     */
    private boolean winner;
    /**
     * 总手数
     */
    private int roundCunt;
}

