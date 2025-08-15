package business.player.feature;

import java.util.List;

import cenum.VisitSignEnum;
import com.ddm.server.common.utils.CommTime;

import business.player.Player;
import com.ddm.server.http.server.HttpUtils;
import core.db.entity.clarkGame.PlayerBO;
import jsproto.c2s.iclass.S1005_PlayerInfo;

/**
 * 不是你的模块，请咨询作者，弄清楚逻辑再动
 * 
 * 
 * @date 2016年1月12日
 */
public class PlayerBase extends Feature {

    public PlayerBase(Player data) {
        super(data);
    }

    // 已经online - 登陆事件
    public void onConnect() {

    }
    // =========================== 接口 ========================
    public void sendMsg(String key) {
    }

    public void sendMsg(String key, List<String> msgList) {
    }

    public void sendSysMessage(String msg) {
    }

    public void sendPopupMessage(String msg) {
    }

    // 改这里了，记得改下面的worldProto
    public S1005_PlayerInfo fullInfo(boolean needSign) {
    	S1005_PlayerInfo info = new S1005_PlayerInfo();
        PlayerBO bo = player.getPlayerBO();
        info.pid = bo.getId();
        
        info.accountID = bo.getAccountID();
        info.name = bo.getName();
        info.icon = bo.getIcon();
        info.sex = bo.getSex();
        info.headImageUrl = bo.getHeadImageUrl();
        info.createTime = bo.getCreateTime();
        
        info.lv = bo.getLv();
        info.gmLevel = bo.getGmLevel();
        info.vipLv = bo.getVipLevel();
        info.vipExp = bo.getVipExp();
        info.totalRecharge = bo.getTotalRecharge();
        info.roomCard = getPlayer().getCurCityRoomCard();
        info.fastCard = bo.getFastCard();
        info.crystal = bo.getCrystal();
        info.gold = bo.getGold();
        info.realName = bo.getRealName();
        info.realNumber = bo.getRealNumber();
        info.currentGameType = bo.getCurrentGameType();
        info.time =CommTime.nowMS();
        info.startServerTime = CommTime.nowMS();
        info.cityId = bo.getCityId();
        if(needSign){
            info.sign = HttpUtils.Server_Charge_Key;
        }
        return info;
    }


    @Override
    public void loadDB() {
    }
}
