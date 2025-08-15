package business.global.pk.nn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * 十三水，设置牌
 * @author Huaxing
 *
 */
public class NNSetCard {

	public ArrayList<Integer> leftCards = new ArrayList<>(); // 扑克牌编号
	private Random random;	
	public NNSetCard(NNRoomSet set){
		random = new Random();
		this.randomCard();
	}
	

	/**
	 * 洗牌
	 */
	public void randomCard(){
		this.leftCards = BasePockerLogic.getRandomPockerList(1, 0, BasePocker.PockerListType.POCKERLISTTYPE_AFIRST);
	}
	
	/*
	 * 洗牌
	 * **/
	public void  onXiPai() {
		Collections.shuffle(this.leftCards);
	}

	/**
	 * 发牌
	 * @param cnt
	 * @return
	 */
	public ArrayList<Integer> popList(int cnt){
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for (int i = 0; i < cnt; i++) {
			Integer Integer1 = this.leftCards.remove(random.nextInt(this.leftCards.size()));
			ret.add(Integer1);
		}
		return ret;
	}

	public ArrayList<Integer> getLeftCards() {
		return leftCards;
	}
}

