package core.server.sss;

import core.config.server.GameTypeMgr;
import core.server.GameServer;
import jsproto.c2s.cclass.GameType;

public class SSSAPP {

    public final static int gameTypeId = 1;

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
