package core.db.mgr;

import java.sql.Connection;

import com.ddm.server.common.CommLogD;
import core.db.version.DBVersionManager;
import core.db.DataBaseMgr;

/**
 * The implementation class of DB version manager and automatic update
 * 
 * @change Clark
 */
public class Game_DBVersionManager extends DBVersionManager {
    private static Game_DBVersionManager instance = null;

    public static Game_DBVersionManager getInstance() {
        if (null == instance) {
            instance = new Game_DBVersionManager();
        }
        return instance;
    }

    /**
     * 新版db获取连接
     * @return
     */
    @Override
    public Connection getConnection(){
        try {
            return DataBaseMgr.get(getSourceName()).getConnection();
        }catch (Exception e){
            CommLogD.error("getConnection fail:clark_game");
        }
        return null;
    }

    @Override
    public String getSourceName(){
        return "clark_game";
    }

}
