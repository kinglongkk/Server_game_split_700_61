package business.global.mj.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import business.global.mj.MJCard;
import business.global.mj.MJCardInit;
import business.global.mj.MJSetPos;
import cenum.mj.MJSpecialEnum;

/**
 * 对子
 * 
 * @author Huaxing
 *
 */
public class CheckHuUtil {
	/**
	 * 获取打牌列表
	 * @return
	 */
	public static List<Integer> OutCardList(MJSetPos mSetPos) {
		MJCardInit mInit = mSetPos.mjCardInit(true);
		if (null == mInit) {
			return null;
		}
		Map<Integer, Long> groupingByMap = mInit.getAllCardInts().stream()
				.collect(Collectors.groupingBy(p -> p, Collectors.counting()));
		if (null == groupingByMap || groupingByMap.size() <= 0) {
			return null;
		}
		List<Integer> allCardType = new ArrayList<>();
		// 获取key
		for (Entry<Integer, Long> map : groupingByMap.entrySet()) {
			allCardType.add(map.getKey());
		}
		// 左牌
		Integer left = 0;
		// 右牌
		Integer right = 0;
		// 尾值
		int mod = 0;
		// 当前牌值
		int value = 0;
		for (int i =0,size = allCardType.size();i< size;i++) {
			// 获取当前牌值
			value = allCardType.get(i);
			// >= 40 ,中发白。。。。
			if (value >= 40) {
				// 跳过
				continue;
			}
			// 获取尾值
			mod = value %10;
			// 如果是 > 1 , > 11 , 21 , 31  
			if (mod > 1) {
				// 计算当期牌值 -1
				left = value - 1;
				// 检查牌值是否存在
				if (!allCardType.contains(left)) {
					allCardType.add(left);
				}
			}
			// 计算当前牌值 + 1
			right = value + 1;
			// 检查牌值是否存在
			if (!allCardType.contains(right)) {
				allCardType.add(right);
			}
		}
		
		if (mSetPos.getSet().getmJinCardInfo().checkExistJin()) {
			int jinCard = mSetPos.getSet().getmJinCardInfo().getJinValues().get(0).getType();
			// 检查是否有金牌数
			if (allCardType.contains(jinCard)) {
				// 如果有金牌数,是否白板替金
//				if (mSetPos.getMJSetCard().isBaiJin()) {
					// 添加白板
					allCardType.add(MJSpecialEnum.BAIBAN.value());
//				}
			} else {
				// 添加金牌
				allCardType.add(jinCard);
			}
		}
		
		
		List<MJCard> aCards = mSetPos.getMJSetCard().getRandomCard().getLeftCards();
		if (null == aCards || aCards.size() <= 0) {
			return allCardType;
		}
		List<Integer> allList = new ArrayList<>();
		Iterator<MJCard> it = aCards.iterator(); // 创建迭代器
		MJCard mCard = null;
		while (it.hasNext()) { // 循环遍历迭代器
			mCard = it.next();
			if(null != mCard) {
				if (allCardType.contains(mCard.getType())) {
					allList.add(mCard.getType());
				}
			}

		}
		
		
//		System.out.println(allList);
		return allList;
	}
}
