package core.network.client2game.handler.family;

import java.io.IOException;

import com.ddm.server.websocket.handler.requset.WebSocketRequest;

import business.player.Player;
import business.player.feature.PlayerFamily;
import core.network.client2game.handler.PlayerHandler;


public class C2119InitPlayerFamily extends PlayerHandler {
	
	
    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws IOException {
    	handle(player, request);
    }
    
    public static void handle(Player player, WebSocketRequest request){
    	PlayerFamily playerFamily = player.getFeature(PlayerFamily.class);
    	request.response(playerFamily.getPlayerFamilyInfo());
    }
}