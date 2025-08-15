package business.dzpk.c2s.cclass;

import jsproto.c2s.cclass.BaseSendMsg;
import lombok.Data;

import java.util.List;

/**
 * 通知 发牌
 */
@Data
public class DZPKFaPai extends BaseSendMsg {
    /**
     * 当前发的牌
     */
    private List<Integer> curCardLsit;//
    /**
     * 已经发的公共牌
     */
    private List<Integer> publicCardLsit;//

    public DZPKFaPai(List<Integer> curCardLsit, List<Integer> publicCardLsit) {
        this.curCardLsit = curCardLsit;
        this.publicCardLsit = publicCardLsit;
    }
}
