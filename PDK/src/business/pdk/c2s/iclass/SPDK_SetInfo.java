package business.pdk.c2s.iclass;
import java.util.HashMap;
import java.util.Map;

public class SPDK_SetInfo {
    private long setID;
    // 每个人的分数
    private Map<Integer,Integer> point = new HashMap<>();
    public SPDK_SetInfo(long setID) {
        this.setID = setID;
    }

    public Map<Integer, Integer> getPoint() {
        return point;
    }

    public long getSetID() {
        return setID;
    }
}

