package business.global.pk.sss.newsss.comparing;

import business.sss.c2s.cclass.newsss.RankingEnum;

import java.util.HashMap;
import java.util.Map;

public class ComparingFacade {

	private final static Map<RankingEnum, IComparing> maps = new HashMap<RankingEnum, IComparing>();
	private final static IComparing defaultComparing = new DefaultComparingImpl();

	static {
		maps.put(RankingEnum.FIVE_OF_THE_KIND, new FiveOfTheKindComparingImpl());
		maps.put(RankingEnum.STRAIGHT_FLUSH, new StraightFlushComparingImpl());
		maps.put(RankingEnum.FOUR_OF_THE_KIND, new FourOfTheKindComparingImpl());
		maps.put(RankingEnum.FULL_HOUSE, new FullHouseComparingImpl());
		maps.put(RankingEnum.FLUSH_TWO_PAIR, new TwoPairFlushComparingImpl());
		maps.put(RankingEnum.FLUSH_ONE_PAIR, new OnePairFlushComparingImpl());
		maps.put(RankingEnum.FLUSH, new FlushComparingImpl());
		maps.put(RankingEnum.STRAIGHT, new StraightComparingImpl());
		maps.put(RankingEnum.THREE_OF_THE_KIND, new ThreeOfTheKindComparingImpl());
		maps.put(RankingEnum.TWO_PAIR, new TwoPairsComparingImpl());
		maps.put(RankingEnum.ONE_PAIR, new OnePairComparingImpl());
		maps.put(RankingEnum.HIGH_CARD, new HighCardComparingImpl());
	}

	public static IComparing getComparing(RankingEnum RankingEnum) {
		IComparing cmp = maps.get(RankingEnum);
		if (cmp == null) {
			return defaultComparing;
		} else {
			return cmp;
		}
	}

}
