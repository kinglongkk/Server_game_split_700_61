package business.global.room.mj;

import business.global.room.base.AbsBaseRoom;
import business.global.room.base.AbsRoomPosMgr;

public class MJRoomPosMgr extends AbsRoomPosMgr {
	
	public MJRoomPosMgr(AbsBaseRoom room) {
		super(room);
	}

	@Override
	protected void initPosList() {
		// 初始化房间位置
		for(int posID = 0; posID < this.getPlayerNum();posID++) {
			this.posList.add(new MJRoomPos(posID,room));
		}		
	}

}
