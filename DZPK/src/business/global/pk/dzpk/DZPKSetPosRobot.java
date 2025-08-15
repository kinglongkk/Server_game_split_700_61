package business.global.pk.dzpk;

import business.global.pk.AbsPKSetPos;
import business.global.pk.PKOpCard;
import business.global.pk.robot.PKSetPosRobot;
import cenum.PKOpType;

public class DZPKSetPosRobot extends PKSetPosRobot {

    public DZPKSetPosRobot(AbsPKSetPos mSetPos) {
        super(mSetPos);
    }

    @Override
    public PKOpCard getAutoCard() {
        return new PKOpCard(PKOpType.PASS_CARD.value());
    }
}		
