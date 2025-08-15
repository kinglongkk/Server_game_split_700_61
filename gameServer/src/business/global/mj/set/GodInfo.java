package business.global.mj.set;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.alibaba.druid.util.StringUtils;

import business.global.mj.AbsMJSetPos;
import business.global.mj.AbsMJSetRoom;
import business.global.mj.MJCard;
import business.global.mj.util.CheckHuUtil;
import cenum.mj.MJSpecialEnum;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

/**
 * 神信息
 * 只能内部测试才能使用
 *
 * @author Administrator
 */
@Data
public class GodInfo {
    /**
     * 麻将牌配置
     */
    protected final MJConfigMgr mConfigMgr;

    /**
     * 神牌Map
     */
    protected HashMap<Long, Integer> GodCardMap = new HashMap<Long, Integer>(4);

    /**
     * 当局信息
     */
    private AbsMJSetRoom set;

    public GodInfo(AbsMJSetRoom set, boolean isConfigName) {
        super();
        this.set = set;
        this.mConfigMgr = new MJConfigMgr(isConfigName ? set.getRoom().getBaseRoomConfigure().getGameType().getName().toUpperCase() : null);
    }

    public void clear() {
        if (Objects.nonNull(this.mConfigMgr)) {
            this.mConfigMgr.clear();
        }
        if (MapUtils.isNotEmpty(this.GodCardMap)) {
            this.GodCardMap.clear();
            this.GodCardMap = null;
        }
        this.set = null;
    }

    /**
     * 获取指定摸的牌(内网测试用。)
     *
     * @return
     */
    public HashMap<Long, Integer> getGodCardMap() {
        return GodCardMap;
    }

    /**
     * 叫牌(内网测试用。)
     *
     * @param pid
     * @param msg
     */
    public void addGodCardMap(long pid, String msg) {
        if (this.mConfigMgr.getGodCard() != MJSpecialEnum.GOD_CARD.value()) {
            return;
        }
        if (StringUtils.isEmpty(msg)) {
            return;
        }
        if ("x".equals(msg)) {
            this.getSet().endSet();
        }
        if (!StringUtils.isNumber(msg)) {
            return;
        }
        int cardType = Integer.parseInt(msg);
        if (cardType > 10 && cardType < 63) {
            this.GodCardMap.put(pid, cardType);
        }
    }

    /**
     * 是否神牌模式
     * @return T：神牌模式，F：普通模式
     */
    public boolean isGodCardMode(){
        return this.mConfigMgr.getGodCard() == MJSpecialEnum.GOD_CARD.value();
    }

    /**
     * 检查是否神牌模式
     *
     * @param setPos
     * @param idxPos
     * @return
     */
    public boolean isGodCard(AbsMJSetPos setPos, int idxPos) {
        if (this.mConfigMgr.getGodCard() == MJSpecialEnum.GOD_CARD.value()) {
            List<Integer> forceInitCard = this.mConfigMgr.getAllPrivateCard().get(idxPos);
            if (CollectionUtils.isNotEmpty(forceInitCard)) {
                setPos.forcePopCard(this.getSet().getMJSetCard().forcePopList(forceInitCard));
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 神牌模式下补牌
     */
    public void godCardPrivate() {
        if (this.mConfigMgr.getGodCard() == MJSpecialEnum.GOD_CARD.value()) {
            for (int idx = 0; idx < this.getSet().getRoom().getPlayerNum(); idx++) {
                AbsMJSetPos mSetPos = this.getSet().getMJSetPos(idx);
                if (mSetPos.sizePrivateCard() == this.getSet().cardSize()) {
                    continue;
                }
                int podIdx = this.getSet().cardSize() - mSetPos.sizePrivateCard();
                List<MJCard> privateList = this.getSet().getMJSetCard().popList(podIdx, idx);
                this.getSet().getMJSetPos(idx).forcePopCard(privateList);
            }
        }
    }

    /**
     * 获取金牌列表
     * @return
     */
    public List<Integer> getJinList() {
        if (this.mConfigMgr.getGodCard() == MJSpecialEnum.GOD_CARD.value()) {
            return null!=this.mConfigMgr.getJinList()?new ArrayList<>(this.mConfigMgr.getJinList()):null;
        }
        return null;
    }

    /**
     * 摸指定的牌（仅内部测试用。）
     *
     * @param setPos 位置信息
     * @return
     */
    public int godHandCard(AbsMJSetPos setPos) {
        if (this.mConfigMgr.getGodCard() != MJSpecialEnum.GOD_CARD.value()) {
            if (setPos.sizePrivateCard() <= 1) {
                return 0;
            }
            if (setPos.isPosRandom()) {
                // 获取靠牌列表
                List<Integer> cardList = CheckHuUtil.OutCardList(setPos);
                // 检查摸得牌
                if (null != cardList && cardList.size() > 0) {
                    return cardList.get((int) (Math.random() * cardList.size()));
                }
            }
            return 0;
        }
        if (this.GodCardMap.containsKey(setPos.getPid())) {
            return this.GodCardMap.get(setPos.getPid());
        }
        return 0;
    }


    public int getJin(int i) {
        if (this.mConfigMgr.getGodCard() != MJSpecialEnum.GOD_CARD.value()) {
            return 0;
        }
        return mConfigMgr.getJin(i);

    }

    public AbsMJSetRoom getSet() {
        return set;
    }


}
