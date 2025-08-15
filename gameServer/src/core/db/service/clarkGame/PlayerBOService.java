package core.db.service.clarkGame;

import com.ddm.server.annotation.Autowired;
import com.ddm.server.annotation.Service;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.GameConfig;
import core.db.DataBaseMgr;
import core.db.persistence.CustomerDao;
import core.db.dao.clarkGame.PlayerBODao;
import core.db.entity.clarkGame.PlayerBO;
import core.db.other.Criteria;
import core.db.service.BaseService;
import core.network.client2game.ClientSession;

@Service(source = "clark_game")
public class PlayerBOService implements BaseService<PlayerBO> {

    @Autowired
    private PlayerBODao playerBODao;

    public long createPlayer(ClientSession session, int serverID,String headImageUrl,
                             int sex, long familyID, int real_referer, int tourist) {
        try {
            String nameSql = "(SELECT CONCAT(\"游客_\",auto_increment) FROM information_schema.`TABLES` WHERE TABLE_SCHEMA='"+DataBaseMgr.get("clark_game").getConnection().getCatalog()+"' AND TABLE_NAME='player')";
            String accountSql = "(SELECT auto_increment FROM information_schema.`TABLES` WHERE TABLE_SCHEMA='"+DataBaseMgr.get("clark_game").getConnection().getCatalog()+"' AND TABLE_NAME='player')";
            return playerBODao.insertAndGetGeneratedKeys("INSERT INTO `player` (`name`,`accountID`,`sid`,`wx_unionid`,`headImageUrl`,`sex`,`familyID`,`real_referer`,`icon`," +
                    "" + "`lv`,`vipLevel`,`gmLevel`,`roomCard`,`crystal`,`gold`"+
                    ") VALUES ("+nameSql+","+accountSql+","+serverID+",\""+session.getWxUnionid()+"\",\""+headImageUrl+"\","+sex+","+familyID+",\""+real_referer+"\","+tourist
                    +",0,0,0,"+GameConfig.NewPlayerCard()+","+GameConfig.NewPlayerCrystal()+","+GameConfig.NewPlayerGold()
                    +")");
        } catch (Exception e){
            CommLogD.error("createPlayer:"+e.getMessage());
            return -1;
        }
    }

    public Long count(Criteria criteria){
        return playerBODao.count(criteria);
    }

    @Override
    public CustomerDao getDefaultDao() {
        return playerBODao;
    }
}
