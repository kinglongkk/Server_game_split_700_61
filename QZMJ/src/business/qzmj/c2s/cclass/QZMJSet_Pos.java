package business.qzmj.c2s.cclass;

import jsproto.c2s.cclass.mj.BaseMJSet_Pos;

/**
 * 红中麻将 配置
 *
 * @author Clark
 */
// 一局中各位置的信息
public class QZMJSet_Pos extends BaseMJSet_Pos {
    private boolean isTing = false;
    private int lianZhuangNum = 0;

    public void setTing(boolean isTing) {
        this.isTing = isTing;
    }

    public void setLianZhuangNum(int lianZhuangNum) {
        this.lianZhuangNum = lianZhuangNum;
    }
}
