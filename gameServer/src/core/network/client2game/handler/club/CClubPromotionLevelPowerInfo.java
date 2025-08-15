package core.network.client2game.handler.club;

import business.global.club.ClubMgr;
import business.player.Player;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import jsproto.c2s.iclass.club.CClub_PromotionList;

import java.io.IOException;

/**
 * 推广列表
 */
public class CClubPromotionLevelPowerInfo extends PlayerHandler {
    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {
        CClub_PromotionList club_promotionList=new Gson().fromJson(message, CClub_PromotionList.class);
        SData_Result result = ClubMgr.getInstance().getClubMemberMgr().getClubPromotionLevelPowerInfo(club_promotionList,player.getPid());
        if(ErrorCode.Success.equals(result.getCode())) {
            request.response(result.getData());
        } else {
            request.error(result.getCode(),result.getMsg());
        }
    }
}
