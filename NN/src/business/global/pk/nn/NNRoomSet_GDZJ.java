package business.global.pk.nn;

import business.global.room.base.AbsRoomPos;
import business.nn.c2s.cclass.NN_define;
import business.nn.c2s.iclass.CNN_CallBacker;
import business.nn.c2s.iclass.CNN_Shimosho;
import com.ddm.server.common.utils.CommTime;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;


/**
 * 牛牛一局游戏逻辑
 *
 * @author Clark
 */

public class NNRoomSet_GDZJ extends NNRoomSet {

    public static final int ONSHIMOSHOCOUNTSET = 3;

    public NNRoomSet_GDZJ(NNRoom room) {
        super(room);
    }

    /*
     * 设置默认参数
     * **/
    @Override
    public void setDefeault() {
        AbsRoomPos roomPos = this.room.getRoomPosMgr().getPosByPosID(0);
        this.setBackerPos(roomPos.getPosID(), false);
    }

    /**
     * 每200ms更新1次   秒
     *
     * @param sec
     * @return T 是 F 否
     */
    @Override
    public boolean update(int sec) {
        boolean isClose = false;
        switch (this.getStatus()) {
            case NN_GAME_STATUS_BET:
                if (CommTime.nowMS() - this.startMS >= this.getWaitTimeByStatus()) {
                    this.onBetEnd();
                }
                break;
            case NN_GAME_STATUS_SENDCARD_SECOND:
                if (CommTime.nowMS() - this.startMS >= this.getWaitTimeByStatus()) {
                    this.onSendCardEnd();
                }
                break;
            case NN_GAME_STATUS_RESULT:
                if (CommTime.nowMS() - this.startMS >= this.getWaitTimeByStatus()) {
                    NNRoomPos pos = (NNRoomPos) this.room.getRoomPosMgr().getPosByPosID(0);
                    if (this.room.roomCfg.shangzhuangfenshu > 0 && pos.getPoint() <= 0) {
                        this.room.endRoom();
                        return true;
                    } else {
//                        this.setAllGameReady(true);
                    }
                }
                isClose = true;
                break;
            default:
                break;
        }

        return isClose;
    }

    @Override
    public void onCallBacker(WebSocketRequest request, CNN_CallBacker Backer) {
        request.error(ErrorCode.NotAllow, "onCallBacker  do not callbacer  ");
    }


    @Override
    public void onShimosho(WebSocketRequest request, CNN_Shimosho addBet) {
        if (this.room.roomCfg.shangzhuangfenshu <= 0) {
            request.error(ErrorCode.NotAllow, "onShimosho is error do not shimosho");
            return;
        }

        if (ONSHIMOSHOCOUNTSET > this.room.getCurSetID() || this.room.roomCfg.shangzhuangfenshu <= 0) {
            request.error(ErrorCode.NotAllow, "onShimosho is error do not  curset:" + this.room.getCurSetID());
            return;
        }

        this.room.endRoom();
        request.response();
    }

    @Override
    public int getSendCardNumber() {
        // TODO 自动生成的方法存根
        int count = 0;
        if (this.getStatus() == NN_define.NN_GameStatus.NN_GAME_STATUS_SENDCARD_SECOND) {
            count = 5;
        }
        return count;
    }

    @Override
    public NN_define.NN_GameStatus getStartStatus() {
        // TODO 自动生成的方法存根
        return NN_define.NN_GameStatus.NN_GAME_STATUS_BET;
    }

    @Override
    public void resultCalc() {
        // TODO 自动生成的方法存根
        NNGameResult result = new NNGameResult(this.room);
        if(room.getRoomCfg().shangzhuangfenshu > 0){
            result.calcJinMuYuan();
        }else{
            result.calcByCallBacker();
        }
    }

    @Override
    public void onHogEnd() {
    }
}
