package business.global.pk.dzpk;

import business.global.room.base.AbsBaseRoom;
import business.global.room.base.RoomPlayBackImplAbstract;
import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 回放的房间管理
 *
 * @author Huaxing
 */
public class DZPKRoomPlayBackImpl extends RoomPlayBackImplAbstract {

    private final static String PosOpCard = "PosOpCard";
    private final static String SetStart = "SetStart";
    private final static String StartVoteDissolve = "StartVoteDissolve";



    public DZPKRoomPlayBackImpl(AbsBaseRoom room) {
        super(room);
    }

    @Override
    public boolean isOpCard(BaseSendMsg msg) {
        return msg.getOpName().indexOf(PosOpCard) > 0 || msg.getOpName().indexOf(SetStart) > 0 || msg.getOpName().indexOf(StartVoteDissolve) > 0;
    }


}		
