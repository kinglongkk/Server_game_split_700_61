package business.pdk.c2s.iclass;

import jsproto.c2s.iclass.room.SBase_Dissolve;

/**
 * 房间解散通知
 * 
 * @author Administrator
 *
 */
public class SPDK_Dissolve extends SBase_Dissolve {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static SPDK_Dissolve make(SBase_Dissolve dissolve) {
		SPDK_Dissolve ret = new SPDK_Dissolve();
		ret.setOwnnerForce(dissolve.isOwnnerForce());
		ret.setRoomID(dissolve.getRoomID());
		ret.setDissolveNoticeType(dissolve.getDissolveNoticeType());
		ret.setMsg(dissolve.getMsg());
		return ret;
	}
}
