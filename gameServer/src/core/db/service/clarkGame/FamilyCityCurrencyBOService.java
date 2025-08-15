package core.db.service.clarkGame;

import com.ddm.server.annotation.Service;
import core.db.dao.clarkGame.BaseClarkGameDao;
import core.db.entity.clarkGame.FamilyCityCurrencyBO;
import core.db.other.Restrictions;
import core.db.persistence.CustomerDao;
import core.db.service.BaseService;

import java.util.Objects;

@Service(source = "clark_game")
public class FamilyCityCurrencyBOService implements BaseService<FamilyCityCurrencyBO> {
    private BaseClarkGameDao<FamilyCityCurrencyBO> playerCityCurrencyBODao = new BaseClarkGameDao<>(FamilyCityCurrencyBO.class);
    @Override
    public CustomerDao getDefaultDao() {
        return playerCityCurrencyBODao;
    }

    @Override
   public long saveIgnoreOrUpDate(FamilyCityCurrencyBO familyCityCurrencyBO) {
        FamilyCityCurrencyBO currencyBO = findOne(Restrictions.and(Restrictions.eq("cityId",familyCityCurrencyBO.getCityId()),Restrictions.eq("familyId",familyCityCurrencyBO.getFamilyId())),null);
        if(Objects.nonNull(currencyBO)) {
            familyCityCurrencyBO.setId(currencyBO.getId());
            familyCityCurrencyBO.setValue(currencyBO.getValue());
            familyCityCurrencyBO.setTime(currencyBO.getTime());
            return currencyBO.getId();
        } else {
            return saveIgnore(familyCityCurrencyBO);
        }

   }
}
