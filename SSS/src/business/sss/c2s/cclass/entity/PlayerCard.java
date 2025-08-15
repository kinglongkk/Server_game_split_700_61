package business.sss.c2s.cclass.entity;

public class PlayerCard {
	public long pid;
	public int posIdx;
	private SimpleResult first;
	private SimpleResult second;
	private SimpleResult third;
	
	public boolean havemapai= false;

	public PlayerCard(long pid, int posIdx, SimpleResult first,
			SimpleResult second, SimpleResult third,boolean havemapai) {
		super();
		this.pid = pid;
		this.posIdx = posIdx;
		this.first = first;
		this.second = second;
		this.third = third;
		this.havemapai = havemapai;
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

	public SimpleResult getFirst() {
		return first;
	}
	public void setFirst(SimpleResult first) {
		this.first = first;
	}
	public SimpleResult getSecond() {
		return second;
	}
	public void setSecond(SimpleResult second) {
		this.second = second;
	}
	public SimpleResult getThird() {
		return third;
	}
	public void setThird(SimpleResult third) {
		this.third = third;
	}
	
	public boolean containMaPai()
	{
		return havemapai;
	}
	
	@Override
	public String toString() {
		return "PlayerCard [pid=" + pid + ", posIdx=" + posIdx + ", first="
				+ first + ", second=" + second + ", third=" + third + "]";
	}
	
	
	
	
}
