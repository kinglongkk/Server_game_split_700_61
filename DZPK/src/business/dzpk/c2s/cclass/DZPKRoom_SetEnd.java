package business.dzpk.c2s.cclass;

import jsproto.c2s.cclass.pk.base.BasePKRoom_SetEnd;
import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
public class DZPKRoom_SetEnd extends BasePKRoom_SetEnd {
    /**
     * 公共牌
     */
    private List<Integer> publicCardList;
}	
