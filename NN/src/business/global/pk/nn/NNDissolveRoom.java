package business.global.pk.nn;

import business.global.room.base.AbsBaseRoom;
import business.global.room.base.AbsRoomPos;
import business.global.room.base.DissolveRoom;
import cenum.room.DissolveType;

public class NNDissolveRoom extends DissolveRoom {
    public NNDissolveRoom(AbsBaseRoom room, int createPos, int WaitSec) {
        super(room, createPos, WaitSec);
    }

    /**
     * 检查是否同意解散
     * @param type 解散类型
     * @return
     */
    @Override
    public boolean isAllAgree(DissolveType type) {
        if (DissolveType.ALL.equals(type)) {
            // 全部同意才解散。
            return this.checkAllAgreeNN();
        } else {
            // 超过一半同意才能解散。
            return this.checkHalfAgree();
        }
    }
    /**
     * 自由扑克全部同意才解散。
     * @return
     */
    private boolean checkAllAgreeNN() {
        boolean flag=this.getRoom().getBaseRoomConfigure().getBaseCreateRoom().getPlayerNum()==this.getRoom().getBaseRoomConfigure().getBaseCreateRoom().getPlayerMinNum();
        if(flag){
            int agreeCnt = 0;
            AbsRoomPos roomPos = null;
            for (int i = 0; i < this.getRoom().getPlayerNum(); i++) {
                roomPos = this.getRoom().getRoomPosMgr().getPosByPosID(i);
                if(null == roomPos) {
                    continue;
                }
                if(roomPos.getPid()==0) {
                    continue;
                }
                if(!roomPos.isPlayTheGame()) {
                    continue;
                }
                if (this.getPosAgreeList().get(i) == 1) {
                    agreeCnt += 1;
                }
            }
            return agreeCnt >= this.getRoom().getPlayingCount();
        }else {
            int agreeCnt = 0;
            int playNum=0;
            AbsRoomPos roomPos = null;
            for (int i = 0; i < this.getRoom().getPlayerNum(); i++) {
                roomPos = this.getRoom().getRoomPosMgr().getPosByPosID(i);
                if(null == roomPos) {
                    continue;
                }
                if(roomPos.getPid()==0) {
                    continue;
                }
                if(!roomPos.isPlayTheGame()) {
                    continue;
                }
                playNum++;
                if (this.getPosAgreeList().get(i) == 1) {
                    agreeCnt += 1;
                }
            }
            return agreeCnt >= playNum;
        }
    }
    /**
     * 超过一半同意才能解散。
     * @return
     */
    private boolean checkHalfAgree() {
        int agreeCnt = 0;
        boolean agreeDissolve = false;
        AbsRoomPos roomPos = null;
        for (int i = 0; i < this.getRoom().getPlayerNum(); i++) {
            roomPos = this.getRoom().getRoomPosMgr().getPosByPosID(i);
            if(null == roomPos) {
                continue;
            }
            if(!roomPos.isPlayTheGame()) {
                continue;
            }
            if (this.getPosAgreeList().get(i) == 1) {
                agreeCnt += 1;
            }
        }
        if (this.getRoom().getPlayingCount() > 2 && agreeCnt >=  Math.ceil((this.getRoom().getPlayingCount() + 1)/2.0)) {
            agreeDissolve = true;
        } else {
            agreeDissolve = agreeCnt >= this.getRoom().getPlayingCount();
        }
        return agreeDissolve;
    }


}
