package business.qzmj.c2s.iclass;

import jsproto.c2s.cclass.*;

import java.util.List;

@SuppressWarnings("serial")
public class SQZMJ_RoomRecord<T> extends BaseSendMsg {

	public List<T> records;

	public static <T> SQZMJ_RoomRecord<T> make(List<T> records) {
		SQZMJ_RoomRecord<T> ret = new SQZMJ_RoomRecord<T>();
		ret.records = records;

		return ret;

	}
}