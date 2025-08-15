package core.network.client2game.handler.game;

import java.io.IOException;

import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;

import com.ddm.server.common.CommLogD;

import business.global.notice.NoticeMgr;
import business.player.Player;
import core.network.client2game.handler.PlayerHandler;
import jsproto.c2s.iclass.S2226_InitNotice;

/**
 * 公告列表
 * 
 * @author liyan
 *
 */
public class CSystemNotice extends PlayerHandler {

	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {

		S2226_InitNotice info = null;
		if (player != null) {
            if (player.isOnline()) {
                try {
                    NoticeMgr noticeMgr = NoticeMgr.getInstance();
                    noticeMgr.NoticeList.clear();
                    noticeMgr.init();
                    info = noticeMgr.getNoticeInfoList();
                } catch (Exception e) {
                    CommLogD.error("get notice's message error" + e);
//					request.error(ErrorCode.ErrorSysMsg,"get notice's message error");
                    request.response();

                    return;
                }
            } else {
                CommLogD.error("player is outline:" + player.getId());
//				request.error(ErrorCode.ErrorSysMsg,"player is outline:" + player.getId());
                request.response();

                return;
            }
        }
		if (null != info) {
			if (info.noticeInfoList != null && info.noticeInfoList.size() > 0) {
				request.response(info.noticeInfoList);
			} else {
				request.response();
			}
		} else {
			request.response();
		}
	}

}
