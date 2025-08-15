package business.global.pk.dzpk;

import business.global.pk.AbsPKSetPos;
import business.global.pk.AbsPKSetRound;
import business.global.pk.robot.PKRobotOpCard;
import cenum.PKOpType;
import com.ddm.server.websocket.handler.requset.WebSocketRequestDelegate;

import java.util.List;

public class DZPKRobotOpCard extends PKRobotOpCard {
    public DZPKRobotOpCard(AbsPKSetRound setRound) {
        super(setRound);
    }


    /**
     * 存在首牌
     *
     * @return
     */
    @Override
    public int existOutCard(List<PKOpType> opTypes, AbsPKSetPos mSetPos) {
        return this.getSetRound().opCard(new WebSocketRequestDelegate(), mSetPos.getPosID(), PKOpType.PASS_CARD, mSetPos.getSetPosRobot().getAutoCard());
    }


}		
