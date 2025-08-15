package business.global.pk.sss;

import business.global.room.base.AbsBaseRoom;
import business.global.room.base.AbsRoomPos;
import business.global.room.base.AbsRoomPosMgr;

import business.player.Robot.RobotMgr;
import business.sss.c2s.cclass.SSSResults;
import com.ddm.server.common.utils.CommTime;
import jsproto.c2s.cclass.Player.ShortPlayer;
import jsproto.c2s.cclass.pk.PKRoom_RecordPosInfo;
import jsproto.c2s.cclass.pos.RoomPlayerPos;
import jsproto.c2s.cclass.room.RoomPosInfo;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SSSRoomPosMgr extends AbsRoomPosMgr {

	public SSSRoomPosMgr(AbsBaseRoom room) {
		super(room);

	}

	/**
	 * 全部准备
	 *
	 * @return
	 */
	public boolean isSSSAllReady() {
		boolean ret = true;
		int count = 0;
		for (AbsRoomPos pos : this.posList) {
			if (pos.getPid() <= 0) {
				continue;
			}
			if (!pos.isReady()) {
				ret = false;
				break;
			}
			count++;
		}
		
		if (count >= 2 && ret) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getAllPlayerNum() {
		int count = 0;
		for (AbsRoomPos pos : this.posList) {
			if (pos.getPid() <= 0) {
				continue;
			}
			count++;
		}
		return count;
	}

	@SuppressWarnings("rawtypes")
	public List<SSSRoomPos> getAllSSSRoomPosList () {
		List<SSSRoomPos> sssRoomPoss = new ArrayList<SSSRoomPos>();
		for (AbsRoomPos pos : this.posList) {
			if (null == pos) {
				continue;
			}
			if (pos.getPid() <= 0) {
				continue;
			}
			sssRoomPoss.add((SSSRoomPos<?>) pos);
		}
		
		return sssRoomPoss;
	}

	/**
	 * 设置所有用户的超时
	 */
	public void setAllLatelyOutCardTime() {
		if (CollectionUtils.isEmpty(this.getPosList())) {
			// 玩家信息列表没数据
			return;
		}
		// 遍历所有玩家
		this.getPosList().forEach(key -> {
			if (Objects.nonNull(key)&&key.getPid()>0) {
				key.setLatelyOutCardTime(CommTime.nowMS());
			}
		});
	}
	@Override
	protected void initPosList() {
		// 初始化房间位置
		for (int posID = 0; posID < super.getPlayerNum(); posID++) {
			this.posList.add(new SSSRoomPos(posID, room));
		}

	}

	@SuppressWarnings("rawtypes")
	@Override
	public int getPlayerNum() {
		int count = 0;
		for (int i = 0; i < super.getPlayerNum() ;i++) {
			SSSRoomPos sPos = (SSSRoomPos) posList.get(i);
			if (null == sPos) {
				continue;
			}
			if (!sPos.isPlayTheGame()) {
				continue;
			}
			count++;
		}
		return count;
	}

	
	/**
	 * 自由扑克 清除牌序的状态
	 */
	public void clearCardReady() {
		for (AbsRoomPos pos : this.posList) {
			SSSRoomPos sssRoomPos=(SSSRoomPos)pos;
			if (null == pos) {
				continue;
			}
			if (pos.getPid() <= 0) {
				continue;
			}
			sssRoomPos.clearCardReady();
		}
	}

	/**
	 * 自由扑克 全部人是否已经摆好牌了。
	 */
	public boolean isAllCardReady() {
		boolean ret = true;
		for (int i = 0; i < this.getPlayerNum(); i++) {
			SSSRoomPos<?> pos = (SSSRoomPos<?>) posList.get(i);
			if (null == pos) {
				continue;
			}
			if (pos.getPid() <= 0) {
				continue;
			}
			if (!pos.isPlayTheGame()) {
				continue;
			}
			if (!pos.isCardReady()) {
				ret = false;
				break;
			}
		}
		return ret;
	}
	
//	public List<RoomPosInfo> getNotify_PosList() {
//		List<RoomPosInfo> ret = new ArrayList<>();
//		for (int i = 0 ;i < this.getPlayerNum();i++) {
//			SSSRoomPos<?> pos = (SSSRoomPos<?>) posList.get(i);
//			if (null == pos)
//				continue;
//			RoomPosInfo tmPos = pos.getNotify_PosInfo();
//			ret.add(tmPos);
//		}
//
//		return ret;
//	}

	
//	public List<ShortPlayer> getShortPlayerList () {
//		PKRoom_RecordPosInfo countRecord  = null;
//		List<ShortPlayer> ret = new ArrayList<>();
//		for (AbsRoomPos pos : this.posList) {
//			SSSRoomPos<?> sssPos = (SSSRoomPos<?>) pos;
//			if (null == sssPos)
//				continue;
//			if (!sssPos.isPlayTheGame())
//				continue;
////			countRecord = (PKRoom_RecordPosInfo) sssPos.getResults();
//			if (null == countRecord)
//				continue;
//
//			ShortPlayer rPlayerPos = pos.getShortPlayer();
//			if (null != rPlayerPos)
//				ret.add(rPlayerPos);
//		}
//		return ret;
//	}
	/**
	 * 是否所有玩家准备
	 *
	 * @return
	 */
	@Override
	public boolean isAllReady() {

		if(this.room.getBaseRoomConfigure().getBaseCreateRoom().getPlayerNum()==this.room.getBaseRoomConfigure().getBaseCreateRoom().getPlayerMinNum()){
			if (null == this.getPosList() || this.getPosList().size() <= 1) {
				// 玩家信息列表没数据
				return false;
			}
			AbsRoomPos result = this.getPosList().stream().filter((x) -> !x.isReady()).findAny().orElse(null);
			if (null != result) {
				return false;
			}
			return true;
		}else {
			List<AbsRoomPos> result = this.getPosList().stream().filter((x) -> x.getPid()!=0).collect(Collectors.toList());
			if (null == this.getPosList() || this.getPosList().size() <= 1) {
				// 玩家信息列表没数据 人数少于两个无法开始
				return false;
			}
			if(null==result||result.size()<2){
				// 房间玩家 人数少于两个无法开始
				return false;
			}
			for(AbsRoomPos con:result){
				if(!con.isReady()){
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * 玩家位置基本信息
	 *
	 * @return
	 */
	@Override
	public List<RoomPlayerPos> getRoomPlayerPosList() {
		List<RoomPlayerPos> roomPlayerPos=new ArrayList<>();
		for(AbsRoomPos con:this.getPosList()){
			if(con.getPid()==0||!con.isPlayTheGame()){
				continue;
			}
			roomPlayerPos.add(con.roomPlayerPos());
		}
		return roomPlayerPos;
	}

	/**
	 * 获取玩家简介列表
	 * @return
	 */
	public List<ShortPlayer> getShortPlayerList (List<SSSResults> sssResults) {
		return this.getPosList().stream().filter(k ->  k.getPid() > 0L && k.isPlayTheGame()&&scoreFlag(sssResults,k.getPid())).map(k -> k.getShortPlayer()).collect(Collectors.toList());
	}


	public boolean scoreFlag(List<SSSResults> sssResults,long pid){
		for(SSSResults con:sssResults){
			if(con.getPid()==pid){
				return true;
			}
		}
		return false;

	}
	/**
	 * 是否所有玩家继续下一局
	 *
	 * @return
	 */
	@Override
	public boolean isAllContinue() {
		if (null == this.getPosList() || this.getPosList().size() <= 1) {
			// 玩家信息列表没数据
			return false;
		}
		if(this.room.getBaseRoomConfigure().getBaseCreateRoom().getFangjian().contains(2)){
	//        //超时继续，萍乡
			this.getPosList().stream().forEach(k -> {
				if (k.getPid() > 0 && !k.isGameReady() && k.getTimeSec() > 0 && CommTime.nowSecond()- k.getTimeSec() >= 10) {
					getRoom().continueGame(k.getPid());
				}
			});
		}
		// 玩家在游戏中并且没有准备。
		return this.getPosList().stream().allMatch(k ->k.getPid()<=0L|| (k.getPid() > 0L && k.isGameReady()));
	}
	/**
	 * 检查用户超时
	 */
	public void checkOverTime(int ServerTime) {
		if (ServerTime == 0) {
			return;
		}
		for (AbsRoomPos pos : this.getPosList()) {
			if (Objects.isNull(pos) || pos.getPid() <= 0L) {
				continue;
			}
			if (pos.getLatelyOutCardTime() <= 0) {
				continue;
			}
			//已经准备
			if (((SSSRoomPos)pos).isCardReady()) {
				continue;
			}
			if (pos.isTrusteeship()) {
				continue;
			}

			if (pos.isRobot() && CommTime.nowMS() > pos.getLatelyOutCardTime() + RobotMgr.getInstance().getThinkTime()) {
				this.getRoom().RobotDeal(pos.getPosID());
				continue;
			}
			if (CommTime.nowMS() > pos.getLatelyOutCardTime() + ServerTime) {
				pos.setLatelyOutCardTime(CommTime.nowMS());
				if (Objects.nonNull(this.getRoom())) {
					// 启动定时器
					this.getRoom().startTrusteeShipTime();
				}
				pos.setTrusteeship(true, false);
				if (Objects.nonNull(this.getRoom())) {
					if(room.needAtOnceOpCard()){
						room.roomTrusteeship(pos.getPosID());
					}
				}

			}
		}
	}

}
