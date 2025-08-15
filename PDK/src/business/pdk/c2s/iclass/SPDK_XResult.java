package business.pdk.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SPDK_XResult extends BaseSendMsg {
    //用户信息
    private Map<Integer, SPDK_UserInfo> userInfo = new HashMap<>();
    //局数信息
    private List<SPDK_SetInfo> setInfo = new ArrayList<>();

    public List<SPDK_SetInfo> getSetInfo() {
        return setInfo;
    }

    public Map<Integer, SPDK_UserInfo> getUserInfo() {
        return userInfo;
    }

}
