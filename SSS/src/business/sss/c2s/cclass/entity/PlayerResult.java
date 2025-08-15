package business.sss.c2s.cclass.entity;

/**
 * 自由扑克
 * 记录个人对比结果
 * @author Huaxing
 *
 */
public class PlayerResult {

	private long pid; //玩家id
	private int posIdx; //位置
	private int shui = 0;//积分
	private int point=0;//积分
	private int baseMark=0;//底分
	private int  doubleNum=0;//倍数
	private Double sportsPoint;//竞技点积分
	
	public PlayerResult() {
		super();
	}
	public PlayerResult(long pid, int posIdx, int shui) {
		super();
		this.pid = pid;
		this.posIdx = posIdx;
		this.shui = shui;
	}
	@Override
	public String toString() {
		return "PlayerResult [pid=" + pid + ", posIdx=" + posIdx + ", shui="
				+ shui + "]";
	}

	public Double getSportsPoint() {
		return sportsPoint;
	}

	public void setSportsPoint(Double sportPoint) {
		this.sportsPoint = sportPoint;
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
	public int getShui() {
		return shui;
	}
	public void setShui(int shui) {
		this.shui = shui;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public int getBaseMark() {
		return baseMark;
	}

	public void setBaseMark(int baseMark) {
		this.baseMark = baseMark;
	}

	public int getDoubleNum() {
		return doubleNum;
	}

	public void setDoubleNum(int doubleNum) {
		this.doubleNum = doubleNum;
	}
}
