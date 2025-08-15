package business.nn.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

import java.util.ArrayList;

/**
 * 接收客户端数据
 * 操作牌
 *
 * @author zaf
 */

public class SNN_OpCard extends BaseSendMsg {

    public long roomID;
    public int pos;  //位置
    public int opCardType;  //PDK_CARD_TYPE 操作类型及牌的类型
    public ArrayList<Integer> cardList;
    public int nextPos;//下一个操作位
    public boolean turnEnd;//是否一轮结束
    public int daiNum;//带几张牌
    public boolean isSetEnd = false;
    public ArrayList<Integer> privateList;
    public boolean isFlash = false; //是否自动打牌
    public boolean isFirstOp = false;//是否是首出

    public static SNN_OpCard make(long roomID, int pos, int opCardType, int nextPos, ArrayList<Integer> cardList, boolean turnEnd, int daiNum, boolean isSetEnd, ArrayList<Integer> privateList, boolean isFlash) {
        SNN_OpCard ret = new SNN_OpCard();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.opCardType = opCardType;
        ret.cardList = cardList;
        ret.nextPos = nextPos;
        ret.turnEnd = turnEnd;
        ret.daiNum = daiNum;
        ret.isSetEnd = isSetEnd;
        ret.privateList = privateList;
        ret.isFlash = isFlash;
        ret.isFirstOp = false;//是否是首出
        return ret;
    }
}
