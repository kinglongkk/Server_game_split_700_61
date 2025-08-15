package jsproto.c2s.iclass.club;

import java.util.ArrayList;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 获取俱乐部游戏设置
 * 
 * @author zaf
 *
 */

public class CClub_Create extends BaseSendMsg {

	private String clubName;// 俱乐部名字
	/**
	 * 城市id
	 */
	private int cityId;
	public static CClub_Create make(String clubName) {
		CClub_Create ret = new CClub_Create();
		ret.clubName = clubName;
		return ret;
	}

	public String getClubName() {
		return clubName;
	}

	public void setClubName(String clubName) {
		this.clubName = clubName;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
}