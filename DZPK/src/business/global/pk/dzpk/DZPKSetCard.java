package business.global.pk.dzpk;

import business.global.pk.AbsPKSetCard;
import com.ddm.server.common.utils.CommMath;
import com.ddm.server.common.utils.Lists;
import jsproto.c2s.cclass.pk.BasePocker;
import jsproto.c2s.cclass.pk.BasePockerLogic;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 长汀510K
 * 每一局麻将底牌信息
 * 抓牌人是逆时针出手
 * 牌是顺时针被抓
 *
 * @author Huaxing
 */
@Data
public class DZPKSetCard extends AbsPKSetCard {
    /**
     * 当局信息
     */
    public DZPKRoomSet set;
    private int randomPartnerCard;
    /**
     * 牌列表
     */
    private ArrayList<Integer> leftCards = Lists.newArrayList();
    /**
     * 公共牌
     */
    private ArrayList<Integer> commonCard = Lists.newArrayList();
    private ArrayList<Integer> publicCard = Lists.newArrayList();
    private ArrayList<Integer> curDealCard = Lists.newArrayList();

    /**
     * @param set
     */

    public DZPKSetCard(DZPKRoomSet set) {
        this.set = set;
        // 源牌	
        this.randomCard();

    }


    /**
     * 随机牌
     */
    public void randomCard() {
        this.leftCards = BasePockerLogic.getOnlyRandomPockerList(1, 0, BasePocker.PockerListType.POCKERLISTTYPE_AEND);
        //去掉2-5
        int cardValue = 0;
        if (getSet().isDUAN_PAI()) {
            ArrayList<Integer> newCard = Lists.newArrayList(leftCards);
            for (Integer card : leftCards) {
                cardValue = BasePockerLogic.getCardValue(card);
                if (cardValue >= 2 && cardValue <= 5) {
                    newCard.remove(card);
                }
            }
            leftCards = newCard;
        }
        // 打散牌组
        this.onXiPai();
        //随机亮牌	
        this.setRandomPartnerCard(this.leftCards.get(CommMath.randomInt(this.leftCards.size() - 1)));
    }

    /**
     * 洗牌
     **/
    public void onXiPai() {
        Collections.shuffle(this.leftCards);
    }

    @Override
    public ArrayList<Integer> popList(int cnt) {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        if (CollectionUtils.isEmpty(this.leftCards) || this.leftCards.size() < cnt) {
            return ret;
        }
        ;
        for (int i = 0; i < cnt; i++) {
            ret.add(this.leftCards.remove(CommMath.randomInt(this.leftCards.size() - 1)));
        }
        return ret;
    }

    /**
     * 游戏是否结束
     *
     * @param over
     */
    public void dealCard(boolean over) {
        if (commonCard.size() >= 5) {
            return;
        }
        curDealCard.clear();
        if (over) {
            int size = commonCard.size();
            for (int i = size; i < 5; i++) {
                curDealCard.add(this.publicCard.remove(0));
            }
        } else {
            if (commonCard.size() == 0) {
                for (int i = 0; i < 3; i++) {
                    curDealCard.add(this.publicCard.remove(0));
                }
            } else {
                curDealCard.add(this.publicCard.remove(0));
            }
        }
        commonCard.addAll(curDealCard);

    }

    /**
     * 强制发牌（测试用）
     *
     * @param forcePop 强制牌列表
     * @return
     */
    @Override
    public List<Integer> forcePopList(List<Integer> forcePop) {
        if (null == forcePop) {
            return Collections.emptyList();
        }
        List<Integer> ret = new ArrayList<>();
        for (int type : forcePop) {
            int mCard = this.removeLeftCardType(type);
            if (mCard > 0) {
                ret.add(mCard);
            }
        }
        return ret;
    }


    /**
     * 移除指定类型的牌
     *
     * @param cardType 牌类型
     * @return
     */
    public Integer removeLeftCardType(int cardType) {
        // 创建迭代器		
        Iterator<Integer> it = this.leftCards.iterator();
        // 循环遍历迭代器		
        while (it.hasNext()) {
            int mCard = it.next();
            if (mCard == cardType) {
                it.remove();
                return mCard;
            }
        }
        return 0;
    }


    @Override
    public Integer pop() {
        return null;
    }

    @Override
    public Integer appointPopCard(int popCard) {
        return null;
    }

    @Override
    public ArrayList<Integer> popList(int cnt, int i) {
        return null;
    }

    public void initPublicCard() {
        if (publicCard == null) {
            publicCard = new ArrayList<>();
        }
        Collections.shuffle(leftCards);
        publicCard.addAll(leftCards.subList(0, 5));
        publicCard = new ArrayList<>(publicCard.subList(0, 5));
    }
}
		
