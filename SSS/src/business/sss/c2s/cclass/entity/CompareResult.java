package business.sss.c2s.cclass.entity;

/**
 * 对比结果
 * Created by Huaxing on 2017/5/11.
 */
public class CompareResult {
    private int PlayerNum1 = -1;//玩家1
    private int PlayerNum2 = -1;//玩家2
	private long pid1;
	private long pid2;


	private int PlayerShui = 0;
    private boolean isKill = false;//是否通杀


    public CompareResult() {
    }

    public CompareResult(int playerNum1, int playerNum2) {
        PlayerNum1 = playerNum1;
        PlayerNum2 = playerNum2;
    }

    public CompareResult(int playerNum1, int playerNum2, int playerShui) {
        PlayerNum1 = playerNum1;
        PlayerNum2 = playerNum2;
        PlayerShui = playerShui;
    }




    public CompareResult(int playerNum1, int playerNum2, long pid1, long pid2) {
		super();
		PlayerNum1 = playerNum1;
		PlayerNum2 = playerNum2;
		this.pid1 = pid1;
		this.pid2 = pid2;
	}
    
    

	@Override
	public String toString() {
		return "CompareResult [PlayerNum1=" + PlayerNum1 + ", PlayerNum2="
				+ PlayerNum2 + ", pid1=" + pid1 + ", pid2=" + pid2
				+ ", PlayerShui=" + PlayerShui + ", isKill=" + isKill + "]";
	}

	public int getPlayerNum1() {
        return PlayerNum1;
    }

    public void setPlayerNum1(int playerNum1) {
        PlayerNum1 = playerNum1;
    }

    public int getPlayerNum2() {
        return PlayerNum2;
    }

    public void setPlayerNum2(int playerNum2) {
        PlayerNum2 = playerNum2;
    }

    public int getPlayerShui() {
        return PlayerShui;
    }

    public void setPlayerShui(int playerShui) {
        PlayerShui = playerShui;
    }
    public boolean isKill() {
        return isKill;
    }

    public void setKill(boolean kill) {
        isKill = kill;
    }

    public long getPid1() {
		return pid1;
	}

	public void setPid1(long pid1) {
		this.pid1 = pid1;
	}

	public long getPid2() {
		return pid2;
	}

	public void setPid2(long pid2) {
		this.pid2 = pid2;
	}


}
