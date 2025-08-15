package core.db.service.clarkGame;

import com.ddm.server.annotation.Service;
import core.db.dao.clarkGame.BaseClarkGameDao;
import core.db.entity.clarkGame.PlayerCityCurrencyBO;
import core.db.other.Restrictions;
import core.db.persistence.CustomerDao;
import core.db.service.BaseService;
import java.util.Objects;

@Service(source = "clark_game")
public class PlayerCityCurrencyBOService implements BaseService<PlayerCityCurrencyBO> {
    private BaseClarkGameDao<PlayerCityCurrencyBO> playerCityCurrencyBODao = new BaseClarkGameDao<>(PlayerCityCurrencyBO.class);
    @Override
    public CustomerDao getDefaultDao() {
        return playerCityCurrencyBODao;
    }

    @Override
   public long saveIgnoreOrUpDate(PlayerCityCurrencyBO playerCityCurrencyBO) {
        PlayerCityCurrencyBO currencyBO = findOne(Restrictions.and(Restrictions.eq("cityId",playerCityCurrencyBO.getCityId()),Restrictions.eq("pid",playerCityCurrencyBO.getPid())),null);
        if(Objects.nonNull(currencyBO)) {
            playerCityCurrencyBO.setId(currencyBO.getId());
            playerCityCurrencyBO.setValue(currencyBO.getValue());
            playerCityCurrencyBO.setPid(currencyBO.getTime());
            return currencyBO.getId();
        } else {
            return saveIgnore(playerCityCurrencyBO);
        }

   }
}
