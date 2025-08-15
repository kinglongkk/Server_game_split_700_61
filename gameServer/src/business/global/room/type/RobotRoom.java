package business.global.room.type;

import business.global.room.base.AbsBaseRoom;
import business.global.room.base.AbsRoomPos;
import business.global.room.base.RoomTyepImpl;
import business.player.Player;
import cenum.RoomTypeEnum;
import cenum.room.PaymentRoomCardType;
import jsproto.c2s.cclass.room.BaseRoomConfigure;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 练习场房间
 *
 * @author
 */
@Data
public class RobotRoom extends RoomTyepImpl {

    public RobotRoom(AbsBaseRoom room) {
        super(room,room.getBaseRoomConfigure().getBaseCreateRoom());
    }

    /**
     * 房间类型
     */
    @Override
    public RoomTypeEnum getRoomTypeEnum() {
        return RoomTypeEnum.ROBOT;
    }

    @Override
    public void clear() {
        super.clear();
    }


    /**
     * 系统维护时强制解散
     */
    @Override
    public void doForceDissolve() {
        this.getRoom().doDissolveRoom();
    }

    /**
     * 获取ID
     *
     * @return
     */
    @Override
    public long getSpecialRoomId() {
        return getRoom().getBaseRoomConfigure().getRobotRoomCfg().getPracticeId();
    }

    @Override
    public void execLuckDrawCondition() {
        // 练习场不能抽奖
        return;
    }

}
