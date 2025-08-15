package jsproto.c2s.cclass.mj.template;

import java.util.HashMap;
import java.util.Map;

public class MJTemplateTingInfo {
    private int cardType;
    private Map<Integer,Integer> pointMap=new HashMap<>();

    public MJTemplateTingInfo(int cardType, Map<Integer, Integer> pointMap) {
        this.cardType = cardType;
        this.pointMap = pointMap;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public Map<Integer, Integer> getPointMap() {
        return pointMap;
    }

    public void setPointMap(Map<Integer, Integer> pointMap) {
        this.pointMap = pointMap;
    }
}
