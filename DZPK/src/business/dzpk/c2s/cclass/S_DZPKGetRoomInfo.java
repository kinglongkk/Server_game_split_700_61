package business.dzpk.c2s.cclass;

import jsproto.c2s.iclass.S_GetRoomInfo;
import lombok.Data;

@Data
public class S_DZPKGetRoomInfo extends S_GetRoomInfo {
    /**
     * 升盲时间
     */
    private long shengMangtime;
    /**
     * 升盲次数
     */
    private int mangCunt;
    /**
     *
     */
    private long localTime;

}