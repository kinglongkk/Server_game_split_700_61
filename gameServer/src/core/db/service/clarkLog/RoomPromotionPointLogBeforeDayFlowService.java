package core.db.service.clarkLog;

import com.ddm.server.annotation.Service;
import com.ddm.server.common.utils.CommTime;
import core.db.dao.clarkLog.BaseClarkLogDao;
import core.db.entity.clarkLog.ClubLevelRoomLogBeforeDayFlow;
import core.db.entity.clarkLog.RoomPromotionPointLogBeforeDayFlow;
import core.db.other.Criteria;
import core.db.other.Restrictions;
import core.db.persistence.CustomerDao;
import core.db.service.BaseService;

import java.util.Arrays;
import java.util.List;

@Service(source = "clark_log")
public class RoomPromotionPointLogBeforeDayFlowService implements BaseService<ClubLevelRoomLogBeforeDayFlow> {
    private BaseClarkLogDao<RoomPromotionPointLogBeforeDayFlow> roomPromotionPointLogBeforeDayFlowBaseClarkLogDao = new BaseClarkLogDao<>(RoomPromotionPointLogBeforeDayFlow.class);

    @Override
    public CustomerDao getDefaultDao() {
        return roomPromotionPointLogBeforeDayFlowBaseClarkLogDao;
    }

    public RoomPromotionPointLogBeforeDayFlow findOne(long id) {
        return roomPromotionPointLogBeforeDayFlowBaseClarkLogDao.findOne(Restrictions.and(Restrictions.eq("id", id)), null,null);
    }

}




