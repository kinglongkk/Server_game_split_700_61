package business.global.pk.sss.newsss;

import BaseCommon.CommLog;
import business.global.pk.sss.SSSRoomPos;
import business.global.pk.sss.SSSRoomSet;
import business.global.pk.sss.newsss.ranking.RankingFacade;
import business.global.pk.sss.newsss.ranking.SShunZiRankingImpl_T;
import business.global.room.RoomMgr;
import business.global.room.base.AbsBaseRoom;
import business.sss.c2s.cclass.CSSS_PlayerRanked;
import business.sss.c2s.cclass.SSSSet_Pos;
import business.sss.c2s.cclass.entity.PlayerCardType;
import business.sss.c2s.cclass.entity.PlayerResult;
import business.sss.c2s.cclass.newsss.CardRankEnum;
import business.sss.c2s.cclass.newsss.CardSuitEnum;
import business.sss.c2s.cclass.newsss.PockerCard;
import business.sss.c2s.cclass.newsss.RankingEnum;
import business.sss.c2s.iclass.CSSS_Ranked;
import cenum.PrizeType;
import com.ddm.server.common.CommLogD;
import jsproto.c2s.cclass.pk.BasePockerLogic;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class PlayerData implements  Cloneable,Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8377177674937905150L;
	public PlayerDun allCards;
	public PlayerDun first;
	public PlayerDun second;
	public PlayerDun third;
	private long roomID;
	public long pid;
	public int posIdx;
	private boolean isSpecial = false;
	private int specialTypt = -1;// 特殊牌型
	private int shuicnt = 0;// 三墩总水
	private boolean isReadyPlayer = false;
	private boolean isXiPai = false;
	public PlayerData(long roomid, long pid, int pos) {
		this.roomID = roomid;
		this.pid = pid;
		this.posIdx = pos;
	}
	
	
	public void clean() {
		this.allCards = null;
		this.first = null;
		this.second = null;
		this.third = null;
	}

	public void setSpecial(int type) {
		this.specialTypt = type;
		this.isSpecial = true;
	}

	public boolean getSpecial() {
		return this.isSpecial;
	}

	public int getSpecialType() {
		return this.specialTypt;
	}

	public int getShui() {
		return this.shuicnt;
	}

	
	
	public boolean isXiPai() {
		return isXiPai;
	}

	public void setXiPai(boolean isXiPai) {
		this.isXiPai = isXiPai;
	}

	/**
	 * 
	 * @return F 出现null 错误
	 */
	public boolean checkDun () {
		if (this.isSpecial) {
            return true;
        }
		if (null == this.first || null == this.second || null == this.third) {
			CommLogD.error("shoucard"+this.allCards.toString());
			CommLogD.error("first"+(null == this.first?0:this.first.toString()));
			CommLogD.error("second"+(null == this.second?0:this.second.toString()));
			CommLogD.error("third"+(null == this.third?0:this.third.toString()));
			CommLogD.error("null == this.first || null == this.second || null == this.third pid：{},roomID：{},specialTypt：{}",this.pid,this.roomID,this.specialTypt);
			return false;
		} else if (this.first.getCardSize() <= 0 || this.second.getCardSize() <= 0 || this.third.getCardSize() <= 0) {
			CommLogD.error("this.first.getCardSize() <= 0 || this.second.getCardSize() <= 0 || this.third.getCardSize() <= 0 pid：{},roomID：{},specialTypt：{}",this.pid,this.roomID,this.specialTypt);
			return false;
		} else if (this.pid <= 0L) {
			CommLogD.error("this.pid <= 0L pid：{},roomID：{},specialTypt：{}",this.pid,this.roomID,this.specialTypt);
			return false;
		} else if (!this.isReadyPlayer) {
			CommLogD.error("!this.isReadyPlayer pid：{},roomID：{},specialTypt：{}",this.pid,this.roomID,this.specialTypt);
			return false;
		}
		return true;
	}
	
	
	// 设置玩家手牌
	public int setPlayerCards(List<PockerCard> cards) {
		this.allCards = new PlayerDun();
		this.allCards.addData(cards);
		RankingFacade.getInstance().resolve(this.allCards);
		if (this.allCards.getRankingResult().getRankingEnum().getPriority() > 80) {
			// 检测到特殊牌 TODO特殊牌型排序 可以删除掉，让客户端自己算，三顺子排序bug 
			int spetype = this.allCards.getRankingResult().getRankingEnum().getPriority();
			setSpecial(spetype);
			if (this.allCards.getRankingResult().getRankingEnum() == RankingEnum.SShunZi || this.allCards.getRankingResult().getRankingEnum() == RankingEnum.STongHuaShun) {
				int guicnt = this.allCards.getGuiCount();
				ArrayList<CardRankEnum> cardsnew = this.allCards.getRanks();
				ArrayList<ArrayList<CardRankEnum>> rets = new ArrayList<ArrayList<CardRankEnum>>();
				SShunZiRankingImpl_T aaa = new SShunZiRankingImpl_T();
				if (this.allCards.getCardSize() == 13) {
					if (guicnt == 0) {
						rets.addAll(aaa.check(cardsnew, 5, 5, 3));
					} else if (guicnt == 1) {
						rets.addAll(aaa.check(cardsnew, 5, 4, 3));
						if (rets.size() == 0) {
							rets.addAll(aaa.check(cardsnew, 5, 5, 2));
						}

					} else if (guicnt == 2) {
						rets.addAll(aaa.check(cardsnew, 5, 5, 1));
						if (rets.size() == 0) {
							rets.addAll(aaa.check(cardsnew, 5, 4, 2));
						}
						if (rets.size() == 0) {
							rets.addAll(aaa.check(cardsnew, 5, 3, 3));
						}
						if (rets.size() == 0) {
							rets.addAll(aaa.check(cardsnew, 4, 4, 3));
						}
					}
				}

				Map<Integer, List<PockerCard>> rankMap = new HashMap<Integer, List<PockerCard>>();
				List<PockerCard> guilist = new ArrayList<PockerCard>();
				for (PockerCard PockerCard : cards) {
					if (PockerCard.getSuit() != CardSuitEnum.GUI) {
						Integer number = new Integer(PockerCard.getRank().getNumber());
						if (!rankMap.containsKey(number)) {
							List<PockerCard> tmp = new ArrayList<PockerCard>();
							tmp.add(PockerCard);
							rankMap.put(number, tmp);
						} else {
							rankMap.get(number).add(PockerCard);
						}
					} else {
						guilist.add(PockerCard);
					}
				}

				if (rets.size() == 3) {
					PlayerDun dun1 = new PlayerDun();
					for (int i = 0; i < rets.get(2).size(); i++) {
						int num = rets.get(2).get(i).getNumber();
						PockerCard card = rankMap.get(num).remove(0);
						dun1.addCard(card);
					}
					this.first = dun1;
					PlayerDun dun2 = new PlayerDun();
					for (int i = 0; i < rets.get(1).size(); i++) {
						int num = rets.get(1).get(i).getNumber();
						PockerCard card = rankMap.get(num).remove(0);
						dun2.addCard(card);
					}
					this.second = dun2;
					PlayerDun dun3 = new PlayerDun();
					for (int i = 0; i < rets.get(0).size(); i++) {
						int num = rets.get(0).get(i).getNumber();
						PockerCard card = rankMap.get(num).remove(0);
						dun3.addCard(card);
					}
					this.third = dun3;
				}

				// 鬼牌处理
				if (this.first.getCardSize() < 3 && guilist.size() > 0) {
					if (this.first.getCardSize() == 1 && guilist.size() == 2) {
						this.first.addCard(guilist.remove(0));
						this.first.addCard(guilist.remove(0));
					} else {
						this.first.addCard(guilist.remove(0));
					}

				}
				if (this.second.getCardSize() < 5 && guilist.size() > 0) {
					if (this.second.getCardSize() == 3 && guilist.size() == 2) {
						this.second.addCard(guilist.remove(0));
						this.second.addCard(guilist.remove(0));
					} else {
						this.second.addCard(guilist.remove(0));
					}

				}
				if (this.third.getCardSize() < 5 && guilist.size() > 0) {
					if (this.second.getCardSize() == 3 && guilist.size() == 2) {
						this.third.addCard(guilist.remove(0));
						this.third.addCard(guilist.remove(0));
					} else {
						this.third.addCard(guilist.remove(0));
					}
				}

				if (this.second.compareTo(this.third) == -1) {
					PlayerDun dun = this.second;
					this.second = this.third;
					this.third = dun;
				}

			}else {
				List<PockerCard> firstCard = this.allCards.getCards().subList(0, 3);
				this.first = new PlayerDun().addData(firstCard);
				List<PockerCard> secondCard = this.allCards.getCards().subList(3, 8);
				this.second = new PlayerDun().addData(secondCard);
				List<PockerCard> thirdCard = this.allCards.getCards().subList(8, 13);
				this.third = new PlayerDun().addData(thirdCard);
			}
		}
		return this.allCards.getRankingResult().getRankingEnum().getPriority();
	}

	// 设置玩家墩牌
	public boolean setCards(HashMap<Integer, List<PockerCard>> list) {
		this.isSpecial = false;
		this.specialTypt = -1;
		this.first = new PlayerDun();
		this.first.addData(list.get(1));
		this.second = new PlayerDun();
		this.second.addData(list.get(2));
		this.third = new PlayerDun();
		this.third.addData(list.get(3));

		if (checkCards()) {
			RankingFacade.getInstance().resolve(this.first);
			RankingFacade.getInstance().resolve(this.second);
			RankingFacade.getInstance().resolve(this.third);
			int firstV = this.first.getRankingResult().getRankingEnum().getPriority();
			int secondV = this.second.getRankingResult().getRankingEnum().getPriority();
			int thirdV = this.third.getRankingResult().getRankingEnum().getPriority();
			if(firstV>secondV || secondV>thirdV)
			{
				return false;
			}
			this.isReadyPlayer = true;
			return true;
		} else {
			return false;
		}
	}

	
	
	// 设置玩家墩牌
	public boolean setCards(CSSS_PlayerRanked ranked) {
		this.isSpecial = false;
		this.specialTypt = -1;
		
		List<String> rankList = new ArrayList<>();
		rankList.addAll(ranked.first);
		rankList.addAll(ranked.second);
		rankList.addAll(ranked.third);
		Map<String, Long> map = rankList.stream()
				.collect(Collectors.groupingBy(p -> p, Collectors.counting()));
		if (null == map || map.size() != 13) {
			if (null != map) {
				CommLogD.error("PlayerData setCards :{}",map.toString());
			} else {
				CommLogD.error("PlayerData setCards null == map {}",rankList);
			}
			return false;
		}
		
		for (long value : map.values()) {
			if(value > 1L) {
				CommLogD.error("setCards value > 1L map:{}",map.toString());
				return false;
			}
		}
		if (!this.allCards.get0xStr().containsAll(rankList)) {
			CommLogD.error("setCards allCards:{},rankList:{}",this.allCards.get0xStr().toString(),rankList.toString());
			return false;
		}
		
		
		this.first = new PlayerDun(ranked.first);
		this.second = new PlayerDun(ranked.second);
		this.third = new PlayerDun(ranked.third);
		if (checkCards()) {
			RankingFacade.getInstance().resolve(this.first);
			RankingFacade.getInstance().resolve(this.second);
			RankingFacade.getInstance().resolve(this.third);
			int firstV = this.first.getRankingResult().getRankingEnum().getPriority();
			int secondV = this.second.getRankingResult().getRankingEnum().getPriority();
			int thirdV = this.third.getRankingResult().getRankingEnum().getPriority();
			if(firstV>secondV || secondV>thirdV)
			{
				return false;
			}
			this.isReadyPlayer = true;
			return true;
		} else {
			return false;
		}
	}

	// 检测牌的合法性
	public boolean checkCards() {
		boolean flag = true;
		if(this.first.getCardSize() !=3 || this.second.getCardSize()!=5 || this.third.getCardSize()!=5)
		{
			flag = false;
		}
			if (!this.allCards.getCards().containsAll(this.first.getCards())) {
				flag = false;
			}
			if (!this.allCards.getCards().containsAll(this.second.getCards())) {
				flag = false;
			}
			if (!this.allCards.getCards().containsAll(this.third.getCards())) {
				flag = false;
			}
			
			List<String> allCardStr = this.allCards.getCardsString();
			List<String> checkAllCards = new ArrayList<String>();
			checkAllCards.addAll(this.first.getCardsString());
			checkAllCards.addAll(this.second.getCardsString());
			checkAllCards.addAll(this.third.getCardsString());
			int count = 0;
			for (int i = 0 ;i<allCardStr.size() ;i++) {
				for (int j = 0;j<checkAllCards.size();j++) {
					if (allCardStr.get(i).equals(checkAllCards.get(j))) {
						count++;
						break;
					}
				}
			}
			
			if (count != 13) {
				flag = false;
			}
				
			
		return flag;
	}

	public List<PockerCard> getCards() {
		return this.allCards.getCards();
	}

	// 获取墩牌牌型
	public PlayerCardType getPlayerCardType(int index) {
		PlayerCardType ret = new PlayerCardType();
		ret.setPid(pid);
		ret.setPosIdx(posIdx);
		switch (index) {
		case 1:
			ret.setCard(this.first.getRankingResult().getRankingEnum().getPriority());
			break;
		case 2:
			ret.setCard(this.second.getRankingResult().getRankingEnum().getPriority());
			break;
		case 3:
			ret.setCard(this.third.getRankingResult().getRankingEnum().getPriority());
			break;
		default:
			break;
		}
		return ret;
	}

	public void addBaseShuiCount(int tmpshui, int index) {
		this.shuicnt += tmpshui;
		if (1 == index) {
			this.first.addShuiCount(tmpshui);
		} else if (2 == index) {
			this.second.addShuiCount(tmpshui);
		} else if (3 == index) {
			this.third.addShuiCount(tmpshui);
		}
	}

	// 特殊牌、打枪使用
	public void addShuiCount(int tmpshui) {
		this.shuicnt += tmpshui;
	}

	public int getFirstTypeShui() {
		int ret = this.first.getRankingResult().getRankingEnum().value();
		if (this.first.getRankingResult().getRankingEnum() == RankingEnum.THREE_OF_THE_KIND) {
			ret = 3 * ret;
		}
		return ret;
	}

	public int getSecondTypeShui() {
		RankingEnum re = this.second.getRankingResult().getRankingEnum();
		int ret = re.value();
		if (re == RankingEnum.FULL_HOUSE || re == RankingEnum.FOUR_OF_THE_KIND || re == RankingEnum.STRAIGHT_FLUSH
				|| re == RankingEnum.FIVE_OF_THE_KIND) {
			ret = 2 * ret;
		}
		return ret;
	}

	public int getThirdTypeShui() {
		return this.third.getRankingResult().getRankingEnum().value();
	}

	// 获取对比结果
	public PlayerResult getPlayerResult(int index) {
		PlayerResult ret = new PlayerResult();
		ret.setPid(pid);
		ret.setPosIdx(posIdx);
		switch (index) {
		case 1:
			ret.setShui(this.first.getShuiCount());
			break;
		case 2:
			ret.setShui(this.second.getShuiCount());
			break;
		case 3:
			ret.setShui(this.third.getShuiCount());
			break;
		default:
			break;
		}
		return ret;
	}

	// 全垒打
	public void quanLeiDa() {
		int total = this.first.getShuiCount() + this.second.getShuiCount() + this.third.getShuiCount();
		this.shuicnt = 4 * total;
	}

	// 获取玩家设置好的牌序
	public CSSS_Ranked getRanked() {
		CSSS_Ranked ret = new CSSS_Ranked();
		ret.roomID = this.roomID;
		ret.pid = this.pid;
		ret.posIdx = this.posIdx;
		ret.isSpecial = this.isSpecial;
		ret.special = this.specialTypt;
		ret.dunPos = this.getPlayerRanked();
		return ret;
	}

	// 获取玩家数据
	public PlayerResult getTotalShuiS() {
		PlayerResult ret = new PlayerResult();
		ret.setPid(pid);
		ret.setPosIdx(posIdx);
		int total = this.shuicnt;
		ret.setShui(total);

		AbsBaseRoom room = RoomMgr.getInstance().getRoom(this.roomID);
		if (room.getBaseRoomConfigure().getPrizeType() == PrizeType.Gold) {
			ret.setShui(total * room.getBaseMark());
		}
		return ret;
	}
	// 获取玩家数据
	public PlayerResult getTotalScore(SSSRoomSet set) {
		PlayerResult ret = new PlayerResult();
		ret.setPid(pid);
		ret.setPosIdx(posIdx);
		int total = this.shuicnt;
		ret.setShui(total);
		ret.setPoint(total);
		ret.setBaseMark(set.room.getBaseMark());
		ret.setDoubleNum(set.room.getBS());
		SSSRoomPos sssRoomPos=(SSSRoomPos)set.room.getRoomPosMgr().getPosByPosID(posIdx);
		Double sportsPoint;
		if(sssRoomPos.setSportsPoint(total)==null){
			sportsPoint=0.0;
		}else {
			sportsPoint=sssRoomPos.setSportsPoint(total);
		}
		ret.setSportsPoint(sportsPoint);
		AbsBaseRoom room = RoomMgr.getInstance().getRoom(this.roomID);
		if (room.getBaseRoomConfigure().getPrizeType() == PrizeType.Gold) {
			ret.setShui(total * room.getBaseMark());
		}
		return ret;
	}
	private CSSS_PlayerRanked getPlayerRanked() {
		CSSS_PlayerRanked ret = new CSSS_PlayerRanked();
		if (ret.first != null && ret.second != null && this.third != null) {
			ret.first = this.first.get0xStr();
			ret.second = this.second.get0xStr();
			ret.third = this.third.get0xStr();
		}

		return ret;

	}

	public SSSSet_Pos getSetPosInfo() {
		SSSSet_Pos ret = new SSSSet_Pos();
		ret.posID = this.posIdx;
		ret.shouCard = allCards.getCardsString();
		ret.special = this.specialTypt;
		return ret;
	}
	
	
	
	/**
	 * 检查扑克类型是否正确
	 * @param cards
	 * @return
	 */
	public boolean checkPoker(List<PockerCard> cards) {
		if (this.setPlayerCards(cards) <= 80) {
			// 不是特殊牌，需要进一步检查
			return this.checkDeBugPokerType(new ArrayList<>(cards));
		}
		return true;
	}
	
	


	
	
	
	
	
	
	
	/**
	 * 检查调试扑克类型
	 * @param ret
	 * @return
	 */
	public boolean checkDeBugPokerType(List<PockerCard> ret) {
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
		
		if (!this.setCards(map)) {
			List<PockerCard> sPCards = map.get(3);
			List<PockerCard> ePCards = map.get(2);
			map.put(2, sPCards);
			map.put(3, ePCards);
			if (!this.setCards(map)) {
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

    /**
     * 对象之间的浅克隆【只负责copy对象本身，不负责深度copy其内嵌的成员对象】
     * @return
     */
    @Override
    public PlayerData clone() {
        try{
            return  (PlayerData) super.clone();
        }catch (CloneNotSupportedException ex){
            CommLogD.error(ex.getClass()+":"+ ex.getMessage());
        }
        return  null;
    }

    /**
     * 实现对象间的深度克隆【从外形到内在细胞，完完全全深度copy】
     * @return
     */
    public PlayerData deepClone(){

        // Anything 都是可以用字节流进行表示，记住是任何！
    	PlayerData cookBook = null;
        try{

           ByteArrayOutputStream baos = new ByteArrayOutputStream();
           ObjectOutputStream oos = new ObjectOutputStream(baos);
           // 将当前的对象写入baos【输出流 -- 字节数组】里
           oos.writeObject(this);

           // 从输出字节数组缓存区中拿到字节流
           byte[] bytes = baos.toByteArray();

           // 创建一个输入字节数组缓冲区
           ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
           // 创建一个对象输入流
           ObjectInputStream ois = new ObjectInputStream(bais);
           // 下面将反序列化字节流 == 重新开辟一块空间存放反序列化后的对象
            cookBook = (PlayerData) ois.readObject();

        }catch (Exception e){
        	CommLogD.error(e.getClass()+":"+e.getMessage());
        }
        return  cookBook;
    }
	
}
