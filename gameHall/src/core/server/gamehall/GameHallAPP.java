package core.server.gamehall;


import core.config.server.GameTypeMgr;
import core.server.GameServer;
import jsproto.c2s.cclass.GameType;

/**
 * 大厅启动项
 * @author Administrator
 *
 */
public class GameHallAPP {
	public static void main(String[] args) throws Exception {
		GameServer app = new GameServer();
		app.init(args);
	}
}
