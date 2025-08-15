package business.global.pk.dzpk;

import business.global.pk.AbsPKSetPos;
import business.global.pk.AbsPKSetPosMgr;
import business.global.pk.AbsPKSetRoom;
import jsproto.c2s.cclass.pk.base.BasePKSet_Pos;

import java.util.ArrayList;
import java.util.List;

public class DZPKSetPosMgr extends AbsPKSetPosMgr {

    public DZPKSetPosMgr(AbsPKSetRoom set) {
        super(set);
    }

    @Override
    public List<BasePKSet_Pos> getAllPlayBackNotify() {
        List<BasePKSet_Pos> setPosList = new ArrayList<BasePKSet_Pos>();
        for (int i = 0; i < this.set.getRoom().getPlayerNum(); i++) {
            AbsPKSetPos setPos = this.set.getPKSetPos(i);
            if (null != setPos) {
                setPosList.add(setPos.getPlayBackNotify());
            }
        }
        return setPosList;
    }


}		
