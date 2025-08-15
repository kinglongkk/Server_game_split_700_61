package core.network.client2game.handler.club;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import business.global.club.Club;
import business.global.club.ClubMember;
import business.global.config.GameListConfigMgr;
import business.player.feature.PlayerCityCurrency;
import business.shareplayer.SharePlayerMgr;
import cenum.VisitSignEnum;
import com.ddm.server.common.Config;
import com.ddm.server.websocket.def.ErrorCode;
import core.network.http.proto.SData_Result;
import jsproto.c2s.cclass.club.Club_define;
import jsproto.c2s.cclass.room.RoomInfoItem;
import jsproto.c2s.iclass.club.CClub_GetAllRoom;
import jsproto.c2s.iclass.club.SClub_DiamondsNotEnough;
import jsproto.c2s.iclass.club.SClub_GetAllRoom;
import business.global.club.ClubMgr;
import business.player.Player;

import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import core.network.client2game.handler.PlayerHandler;
import jsproto.c2s.iclass.club.SClub_GetAllRoomGroup;

/**
 * 获取俱乐部房间
 *
 * @author zaf
 */
public class CClubGetAllRoom extends PlayerHandler {

    @Override
    public void handle(Player player, WebSocketRequest request, String message)
            throws WSException, IOException {
        final CClub_GetAllRoom req = new Gson().fromJson(message, CClub_GetAllRoom.class);
        Club club = ClubMgr.getInstance().getClubListMgr().findClub(req.getClubId());
        if (null == club) {
            request.error(ErrorCode.CLUB_NOT_EXIST,"CLUB_NOT_EXIST");
            return;
        }
        if (GameListConfigMgr.getInstance().banCity(club.getClubListBO().getCityId())) {
            request.error(ErrorCode.BAN_CITY,"BAN_CITY");
            return;
        }
        SData_Result result = ClubMgr.getInstance().getClubMemberMgr().checkExistUnion(club);
        if (!ErrorCode.NotAllow.equals(result.getCode())) {
            request.error(result.getCode(),result.getMsg());
            return;
        }
        ClubMember clubMember = ClubMgr.getInstance().getClubMemberMgr().find(player.getPid(), req.getClubId(), Club_define.Club_Player_Status.PLAYER_JIARU);
        ClubMember clubMember1=ClubMgr.getInstance().getClubMemberMgr().find(player.getPid(),req.getClubId(),Club_define.Club_Player_Status.PLAYER_TUICHU_WEIPIZHUN);
        if (null == clubMember) {
            if(Objects.nonNull(clubMember1)){
                request.error(ErrorCode.CLUB_CLUBMEMBER_EXIST_APPLOVE,"CLUB_CLUBMEMBER_EXIST_APPLOVE");
                return;
            }
            request.error(ErrorCode.CLUB_NOTCLUBMEMBER,"CLUB_NOTCLUBMEMBER");
            return;
        }


        request.response();
        if (clubMember.isBanGame()) {
            return;
        }
        player.pushProto(SClub_GetAllRoom.make(club.getClubListBO().getId(),ClubMgr.getInstance().onClubGetAllRoom(req.getClubId(),req.getPageNum()),req.getPageNum()));

        this.checkDiamondsAttention(club,clubMember,player);

    }


    /**
     * 钻石消耗通知 全员 或者只通知管理
     * @param club
     */
    private void checkDiamondsAttention(Club club,ClubMember clubMember,Player player) {
        if(Objects.isNull(club)){
            return;
        }
        //已经加入赛事的话 就只需要通知赛事的
        if (club.getClubListBO().getUnionId() > 0L) {
            return;
        }
        //没有加入赛事的话 通知亲友圈
        //获取亲友圈圈主的钻石
        int diamondsValue=club.getOwnerPlayer().getFeature(PlayerCityCurrency.class).getPlayerCityCurrencyBO(club.getClubListBO().getCityId()).getValue();
        //如果是管理员的话
        if(clubMember.isMinister()){
            if(club.getClubListBO().getDiamondsAttentionMinister()>diamondsValue&&!player.isDiamondsAttentionMinister()){
                player.setDiamondsAttentionMinister(true);
                player.pushProto(SClub_DiamondsNotEnough.make(club.getClubListBO().getId(),club.getClubListBO().getName(),club.getClubListBO().getDiamondsAttentionMinister()));
            }
        }else {
            //如果钻石小于设定的值则发起通知
            if(club.getClubListBO().getDiamondsAttentionAll()>diamondsValue&&!player.isDiamondsAttentionAll()){
                player.setDiamondsAttentionAll(true);
                player.pushProto(SClub_DiamondsNotEnough.make(club.getClubListBO().getId(),club.getClubListBO().getName(),club.getClubListBO().getDiamondsAttentionAll()));
            }
        }
        //如果钻石数量超过的时候 把通知的标志设置回来
        if(diamondsValue>club.getClubListBO().getDiamondsAttentionAll()&&player.isDiamondsAttentionAll()){
            player.setDiamondsAttentionAll(false);
        }
        if(diamondsValue>club.getClubListBO().getDiamondsAttentionMinister()&&player.isDiamondsAttentionMinister()){
            player.setDiamondsAttentionMinister(false);
        }
    }
}
