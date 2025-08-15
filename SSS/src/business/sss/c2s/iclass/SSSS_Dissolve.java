package business.sss.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.iclass.room.SBase_Dissolve;

public class SSSS_Dissolve extends SBase_Dissolve {

    private static final long serialVersionUID = 1L;


    public static SSSS_Dissolve make(SBase_Dissolve dissolve) {
    	SSSS_Dissolve ret = new SSSS_Dissolve();
        ret.setOwnnerForce(dissolve.isOwnnerForce());
        ret.setRoomID(dissolve.getRoomID());
        ret.setDissolveNoticeType(dissolve.getDissolveNoticeType());
        ret.setMsg(dissolve.getMsg());
        return ret;
    

    }
}
