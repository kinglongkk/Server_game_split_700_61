package business.global.mj.qzmj;

import java.util.ArrayList;
import java.util.List;

import business.global.mj.AbsMJSetCard;
import business.global.mj.MJCard;
import business.global.mj.RandomCard;
import cenum.mj.MJCardCfg;
import cenum.mj.MJSpecialEnum;

/**
 * 麻将
 * 每一局麻将底牌信息
 * 抓牌人是逆时针出手
 * 牌是顺时针被抓
 * @author Huaxing
 *
 */
public class QZMJSetCard extends AbsMJSetCard {
	public QZMJRoomSet set;
	private int kaiJinHuaNum=0;
	public QZMJSetCard(QZMJRoomSet set){
		this.set = set;
		this.room = set.getRoom();
		this.randomCard();
	}
	
	/**
	 * 洗牌
	 */
	@Override
	public void randomCard(){
		List<MJCardCfg> mCfgs = new ArrayList<MJCardCfg>();
		mCfgs.add(MJCardCfg.WANG);
		mCfgs.add(MJCardCfg.TIAO);
		mCfgs.add(MJCardCfg.TONG);
		mCfgs.add(MJCardCfg.FENG);
		mCfgs.add(MJCardCfg.JIAN);
		mCfgs.add(MJCardCfg.BAI);
		mCfgs.add(MJCardCfg.HUA);
		this.setRandomCard(new RandomCard(mCfgs,this.room.getPlayerNum(),this.room.getXiPaiList().size()));
		this.initDPos(this.set);
	}
	

	@Override
	protected boolean firstRandomDPos() {
		return false;
	}

	@Override
	public MJCard pop(boolean isNormalMo,int cardType) {
		// 留牌 16张
		if (this.randomCard.getSize() <= 16) {
			return null;
		}
		MJCard ret = this.getGodCard(cardType);
		ret = null != ret ? ret : this.randomCard.removeLeftCards(0);
		if (isNormalMo) {
            this.randomCard.setNormalMoCnt(this.randomCard.getNormalMoCnt() + 1);
        } else {
            this.randomCard.setGangMoCnt(this.randomCard.getGangMoCnt() + 1);
        }
		return ret;
	}
	/**
	 *   不开花
	 * @return
	 */
	public MJCard popKaiJin(){
		MJCard card=this.randomCard.getLeftCards().get(kaiJinHuaNum);
		//如果是花的话重新开一张  春夏秋冬 梅兰竹菊
		if(card.type >=  MJSpecialEnum.NOT_HUA.value()){
			kaiJinHuaNum++;
			this.popKaiJin();
		}
		this.randomCard.getLeftCards().remove(card);
		this.randomCard.setNormalMoCnt(this.randomCard.getNormalMoCnt() + 1);
		return card;
	}
	
	
}

