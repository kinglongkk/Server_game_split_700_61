package business.nn.c2s.iclass;

import java.util.ArrayList;
import java.util.List;

import jsproto.c2s.cclass.BaseSendMsg;


public class SNN_SetEnd extends BaseSendMsg {

    public long roomID;
    public int setStatus;
    public long startTime;
    public List<Integer> crawTypeList;//牛牛类型
    public List<Integer> pointList; //得分
    public ArrayList<ArrayList<Integer>> cards = new ArrayList<ArrayList<Integer>>();    //牌
    public List<Double> sportsPointList;

    public static SNN_SetEnd make(long roomID, int setStatus, long startTime, List<Integer> crawTypeList, List<Integer> pointList, ArrayList<ArrayList<Integer>> cards,List<Double> sportsPointList) {
        SNN_SetEnd ret = new SNN_SetEnd();
        ret.roomID = roomID;
        ret.setStatus = setStatus;
        ret.startTime = startTime;
        ret.crawTypeList = crawTypeList;
        ret.pointList = pointList;
        ret.cards = cards;
        ret.sportsPointList = sportsPointList;
        return ret;
    }
}
