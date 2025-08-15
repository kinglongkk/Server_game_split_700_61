package business.global.pk.pdk;

import business.pdk.c2s.cclass.PDK_define.PDK_WANFA;
import jsproto.c2s.cclass.pk.BasePocker;
import jsproto.c2s.cclass.pk.BasePocker.PockerListType;
import jsproto.c2s.cclass.pk.BasePockerLogic;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 跑得快，设置牌
 * @author Huaxing
 *
 */
public class PDKSetCard {

	public ArrayList<Integer> leftCards = new ArrayList<Integer>(); // 扑克牌编号
	public ArrayList<Integer> backUpCards = new ArrayList<Integer>(); // 扑克牌编号
	private Random random;
	public PDKRoom zRoom;
	@SuppressWarnings("unchecked")
	public PDKSetCard(PDKRoom room){
		random = new Random();
		zRoom = room;
		int cardNum = room.getRoomCfg().shoupai > 2 ? 2 : 1;
		
		this.leftCards = BasePockerLogic.getRandomPockerList(cardNum, 0, PockerListType.POCKERLISTTYPE_TWOEND);
		
		if (room.isWanFaByType(PDK_WANFA.PDK_WANFA_QUSANZHANG)) {
			//52变48
			for (int byte1 : room.getConfigMgr().getDeleteCard().get(room.getRoomCfg().shoupai + 1)) {
				BasePockerLogic.deleteSameCard(this.leftCards, (byte) byte1, false);
			}
			
			//48变45
			for (int i = 0; i < 3; i++) {
				int index = random.nextInt(this.leftCards.size());
				int card = this.leftCards.get(index);
				// 不能去掉黑桃3
				if (card != 0x33) {
					this.leftCards.remove(index);
				}
			}
		} else {
			//去掉牌
			for (int byte1 : room.getConfigMgr().getDeleteCard().get(room.getRoomCfg().shoupai)) {
				BasePockerLogic.deleteSameCard(this.leftCards, (byte) byte1, false);
			}
		}
		
		
		
		this.backUpCards = (ArrayList<Integer>) this.leftCards.clone();
	}
	
	public void clean() {
		this.random = null;
		if (null != this.leftCards) {
			this.leftCards.clear();
			this.leftCards = null;
		}
		if (null != this.backUpCards) {
			this.backUpCards.clear();
			this.backUpCards = null;
		}
	}

	
	/*
	 * 洗牌
	 * **/
	@SuppressWarnings("unchecked")
	public void  onXiPai() {
		Collections.shuffle(this.leftCards);
		this.backUpCards = (ArrayList<Integer>) this.leftCards.clone();
	}

	/**
	 * 发牌
	 * @param cnt
	 * @return
	 */
	public ArrayList<Integer> popList(int cnt){
		ArrayList<Integer> ret = new ArrayList<Integer>();
		if(this.leftCards.size() <= 0) {
            return ret;
        }
		for (int i = 0; i < cnt; i++) {
			if(this.leftCards.size() <= 0) {
                return ret;
            }
			//无炸玩法
			if(this.zRoom.isWanFaByType(PDK_WANFA.PDK_WANFA_WUZHA)){
				randomCardNoBombCard(ret);
			}else{
				Integer byte1 = this.leftCards.remove(random.nextInt(this.leftCards.size()));
				int count1 = checkCount(ret);
				ret.add(byte1);
				int count2 = checkCount(ret);
				if(count2 - count1>=1){//炸弹加了一个
					int cardRandom = new Random().nextInt(100);
					if(cardRandom<80){
						ret.remove(byte1);
						this.leftCards.add(byte1);
						Integer byte2 = this.leftCards.remove(random.nextInt(this.leftCards.size()));
						ret.add(byte2);
					}
				}
			}
		}
		return ret;
	}

	/**
	 * 检测增加后的炸弹数量
	 * @param ret
	 * @return
	 */
	private int checkCount(ArrayList<Integer> ret) {
		final boolean needLimit3A = this.zRoom.isWanFaByType(PDK_WANFA.PDK_WANFA_3AZHA)?true:false;
		Map<Integer, List<Integer>>  cardMaps = ret.stream().collect(Collectors.groupingBy(p -> BasePocker.getCardValue(p)));
		List<Integer> cardList = cardMaps.entrySet().stream().filter(n -> (needLimit3A && (n.getKey() == BasePocker.getCardValue(0x1E) && n.getValue().size() >= 3)) || (n.getValue().size() >= 4)).map(k -> k.getKey()).collect(Collectors.toList());
		int count = CollectionUtils.isNotEmpty(cardList)?cardList.size():0;
		return count;
	}

	private void randomCardNoBombCard(ArrayList<Integer> ret) {
		final boolean needLimit3A = this.zRoom.isWanFaByType(PDK_WANFA.PDK_WANFA_3AZHA)?true:false;
		Map<Integer, List<Integer>> cardMaps = ret.stream().collect(Collectors.groupingBy(p -> BasePocker.getCardValue(p)));
		List<Integer> cardList = cardMaps.entrySet().stream().filter(n -> (needLimit3A && (n.getKey() == BasePocker.getCardValue(0x1E) && n.getValue().size() >= 2)) || (n.getValue().size() >= 3)).map(k -> k.getKey()).collect(Collectors.toList());
		if(CollectionUtils.isNotEmpty(cardList)){
			List<Integer> filterCardList = this.leftCards.stream().filter(n -> !cardList.contains(BasePocker.getCardValue(n))).collect(Collectors.toList());
			if(CollectionUtils.isNotEmpty(filterCardList)){
				Integer removeCard = filterCardList.remove(new Random().nextInt(filterCardList.size()));
				ret.add(removeCard);
				this.leftCards.remove(removeCard);
				return;
			}
		}
		Integer removeCard = this.leftCards.remove(new Random().nextInt(this.leftCards.size()));
		ret.add(removeCard);
	}

	/**
	 * 发牌
	 * @param cnt
	 * @return
	 */
//	public Integer popCard(){
//		return this.leftCards.remove(random.nextInt(this.leftCards.size()));
//	}

	/**
	 * 获取牌
	 */
	@SuppressWarnings("unchecked")
	public void randomCard(){
		int cardNum = zRoom.getRoomCfg().shoupai > 2 ? 2 : 1;

		this.leftCards = BasePockerLogic.getRandomPockerList(cardNum, 0, PockerListType.POCKERLISTTYPE_TWOEND);

		if (zRoom.isWanFaByType(PDK_WANFA.PDK_WANFA_QUSANZHANG)) {
			//52变48
			for (int byte1 : zRoom.getConfigMgr().getDeleteCard().get(zRoom.getRoomCfg().shoupai + 1)) {
				BasePockerLogic.deleteSameCard(this.leftCards, (byte) byte1, false);
			}

			//48变45
			for (int i = 0; i < 3; i++) {
				int index = random.nextInt(this.leftCards.size());
				int card = this.leftCards.get(index);
				// 不能去掉黑桃3
				if (card != 0x33) {
					this.leftCards.remove(index);
				}
			}
		} else {
			//去掉牌
			for (int byte1 : zRoom.getConfigMgr().getDeleteCard().get(zRoom.getRoomCfg().shoupai)) {
				BasePockerLogic.deleteSameCard(this.leftCards, (byte) byte1, false);
			}
		}

		this.backUpCards = (ArrayList<Integer>) this.leftCards.clone();
	}

	public ArrayList<Integer> getLeftCards() {
		return leftCards;
	}
}

