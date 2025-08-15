package business.dzpk.c2s.cclass;

import jsproto.c2s.cclass.pk.base.BasePKRoom_RoundPos;
import lombok.Setter;

import java.util.List;

/**
 * 具体回合操作位置
 */
@Setter
public class DZPK_RoundPos extends BasePKRoom_RoundPos {
   private List<Integer> betOptions;
}		
