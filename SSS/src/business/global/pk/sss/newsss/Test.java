package business.global.pk.sss.newsss;

import business.global.pk.sss.newsss.ranking.RankingFacade;
import business.sss.c2s.cclass.newsss.CardSuitEnum;
import business.sss.c2s.cclass.newsss.Constants;
import business.sss.c2s.cclass.newsss.PockerCard;
import jsproto.c2s.cclass.pk.BasePockerLogic;


import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Test {
	
//	RankingEnum {
//		HIGH_CARD(0,"乌龙",1 ), 
//		ONE_PAIR(1, "一对",1), 
//		TWO_PAIR(2, "两对",1), 
//		THREE_OF_THE_KIND(3, "三条",1), 
//		STRAIGHT(4, "顺子",1), 
//		FLUSH(5, "同花",1), 
//		FLUSH_ONE_PAIR(6, "一对同花",1), //新增
//		FLUSH_TWO_PAIR(7, "两对同花",1), //新增
//		FULL_HOUSE(8, "葫芦",1), 
//		FOUR_OF_THE_KIND(9, "铁支",4), 
//		STRAIGHT_FLUSH(10, "同花顺",5), 
//		FIVE_OF_THE_KIND(11, "五同",10), //新增
	
	
	
	
	public boolean testResult(List<PockerCard> ret) {
		PlayerData playerData = new PlayerData(0, 0, 0);
		int res = playerData.setPlayerCards(ret);
		if (res > 80) {
			return true;
		}
		List<PockerCard> cards = new ArrayList<>();
		List<PockerCard> guiList = new ArrayList<>();
		for (int k = 0; k < ret.size(); k++) {
			if (ret.get(k).getSuit() != CardSuitEnum.GUI) {
				cards.add(ret.get(k));
			} else {
				guiList.add(ret.get(k));
			}
		}
		HashMap<Integer,List<PockerCard>> map = new HashMap<>();
		// 获取五同
		List<List<PockerCard>> pockerCardList = this.getSameCardList(new ArrayList<>(cards), new ArrayList<>(guiList), 5, true,false);
		if(null != pockerCardList &&  pockerCardList.size() > 0) {
			for (List<PockerCard> list : pockerCardList){
				if (this.addMap(map, list)) {
					this.removeCard(cards, guiList, list); 
				}
			}
		}
		
		// 获取同花顺
		pockerCardList = this.getTongHuaShunzi(new ArrayList<>(cards), new ArrayList<>(guiList));
		if(null != pockerCardList &&  pockerCardList.size() > 0) {
			for (List<PockerCard> list : pockerCardList){
				if (this.addMap(map, list)) {
					this.removeCard(cards, guiList, list); 
				}
			}
		}

		
		// 获取炸弹
		pockerCardList = this.getSameCardList(new ArrayList<>(cards), new ArrayList<>(guiList), 4, true,false);
		if(null != pockerCardList &&  pockerCardList.size() > 0) {
			for (List<PockerCard> list : pockerCardList){
				if (this.addMap(map, list)) {
					this.removeCard(cards, guiList, list); 
				}
			}
		}
		
		// 获取葫芦
		pockerCardList = this.getHuluDui(new ArrayList<>(cards), new ArrayList<>(guiList), new ArrayList<>());
		if(null != pockerCardList &&  pockerCardList.size() > 0) {
			for (List<PockerCard> list : pockerCardList){
				if (this.addMap(map, list)) {
					this.removeCard(cards, guiList, list); 
				}
			}
		}
		
		// 获取葫芦
		pockerCardList = this.getHulu(new ArrayList<>(cards), new ArrayList<>(guiList), new ArrayList<>());
		if(null != pockerCardList &&  pockerCardList.size() > 0) {
			for (List<PockerCard> list : pockerCardList){
				if (this.addMap(map, list)) {
					this.removeCard(cards, guiList, list); 
				}
			}
		}
		
		// 获取同花
		pockerCardList = this.getSameCardList(new ArrayList<>(cards), new ArrayList<>(guiList), 5, false,false);
		if(null != pockerCardList &&  pockerCardList.size() > 0) {
			for (List<PockerCard> list : pockerCardList){
				if (this.addMap(map, list)) {
					this.removeCard(cards, guiList, list); 
				}
			}
		}
		
		// 获取顺子
		pockerCardList = this.getShunzi(new ArrayList<>(cards), new ArrayList<>(guiList));
		if(null != pockerCardList &&  pockerCardList.size() > 0) {
			for (List<PockerCard> list : pockerCardList){
				if (this.addMap(map, list)) {
					this.removeCard(cards, guiList, list); 
				}
			}
		}
		
		// 获取三条
		pockerCardList = this.getSameCardList(new ArrayList<>(cards), new ArrayList<>(guiList), 3, true,false);
		if(null != pockerCardList &&  pockerCardList.size() > 0) {
			for (List<PockerCard> list : pockerCardList){
				if (this.addMap(map, list)) {
					this.removeCard(cards, guiList, list); 
				}
			}
		}
		
		// 获取两对
		pockerCardList = getLiangDui(new ArrayList<>(cards), new ArrayList<>(guiList), new ArrayList<>());
		if(null != pockerCardList &&  pockerCardList.size() > 0) {
			for (List<PockerCard> list : pockerCardList){
				if (this.addMap(map, list)) {
					this.removeCard(cards, guiList, list); 
				}
			}
		}
		
		// 获取对子
		pockerCardList = this.getSameCardList(new ArrayList<>(cards), new ArrayList<>(guiList), 2, true,false);
		if(null != pockerCardList &&  pockerCardList.size() > 0) {
			for (List<PockerCard> list : pockerCardList){
				if (this.addMap(map, list)) {
					this.removeCard(cards, guiList, list); 
				}
			}
		}
		
		cards.addAll(guiList);
		// 获取单张牌
		if (map.size() <= 1) {
			// 从大到小
			cards.sort((PockerCard itme1, PockerCard itme2) -> itme2.cardID - itme1.cardID);			
		} else {
			// 从小到大
			cards.sort((PockerCard itme1, PockerCard itme2) -> itme1.cardID - itme2.cardID);
		}

		List<PockerCard> list = null;
		for (PockerCard pCard : cards) {
			list = new ArrayList<>();
			list.add(pCard);
			this.addMap(map, list);
			
		}		
		
		if (!playerData.setCards(map)) {
			List<PockerCard> sPCards = map.get(3);
			List<PockerCard> ePCards = map.get(2);
			map.put(2, sPCards);
			map.put(3, ePCards);
			if (!playerData.setCards(map)) {
				System.out.println(map.toString());
				return false;
			}
			
			
		}
		return true;
	}
	
	
	private boolean addMap (HashMap<Integer,List<PockerCard>> map,List<PockerCard> cards) {
		List<PockerCard> sPCards = map.get(3);
		if (null == sPCards || sPCards.size() <= 0) {
			map.put(3, cards);
			return true;
		} else {
			if(sPCards.size() + cards.size() <= 5) {
				sPCards.addAll(cards);
				map.put(3, sPCards);
				return true;
			}
		}
		
		List<PockerCard> ePCards = map.get(2);
		if (null == ePCards || ePCards.size() <= 0) {
			map.put(2, cards);
			return true;
		} else {
			if(ePCards.size() + cards.size() <= 5) {
				ePCards.addAll(cards);
				map.put(2, ePCards);
				return true;
			}
		}
		
		List<PockerCard> yPCards = map.get(1);
		if (null == yPCards || yPCards.size() <= 0) {
			if (cards.size() <= 3) {
				map.put(1, cards);
				return true;
			}
		} else {
			if(yPCards.size() + cards.size() <= 3) {
				yPCards.addAll(cards);
				map.put(1, yPCards);
				return true;
			}
		}
		
		return false;
	}
	
	
	
	/**
	 * 获取特殊牌
	 * @param cards
	 * @return
	 */
	public int setPlayerCards(List<PockerCard> cards) {
		PlayerDun allCards = new PlayerDun();
		allCards.addData(cards);
		RankingFacade.getInstance().resolve(allCards);
		return allCards.getRankingResult().getRankingEnum().getPriority();
	}
	
	
	/**
	 * 获取相同牌的列表
	 * @param cards 手上的牌
	 * @param guiCards 鬼牌 
	 * @param sameCount 相同牌的张数
	 * @param isCardID 是否按牌ID分组， T: 按 cardID,F:按type
	 * @param asc T 升序（1..2）,F降序（2..1）
	 * @return
	 */
	public List<List<PockerCard>> getSameCardList(List<PockerCard> cards,List<PockerCard> guiCards,int sameCount,boolean isCardID,boolean asc) {
		// 鬼数量
		int guiCount = guiCards.size();
		// 牌分组。
		Map<Integer, List<PockerCard>> map = cards.stream()
				.collect(Collectors.groupingBy(p -> isCardID?p.cardID:p.type));
		// 检查map数据是否存在。
		if (null == map || map.size() <= 0) {
			return null;
		}		
		// 结果列表
		List<List<PockerCard>> resultList = new ArrayList<>();
		List<Entry<Integer, List<PockerCard>>> mapList = new ArrayList<>(map.entrySet());
		// 按Key排序
		mapList.sort((Entry<Integer, List<PockerCard>> itme1, Entry<Integer, List<PockerCard>> itme2) -> asc ? itme1.getKey() - itme2.getKey() : itme2.getKey() - itme1.getKey());
		// 遍历map列表 key::value
		for (Entry<Integer, List<PockerCard>> value:mapList) {
			int count = value.getValue().size();
			if (count + guiCount >= sameCount) {
				if (guiCount <= 0 || count >= sameCount ) {
					// 没有鬼牌
					// 记录结果
					resultList.add(value.getValue().subList(0, sameCount));
				} else {
					// 遍历检查 使用几张鬼牌 ，符合牌型。
					for (int i = 1,size = guiCount;i<=size;i++) {
						if (count + i == sameCount) {
							List<PockerCard> guiCard = new ArrayList<>();
							guiCard.addAll(value.getValue());
							guiCard.addAll(guiCards.subList(0, i));
							if (i == 1) {
								guiCards.remove(0);
							}
							guiCount-=i;
							// 记录结果
							resultList.add(guiCard);
							break;
						}
					}
				}				
			}
		}
		// 返回检查的结果列表
		return resultList;	
	}
	
	
	/**
	 * 获取对子
	 * @param cards 手上的牌
	 * @param guiCards 鬼牌 
	 * @param sameCount 相同牌的张数
	 * @param isCardID 是否按牌ID分组， T: 按 cardID,F:按type
	 * @param asc T 升序（1..2）,F降序（2..1）
	 * @return
	 */
	public List<List<PockerCard>> getDuiZi(List<PockerCard> cards,List<PockerCard> guiCards,int sameCount,boolean isCardID,boolean asc) {
		// 鬼数量
		int guiCount = guiCards.size();
		// 牌分组。
		Map<Integer, List<PockerCard>> map = cards.stream()
				.collect(Collectors.groupingBy(p -> isCardID?p.cardID:p.type));
		// 检查map数据是否存在。
		if (null == map || map.size() <= 0) {
			return null;
		}		
		// 结果列表
		List<List<PockerCard>> resultList = new ArrayList<>();
		List<Entry<Integer, List<PockerCard>>> mapList = new ArrayList<>(map.entrySet());
		// 按Key排序
		mapList.sort((Entry<Integer, List<PockerCard>> itme1, Entry<Integer, List<PockerCard>> itme2) -> asc ? itme1.getKey() - itme2.getKey() : itme2.getKey() - itme1.getKey());
		// 遍历map列表 key::value
		for (Entry<Integer, List<PockerCard>> value:mapList) {
			int count = value.getValue().size();
			if (count + guiCount >= sameCount) {
				if (count == sameCount ) {
					// 没有鬼牌
					// 记录结果
					resultList.add(value.getValue().subList(0, sameCount));
				} else {
					// 遍历检查 使用几张鬼牌 ，符合牌型。
					for (int i = 1,size = guiCount;i<=size;i++) {
						if (count + i == sameCount) {
							List<PockerCard> guiCard = new ArrayList<>();
							guiCard.addAll(value.getValue());
							guiCard.addAll(guiCards.subList(0, i));
							if (i == 1) {
								guiCards.remove(0);
							}
							guiCount-=i;
							// 记录结果
							resultList.add(guiCard);
							break;
						}
					}
				}				
			}
		}
		// 返回检查的结果列表
		return resultList;	
	}

	
	
	/**
	 * 获取葫芦
	 * @param cards
	 * @param guiList
	 * @return
	 */
	public List<List<PockerCard>> getHuluDui(List<PockerCard> cards,List<PockerCard> guiCards,List<List<PockerCard>> resultList) {		
		// 三条
		List<List<PockerCard>> sanList = this.getSameCardList(cards, guiCards,3,true,false);
		if (null == sanList || sanList.size() <= 0) {
			return resultList;
		}
		this.removeCard(cards, guiCards, sanList.get(0));
		// 两对
		List<List<PockerCard>> liangList = this.getDuiZi(cards, guiCards,2,true,true);
		if (null == liangList || liangList.size() <= 0) {
			return resultList;
		}
		this.removeCard(cards, guiCards, liangList.get(0));
		// 拼成葫芦
		List<PockerCard> pockerCards = new ArrayList<>();
		pockerCards.addAll(sanList.get(0));
		pockerCards.addAll(liangList.get(0));
		// 记录结果
		resultList.add(pockerCards);
		return getHuluDui(cards, guiCards,resultList);	
	}
	
	/**
	 * 获取葫芦
	 * @param cards
	 * @param guiList
	 * @return
	 */
	public List<List<PockerCard>> getHulu(List<PockerCard> cards,List<PockerCard> guiCards,List<List<PockerCard>> resultList) {		
		// 三条
		List<List<PockerCard>> sanList = this.getSameCardList(cards, guiCards,3,true,false);
		if (null == sanList || sanList.size() <= 0) {
			return resultList;
		}
		this.removeCard(cards, guiCards, sanList.get(0));
		// 两对
		List<List<PockerCard>> liangList = this.getSameCardList(cards, guiCards,2,true,true);
		if (null == liangList || liangList.size() <= 0) {
			return resultList;
		}
		this.removeCard(cards, guiCards, liangList.get(0));
		// 拼成葫芦
		List<PockerCard> pockerCards = new ArrayList<>();
		pockerCards.addAll(sanList.get(0));
		pockerCards.addAll(liangList.get(0));
		// 记录结果
		resultList.add(pockerCards);
		return getHulu(cards, guiCards,resultList);	
	}

	
	
	/**
	 * 获取同花顺
	 * @param cards
	 * @param guiList
	 * @param resultList
	 * @return
	 */
	public List<List<PockerCard>> getTongHuaShunzi(List<PockerCard> cards,List<PockerCard> guiList) {
		// 同花列表
		List<List<PockerCard>> tongHuaList = this.getSameCardList(new ArrayList<>(cards), new ArrayList<>(guiList),5,false,false);
		if (null == tongHuaList || tongHuaList.size() <= 0) {
			return null;
		}
		// 结果同花顺列表
		List<List<PockerCard>> resultList = new ArrayList<>();
		// 记录顺子结果列表
		List<List<PockerCard>> shunZi = null;
		// 同-普通牌
		List<PockerCard> tCards = null;
		// 同-鬼牌
		List<PockerCard> tGuiList  = null;
		for (List<PockerCard> list : tongHuaList) {
			tCards = new ArrayList<>();
			tGuiList = new ArrayList<>();
			for (int k = 0; k < list.size(); k++) {
				if (list.get(k).getSuit() != CardSuitEnum.GUI) {
					tCards.add(list.get(k));
				} else {
					tGuiList.add(list.get(k));
				}
			}
			// 检查是否顺子
			shunZi = this.getShunzi(new ArrayList<>(tCards), new ArrayList<>(tGuiList));
			if (null == shunZi ||shunZi.size() <= 0) {
				continue;
			}
			// 记录同花顺。
			resultList.addAll(shunZi);
		}
		return resultList;
	}
	
	
	/**
	 * 记录顺子。
	 * @param cards
	 * @param guiCards
	 * @return
	 */
	public List<List<PockerCard>> getShunzi(List<PockerCard> cards,List<PockerCard> guiCards) {
		// 鬼数量
		int guiCount = guiCards.size();
		// 牌分组。
		Map<Integer, List<PockerCard>> map = cards.stream()
				.collect(Collectors.groupingBy(p -> p.cardID));
		// 检查map数据是否存在。
		if (null == map || (map.size()+guiCount) < 5) {
			return null;
		}
		List<Integer> cardKeyList = new ArrayList<>();
		cardKeyList.addAll(map.keySet());
		BasePockerLogic.getSort(cardKeyList, false);
		List<List<PockerCard>> resultList = new ArrayList<>();
		for (int i = 0,size=cardKeyList.size() ;i<size;i++) {
			int toIndex = (5-guiCount)+i;
			if (toIndex > size) {
				break;
			}
			List<Integer> keyList = cardKeyList.subList(0+i, toIndex);
	        // 获取列表中最大值
	        int maxCard = keyList.get(0);
	        // 获取列表中最小值
	        int minCard = keyList.get(keyList.size()-1);
			
	        int value = Math.abs(maxCard - minCard);
	        if (value > (maxCard-1)) {
	        	continue;
	        }
	        List<Integer> cardList = new ArrayList<>();
	        // 遍历从最小值+i,计算获取列表
	        for (int j =0;j<5;j++ ) {
	        	// 获取最小值顺子列表
	        	cardList.add((minCard+j));
	        }
	        // 检查是否有符合的列表
	        if (!cardList.containsAll(keyList)) {
	        	continue;
	        }
	        BasePockerLogic.getSort(cardList, false);
            // 检查是否顺子
            if (!BasePockerLogic.isContinuous(cardList)){
            	continue;
            }
            // 获取牌ID
            List<PockerCard> list = this.pockerCardList(cards, keyList);
            if (guiCount > 0 ) {
            	list.addAll(guiCards.subList(0, guiCount));      
            	guiCount = 0;
            }
    
            if (list.size() == 5) {
            	resultList.add(list);
            }
		}
		return resultList;	
	}

	
	
	/**
	 * 获取连对
	 * @param cards
	 * @param guiList
	 * @return
	 */
	public List<List<PockerCard>> getLiangDui(List<PockerCard> cards,List<PockerCard> guiCards,List<List<PockerCard>> resultList) {		
		// 两对，大
		List<List<PockerCard>> maxList = this.getSameCardList(cards, guiCards,2,true,false);
		if (null == maxList || maxList.size() <= 0) {
			return resultList;
		}
		this.removeCard(cards, guiCards, maxList.get(0));
		// 两对，小
		List<List<PockerCard>> minList = this.getSameCardList(cards, guiCards,2,true,true);
		if (null == minList || minList.size() <= 0) {
			return resultList;
		}
		this.removeCard(cards, guiCards, minList.get(0));
		// 拼成葫芦
		List<PockerCard> pockerCards = new ArrayList<>();
		pockerCards.addAll(maxList.get(0));
		pockerCards.addAll(minList.get(0));
		// 记录结果
		resultList.add(pockerCards);
		return getHulu(cards, guiCards,resultList);	
	}

	
	/**
	 * 获取牌ID
	 * @param cards
	 * @param guiList
	 * @param resultList
	 */
	public List<PockerCard> pockerCardList(List<PockerCard> cards,List<Integer> resultList) {
		int rCard = -1;
		List<PockerCard> cardList = new ArrayList<>();
		for (int i = 0,size =resultList.size();i<size;i++) {
			rCard = resultList.get(i);
			// 遍历手上的牌
			Iterator<PockerCard> cIt = cards.iterator();
			while(cIt.hasNext()){
				PockerCard cCard = cIt.next();
			    if(rCard == cCard.cardID){
			    	cardList.add(cCard);
			    	cIt.remove();
			    	break;
			    }
			}
		}
		return cardList;
	}

	

	
	/**
	 * 移除牌
	 * @param cards
	 * @param guiList
	 * @param resultList
	 */
	public void removeCard(List<PockerCard> cards,List<PockerCard> guiList,List<PockerCard> resultList) {
		PockerCard rCard = null;
		for (int i = 0,size =resultList.size();i<size;i++) {
			rCard = resultList.get(i);
			// 遍历鬼牌
			Iterator<PockerCard> gIt = guiList.iterator();
			while(gIt.hasNext()){
				PockerCard gCard = gIt.next();
			    if(rCard.getKey().equals(gCard.getKey())){
			    	gIt.remove();
			    }
			}
			// 遍历手上的牌
			Iterator<PockerCard> cIt = cards.iterator();
			while(cIt.hasNext()){
				PockerCard cCard = cIt.next();
			    if(rCard.getKey().equals(cCard.getKey())){
			    	cIt.remove();
			    }
			}
		}
	}

	
	public static void main(String[] args) {
		List<Integer> huase = Arrays.asList(0,1,2,3);
		for (int i =0;i < 9000000;i++) {
			Poker poker = new Poker(8, huase, true, false, 0);
			for (int q =0;q < 8;q++) {
				
				List<PockerCard> ret = new ArrayList<PockerCard>();
				for (int j = 0; j < Constants.HAND_CARD_NUMBER; j++) {
					ret.add(poker.dispatch());
				}
				
				Test test = new Test();
				if (!test.testResult(new ArrayList<>(ret))) {
					System.out.println(ret.toString());
				}
			}
	
		}
		
		
//		List<PockerCard> cards = new ArrayList<PockerCard>();
//		List<String> list = Arrays.asList("0x42", "0x1d", "0x13", "0x16", "0x07", "0x184", "0x3e3", "0x134", "0x3c3", "0x33", "0x23", "0x124", "0x231");
//		for (int i = 0; i < list.size(); i++) {
//			cards.add(new PockerCard(list.get(i)));
//		}
//		Collections.sort(cards);
//		Test test = new Test();
//		if (!test.testResult(new ArrayList<>(cards))) {
//			System.out.println(cards.toString());
//		}
	}
	
	
}
