package business.global.pk.dzpk;

import business.global.pk.dzpk.base.DZPK_CardTypeImpl;
import business.global.pk.dzpk.cardtype.DZPK_GaoPaiCardType;
import business.global.room.base.AbsBaseRoom;
import business.global.room.base.AbsRoomPos;
import cenum.RoomTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class DZPKRoomPos extends AbsRoomPos {

    /**
     * 胜局数
     */
    private int winCount;
    /**
     * m每阶段下了多少
     */
    private List<Integer> lastStateBetList = new ArrayList<>();
    private DZPK_CardTypeImpl lastCardTypeInfo = new DZPK_GaoPaiCardType();
    private List<Integer> lastPrivateCards = new ArrayList<>();
    /**
     * 总数赢
     */
    private int totoalWinPoint;


    /**
     * 构造函数
     *
     * @param posID 位置
     * @param room  房间信息
     */
    public DZPKRoomPos(int posID, AbsBaseRoom room) {
        super(posID, room);
        this.setPlayTheGame(true);
    }

    public void initPoint(int initPoint) {
        if (RoomTypeEnum.UNION.equals(this.getRoom().getRoomTypeEnum())) {
            this.setPoint(initPoint > getRoomSportsPointValue() ? (int) getRoomSportsPointValue() : initPoint);
        } else {
            this.setPoint(initPoint);
        }
    }

    public int getWinCount() {
        return winCount;
    }

    public void addWinCount(int winCount) {
        this.winCount += winCount;
    }


}
