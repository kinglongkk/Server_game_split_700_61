package business.global.pk.robot;

import business.global.mj.AbsMJSetPos;
import business.global.mj.MJCard;
import business.global.pk.AbsPKSetPos;
import business.global.pk.PKOpCard;
import business.player.Robot.*;
import business.player.Robot.TileRank.HuaRank;
import business.player.Robot.TileRank.NumberRank;
import business.player.Robot.TileRank.ZiRank;
import cenum.mj.MJSpecialEnum;
import cenum.mj.OpType;
import lombok.Data;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * 机器人位置操作
 *
 * @author Administrator
 */
@Data
public class PKSetPosRobot {
    protected AbsPKSetPos mSetPos;


    public PKSetPosRobot(AbsPKSetPos mSetPos) {
        this.mSetPos = mSetPos;
    }


    public PKOpCard getAutoCard() {
        return PKOpCard.OpCard(0);
    }


}
	
	

	
	

