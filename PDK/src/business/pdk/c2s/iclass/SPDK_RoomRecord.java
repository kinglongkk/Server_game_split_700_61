package business.pdk.c2s.iclass;

import jsproto.c2s.cclass.*;

import java.util.List;

@SuppressWarnings("serial")
public class SPDK_RoomRecord<T> extends BaseSendMsg {

	public List<T> records;

	public static <T> SPDK_RoomRecord<T> make(List<T> records) {
		SPDK_RoomRecord<T> ret = new SPDK_RoomRecord<T>();
		ret.records = records;

		return ret;

	}
}
