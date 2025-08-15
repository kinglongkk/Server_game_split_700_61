package business.dzpk.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 位置投票通知
 *
 * @author Administrator
 */
@SuppressWarnings("serial")
public class SDZPK_PosDealVote extends BaseSendMsg {
    // 房间ID	
    private long roomID;
    // 位置	
    private int pos;
    // T:同意解散,F:拒绝解散	
    private boolean agreeDissolve;

    public static SDZPK_PosDealVote make(long roomID, int pos, boolean agreeDissolve) {
        SDZPK_PosDealVote ret = new SDZPK_PosDealVote();
        ret.setRoomID(roomID);
        ret.setPos(pos);
        ret.setAgreeDissolve(agreeDissolve);
        return ret;
    }

    public long getRoomID() {
        return roomID;
    }

    public void setRoomID(long roomID) {
        this.roomID = roomID;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public boolean isAgreeDissolve() {
        return agreeDissolve;
    }

    public void setAgreeDissolve(boolean agreeDissolve) {
        this.agreeDissolve = agreeDissolve;
    }

}		
