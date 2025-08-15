package jsproto.c2s.iclass.room;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 游戏ID列表
 * @author Administrator
 *
 */
public class CBase_GameIdList extends BaseSendMsg {
	private int selectCityId;
	private int gameId;
	public static CBase_GameIdList make(int selectCityId) {
		CBase_GameIdList ret = new CBase_GameIdList();
		ret.setSelectCityId(selectCityId);
		return ret;
	}
	public int getSelectCityId() {
		return selectCityId;
	}
	public void setSelectCityId(int selectCityId) {
		this.selectCityId = selectCityId;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
}