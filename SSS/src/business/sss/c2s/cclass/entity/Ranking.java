package business.sss.c2s.cclass.entity;

/**
 * 排名
 * Created by Huaxing on 2017/5/12.
 */
public class Ranking {
	private long keyPid;
    private int key;
    private int shui;
    private long toPid;
    private int to ;
    

    public Ranking(long keyPid, int key, int shui, long toPid, int to) {
		super();
		this.keyPid = keyPid;
		this.key = key;
		this.shui = shui;
		this.toPid = toPid;
		this.to = to;
	}
    
    

	public Ranking(long keyPid, int key, long toPid, int to) {
		super();
		this.keyPid = keyPid;
		this.key = key;
		this.toPid = toPid;
		this.to = to;
	}



	public long getKeyPid() {
		return keyPid;
	}

	public void setKeyPid(long keyPid) {
		this.keyPid = keyPid;
	}

	public long getToPid() {
		return toPid;
	}

	public void setToPid(long toPid) {
		this.toPid = toPid;
	}

	public Ranking() {
    }

    public Ranking(int key, int shui, int to) {
        this.key = key;
        this.to = to;
        this.shui = shui;
    }

    public Ranking(int key, int shui) {
        this.key = key;
        this.shui = shui;
    }


    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getShui() {
        return shui;
    }

    public void setShui(int shui) {
        this.shui = shui;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

	@Override
	public String toString() {
		return "Ranking [keyPid=" + keyPid + ", key=" + key + ", shui=" + shui
				+ ", toPid=" + toPid + ", to=" + to + "]";
	}


}
