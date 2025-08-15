package core.server.qzmj;

import core.config.server.GameTypeMgr;
import core.server.GameServer;
import jsproto.c2s.cclass.GameType;

/**
 * 单游戏启动项
 * @author Administrator
 *
 */
public class QZMJAPP {

	public final static int gameTypeId = 6;

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
