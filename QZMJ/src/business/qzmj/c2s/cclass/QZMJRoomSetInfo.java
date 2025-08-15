package business.qzmj.c2s.cclass;

import jsproto.c2s.cclass.mj.MJRoomSetInfo;

/**
 * 红中麻将当局信息
 * @author Administrator
 *
 */
public class QZMJRoomSetInfo extends MJRoomSetInfo {
	private QZMJSetRoonCfg roomSetCfg;

	// 金
	private int jin = 0; 

	public int getJin() {
		return jin;
	}

	public void setJin(int jin) {
		this.jin = jin;
	}

	public QZMJSetRoonCfg getRoomSetCfg() {
		return roomSetCfg;
	}

	public void setRoomSetCfg(QZMJSetRoonCfg roomSetCfg) {
		this.roomSetCfg = roomSetCfg;
	}
	
	
}
