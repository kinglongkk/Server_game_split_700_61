package business.sss.c2s.cclass.entity;

/**
 * 自由扑克
 * 玩家牌的类型
 * @author Huaxing
 *
 */
public class PlayerCardType {
	private long pid;
	private int posIdx;
	private int card = 0;
	
	
	
	public PlayerCardType() {
		super();
	}
	
	public PlayerCardType(long pid, int posIdx, int card) {
		super();
		this.pid = pid;
		this.posIdx = posIdx;
		this.card = card;
	}
	@Override
	public String toString() {
		return "PlayerCardType [pid=" + pid + ", posIdx=" + posIdx + ", card="
				+ card + "]";
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
	public int getCard() {
		return card;
	}
	public void setCard(int card) {
		this.card = card;
	}
	
	
	

}
