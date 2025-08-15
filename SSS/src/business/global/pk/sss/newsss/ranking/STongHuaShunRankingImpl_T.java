package business.global.pk.sss.newsss.ranking;

import business.global.pk.sss.newsss.PlayerDun;
import business.global.pk.sss.newsss.Poker;

import business.sss.c2s.cclass.newsss.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * 三同花顺
 */
public class STongHuaShunRankingImpl_T extends AbstractRanking {
	@Override
	protected RankingResult doResolve(PlayerDun player) {
		RankingResult result = null;
		int guicnt = player.getGuiCount();
		// 先验证是否出现三同花
		STongHuaRankingImpl_T sTongHua = new STongHuaRankingImpl_T();
		RankingResult sthResult = sTongHua.doResolve(player);
		// 如果没有三同花，就不可能有三同花顺。
		if (null == sthResult) {
			return result;
		}

		ArrayList<CardRankEnum> cardsnew = player.getRanks();
		ArrayList<ArrayList<CardRankEnum>> rets = new ArrayList<ArrayList<CardRankEnum>>();
		if (player.getCardSize() == 13) {
			if (guicnt == 0) {
				rets.addAll(checkAll(cardsnew, 5, 5, 3));
			} else if (guicnt == 1) {
				rets.addAll(checkAll(cardsnew, 5, 4, 3));
				rets.addAll(checkAll(cardsnew, 5, 5, 2));
			} else if (guicnt == 2) {
				rets.addAll(checkAll(cardsnew, 5, 5, 1));
				rets.addAll(checkAll(cardsnew, 5, 4, 2));
				rets.addAll(checkAll(cardsnew, 5, 3, 3));
				rets.addAll(checkAll(cardsnew, 4, 4, 3));
			}
		}

		if (rets.size() > 2) {
			Map<Integer, List<PockerCard>> pockerCardMap = pockerCardMap(player.getCards());
			if (null == pockerCardMap) {
                return result;
            }
			if (pockerCardMap.size() == 1) {
				result = new RankingResult();
				result.setPockerCards(player.getCards());
				result.setRankingEnum(RankingEnum.STongHuaShun);
			} else if (pockerCardMap.size() == 2 || pockerCardMap.size() == 3) {
				Map<Integer, List<PockerCard>> pockerCardMapList = null;
				List<PockerCard> addPockerCard = new ArrayList<PockerCard>();
				for (int j = 0; j < rets.size(); j += 3) {
					pockerCardMapList = pockerCardMap(player.getCards());
					if (null == pockerCardMapList) {
                        return result;
                    }
					
					int i = 0;
					if (equalsCardPocker(j,addPockerCard,pockerCardMapList, rets) <= 0) {
						addPockerCard.clear();
						continue;
					}
					i++;
					if (equalsCardPocker(j + 1,addPockerCard, pockerCardMapList, rets) <= 0) {
						addPockerCard.clear();
						continue;
					}
					i++;
					if (equalsCardPocker(j + 2,addPockerCard, pockerCardMapList, rets) <= 0) {
						addPockerCard.clear();
						continue;
					}
					i++;

					if (i == 3) {
						result = new RankingResult();
						result.setPockerCards(addPockerCard);
						result.setRankingEnum(RankingEnum.STongHuaShun);
						return result;
					}
				}
			}

		}
		return result;
	}

	private int equalsCardPocker(int j,
			List<PockerCard> addPockerCard,
			Map<Integer, List<PockerCard>> pockerCardMapList,
			ArrayList<ArrayList<CardRankEnum>> rets) {
		int i = 0;
		for (Entry<Integer, List<PockerCard>> entry : pockerCardMapList
				.entrySet()) {
			if (equalsCardPocker(addPockerCard,rets.get(j), entry.getValue())) {
				i = 1;
				break;
			}
		}
		return i;
	}

	private boolean equalsCardPocker(List<PockerCard> addPockerCard,List<CardRankEnum> cRankEnums,
			List<PockerCard> cPocker) {
		int size = cRankEnums.size();
		int i = 0;
		for (CardRankEnum cRankEnum : cRankEnums) {
			for (PockerCard pCard : cPocker) {
				if (cRankEnum.equals(pCard.getRank())) {
					i++;
					break;
				}
			}
		}

		if (size == i) {
			for (CardRankEnum cRankEnum : cRankEnums) {
				for (PockerCard pCard : cPocker) {
					if (cRankEnum.equals(pCard.getRank())) {
						addPockerCard.add(pCard);
						cPocker.remove(pCard);
						break;
					}
				}
			}
			return true;
		}
		return false;
	}

	private Map<Integer, List<PockerCard>> pockerCardMap(
			List<PockerCard> pockerCards) {
		Map<Integer, List<PockerCard>> pockerCardMap = pockerCards.stream()
				.collect(Collectors.groupingBy(p -> p.type));
		if (null == pockerCardMap) {
            return null;
        }
		// 检查是否有鬼牌，并且去除鬼牌
		if (pockerCardMap.containsKey(CardSuitEnum.GUI.ordinal())) {
			pockerCardMap.remove(CardSuitEnum.GUI.ordinal());
		}
		// 获取分组大小
		int sizePockerMap = pockerCardMap.size();
		if (sizePockerMap == 4) {
            return null;
        }

		return pockerCardMap;
	}

	public static void main(String[] args) {
		for (int i = 0;i<99999999;i++) {
			Poker poker = new Poker(4, new ArrayList<Integer>(), false, false,0);
			for (int p = 0;p<4;p++) {
				List<PockerCard> ret = new ArrayList<PockerCard>();
				for (int j = 0; j < Constants.HAND_CARD_NUMBER; j++) {
					ret.add(poker.dispatch());
				}
				STongHuaShunRankingImpl_T shunRankingImpl_T = new STongHuaShunRankingImpl_T();
				PlayerDun player = new PlayerDun();
				player.addData(ret);
				shunRankingImpl_T.doResolve(player);
			}
			

		}


	}

}
