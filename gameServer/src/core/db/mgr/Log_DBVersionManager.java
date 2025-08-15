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
public class Log_DBVersionManager extends DBVersionManager {
    private static Log_DBVersionManager instance = null;

    public static Log_DBVersionManager getInstance() {
        if (null == instance) {
            instance = new Log_DBVersionManager();
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
            CommLogD.error("getConnection fail:clark_log");
        }
        return null;
    }


    @Override
    public String getSourceName(){
        return "clark_log";
    }
}
