package business.global.pk.nn;



import business.nn.c2s.cclass.NN_define.NN_GameStatus;
import business.nn.c2s.iclass.CNN_AddBet;
import business.nn.c2s.iclass.CNN_CallBacker;
import com.ddm.server.common.utils.CommTime;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;


/**
 * 牛牛一局游戏逻辑
 * 
 * @author Clark
 *
 */

public class NNRoomSet_TBNN extends NNRoomSet{
	

	public NNRoomSet_TBNN(NNRoom room) {
		super(room);
	}
	

	/**
	 * 每200ms更新1次   秒
	 * @param sec 
	 * @return T 是 F 否
	 */
	public boolean update(int sec) {
		boolean isClose = false;
		switch(this.getStatus()){
			case NN_GAME_STATUS_SENDCARD_SECOND:
				if( CommTime.nowMS() - this.startMS >=  this.getWaitTimeByStatus()){
					this.onSendCardEnd();
				}
				break;
			case NN_GAME_STATUS_RESULT:
				if( CommTime.nowMS() - this.startMS >=  this.getWaitTimeByStatus()){
//					this.setAllGameReady(true);
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
	public void onAddBet(WebSocketRequest request, CNN_AddBet addBet) {
		request.error(ErrorCode.NotAllow, "onAddBet  do not addBet");
	}

	
	@Override
	public int getSendCardNumber() {
		// TODO 自动生成的方法存根
		int count = 0;
		if(this.getStatus() == NN_GameStatus.NN_GAME_STATUS_SENDCARD_SECOND)
		{
			count = 5;
		}
		return count;
	}

	@Override
	public NN_GameStatus getStartStatus() {
		// TODO 自动生成的方法存根
		return NN_GameStatus.NN_GAME_STATUS_SENDCARD_SECOND;
	}

	@Override
	public void resultCalc() {
		// TODO 自动生成的方法存根
		NNGameResult result = new NNGameResult(this.room);
		result.calcByAll();
	}





	@Override
	public void setDefeault() {
		// TODO 自动生成的方法存根
		
	}


	@Override
	public void onHogEnd() {
		// TODO 自动生成的方法存根
		
	}
}
