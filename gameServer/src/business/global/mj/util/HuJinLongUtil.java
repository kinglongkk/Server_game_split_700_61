package business.global.mj.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ddm.server.common.utils.CommString;

import business.global.mj.MJCardInit;

public class HuJinLongUtil {

	private volatile static HuJinLongUtil singleton;

	private HuJinLongUtil() {
	}

	public static HuJinLongUtil getInstance() {
		if (singleton == null) {
			synchronized (HuJinLongUtil.class) {
				if (singleton == null) {
					singleton = new HuJinLongUtil();
				}
			}
		}
		return singleton;
	}

	/**
	 * 胡
	 * @param allCardInts
	 * @param Jins
	 * @param totalJin
	 * @param cardType
	 * @return
	 */
	public boolean checkHu(MJCardInit mCardInit) {		
		List<Integer> allCards = mCardInit.getAllCardInts();
		Collections.sort(allCards, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
            	// 从小到大
                return o1 - o2;
            }
        });
		return check3nP2(allCards,mCardInit.sizeJin());
	}
	

	/**
	 * 胡
	 * @param allCardInts
	 * @param Jins
	 * @param totalJin
	 * @param cardType
	 * @return
	 */
	public boolean checkHu(List<Integer> allCards, int totalJin) {		
		Collections.sort(allCards, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
            	// 从小到大
                return o1 - o2;
            }
        });
		return check3nP2(allCards,totalJin);
	}

	
	public boolean check3nP2(List<Integer> allCards, int totalJin) {
		// 小于3个金
		if (totalJin < 3) {
			return false;
		}

		if (allCards.size() <= 0 && totalJin > 0) {
            return true;
        }

		List<String> resultList = new ArrayList<>();
		List<Integer> typeList = new ArrayList<>(); // 计算牌的品类
		int lastType = -1;
		for (int type : allCards) {
			if (type != lastType) {
				typeList.add(type);
				lastType = type;
			}
		}

		// 遍历每种牌
		for (Integer type : typeList) {
			List<Integer> dui = new ArrayList<>();
			List<Integer> leftCards = new ArrayList<>(allCards);
			leftCards.remove(type);
			dui.add(type);
			int leftJin = totalJin - 3;
			if (leftCards.contains(type)) {
				leftCards.remove(type);
				dui.add(type);
			} else {
				if (leftJin == 0) {
					continue;
				}
				leftJin -= 1;
				dui.add(0);
			}

			List<List<Integer>> matched = new ArrayList<>();
			//
			matchThird(resultList, 0, dui, matched, leftJin, leftCards);
		}

		return resultList.size() > 0;
	}

	public void matchThird(List<String> resultList, int tabCnt,
			List<Integer> dui, List<List<Integer>> matched, int leftJin,
			List<Integer> leftCards) {
		if (leftCards.size() == 0 && leftJin % 3 == 0) {
			String end = CommString.join(",", dui);
			for (List<Integer> match : matched) {
				end += "  :  " + CommString.join(",", match);
			}
			for (int i = 1; i <= leftJin / 3; i++) {
				end += "  :  " + "0,0,0";
			}
			if (!resultList.contains(end)) {
                resultList.add(end);
            }
			return;
		}

		if (leftCards.size() == 0) {
			return;
		}
		// 取一个值
		Integer type = leftCards.get(0);
		leftCards.remove(type);

		// 尝试匹配 3type
		List<Integer> newMatched = new ArrayList<>();
		List<Integer> newLeftCard = matchKe(new ArrayList<>(leftCards), type,
				leftJin, newMatched);
		if (null != newLeftCard) {
			List<List<Integer>> newMatchList = new ArrayList<>(matched);
			newMatchList.add(newMatched);
			int newLeftJin = leftJin - calcNum(newMatched, 0);
			matchThird(resultList, tabCnt + 1, dui, newMatchList, newLeftJin,
					newLeftCard);
		}
		// 尝试匹配 shun1
		List<Integer> newMatched1 = new ArrayList<>();
		List<Integer> newLeftCard1 = matchShunLeft(new ArrayList<>(leftCards),
				type, 0, newMatched1);
		if (null != newLeftCard1) {
			List<List<Integer>> newMatchList1 = new ArrayList<>(matched);
			newMatchList1.add(newMatched1);
			int newLeftJin = leftJin - calcNum(newMatched1, 0);
			matchThird(resultList, tabCnt + 1, dui, newMatchList1, newLeftJin,
					newLeftCard1);
		}
		// 尝试匹配 shun2
		List<Integer> newMatched2 = new ArrayList<>();
		List<Integer> newLeftCard2 = matchShunMid(new ArrayList<>(leftCards),
				type, 0, newMatched2);
		if (null != newLeftCard2) {
			List<List<Integer>> newMatchList2 = new ArrayList<>(matched);
			newMatchList2.add(newMatched2);
			int newLeftJin = leftJin - calcNum(newMatched2, 0);
			matchThird(resultList, tabCnt + 1, dui, newMatchList2, newLeftJin,
					newLeftCard2);
		}
		// 尝试匹配 shun3
		List<Integer> newMatched3 = new ArrayList<>();
		List<Integer> newLeftCard3 = matchShunRight(new ArrayList<>(leftCards),
				type, 0, newMatched3);
		if (null != newLeftCard3) {
			List<List<Integer>> newMatchList3 = new ArrayList<>(matched);
			newMatchList3.add(newMatched3);
			int newLeftJin = leftJin - calcNum(newMatched3, 0);
			matchThird(resultList, tabCnt + 1, dui, newMatchList3, newLeftJin,
					newLeftCard3);
		}
	}

	public List<Integer> matchKe(List<Integer> srcCards, Integer type, int jin,
			List<Integer> newMatched) {
		newMatched.add(type);
		if (srcCards.contains(type)) {
			srcCards.remove(type);
			newMatched.add(type);
		} else {
			jin -= 1;
			newMatched.add(0);
		}
		if (srcCards.contains(type)) {
			srcCards.remove(type);
			newMatched.add(type);
		} else {
			jin -= 1;
			newMatched.add(0);
		}
		return jin >= 0 ? srcCards : null;
	}

	public List<Integer> matchShunLeft(List<Integer> srcCards, Integer type,
			int jin, List<Integer> newMatched) {
		if (type > 40) {
			return null;
		}

		if (type % 10 + 1 > 9 || type % 10 + 2 > 9) {
            return null;
        }

		newMatched.add(type);
		Integer right1 = type + 1;
		if (srcCards.contains(right1)) {
			srcCards.remove(right1);
			newMatched.add(right1);
		} else {
			jin -= 1;
			newMatched.add(0);
		}

		Integer right2 = type + 2;
		if (srcCards.contains(right2)) {
			srcCards.remove(right2);
			newMatched.add(right2);
		} else {
			jin -= 1;
			newMatched.add(0);
		}
		return jin >= 0 ? srcCards : null;
	}

	public List<Integer> matchShunMid(List<Integer> srcCards, Integer type,
			int jin, List<Integer> newMatched) {
		if (type > 40) {
			return null;
		}
		if (type % 10 + 1 > 9 || type % 10 - 1 < 1) {
            return null;
        }

		newMatched.add(type);
		Integer right1 = type + 1;
		if (srcCards.contains(right1)) {
			srcCards.remove(right1);
			newMatched.add(right1);
		} else {
			jin -= 1;
			newMatched.add(0);
		}

		Integer left1 = type - 1;
		if (srcCards.contains(left1)) {
			srcCards.remove(left1);
			newMatched.add(left1);
		} else {
			jin -= 1;
			newMatched.add(0);
		}
		return jin >= 0 ? srcCards : null;
	}

	public List<Integer> matchShunRight(List<Integer> srcCards, Integer type,
			int jin, List<Integer> newMatched) {
		if (type > 40) {
			return null;
		}
		if (type % 10 - 1 < 1 || type % 10 - 2 < 1) {
            return null;
        }

		newMatched.add(type);
		Integer left1 = type - 1;
		if (srcCards.contains(left1)) {
			srcCards.remove(left1);
			newMatched.add(left1);
		} else {
			jin -= 1;
			newMatched.add(0);
		}

		Integer left2 = type - 2;
		if (srcCards.contains(left2)) {
			srcCards.remove(left2);
			newMatched.add(left2);
		} else {
			jin -= 1;
			newMatched.add(0);
		}

		return jin >= 0 ? srcCards : null;
	}

	public int calcNum(List<Integer> total, int match) {
		int ret = 0;

		for (int i : total) {
			if (i == match) {
                ret += 1;
            }
		}

		return ret;
	}
}
