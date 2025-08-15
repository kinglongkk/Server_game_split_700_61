package core.server.nn;

import core.config.server.GameTypeMgr;
import core.server.GameServer;
import jsproto.c2s.cclass.GameType;

/**
 * @author zhujianming
 * @date 2020-07-31 10:03
 */
public class NNAPP {

    public final static int gameTypeId = 4;

    /**
     * 游戏类型
     * @return
     */
    public static GameType GameType() {
        return GameTypeMgr.getInstance().gameType(gameTypeId);
    }

    public static void main(String[] args) throws Exception {
        GameServer app = new GameServer();
        app.init(args);
    }
}
