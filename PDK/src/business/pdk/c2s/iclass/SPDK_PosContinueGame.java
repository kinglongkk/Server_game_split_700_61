package business.pdk.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 位置继续游戏通知
 * 
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
public class SPDK_PosContinueGame extends BaseSendMsg {
	// 房间ID
	private long roomID;
	// 位置
	private int pos;

	public static SPDK_PosContinueGame make(long roomID, int pos) {
		SPDK_PosContinueGame ret = new SPDK_PosContinueGame();
		ret.setRoomID(roomID);
		ret.setPos(pos);
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

}
