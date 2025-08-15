package core.network.client2game.handler.pdk;

import business.global.pk.pdk.PDKRoom;
import business.global.room.RoomMgr;
import business.global.room.base.AbsRoomPos;
import business.pdk.c2s.cclass.PDKRoom_RecordPosInfo;
import business.pdk.c2s.iclass.SPDK_SetInfo;
import business.pdk.c2s.iclass.SPDK_UserInfo;
import business.pdk.c2s.iclass.SPDK_XResult;
import business.player.Player;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;
import jsproto.c2s.iclass.room.CBase_GetRoomInfo;

import java.io.IOException;
import java.util.List;

public class CPDKRoomXRecord extends PlayerHandler {

    @SuppressWarnings("rawtypes")
    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws IOException {
        final CBase_GetRoomInfo req = new Gson().fromJson(message, CBase_GetRoomInfo.class);
        long roomID = req.getRoomID();

        PDKRoom room = (PDKRoom) RoomMgr.getInstance().getRoom(roomID);
        if (null == room){
            request.error(ErrorCode.NotAllow, "CHBMJRoomEndResult not find room:"+roomID);
            return;
        }
        SPDK_XResult result = new SPDK_XResult();
        for(AbsRoomPos roomPos: room.getRoomPosMgr().getPosList()){
            result.getUserInfo().put(roomPos.getPosID(),new SPDK_UserInfo(roomPos.getName(),roomPos.getPid(),roomPos.getPoint()));
            if(roomPos.getResults()!=null){
                List<Integer> pointList = ((PDKRoom_RecordPosInfo) roomPos.getResults()).getPointList();
                for(int i =0;i<pointList.size();i++){
                    if(result.getSetInfo().size()<i+1){
                        result.getSetInfo().add(new SPDK_SetInfo(i+1));
                    }
                    SPDK_SetInfo setInfo = result.getSetInfo().get(i);
                    setInfo.getPoint().put(roomPos.getPosID(),pointList.get(i));
                }
            }
        }
        request.response(result);
    }
}
