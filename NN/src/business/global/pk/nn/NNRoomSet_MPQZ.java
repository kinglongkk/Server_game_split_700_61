package business.global.pk.nn;

import business.nn.c2s.iclass.SNN_StatusChange;
import com.ddm.server.common.utils.CommTime;
import business.nn.c2s.cclass.NN_define.NN_GameStatus;
import jsproto.c2s.cclass.pk.Victory;

import java.util.ArrayList;


/**
 * 牛牛一局游戏逻辑
 * @author zaf
 */

public class NNRoomSet_MPQZ extends NNRoomSet{
	

	public NNRoomSet_MPQZ(NNRoom room) {
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
			case NN_GAME_STATUS_SENDCARD_ONE:
				if( CommTime.nowMS() - this.startMS >=  this.getWaitTimeByStatus()){
					this.onSendCardOneEnd();
				}
				break;
			case NN_GAME_STATUS_HOG:
				if( CommTime.nowMS() - this.startMS >=  this.getWaitTimeByStatus()){
					this.onHogEnd();
				}
				break;	
			case NN_GAME_STATUS_ONSURECALLBACKER:
				if( CommTime.nowMS() - this.startMS >=  this.getWaitTimeByStatus()){
					this.onSureCallbacker();
				}
				break;
			case NN_GAME_STATUS_BET:
				if( CommTime.nowMS() - this.startMS >=  this.getWaitTimeByStatus()){
					this.onBetEnd();
				}
				break;
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
	
	//第一次发牌结束
	public void onSendCardOneEnd() {
		this.setStatus(NN_GameStatus.NN_GAME_STATUS_HOG);
		this.room.getRoomPosMgr().notify2All(SNN_StatusChange.make(this.room.getRoomID(), this.getStatus().value(), this.startMS, this.getSendCardNumber(),  this.getBackerPos(), this.isRandBackPos(), 0, this.callbackerList));
	}
	
	//抢庄结束
	@Override
	public void onHogEnd(){		
		int rand = 0;
		boolean isRandBackerPos = true;
		if (this.getCallBackerCount() > 0) {
			
			ArrayList<Victory> maxCallBackerList = new ArrayList<Victory>();
			int  maxCallBackerNum = 0;
			int  maxCallBackerPos = 0;
			for (Victory vic : this.callbackerList) {
				if(vic.getNum() > maxCallBackerNum ){
					maxCallBackerPos = vic.getPos();
					maxCallBackerNum = vic.getNum();
					maxCallBackerList.clear();
				}else if(maxCallBackerNum >= 1 && vic.getNum() == maxCallBackerNum ){
					if (maxCallBackerList.size() == 0) {
						maxCallBackerList.add(new Victory(maxCallBackerPos, maxCallBackerNum));
					} 
					maxCallBackerList.add(vic);
				}
			}
			
			if (maxCallBackerNum >  0) {
				if (maxCallBackerList.size() == 0) {
					rand = maxCallBackerPos;
					isRandBackerPos = false;
				} else {
					rand = (int) (Math.random()*maxCallBackerList.size());
					rand = maxCallBackerList.get(rand).getPos();
				}
			}else{
				rand = this.getRandPos();
			}
		}else{
			rand = this.getRandPos();
		}
		int callbacker = rand;
		
		
		this.setBackerPos(callbacker, isRandBackerPos);
		
		this.setStatus(NN_GameStatus.NN_GAME_STATUS_ONSURECALLBACKER);

		this.sendTuZhuPoint();
	
		
	}
	

	

	
	@Override
	public int getSendCardNumber() {
		// TODO 自动生成的方法存根
		int count = 4;
		if(this.getStatus() == NN_GameStatus.NN_GAME_STATUS_SENDCARD_SECOND)
		{
			count = 1;
		}
		return count;
	}

	@Override
	public NN_GameStatus getStartStatus() {
		// TODO 自动生成的方法存根
		return NN_GameStatus.NN_GAME_STATUS_SENDCARD_ONE;
	}

	@Override
	public void resultCalc() {
		// TODO 自动生成的方法存根
		NNGameResult result = new NNGameResult(this.room);
		result.calcByCallBacker();
	}



	@Override
	public void setDefeault() {
		// TODO 自动生成的方法存根
		
	}
}
