package business.global.mj.qzmj;

import java.util.List;

import com.google.gson.Gson;

import business.global.mj.AbsMJSetRoom;
import business.global.mj.qzmj.QZMJRoomEnum.QZMJCfg;
import business.global.mj.qzmj.QZMJRoomEnum.QZMJFangGang;
import business.global.room.mj.MahjongRoom;
import business.qzmj.c2s.cclass.QZMJRoomSetInfo;
import business.qzmj.c2s.iclass.CQZMJ_CreateRoom;
import business.qzmj.c2s.iclass.SQZMJ_ChangePlayerNum;
import business.qzmj.c2s.iclass.SQZMJ_ChangePlayerNumAgree;
import business.qzmj.c2s.iclass.SQZMJ_ChangeRoomNum;
import business.qzmj.c2s.iclass.SQZMJ_ChatMessage;
import business.qzmj.c2s.iclass.SQZMJ_Dissolve;
import business.qzmj.c2s.iclass.SQZMJ_LostConnect;
import business.qzmj.c2s.iclass.SQZMJ_PosContinueGame;
import business.qzmj.c2s.iclass.SQZMJ_PosDealVote;
import business.qzmj.c2s.iclass.SQZMJ_PosLeave;
import business.qzmj.c2s.iclass.SQZMJ_PosReadyChg;
import business.qzmj.c2s.iclass.SQZMJ_PosUpdate;
import business.qzmj.c2s.iclass.SQZMJ_RoomEnd;
import business.qzmj.c2s.iclass.SQZMJ_RoomRecord;
import business.qzmj.c2s.iclass.SQZMJ_StartVoteDissolve;
import business.qzmj.c2s.iclass.SQZMJ_Trusteeship;
import business.qzmj.c2s.iclass.SQZMJ_Voice;
import business.qzmj.c2s.iclass.SQZMJ_XiPai;
import cenum.ChatType;
import cenum.ClassType;
import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.RoomEndResult;
import jsproto.c2s.cclass.room.BaseRoomConfigure;
import jsproto.c2s.cclass.room.GetRoomInfo;
import jsproto.c2s.cclass.room.RoomPosInfo;
import jsproto.c2s.iclass.S_GetRoomInfo;
import jsproto.c2s.iclass.room.SBase_Dissolve;
import jsproto.c2s.iclass.room.SBase_PosLeave;

/**
 * 游戏房间
 * 
 * @author Administrator
 *
 */
public class QZMJRoom extends MahjongRoom {
	// 房间配置
	private CQZMJ_CreateRoom roomCfg = null;
	private int setCount = 311;
	public boolean isEnd = false;

	protected QZMJRoom(BaseRoomConfigure<CQZMJ_CreateRoom> baseRoomConfigure, String roomKey, long ownerID) {
		super(baseRoomConfigure, roomKey, ownerID);
		initShareBaseCreateRoom(CQZMJ_CreateRoom.class, baseRoomConfigure);
		this.roomCfg = (CQZMJ_CreateRoom) baseRoomConfigure.getBaseCreateRoom();
	}

	@Override
	public boolean isEnd(){
		return isEnd;
	}
	/**
	 * 清除记录。
	 */
	@Override
	public void clearEndRoom() {
		super.clear();
		this.roomCfg = null;
	}
	@Override
	public int getWanfa() {
		return this.roomCfg.getWanfa();
	}

	/**
	 * 获取房间配置
	 * 
	 * @return
	 */
	public CQZMJ_CreateRoom getRoomCfg() {
		if(this.roomCfg == null){
			initShareBaseCreateRoom(CQZMJ_CreateRoom.class, getBaseRoomConfigure());
			return (CQZMJ_CreateRoom) getBaseRoomConfigure().getBaseCreateRoom();
		}
		return this.roomCfg;
	}
	/**
	 * 是不是一颗
	 * @return
	 */
	public boolean isYiKe(){
		return getRoomCfg().getSetCount()>=setCount;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCfg() {
		return (T) getRoomCfg();
	}

	@Override
	public String dataJsonCfg() {
		// 获取房间配置
		return new Gson().toJson(this.getRoomCfg());
	}

	@Override
	public <E> boolean RoomCfg(E m) {
		QZMJCfg cfgEnum = (QZMJCfg) m;
		int cfgInt = cfgEnum.ordinal();
		if (this.getRoomCfg().getKexuanwanfa().contains(cfgInt)) {
			return true;
		}
		return false;
	}

	@Override
	protected AbsMJSetRoom newMJRoomSet(int curSetID, MahjongRoom room, int dPos) {
		return new QZMJRoomSet(curSetID, (QZMJRoom) room, dPos);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public GetRoomInfo getRoomInfo(long pid) {
		S_GetRoomInfo ret = new S_GetRoomInfo();
		// 设置房间公共信息
		this.getBaseRoomInfo(ret);
		if (null != this.getCurSet()) {
			ret.setSet(this.getCurSet().getNotify_set(pid));
		} else {
			ret.setSet(new QZMJRoomSetInfo());
		}
		return ret;
	}

	@Override
	public BaseSendMsg Trusteeship(long roomID, long pid, int pos, boolean trusteeship) {
		return SQZMJ_Trusteeship.make(roomID, pid, pos, trusteeship);
	}


	@Override
	public BaseSendMsg PosLeave(SBase_PosLeave posLeave) {
		return SQZMJ_PosLeave.make(posLeave);
	}

	@Override
	public BaseSendMsg LostConnect(long roomID, long pid, boolean isLostConnect,boolean isShowLeave) {
		return SQZMJ_LostConnect.make(roomID, pid, isLostConnect,isShowLeave);
	}

	@Override
	public BaseSendMsg PosContinueGame(long roomID, int pos) {
		return SQZMJ_PosContinueGame.make(roomID, pos);
	}

	@Override
	public BaseSendMsg PosUpdate(long roomID, int pos, RoomPosInfo posInfo, int custom) {
		return SQZMJ_PosUpdate.make(roomID, pos, posInfo, custom);
	}

	@Override
	public BaseSendMsg PosReadyChg(long roomID, int pos, boolean isReady) {
		return SQZMJ_PosReadyChg.make(roomID, pos, isReady);
	}

	@Override
	public BaseSendMsg Dissolve(SBase_Dissolve dissolve) {
		return SQZMJ_Dissolve.make(dissolve);
	}
	@Override
	public BaseSendMsg StartVoteDissolve(long roomID, int createPos, int endSec) {
		return SQZMJ_StartVoteDissolve.make(roomID, createPos, endSec);
	}

	@Override
	public BaseSendMsg PosDealVote(long roomID, int pos, boolean agreeDissolve,int endSec) {
		return SQZMJ_PosDealVote.make(roomID, pos, agreeDissolve);
	}

	@Override
	public BaseSendMsg Voice(long roomID, int pos, String url) {
		return SQZMJ_Voice.make(roomID, pos, url);
	}

	@Override
	public <T> BaseSendMsg RoomRecord(List<T> records) {
		return SQZMJ_RoomRecord.make(records);
	}

	@Override
	public <T> BaseSendMsg RoomEnd(T record, RoomEndResult<?> sRoomEndResult) {
		return SQZMJ_RoomEnd.make(this.getMJRoomRecordInfo(), this.getRoomEndResult());
	}

	@Override
	public BaseSendMsg XiPai(long roomID, long pid, ClassType cType) {
		return SQZMJ_XiPai.make(roomID, pid, cType);
	}

	@Override
	public BaseSendMsg ChatMessage(long pid, String name, String content, ChatType type, long toCId, int quickID) {
		return SQZMJ_ChatMessage.make(pid, name, content, type, toCId, quickID);
	}

	@Override
	public BaseSendMsg ChangePlayerNum(long roomID, int createPos, int endSec, int playerNum) {
		return SQZMJ_ChangePlayerNum.make(roomID, createPos, endSec, playerNum);
	}

	@Override
	public BaseSendMsg ChangePlayerNumAgree(long roomID, int pos, boolean agreeChange) {
		return SQZMJ_ChangePlayerNumAgree.make(roomID, pos, agreeChange);
	}

	@Override
	public BaseSendMsg ChangeRoomNum(long roomID, String roomKey, int createType) {
		return SQZMJ_ChangeRoomNum.make(roomID, roomKey, createType);
	}


	public int getYouJinBeiShu() {
		return QZMJRoomEnum.QZMJYouJin.valueOf(roomCfg.beishu).value();
	}


	/**
	 * 结算方式 放胡三家陪
	 * @param jieSuan
	 * @return
	 */
	public boolean getJieSuan(QZMJRoomEnum.QZMJJieSuan jieSuan){

		int cfgInt = jieSuan.ordinal();
		if (this.getRoomCfg().jiesuan==cfgInt) {
			return true;
		}
		return false;
	}
}
