package business.qzmj.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.room.RoomPosInfo;

/**
 * 位置更新通知
 * 
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
public class SQZMJ_PosUpdate extends BaseSendMsg {
	// 房间ID
	private long roomID;
	// 玩家位置
	private int pos;
	// 房间玩家信息
	private RoomPosInfo posInfo;
	// 自定义数据
	private int custom;

	public static SQZMJ_PosUpdate make(long roomID, int pos, RoomPosInfo posInfo, int custom) {
		SQZMJ_PosUpdate ret = new SQZMJ_PosUpdate();
		ret.setRoomID(roomID);
		ret.setPos(pos);
		ret.setPosInfo(posInfo);
		ret.setCustom(custom);
		return ret;
	}

	public long getRoomID() {
		return roomID;
	}

	public void setRoomID(long roomID) {
		this.roomID = roomID;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public RoomPosInfo getPosInfo() {
		return posInfo;
	}

	public void setPosInfo(RoomPosInfo posInfo) {
		this.posInfo = posInfo;
	}

	public int getCustom() {
		return custom;
	}

	public void setCustom(int custom) {
		this.custom = custom;
	}

}