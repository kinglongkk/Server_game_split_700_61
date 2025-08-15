package business.global.pk.sss.newsss.comparing;

import business.global.pk.sss.newsss.PlayerDun;

import java.util.Map;

/**
 * 两对的大小比较(先比较第一对, 再比较第二对, 再比较单牌)
 */
public class TwoPairsComparingImpl extends AbstractComparing {

	@Override
	public int compare(PlayerDun o1, PlayerDun o2) {
		Map<Integer, Integer> p1CardMap = o1.getCardsRankCountMap();
		Map<Integer, Integer> p2CardMap = o2.getCardsRankCountMap();
		int ret = this.pairComparing(p1CardMap, p2CardMap, 2, 3);
		if (ret == 0 && o1.getGuiCount() > 0 && o2.getGuiCount() == 0) {
			return 1;
		} else if (ret == 0 && o2.getGuiCount() > 0 && o1.getGuiCount() == 0) {
			return -1;
		}
		return ret;
	}

}
