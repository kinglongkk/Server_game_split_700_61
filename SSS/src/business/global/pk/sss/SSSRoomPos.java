package business.global.pk.sss;

import business.global.room.base.AbsBaseRoom;
import business.global.room.base.AbsRoomPos;
import business.sss.c2s.cclass.SSSRoomPosInfo;
import business.sss.c2s.iclass.SSSS_CardReadyChg;
import cenum.room.RoomState;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;


public class SSSRoomPos<T> extends AbsRoomPos {

	// 自由扑克
	// 是否已经准备好，全部准备好，才能开始进行第一轮游戏
	private boolean isCardReady = false;
	public SSSRoomPos(int posID, AbsBaseRoom room) {
		super(posID, room);
	}

	
	/**
	 * 玩家的牌准备
	 * 
	 * @param request
	 * @param isReady
	 * @param pid
	 */
	public void playerCardReady(WebSocketRequest request, boolean isReady, long pid) {
		if (this.getRoom().getRoomState() != RoomState.Playing) {
			request.error(ErrorCode.NotAllow, "cur state：" + this.getRoom().getRoomState().toString());
			return;
		}
		setCardReady(isReady);
		request.response();
	}


	/**
	 * 自由扑克 设置准备
	 * 
	 * @param isReady
	 */
	public void setCardReady(boolean isReady) {
		if (this.isCardReady) {
            return;
        }
		this.isCardReady = isReady;
		this.setLatelyOutCardTime(0L);
		this.getRoom().getRoomPosMgr().notify2All(SSSS_CardReadyChg.make(this.getRoom().getRoomID(), this.getPosID(), isReady));
	}

	/**
	 * 自由扑克 清除牌序准备状态
	 */
	public void clearCardReady() {
		this.isCardReady = false;
	}

	/**
	 * 自由扑克 获取牌序状态
	 * 
	 * @return
	 */
	public boolean isCardReady() {
		return this.isCardReady;
	}
	
	@Override
	public SSSRoomPosInfo getNotify_PosInfo() {
		SSSRoomPosInfo tmPos = (SSSRoomPosInfo) this.getRoomPosInfo();
		tmPos.setTrusteeship(this.isTrusteeship());// 托管状态
		tmPos.isCardReady = isCardReady;
		return tmPos;
	}



	/**
	 * 新房间位置信息
	 * @return
	 */
	@Override
	public SSSRoomPosInfo newRoomPosInfo() {
		return new SSSRoomPosInfo();
	}

}
