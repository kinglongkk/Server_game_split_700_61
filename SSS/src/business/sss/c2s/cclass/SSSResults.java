package business.sss.c2s.cclass;

import jsproto.c2s.cclass.room.AbsBaseResults;

public class SSSResults extends AbsBaseResults {
    public   	int  				winCount  = 0 ; // 赢场数
    public  	int    				loseCount = 0; // 输场数
    public  	int 				flatCount = 0; // 平场数
    public      boolean             bigWinner=false;
}
