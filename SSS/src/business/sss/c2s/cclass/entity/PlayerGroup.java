package business.sss.c2s.cclass.entity;

import business.sss.c2s.cclass.newsss.PockerCard;

import java.util.ArrayList;


/**
 * 玩家组
 * Created by Huaxing on 2017/5/11.
 */
public class PlayerGroup {
    private ArrayList<PockerCard> gamePlayerNum;
	private long roomID;
	private long pid;
	private int posIdx;
    public PlayerGroup() {
    }

    public PlayerGroup(ArrayList<PockerCard> gamePlayerNum) {
        this.gamePlayerNum = gamePlayerNum;
    }
    


    public PlayerGroup(ArrayList<PockerCard> gamePlayerNum, long roomID,
			long pid, int posIdx) {
		super();
		this.gamePlayerNum = gamePlayerNum;
		this.roomID = roomID;
		this.pid = pid;
		this.posIdx = posIdx;
	}

	public long getRoomID() {
		return roomID;
	}

	public void setRoomID(long roomID) {
		this.roomID = roomID;
	}

	public long getPid() {
		return pid;
	}

	public void setPid(long pid) {
		this.pid = pid;
	}

	public int getPosIdx() {
		return posIdx;
	}

	public void setPosIdx(int posIdx) {
		this.posIdx = posIdx;
	}

	public ArrayList<PockerCard> getGamePlayerNum() {
        return gamePlayerNum;
    }

    public void setGamePlayerNum(ArrayList<PockerCard> gamePlayerNum) {
        this.gamePlayerNum = gamePlayerNum;
    }

	@Override
	public String toString() {
		return "PlayerGroup [gamePlayerNum=" + gamePlayerNum + ", roomID="
				+ roomID + ", pid=" + pid + ", posIdx=" + posIdx + "]";
	}


}
