package business.global.pk.pdk;

import business.global.room.base.AbsBaseRoom;
import business.global.room.base.RoomPlayBackImplAbstract;
import jsproto.c2s.cclass.BaseSendMsg;

public class PDKRoomPlayBackImpl extends RoomPlayBackImplAbstract {

	private static final String opCard = "OpCard"; // 监听的消息
	private final static String SetStart = "SetStart";
	private final static String StartVoteDissolve = "StartVoteDissolve";


	public PDKRoomPlayBackImpl(AbsBaseRoom room) {
		super(room);
	}

	@Override
	public boolean isOpCard(BaseSendMsg msg) {

		return msg.getOpName().indexOf(opCard) > 0 || msg.getOpName().indexOf(SetStart) > 0 || msg.getOpName().indexOf(StartVoteDissolve) > 0;
	}
}
