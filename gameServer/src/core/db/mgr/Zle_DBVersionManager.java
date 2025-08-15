package core.db.mgr;

import com.ddm.server.common.CommLogD;
import core.db.version.DBVersionManager;
import core.db.DataBaseMgr;

import java.sql.Connection;

/**
 * The implementation class of DB version manager and automatic update
 * 
 * @change Clark
 */
public class Zle_DBVersionManager extends DBVersionManager {
    private static Zle_DBVersionManager instance = null;

    public static Zle_DBVersionManager getInstance() {
        if (null == instance) {
            instance = new Zle_DBVersionManager();
        }

        return instance;
    }

    @Override
    public Connection getConnection() {
        try {
            return DataBaseMgr.get(getSourceName()).getConnection();
        }catch (Exception e){
            CommLogD.error("getConnection fail:db_zle");
        }
        return null;
    }

    @Override
    public String getSourceName() {
        return "db_zle";
    }
}
