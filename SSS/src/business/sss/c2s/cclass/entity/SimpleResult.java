package business.sss.c2s.cclass.entity;

import business.sss.c2s.cclass.newsss.PockerCard;

import java.util.List;


/**
 * 普通牌
 * Created by Huaxing on 2017/5/11.
 */
public class SimpleResult {
	private long pid; //玩家id
	private int posIdx; //玩家位置
	
    private int simple= 0;  //普通
    private int special = 0;//特殊
    private int shui = 0;
    private List<Integer> maxArray;
    public int maxCode = 0; //
    private boolean isT = false; //是否特殊牌 t是，f 否
    private List<PockerCard> pockerCards;



	public SimpleResult(int special,int maxCode,int shui, boolean isT) {
        this.special = special;
        this.shui = shui;
        this.maxCode = maxCode;
        this.isT = isT;
    }

    
    public SimpleResult(int special,List<Integer> maxArray,int shui, boolean isT) {
        this.special = special;
        this.shui = shui;
        this.maxArray = maxArray;
        this.isT = isT;
    }
    
    public SimpleResult(long pid, int posIdx) {
		super();
		this.pid = pid;
		this.posIdx = posIdx;
	}

	public SimpleResult() {

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

	public int getSpecial() {
        return special;
    }

    public void setSpecial(int special) {
        this.special = special;
    }

    public SimpleResult(int simple, int shui, int maxCode) {
        this.simple = simple;
        this.shui = shui;
        this.maxCode = maxCode;
    }

    public int getMaxCode() {
        return maxCode;
    }

    public void setMaxCode(int maxCode) {
        this.maxCode = maxCode;
    }

    @Override
    public String toString() {
        return "SimpleResult{" +
                "simple=" + simple +
                ", special=" + special +
                ", shui=" + shui +
                ", maxArray=" + maxArray +
                ", maxCode=" + maxCode +
                ", isT=" + isT +
                '}';
    }

    public boolean isT() {
        return isT;
    }

    public void setT(boolean t) {
        isT = t;
    }
    public SimpleResult(int simple) {
        this.simple = simple;
    }

    public SimpleResult(int simple, List<Integer> maxArray) {
        this.simple = simple;
        this.maxArray = maxArray;
    }

    public SimpleResult(int simple, int shui) {
        this.simple = simple;
        this.shui = shui;

    }

    public SimpleResult(int simple, int shui,boolean isT) {
        if (isT) {
            this.special = simple;
        } else {
            this.simple = simple;
        }
        this.isT = isT;
        this.shui = shui;

    }

    public SimpleResult(int simple, int shui, List<Integer> maxArray) {
        this.simple = simple;
        this.shui = shui;
        this.maxArray = maxArray;
    }

    public int getSimple() {
        return simple;
    }

    public void setSimple(int simple) {
        this.simple = simple;
    }

    public int getShui() {
        return shui;
    }

    public void setShui(int shui) {
        this.shui = shui;
    }

    public List<Integer> getMaxArray() {
        return maxArray;
    }

    public void setMaxArray(List<Integer> maxArray) {
        this.maxArray = maxArray;
    }
    
    public List<PockerCard> getPockerCards() {
		return pockerCards;
	}

	public void setPockerCards(List<PockerCard> pockerCards) {
		this.pockerCards = pockerCards;
	}
}
