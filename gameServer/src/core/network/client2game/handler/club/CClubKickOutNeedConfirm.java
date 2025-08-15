package core.network.client2game.handler.club;

import business.global.club.Club;
import business.global.club.ClubMember;
import business.global.club.ClubMgr;
import business.global.union.Union;
import business.global.union.UnionMgr;
import business.player.Player;
import business.player.PlayerMgr;
import cenum.ItemFlow;
import cenum.RoomTypeEnum;
import com.ddm.server.common.utils.CommMath;
import com.ddm.server.common.utils.CommTime;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.db.entity.clarkGame.UnionDynamicBO;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import jsproto.c2s.cclass.club.ClubKickOutNeedConfirm;
import jsproto.c2s.cclass.club.Club_define.Club_Player_Status;
import jsproto.c2s.cclass.union.UnionDefine;
import jsproto.c2s.iclass.club.CClub_PlayerStatusChange;

import java.io.IOException;
import java.util.Objects;

/**
 * 改变俱乐部玩家状态
 *
 * @author zaf
 */
public class CClubKickOutNeedConfirm extends PlayerHandler {

    @Override
    public void handle(Player player, WebSocketRequest request, String message)
            throws WSException, IOException {
        final CClub_PlayerStatusChange req = new Gson().fromJson(message, CClub_PlayerStatusChange.class);

        ClubMember myClubMember = ClubMgr.getInstance().getClubMemberMgr().find(player.getPid(), req.clubId);
        if (null == myClubMember) {
            request.error(ErrorCode.NotAllow, "CClubChangePlayerStatus not find myClubMember");
            return;
        }
        Club club = ClubMgr.getInstance().getClubListMgr().findClub(req.clubId);
        if (null == club) {
            request.error(ErrorCode.NotAllow, "CClubChangePlayerStatus not find club  clubId = " + req.clubId);
            return;
        }
        if (club.getClubListBO().getOwnerID() == req.pid) {
            request.error(ErrorCode.NotAllow, "CClubChangePlayerStatus club.getClubListBO().getOwnerID() == req.pid" + req.clubId);
            return;
        }
        ClubMember tempMember = ClubMgr.getInstance().getClubMemberMgr().find(req.pid, req.clubId);
        if (null == tempMember) {
            request.error(ErrorCode.NotAllow, "CClubChangePlayerStatus not find clubmember pid=" + req.pid + ", clubId = " + req.clubId);
            return;
        }
        ClubKickOutNeedConfirm clubKickOutNeedConfirm=new ClubKickOutNeedConfirm();
        clubKickOutNeedConfirm.setPid(req.pid);
        if(tempMember.getClubMemberBO().getCaseSportsPoint()!=0){
            clubKickOutNeedConfirm.setType(1);
        }
        request.response(clubKickOutNeedConfirm);
    }


}
