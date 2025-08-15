package business.sss.c2s.cclass;

/**
 * 一局中每个位置信息
 * 
 * @author Clark
 *
 */
public class SSSRoomSet_Pos {

	public int posID = 0; // 作为ID
	public long pid = 0; // 账号
	public boolean isPlaying = false;

	// public List<String> cards = new ArrayList<>();
	// public boolean isOped = false; // 是否已经操作
	// public int delayTimes = 0; // 延迟次数
	// public int leftSec = 0; // 剩余操作时间
	// public boolean isLeaved = false; // 是否离开

	// public SSSRoomSet_Pos(int posID, long pid, List<String> cards) {
	// super();
	// this.posID = posID;
	// this.pid = pid;
	// this.cards = cards;
	// }
	//

	public SSSRoomSet_Pos() {
	}

	public SSSRoomSet_Pos(int posID, long pid,
			boolean isPlaying) {
		super();
		this.posID = posID;
		this.pid = pid;
		this.isPlaying = isPlaying;
	}

}
