package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;
import business.sss.c2s.cclass.newsss.CardRankEnum;
import business.sss.c2s.cclass.newsss.CardSuitEnum;
import business.sss.c2s.cclass.newsss.PockerCard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Class {@code AbstractRanking} 抽象牌型解析接口, 抽象解析流程
 */
public abstract class AbstractRanking implements IRanking {
	public static ArrayList<ArrayList<CardRankEnum>> twoCars = new ArrayList<ArrayList<CardRankEnum>>();
	public static ArrayList<ArrayList<CardRankEnum>> threeCars = new ArrayList<ArrayList<CardRankEnum>>();
	public static ArrayList<ArrayList<CardRankEnum>> threeCarsTwo = new ArrayList<ArrayList<CardRankEnum>>();
	public static ArrayList<ArrayList<CardRankEnum>> fourCars = new ArrayList<ArrayList<CardRankEnum>>();
	public static ArrayList<ArrayList<CardRankEnum>> fiveCars = new ArrayList<ArrayList<CardRankEnum>>();
	static {
		fiveCars = getAllList(5);// 不包含鬼牌
		twoCars = getAllList(2);// 一张鬼牌
		threeCars = getAllList(3);// 不包含鬼牌
		threeCarsTwo = getAllList32();// 3+2 鬼牌
		fourCars = getAllList(4);// 4+1 鬼牌
	}

	@Override
	public RankingResult resolve(PlayerDun player) {
		this.preResolve(player);
		RankingResult result = this.doResolve(player);
		this.postResolve(player, result);
		return result;
	}

	protected static ArrayList<ArrayList<CardRankEnum>> getAllList(int length) {
		ArrayList<ArrayList<CardRankEnum>> ret = new ArrayList<ArrayList<CardRankEnum>>();
		for (CardRankEnum rankEnum : CardRankEnum.values()) {
			if (rankEnum.getNumber() < CardRankEnum.CARD_XGUI.getNumber()) {
				ArrayList<ArrayList<CardRankEnum>> tmp = getList(rankEnum, length);
				if (tmp != null && tmp.size() > 0) {
					ret.addAll(tmp);
				}
			}
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	protected static ArrayList<ArrayList<CardRankEnum>> getAllList32() {
		ArrayList<ArrayList<CardRankEnum>> ret = new ArrayList<ArrayList<CardRankEnum>>();
		for (int i = 0; i < AbstractRanking.fiveCars.size(); i++) {
			ArrayList<CardRankEnum> sublist = (ArrayList<CardRankEnum>) AbstractRanking.fiveCars.get(i).clone();
			for (int j = 0; j < sublist.size(); j++) {
				for (int k = j + 1; k < sublist.size(); k++) {
					ArrayList<CardRankEnum> tmp = (ArrayList<CardRankEnum>) AbstractRanking.fiveCars.get(i).clone();
					tmp.remove(sublist.get(j));
					tmp.remove(sublist.get(k));
					if (!ret.contains(tmp)) {
						ret.add(tmp);
					}
				}
			}
		}

		return ret;
	}

	protected static ArrayList<CardRankEnum> listRemove(ArrayList<CardRankEnum> alllist, List<CardRankEnum> rmlist) {
		Iterator<CardRankEnum> it = alllist.iterator();
		while (it.hasNext()) {
			CardRankEnum x = it.next();
			if (rmlist.contains(x)) {
				rmlist.remove(x);
				it.remove();
			}
		}
		return alllist;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<ArrayList<CardRankEnum>> check(ArrayList<CardRankEnum> cards, int firstcnt, int secondcnt,
			int thirdcnt) {
		ArrayList<ArrayList<CardRankEnum>> retlist = new ArrayList<ArrayList<CardRankEnum>>();
		ArrayList<ArrayList<CardRankEnum>> firstlst = new ArrayList<ArrayList<CardRankEnum>>();
		ArrayList<ArrayList<CardRankEnum>> tmplst = getSZListByCards(cards, firstcnt);
		if (tmplst.size() > 0) {
			firstlst = tmplst;
		}

		for (int i = 0; i < firstlst.size(); i++) {
			ArrayList<CardRankEnum> cards2 = new ArrayList<CardRankEnum>();
			cards2 = (ArrayList<CardRankEnum>) cards.clone();
			cards2 = listRemove(cards2, (ArrayList<CardRankEnum>) firstlst.get(i).clone());
			ArrayList<ArrayList<CardRankEnum>> secondlst = new ArrayList<ArrayList<CardRankEnum>>();
			ArrayList<ArrayList<CardRankEnum>> tmplst2 = new ArrayList<ArrayList<CardRankEnum>>();
			if (secondcnt == 3) {
				tmplst2 = getSZListByCards2(cards2);
			} else {
				tmplst2 = getSZListByCards(cards2, secondcnt);
			}
			if (tmplst2.size() > 0) {
				secondlst = tmplst2;
			}

			for (int j = 0; j < secondlst.size(); j++) {
				ArrayList<CardRankEnum> cards3 = new ArrayList<CardRankEnum>();
				cards3 = (ArrayList<CardRankEnum>) cards2.clone();
				cards3 = listRemove(cards3, (ArrayList<CardRankEnum>) secondlst.get(j).clone());
				ArrayList<ArrayList<CardRankEnum>> tmplst3 = getSZListByCards(cards3, thirdcnt);

				if (tmplst3.size() > 0) {
					retlist.add(firstlst.get(i));
					retlist.add(secondlst.get(j));
					retlist.add(tmplst3.get(0));
					return retlist;
				}
			}
		}
		return retlist;
	}

	
	@SuppressWarnings("unchecked")
	public ArrayList<ArrayList<CardRankEnum>> checkAll(ArrayList<CardRankEnum> cards, int firstcnt, int secondcnt,
			int thirdcnt) {
		ArrayList<ArrayList<CardRankEnum>> retlist = new ArrayList<ArrayList<CardRankEnum>>();
		ArrayList<ArrayList<CardRankEnum>> firstlst = new ArrayList<ArrayList<CardRankEnum>>();
		ArrayList<ArrayList<CardRankEnum>> tmplst = getSZListByCards(cards, firstcnt);
		if (tmplst.size() > 0) {
			firstlst = tmplst;
		}

		for (int i = 0; i < firstlst.size(); i++) {
			ArrayList<CardRankEnum> cards2 = new ArrayList<CardRankEnum>();
			cards2 = (ArrayList<CardRankEnum>) cards.clone();
			cards2 = listRemove(cards2, (ArrayList<CardRankEnum>) firstlst.get(i).clone());
			ArrayList<ArrayList<CardRankEnum>> secondlst = new ArrayList<ArrayList<CardRankEnum>>();
			ArrayList<ArrayList<CardRankEnum>> tmplst2 = new ArrayList<ArrayList<CardRankEnum>>();
			if (secondcnt == 3) {
				tmplst2 = getSZListByCards2(cards2);
			} else {
				tmplst2 = getSZListByCards(cards2, secondcnt);
			}
			if (tmplst2.size() > 0) {
				secondlst = tmplst2;
			}

			for (int j = 0; j < secondlst.size(); j++) {
				ArrayList<CardRankEnum> cards3 = new ArrayList<CardRankEnum>();
				cards3 = (ArrayList<CardRankEnum>) cards2.clone();
				cards3 = listRemove(cards3, (ArrayList<CardRankEnum>) secondlst.get(j).clone());
				ArrayList<ArrayList<CardRankEnum>> tmplst3 = getSZListByCards(cards3, thirdcnt);

				if (tmplst3.size() > 0) {
					retlist.add(firstlst.get(i));
					retlist.add(secondlst.get(j));
					retlist.add(tmplst3.get(0));
				}
			}
		}
		return retlist;
	}

	
	/**
	 * 单数字的顺子
	 * 
	 * @param rank
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected static ArrayList<ArrayList<CardRankEnum>> getList(CardRankEnum rank, int length) {
		int num = rank.getNumber();
		// 处理带A的顺子
		if (rank == CardRankEnum.CARD_ACE) {
			num = 1;
		}
		int maxnum = num + length - 1;
		if (maxnum > 14) {
			return null;
		}

		// 一张鬼的情况
		if (4 == length || 2 == length) {
			ArrayList<CardRankEnum> ret = new ArrayList<CardRankEnum>();
			ArrayList<ArrayList<CardRankEnum>> ret2 = new ArrayList<ArrayList<CardRankEnum>>();
			for (int i = 0; i < length; i++) {
				int index = num + i;
				if (index == 1) {
					index = 14;
				}
				ret.add(CardRankEnum.valueOf(index));
			}
			ret2.add(ret);
			// 添加间隔顺子
			if (maxnum < 14) {
				CardRankEnum adde = CardRankEnum.valueOf(maxnum + 1);
				for (int i = 1; i < ret.size(); i++) {
					ArrayList<CardRankEnum> tmp = (ArrayList<CardRankEnum>) ret.clone();
					tmp.remove(i);
					tmp.add(adde);
					ret2.add(tmp);
				}
			}
			return ret2;
		} else {
			ArrayList<CardRankEnum> ret = new ArrayList<CardRankEnum>();
			for (int i = 0; i < length; i++) {
				int index = num + i;
				if (index == 1) {
					index = 14;
				}
				ret.add(CardRankEnum.valueOf(index));
			}
			ArrayList<ArrayList<CardRankEnum>> ret2 = new ArrayList<ArrayList<CardRankEnum>>();
			ret2.add(ret);
			return ret2;
		}
	}

	/**
	 * 筛选出手牌所包含的顺子
	 */
	protected ArrayList<ArrayList<CardRankEnum>> getSZListByCards(ArrayList<CardRankEnum> cards, int length) {
		ArrayList<ArrayList<CardRankEnum>> ret = new ArrayList<ArrayList<CardRankEnum>>();
		if (length == 5) {
			for (int i = 0; i < fiveCars.size(); i++) {
				if (cards.containsAll(AbstractRanking.fiveCars.get(i))) {
					ret.add(AbstractRanking.fiveCars.get(i));
				}
			}
		} else if (length == 4) {
			for (int i = 0; i < AbstractRanking.fourCars.size(); i++) {
				if (cards.containsAll(AbstractRanking.fourCars.get(i))) {
					ret.add(AbstractRanking.fourCars.get(i));
				}
			}
		} else if (length == 3) {
			for (int i = 0; i < AbstractRanking.threeCars.size(); i++) {
				if (cards.containsAll(AbstractRanking.threeCars.get(i))) {
					ret.add(AbstractRanking.threeCars.get(i));
				}
			}
		} else if (length == 2) {
			for (int i = 0; i < AbstractRanking.twoCars.size(); i++) {
				if (cards.containsAll(AbstractRanking.twoCars.get(i))) {
					ret.add(AbstractRanking.twoCars.get(i));
				}
			}
		} else if (length == 1) {
			ret.add(cards);
		}
		return ret;
	}

	protected ArrayList<ArrayList<CardRankEnum>> getSZListByCards2(ArrayList<CardRankEnum> cards) {
		ArrayList<ArrayList<CardRankEnum>> ret = new ArrayList<ArrayList<CardRankEnum>>();
		for (int i = 0; i < threeCarsTwo.size(); i++) {
			if (cards.containsAll(AbstractRanking.threeCarsTwo.get(i))) {
				ret.add(AbstractRanking.threeCarsTwo.get(i));
			}
		}
		return ret;
	}

	private void preResolve(PlayerDun player) {
	}

	private void postResolve(PlayerDun player, RankingResult result) {
		if (result != null) {
			result.setHighCard((player.getCards().get(0)));
		}
		player.setRankingResult(result);
	}

	protected abstract RankingResult doResolve(PlayerDun player);

	protected boolean isSameSuit(List<PockerCard> cards) {
		if (cards == null || cards.size() == 0) {
			return false;
		}
		if (cards.size() == 1) {
			return true;
		}
		if (cards.size() > 1) {
			CardSuitEnum suitEnum = CardSuitEnum.CLUBS;
			for(int i = 0 ; i <cards.size();i++)
			{
				PockerCard card = cards.get(i);
				if(card.getSuit() != CardSuitEnum.GUI)
				{
					suitEnum = card.getSuit();
					break;
				}
			}
			for (int i = 1; i < cards.size(); i++) {
				if (cards.get(i).getSuit() != CardSuitEnum.GUI) {
					if (suitEnum == null) {
						suitEnum = cards.get(i).getSuit();
					} else if (suitEnum != cards.get(i).getSuit()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	protected int getGuiPaiCount(List<PockerCard> cards) {
		int ret = 0;
		for (int i = 1; i < cards.size(); i++) {
			if (CardSuitEnum.GUI != cards.get(i).getSuit()) {
				ret++;
			}
		}
		return ret;
	}

	public static void main(String[] args) {
		// for(int i = 0 ; i < AbstractRanking.fourCars.size(); i ++)
		// {
		//
		// }

		System.out.printf("twoCars=====size:%d===", AbstractRanking.twoCars.size());
		System.out.println(AbstractRanking.twoCars);

		System.out.printf("threeCars=====size:%d===", AbstractRanking.threeCars.size());
		System.out.println(AbstractRanking.threeCars);

		System.out.printf("threeCarsTwo=====size:%d===", AbstractRanking.threeCarsTwo.size());
		System.out.println(AbstractRanking.threeCarsTwo);

		System.out.printf("fourCars=====size:%d===", AbstractRanking.fourCars.size());
		System.out.println(AbstractRanking.fourCars);

		System.out.printf("fiveCars=====size:%d===", AbstractRanking.fiveCars.size());
		System.out.println(AbstractRanking.fiveCars);
	}
}
