package business.global.pk.sss.newsss.comparing;

import business.global.pk.sss.newsss.PlayerDun;
import business.sss.c2s.cclass.newsss.PockerCard;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class AbstractComparing implements IComparing {

	/**
	 * 按照三条或者或者四条的比较
	 *
	 * @param map1
	 * @param map2
	 * @param pair
	 * @return
	 */
	protected int multiComparing(Map<Integer, Integer> map1, Map<Integer, Integer> map2, int pair) {

		int p1Number = -1;
		int p2Number = -1;

		Iterator<Map.Entry<Integer, Integer>> it = map1.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Integer> next = it.next();
			if (next.getValue() == pair) {
				p1Number = next.getKey();
			}
		}

		Iterator<Map.Entry<Integer, Integer>> it2 = map2.entrySet().iterator();
		while (it2.hasNext()) {
			Map.Entry<Integer, Integer> next = it2.next();
			if (next.getValue() == pair) {
				p2Number = next.getKey();
			}
		}

		if (p1Number > p2Number) {
			return -1;
		}
		if (p1Number < p2Number) {
			return 1;
		}

		return 0;
	}

	/**
	 * 按照顺序的比较
	 *
	 * @param p1
	 * @param p2
	 * @return
	 */
	protected int seqComparing(PlayerDun p1, PlayerDun p2) {
		List<PockerCard> p1Cards = p1.getCards();
		List<PockerCard> p2Cards = p2.getCards();

		int size = p1.getCardSize();

		for (int i = 0; i < size; i++) {
			if (p1Cards.get(i).getRank().getNumber() < p2Cards.get(i).getRank().getNumber()) {
				return 1;
			}
			if (p1Cards.get(i).getRank().getNumber() > p2Cards.get(i).getRank().getNumber()) {
				return -1;
			}
			if (p1Cards.get(i).getRank().getNumber().equals(p2Cards.get(i).getRank().getNumber())) {
				continue;
			}
		}
		return 0;
	}
	
	/**
	 * 按照顺序的比较
	 *
	 * @param p1
	 * @param p2
	 * @return
	 */
	protected int seqComparing(List<PockerCard> p1Cards, List<PockerCard> p2Cards) {
		int size = p1Cards.size();

		for (int i = 0; i < size; i++) {
			if (p1Cards.get(i).getRank().getNumber() < p2Cards.get(i).getRank().getNumber()) {
				return 1;
			}
			if (p1Cards.get(i).getRank().getNumber() > p2Cards.get(i).getRank().getNumber()) {
				return -1;
			}
			if (p1Cards.get(i).getRank().getNumber().equals(p2Cards.get(i).getRank().getNumber())) {
				continue;
			}
		}
		return 0;
	}

	/**
	 * @param map1
	 * @param map2
	 * @param pair
	 *            对子的数量
	 * @param maxPairLoopAddOne
	 *            对子最大的循环数量+1
	 * @return
	 */
	protected int pairComparing(Map<Integer, Integer> map1, Map<Integer, Integer> map2, int pair,
			int maxPairLoopAddOne) {
		if (maxPairLoopAddOne - 1 == 0) {
			pair = 1;
		}
		int p1MaxNum = this.findMaxNumber(map1, pair);
		int p2MaxNum = this.findMaxNumber(map2, pair);

		if (p1MaxNum < p2MaxNum) {
			return 1;
		}
		if (p1MaxNum > p2MaxNum) {
			return -1;
		}
		if (p1MaxNum == p2MaxNum) {
			map1.remove(p1MaxNum);
			map2.remove(p2MaxNum);
			if (map1.size() == 0 && map2.size() == 0) {
				return 0;
			}
			if (map1.size() == map2.size() && 0 == maxPairLoopAddOne - 1) {
				return this.pairComparing(map1, map2, pair - 1, 1);
			}
			return this.pairComparing(map1, map2, pair, maxPairLoopAddOne - 1);
		}
		return 0;
	}

	public Map<Integer, Integer> getCardsRankCountMap(List<PockerCard> cards) {
		Map<Integer, Integer> rankCount = new HashMap<Integer, Integer>();
		for (PockerCard PockerCard : cards) {
			Integer number = new Integer(PockerCard.getRank().getNumber());
			if (!rankCount.containsKey(number)) {
				rankCount.put(number, 1);
			} else {
				rankCount.put(number, rankCount.get(number) + 1);
			}
		}
		return rankCount;
	}

	public int findMaxNumber(Map<Integer, Integer> map, int pair) {
		int p1Number = -1;

		Iterator<Map.Entry<Integer, Integer>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Integer> next = it.next();
			if (next.getValue() == pair) {
				int number = next.getKey();
				if (number > p1Number) {
					p1Number = number;
				}
			}
		}
		return p1Number;

	}

}
