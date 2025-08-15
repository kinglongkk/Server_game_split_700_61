package business.global.pk;

import business.global.mj.AbsMJSetPos;
import business.global.room.base.AbsBaseRoom;
import business.global.room.base.AbsRoomPos;
import cenum.RoomTypeEnum;
import cenum.room.RoomDissolutionState;
import com.ddm.server.common.utils.CommMath;
import org.apache.commons.collections.MapUtils;

/**
 * 扑克房间位置信息
 *
 * @author Administrator
 */
public class PKRoomPos extends AbsRoomPos {
    private int cnt = 0;
    private int huCnt = 0; // 胡牌次数
    private int tempPoint = 0;

    public PKRoomPos(int posID, AbsBaseRoom room) {
        super(posID, room);
        // 玩过这个游戏
        this.setPlayTheGame(false);
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public int getHuCnt() {
        return huCnt;
    }

    public void setHuCnt(int huCnt) {
        this.huCnt = huCnt;
    }

    public int getTempPoint() {
        return tempPoint;
    }

    public void setTempPoint(int tempPoint) {
        this.tempPoint = tempPoint;
    }


    /**
     * 计算房间分数
     */
    public void calcRoomPoint(int point) {
        AbsPKSetRoom set = (AbsPKSetRoom) this.getRoom().getCurSet();
        if(set==null|| MapUtils.isEmpty(set.getPosDict())){
            // todo 防止当前局出现空（例如吊蟹在牌局开始之前需扣底分）
            super.calcRoomPoint(point);
            return;
        }
        AbsPKSetPos setPos = set.getPKSetPos(this.getPosID());
        this.setPoint(this.getPoint() + point);
        this.setPointYiKao(CommMath.addDouble(this.getPointYiKao() ,setPos.getDeductEndPoint()));
        this.calcRoomSportsPoint(point,setPos.getDeductEndPoint());
        if (getRoom().isRulesOfCanNotBelowZero() && RoomTypeEnum.UNION.equals(this.getRoom().getRoomTypeEnum())) {
            //小于0的 直接重置0
            if (this.getRoomSportsPoint() <= 0) {
                this.setRoomSportsPoint(0D);
            }
        }
    }


    /**
     * 竞技点
     */
    @Override
    public Double sportsPoint() {
        if ((getRoom().calcFenUseYiKao()||getRoom().isRulesOfCanNotBelowZero()) && RoomTypeEnum.UNION.equals(this.getRoom().getRoomTypeEnum())) {
            if (RoomTypeEnum.UNION.equals(this.getRoom().getRoomTypeEnum())) {
                return CommMath.FormatDouble(this.getPointYiKao());
            } else {
                return null;
            }
        }
        return super.sportsPoint();
    }
}
