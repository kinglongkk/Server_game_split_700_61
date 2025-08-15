package core.db.service.clarkGame;

import com.ddm.server.annotation.Service;
import core.db.dao.clarkGame.BaseClarkGameDao;
import core.db.entity.clarkGame.CityGiveBO;
import core.db.entity.clarkGame.DiscountBO;
import core.db.persistence.CustomerDao;
import core.db.service.BaseService;

@Service(source = "clark_game")
public class CityGiveBOService implements BaseService<CityGiveBO> {
    private BaseClarkGameDao<CityGiveBO> discountBODao = new BaseClarkGameDao<>(CityGiveBO.class);
    @Override
    public CustomerDao<CityGiveBO> getDefaultDao() {
        return discountBODao;
    }
}


