package business.nn.c2s.iclass;

import java.util.ArrayList;
import java.util.List;

import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.pk.Victory;

/*
 * 通知客户端开始抢庄
 * @author zaf
 * */

public class SNN_StatusChange extends BaseSendMsg {

    public long roomID;
    public int state;
    public long startTime;

    public int backerPos; //庄家位置
    public boolean isRandBackerPos;//是否随机庄家
    public int maxBet;//可以推注时最大分数

    public int sendCardNumber;  //发牌数目

    public List<Victory> callbackerList = new ArrayList<Victory>();        //是否抢过庄

    public static SNN_StatusChange make(long roomID, int state, long startTime, int sendCardNumber, int backerPos,
                                        boolean isRandBackerPos, int maxBet, List<Victory> callbackerList) {
        SNN_StatusChange ret = new SNN_StatusChange();
        ret.roomID = roomID;
        ret.state = state;
        ret.startTime = startTime;
        ret.backerPos = backerPos;
        ret.maxBet = maxBet;
        ret.sendCardNumber = sendCardNumber;
        ret.isRandBackerPos = isRandBackerPos;
        ret.callbackerList = callbackerList;
        return ret;
    }
}
