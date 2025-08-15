package jsproto.c2s.iclass.union;

import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.room.RoomPosInfo;
import lombok.Data;

/**
 *
 * @author zaf
 *
 */
@Data
public class CUnion_Base extends BaseSendMsg {
    /**
     * 赛事Id
     */
    private long unionId;
    /**
     * 亲友圈Id
     */
    private long clubId;

    public CUnion_Base(long unionId, long clubId) {
        this.unionId = unionId;
        this.clubId = clubId;
    }

    public CUnion_Base() {
    }
}