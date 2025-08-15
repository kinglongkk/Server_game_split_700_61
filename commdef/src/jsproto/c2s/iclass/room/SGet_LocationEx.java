package jsproto.c2s.iclass.room;

import java.util.ArrayList;
import java.util.List;

import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.LocationInfo;

public class SGet_LocationEx extends BaseSendMsg{
	public List<LocationInfo> locationInfos;
	
	public static SGet_LocationEx make(List<LocationInfo> locationInfos) {
		SGet_LocationEx ret = new SGet_LocationEx();
        ret.locationInfos = locationInfos;
        return ret;
    }
}
