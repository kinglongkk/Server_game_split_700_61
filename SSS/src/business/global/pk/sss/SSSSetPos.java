package business.global.pk.sss;

import business.sss.c2s.cclass.SSSResults;
import business.sss.c2s.cclass.entity.PlayerResult;
import business.sss.c2s.cclass.newsss.PockerCard;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 自由扑克 每个位置信息
 * 
 * @author Huaxing
 *
 */
public class SSSSetPos {
	public int posID = 0; // 1-4号位置
	public SSSRoomPos roomPos = null;
	public SSSRoomSet set = null; // 父节点
	public List<PockerCard> privateCards = new ArrayList<>(); // 手牌


	public SSSSetPos(int posID, SSSRoomPos roomPos, SSSRoomSet set) {
		this.posID = posID;
		this.roomPos = roomPos;
		this.set = set;
	}

	public void clean() {
		this.roomPos = null;
		this.set = null;
		if (null != this.privateCards) {
			this.privateCards.clear();
			this.privateCards = null;
		}
	}

	
	/**
	 * 手牌排序
	 */
	private void sortCards() {
		Collections.sort(this.privateCards, new Comparator<PockerCard>() {
			@Override
			public int compare(PockerCard o1, PockerCard o2) {
				return o2.cardID - o1.cardID;
			}
		});
	}



	/**
	 * 初始化手牌
	 * 
	 * @param cards
	 */
	public void init(List<PockerCard> cards) {
		this.privateCards = new ArrayList<>(cards);
		for (PockerCard card : this.privateCards) {
			card.ownnerPos = posID;
		}
		this.sortCards();
		getKeyCard();
	}

	private void getKeyCard() {
		if (posID == 0) {
			List<String> keys = new ArrayList<String>();
			for (PockerCard pCard : this.privateCards) {
				keys.add(pCard.toString());
			}
			set.room.setPlayerCard(keys);
		}
	}




	public void calcPosEnd() {
		SSSResults cRecord = (SSSResults)roomPos.getResults();
		if (null == cRecord) {
			cRecord = new SSSResults();
		}
		cRecord.setPid(roomPos.getPid());
		cRecord.setPosId(roomPos.getPosID());
		for (PlayerResult pResult : set.sRankingResult.posResultList) {
			if (pResult.getPid() == roomPos.getPid()) {
				roomPos.calcRoomPoint(pResult.getShui());// 更新本场积分
				cRecord.setPoint(roomPos.getPoint());
				SSSRoomPos sssRoomPos=(SSSRoomPos)set.room.getRoomPosMgr().getPosByPosID(this.posID);
				Double sportsPoint;
				if(sssRoomPos.setSportsPoint(roomPos.getPoint())==null){
					sportsPoint=0.0;
				}else {
					sportsPoint=sssRoomPos.setSportsPoint(roomPos.getPoint());
				}
				cRecord.setSportsPoint(sportsPoint);
				if (pResult.getShui() > 0) {
					cRecord.winCount = (cRecord.winCount + 1);
				} else if (pResult.getShui() < 0) {
					cRecord.loseCount = (cRecord.loseCount + 1);
				} else if (pResult.getShui() == 0) {
					cRecord.flatCount = (cRecord.flatCount + 1);
				}
			}
		}
		roomPos.setResults(cRecord);

	}

	@Override
	public String toString() {
		return "SSSSetPos [posID=" + posID + ", roomPos=" + roomPos.getPid()+", Point= "+roomPos.getPoint() + "]";
	}





}
