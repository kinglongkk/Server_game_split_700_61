package business.sss.c2s.cclass;

import jsproto.c2s.cclass.BaseSendMsg;

import java.util.ArrayList;
import java.util.List;

public class SSSCard_Recoed extends BaseSendMsg {
	private List<String> cardList = new ArrayList<>(); //最终胡牌的列表
	private int shootNum = 0;//打枪的次数
	private int fourbagger = 0;//全垒打
	
	public List<String> getCardList() {
		return cardList;
	}
	public void setCardList(List<String> cardList) {
		this.cardList = cardList;
	}
	public int getShootNum() {
		return shootNum;
	}
	public void setShootNum(int shootNum) {
		this.shootNum = shootNum;
	}
	public int getFourbagger() {
		return fourbagger;
	}
	public void setFourbagger(int fourbagger) {
		this.fourbagger = fourbagger;
	}
	
}
