package business.dzpk.c2s.cclass;

import jsproto.c2s.cclass.BaseSendMsg;
import lombok.Data;

/**
 * 升盲
 */
@Data
public class DZPKShengMang extends BaseSendMsg {
    private int daMang;
    private int xiaoMang;
    private int nextDaMang;//下轮大盲
    private int nextXiaoMang;//下轮小盲

}
