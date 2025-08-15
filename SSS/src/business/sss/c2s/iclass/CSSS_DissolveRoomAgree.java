package business.sss.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 自由扑克
 * 接收客户端数据
 * 解散房间，同意
 * @author Huaxing
 *
 */
public class CSSS_DissolveRoomAgree extends BaseSendMsg {
    
    public long roomID;


    public static CSSS_DissolveRoomAgree make(long roomID) {
        CSSS_DissolveRoomAgree ret = new CSSS_DissolveRoomAgree();
        ret.roomID = roomID;

        return ret;
    

    }
}