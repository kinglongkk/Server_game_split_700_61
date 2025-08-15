package core.db.service.clarkLog;

import com.ddm.server.annotation.Service;
import com.ddm.server.common.utils.CommTime;
import core.db.dao.clarkLog.BaseClarkLogDao;
import core.db.entity.clarkLog.ClubLevelRoomLogFlow;
import core.db.entity.clarkLog.UnionSportsPointProfitLogFlow;
import core.db.other.Criteria;
import core.db.persistence.CustomerDao;
import core.db.service.BaseService;

import java.util.Arrays;
import java.util.List;

@Service(source = "clark_log")
public class UnionSportsPointProfitLogFlowService implements BaseService<UnionSportsPointProfitLogFlow> {
    private BaseClarkLogDao<UnionSportsPointProfitLogFlow> unionSportsPointProfitLogFlowBaseClarkLogDao = new BaseClarkLogDao<>(UnionSportsPointProfitLogFlow.class);

    @Override
    public CustomerDao getDefaultDao() {
        return unionSportsPointProfitLogFlowBaseClarkLogDao;
    }



}




