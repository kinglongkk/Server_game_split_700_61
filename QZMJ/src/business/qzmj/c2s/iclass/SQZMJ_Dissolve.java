package business.qzmj.c2s.iclass;

import jsproto.c2s.iclass.room.SBase_Dissolve;

/**
 * 房间解散通知
 * 
 * @author Administrator
 *
 */
public class SQZMJ_Dissolve extends SBase_Dissolve {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static SQZMJ_Dissolve make(SBase_Dissolve dissolve) {
		SQZMJ_Dissolve ret = new SQZMJ_Dissolve();
		ret.setOwnnerForce(dissolve.isOwnnerForce());
		ret.setRoomID(dissolve.getRoomID());
		ret.setDissolveNoticeType(dissolve.getDissolveNoticeType());
		ret.setMsg(dissolve.getMsg());
		return ret;
	}
}