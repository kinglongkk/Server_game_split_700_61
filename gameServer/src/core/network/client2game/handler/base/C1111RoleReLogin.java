package core.network.client2game.handler.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import business.global.GM.MaintainServerMgr;
import business.global.club.ClubListMgr;
import business.global.club.ClubMember;
import business.global.club.ClubMemberMgr;
import business.global.club.ClubMgr;
import business.shareplayer.SharePlayerMgr;
import core.db.entity.clarkGame.ClubMemberBO;
import org.apache.commons.lang3.StringUtils;

import com.ddm.server.common.Config;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.player.Player;
import business.player.PlayerMgr;
import business.player.feature.PlayerBase;
import core.config.server.GameTypeMgr;
import core.network.client2game.ClientSession;
import core.network.client2game.handler.BaseHandler;
import jsproto.c2s.cclass.GameType;
import jsproto.c2s.iclass.C1111_RoleReLogin;

public class C1111RoleReLogin extends BaseHandler {

    @Override
    public void handle(WebSocketRequest request, String message) throws IOException {

        final C1111_RoleReLogin req = new Gson().fromJson(message, C1111_RoleReLogin.class);
        // 玩家登陆。
        this.roleLogin(request, req);
    }
    /**
     * 玩家登陆。
     *
     * @param request
     * @param req
     */
    private void roleLogin(WebSocketRequest request, C1111_RoleReLogin req) {
        long accountID = req.accountID;
        String uuid = req.uuid;
        String gameName = req.gameName;
        if (StringUtils.isEmpty(gameName)) {
            // 游戏类型为空
            request.error(ErrorCode.NotAllow, "gameName error gameName:{}", gameName);
            return;
        }
        gameName = gameName.toUpperCase();
        // 通过指定的账号ID，获取玩家信息
        Player player = PlayerMgr.getInstance().getPlayerByAccountID(accountID);
        if (Objects.isNull(player)) {
            // 找不到指定的玩家
            request.error(ErrorCode.ErrorSysMsg, "accountID error :{}", accountID);
            return;
        }
        // 检查是否处于维护中
        if (!MaintainServerMgr.getInstance().checkUnderMaintenance(request,player)) {
            // 维护中
            return;
        }
        if (player.isBannedLogin(request)) {
            return;
        }
        // 检查 uuid是否正确。先注释掉客户端目前没有解决方案傅哥需求
//        if (!player.checkuUID(uuid)) {
//            request.error(ErrorCode.ErrorSysMsg, "checkuUID uuid error uuid:%s,gameName:%s", uuid, gameName);
//            return;
//        }
        // 获取连接
        ClientSession session = (ClientSession) request.getSession();
        session.setValid(true);
        session.setAccountID(accountID);
        session.setPlayerSid(player.getPlayerBO().getSid());
        PlayerMgr.getInstance().uuidConnectPlayer(session, player);
        if(Config.isShare()) {
            //更新缓存玩家数据
            SharePlayerMgr.getInstance().getPlayer(player);
//            ArrayList<ClubMember> clubMembers = ClubMgr.getInstance().getClubMemberMgr().findAllMember(player.getPid());
//            for(ClubMember clubMember:clubMembers){
//                //更新玩家亲友圈数据
//                ClubMgr.getInstance().getClubMemberMgr().onUpdateMemberShare(clubMember.getId());
//            }
        }
        request.response(player.getFeature(PlayerBase.class).fullInfo(true));
    }


}
