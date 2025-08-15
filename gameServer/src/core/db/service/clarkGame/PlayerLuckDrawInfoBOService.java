package core.db.service.clarkGame;

import com.ddm.server.annotation.Service;
import com.ddm.server.common.ehcache.DataConstants;
import com.ddm.server.common.ehcache.EhCacheFactory;
import com.ddm.server.common.ehcache.configuration.DefaultCacheConfiguration;
import com.ddm.server.common.utils.CommTime;
import core.db.dao.clarkGame.BaseClarkGameDao;
import core.db.entity.clarkGame.PlayerLuckDrawInfoBO;
import core.db.other.AsyncInfo;
import core.db.other.Restrictions;
import core.db.persistence.BaseDao;
import core.db.persistence.CustomerDao;
import core.db.service.BaseService;
import jsproto.c2s.cclass.QueryIdItem;
import jsproto.c2s.cclass.union.UnionScoreDividedIntoValueItem;
import jsproto.c2s.cclass.union.UnionScorePercentItem;

@Service(source = "clark_game")
public class PlayerLuckDrawInfoBOService implements BaseService<PlayerLuckDrawInfoBO> {
    private BaseClarkGameDao<PlayerLuckDrawInfoBO> playerLuckDrawInfoBODao = new BaseClarkGameDao<>(PlayerLuckDrawInfoBO.class);

    @Override
    public CustomerDao getDefaultDao() {
        return playerLuckDrawInfoBODao;
    }

//    @Override
//    public long saveOrUpDate(PlayerLuckDrawInfoBO element) {
//        PlayerLuckDrawInfoBO idItem = findOne(Restrictions.and(Restrictions.eq("pid", element.getPid()), Restrictions.eq("configId", element.getConfigId())),null);
//        if (idItem != null) {
//            element.setId(idItem.getId());
////            System.out.println();
////            element.setCount(idItem.getType() <= 0?element.getCount():element.getCount() + idItem.getCount());
//            return update(element);
//        }
//        return saveIgnore(element);
//
//    }
}



