package business.global.pk.nn;

import business.nn.c2s.iclass.CNN_CallBacker;

import com.ddm.server.common.utils.CommTime;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import business.nn.c2s.cclass.NN_define.NN_GameStatus;

/**
 * 牛牛一局游戏逻辑
 *
 * @author Clark
 */

public class NNRoomSet_NNSZ extends NNRoomSet {


    public NNRoomSet_NNSZ(NNRoom room) {
        super(room);
    }

    /*
     * 确定庄家
     * **/
    @Override
    public void setDefeault() {
        int backerPos = -1;
        boolean isRandBackerPos = false;
        if (this.room.getCurSetID() == 1) {
            backerPos = this.getRandPos();
            isRandBackerPos = true;
        } else {
            backerPos = this.room.getCallBacker();
        }
        this.setBackerPos(backerPos, isRandBackerPos);
    }

    /**
     * 每200ms更新1次   秒
     *
     * @param sec
     * @return T 是 F 否
     */
    public boolean update(int sec) {
        boolean isClose = false;
        switch (this.getStatus()) {
            case NN_GAME_STATUS_ONSURECALLBACKER:
                if (CommTime.nowMS() - this.startMS >= this.getWaitTimeByStatus()) {
                    this.onSureCallbacker();
                }
                break;
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
//                    this.setAllGameReady(true);
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
    public int getSendCardNumber() {
        // TODO 自动生成的方法存根
        int count = 0;
        if (this.getStatus() == NN_GameStatus.NN_GAME_STATUS_SENDCARD_SECOND) {
            count = 5;
        }
        return count;
    }

    @Override
    public NN_GameStatus getStartStatus() {
        // TODO 自动生成的方法存根
        return NN_GameStatus.NN_GAME_STATUS_ONSURECALLBACKER;
    }

    @Override
    public void resultCalc() {
        // TODO 自动生成的方法存根
        NNGameResult result = new NNGameResult(this.room);
        result.calcByCallBacker();

        int maxCrawType = this.crawTypeList.get(0);
        int pos = 0;
        for (int i = 0; i < this.room.getMaxPlayerNum(); i++) {
            if (!this.playingList.get(i)) continue;
            int tempType = this.crawTypeList.get(i);
            if (maxCrawType < tempType) {
                maxCrawType = tempType;
                pos = i;
            } else if (maxCrawType == tempType) {
                NNRoomPos tempRoomPos = (NNRoomPos) this.room.getRoomPosMgr().getPosByPosID(i);
                NNRoomPos maxRoomPos = (NNRoomPos) this.room.getRoomPosMgr().getPosByPosID(pos);
                if (NNGameLogic.CompareCard(tempRoomPos.privateCards, maxRoomPos.privateCards)) {
                    maxCrawType = tempType;
                    pos = i;
                }
            }
        }

        if (maxCrawType >= 10) {
            this.room.setCallBacker(pos);
        } else {
            this.room.setCallBacker(this.getBackerPos());
        }

    }


    @Override
    public void onHogEnd() {
        // TODO 自动生成的方法存根

    }
}
