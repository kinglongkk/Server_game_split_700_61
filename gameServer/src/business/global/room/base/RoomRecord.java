package business.global.room.base;

import java.util.ArrayList;
import java.util.List;

import com.ddm.server.common.CommLogD;

import business.global.club.Club;
import business.global.family.Family;
import business.global.family.FamilyManager;
import cenum.PrizeType;
import cenum.RoomTypeEnum;
import cenum.room.PaymentRoomCardType;
import core.config.refdata.ref.RefRoomCost;
import core.logger.flow.FlowLogger;

/**
 * 房间记录
 * 
 * @author Huaxing
 *
 */
public class RoomRecord {
	// 房间信息
	public AbsBaseRoom room;

	public RoomRecord(AbsBaseRoom room) {
		this.room = room;
		this.initSetRecord();
		this.clear();
	}
	
	/**
	 * 清空
	 */
	private void clear() {
		this.room = null;
	}
	
	private void initSetRecord() {
		// 检查如果是金币场则不记录
		if (PrizeType.Gold.equals(this.room.getBaseRoomConfigure().getPrizeType())) {
			return;
		}
		// 添加房卡消耗日志
		this.addPlayerRoomLog();
		//红包活动
		this.room.checkHaveHongBao();
	}

	/**
	 * 添加房卡消耗日志
	 */
	public void addPlayerRoomLog() {
		FlowLogger.playerRoomLog(String.valueOf(this.room.getGameRoomBO().getDateTime()),this.room.getOwnerID(), this.room.getRoomID(), this.room.getCurSetID(),
				this.room.getGameRoomBO().getPlayerList(), this.room.getCount(), this.room.getBaseRoomConfigure().getBaseCreateRoom().getClubId(),
				this.room.getRoomKey(), this.room.getConsumeValue(), this.room.getGameRoomBO().getCreateTime(),
				this.room.getValueType().value(), this.room.getBaseRoomConfigure().getGameType().getId(),this.room.getCityId(),this.room.getBaseRoomConfigure().getBaseCreateRoom().getUnionId());
	}
}
