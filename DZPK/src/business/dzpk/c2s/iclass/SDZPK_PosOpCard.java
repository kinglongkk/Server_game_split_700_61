package business.dzpk.c2s.iclass;

import cenum.PKOpType;
import jsproto.c2s.cclass.BaseSendMsg;
import lombok.Data;

import java.util.List;


@SuppressWarnings("serial")
@Data
public class SDZPK_PosOpCard<T> extends BaseSendMsg {

    public long roomID;
    public int pos;
    public T set_Pos;
    public PKOpType opType;
    public int cardType;
    public boolean firstFlag;
    public List<Integer> opCards;
    public boolean isFlash = false;
    /**
     * 总下注（底池）
     */
    private int totalBetPoint;
    /**
     * 每轮总下注（底池）
     */
    private int roundBetPoint;
}
