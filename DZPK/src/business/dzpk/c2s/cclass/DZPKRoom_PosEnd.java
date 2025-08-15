package business.dzpk.c2s.cclass;

import jsproto.c2s.cclass.pk.base.BasePKRoom_PosEnd;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DZPKRoom_PosEnd extends BasePKRoom_PosEnd {
    /**
     * 重组后的牌 5张,如果半途 棋牌 则为0
     */
    protected List<Integer> newCardList = new ArrayList<>();
    /**
     * 赢得底池分
     */
    protected int diChiPoint;
    /**
     * 赢得底池分
     */
    protected int cardType;

    /**
     * 本局输赢
     */
    protected int winPoint;
    /**
     * 每轮详情
     */
    DZPKRoom_PosRoundInfo roundInfo;

    @Data
    public static class DZPKRoom_PosRoundInfo {
        /**
         * 每轮下注了多少
         */
        protected List<Integer> betList = new ArrayList<>();
        /**
         * 每轮操作
         */
        protected List<Integer> opList = new ArrayList<>();
        /**
         * 牌
         */
        protected List<List<Integer>> cardList = new ArrayList<>();

    }

}
