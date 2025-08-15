package business.global.pk.sss.newsss.comparing;

import business.global.pk.sss.newsss.PlayerDun;

/**
 * 高牌的大小比较(按顺序比较)
 */
public class HighCardComparingImpl extends AbstractComparing {

    @Override
	public int compare(PlayerDun o1, PlayerDun o2) {
        return this.seqComparing(o1, o2);
    }

}
