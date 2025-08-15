package jsproto.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 用户兑换
 * @author Huaxing
 *
 */
public class CPlayer_ChangeNameAndHeadImg extends BaseSendMsg {
	public String name;
    public String headImg;
    public static CPlayer_ChangeNameAndHeadImg make(String productID,String headImg) {
    	CPlayer_ChangeNameAndHeadImg ret = new CPlayer_ChangeNameAndHeadImg();
    	ret.headImg = productID;
        ret.headImg = productID;
        return ret;
    

    }
}
