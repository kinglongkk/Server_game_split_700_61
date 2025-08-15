package business.global.pk.nn;

import business.nn.c2s.cclass.NN_define.NN_GameStatus;

import com.ddm.server.common.utils.CommTime;
import jsproto.c2s.cclass.pk.Victory;


/**
 * 牛牛一局游戏逻辑
 * 
 * @author zaf
 *
 */

public class NNRoomSet_ZYQZ extends NNRoomSet{
	

	public NNRoomSet_ZYQZ(NNRoom room) {
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
	
	
	//抢庄结束
	@Override
	public void onHogEnd(){		
		int rand = 0;
		
		boolean isRandBackerPos = true;
		if (this.getCallBackerCount() > 0) {
			if (this.getCallBackerCount() <= 1) {
				isRandBackerPos = false;
			}
			rand = (int) (Math.random()*this.callbackerList.size());

			Victory vic = this.callbackerList.get(rand);
			if(null != vic && vic.getNum() == 1){
				rand =vic.getPos();
			} else {
				for(int i = 0 ;i < this.callbackerList.size(); i++){
					rand = (rand + 1) % this.callbackerList.size();
					Victory temp = this.callbackerList.get(rand);
					if(null != temp && temp.getNum() == 1){
						rand =temp.getPos();
						break;
					}
				}
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
		return NN_GameStatus.NN_GAME_STATUS_HOG;
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
