package business.qzmj.c2s.cclass;

import cenum.mj.HuType;
import jsproto.c2s.cclass.room.AbsBaseResults;

/**
 * 红中麻将总结算信息
 * @author Huaxing
 *
 */
public class QZMJResults extends AbsBaseResults {
    private int danYouPoint;//单游次数
    private int shuangYouPoint;//双游次数
    private int sanYouPoint;//三游次数
    private int otherPoint;//其他胡次数
    public void addZimoPoint(HuType hType) {
        switch (hType){
            case ZiMo:
            case DanYou:
            case ShuangYou:
            case SanJinYou:
            case TianHu:
            case SanJinDao:
            case SiJinDao:
            case QiangJin:
                this.setZiMoPoint(this.getZiMoPoint()+1);
                break;
        }

    }

    public void addHuType(HuType huType) {
        switch (huType) {
            case NotHu:
                break;
            case DanYou:
                this.danYouPoint += 1;
                break;
            case ShuangYou:
                this.shuangYouPoint += 1;
                break;
            case SanYou:
                this.sanYouPoint += 1;
                break;
            default:
                this.otherPoint += 1;
                break;
        }
    }
}
