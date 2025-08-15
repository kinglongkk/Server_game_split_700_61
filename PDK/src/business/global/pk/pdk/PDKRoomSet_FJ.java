package business.global.pk.pdk;

import business.pdk.c2s.cclass.PDK_define;
import business.pdk.c2s.cclass.PDK_define.PDK_WANFA;
import jsproto.c2s.cclass.pk.Victory;

import java.util.ArrayList;
import java.util.List;

/**
 * 跑得快一局游戏逻辑
 * @author zaf
 */

public  class PDKRoomSet_FJ extends PDKRoomSet{

	public ArrayList<Victory> roomDouble ;		//房间倍数

	@SuppressWarnings("rawtypes")
	public PDKRoomSet_FJ( PDKRoom room) {
		super(room);
		this.roomDouble 				= new ArrayList<Victory>();
	}

	/**
	 * @return m_RoomDouble
	 */
	@Override
	public int getRoomDouble(int pos) {
		return Math.max(1,  this.getNumByList(this.roomDouble, pos));
	}


	/**
	 */
	@Override
	public void addRoomDouble(int pos, int roomAddDouble) {
		if (PDK_define.BombAlgorithm.PASS.has(this.room.getRoomCfg().zhadansuanfa)) {
			return;
		}
		if(this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_MAXZHADAN)  ){
			if (this.m_AddRoomDoubleCount >= this.room.getConfigMgr().getMaxRoomAddDouble()) {
				return;
			} else {
				this.m_AddRoomDoubleCount++;
			}
		}
		this.addNumByList(this.roomDouble, pos, roomAddDouble);
	}

	@Override
	public void addNumByList(ArrayList<Victory> list, int pos, int num) {
		boolean flag = false;
		for (Victory victory : list) {
			if (victory == null) {
                return;
            }
			if (victory.getPos() == pos) {
				int count = 0;
				if(num != 0) {
					count = victory.getNum() != 0 ? victory.getNum() : 1;
				}
				num = num != 0 ? num : 1;
				victory.setNum(num + count);
				flag = true;
			}
		}
		if (!flag) {
			list.add(new Victory(pos, num));
		}
	}

	@Override
	protected List<Victory> getRoomDoubleList() {
		return roomDouble;
	}
}
