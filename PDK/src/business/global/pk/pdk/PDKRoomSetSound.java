package business.global.pk.pdk;

import business.global.room.base.AbsRoomPos;
import business.pdk.c2s.cclass.PDK_define;
import business.pdk.c2s.cclass.PDK_define.PDK_CARD_TYPE;
import business.pdk.c2s.cclass.PDK_define.PDK_ROBCLOSE_STATUS;
import business.pdk.c2s.cclass.PDK_define.PDK_WANFA;
import business.pdk.c2s.iclass.CPDK_OpCard;
import business.pdk.c2s.iclass.SPDK_OpCard;
import business.global.pk.pdk.PDKRoom;
import business.global.pk.pdk.PDKRoomPos;
import business.global.pk.pdk.PDKRoomPosMgr;
import business.global.pk.pdk.PDKRoomSet;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.utils.CommTime;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.ddm.server.websocket.handler.requset.WebSocketRequestDelegate;
import jsproto.c2s.cclass.pk.BasePocker;
import jsproto.c2s.cclass.pk.BasePocker.PockerValueType;
import jsproto.c2s.cclass.pk.BasePockerLogic;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 跑得快一局游戏逻辑
 *
 * @author zaf
 *
 */

public class PDKRoomSetSound {

	public PDKRoom room = null;
	public PDKRoomSet set = null;
	private int m_OpPos = -1 ; //当前操作位置
	private int m_lastOpPos = -1;//最后一次操作位置
	private int m_lastOpPosBack = -1;//最后一次操作位置
	private int m_opCardType = PDK_CARD_TYPE.PDK_CARD_TYPE_NOMARL.value();  //PDK_CARD_TYPE 操作类型及牌的类型
	private int m_opCardTypeBackUp = PDK_CARD_TYPE.PDK_CARD_TYPE_NOMARL.value();  //PDK_CARD_TYPE 操作类型及牌的类型
	private ArrayList<Integer> m_cardList = new ArrayList<Integer>();
	private boolean m_bTurnEnd = false;
	private boolean m_bSetEnd = false;

	private static final int INTERVAL = 30000;//时间间隔
	private static final int AVAULE = 0x0E;//a的值
	private int roundBombCount = 0; //本轮炸弹个数
	private int maxBombPos = -1;//本轮最大炸弹玩家
	private boolean isCheck = false;//是否进行了最后一手检测

	public PDKRoomSetSound( PDKRoomSet set) {
		this.set = set;
		this.room = set.room;
		m_lastOpPosBack = /*this.m_lastOpPos = */ this.m_OpPos = set.getOpPos();
	}

	public void clean () {
		this.room  = null;
		this.set = null;
		this.m_cardList = null;
	}

	/**
	 *  尝试开始回合, 如果失败，则set结束
	 * @return
	 */
	public boolean tryStartRound() {

		return true;
	}

	public boolean update(int sec){
		if(m_bTurnEnd || m_bSetEnd) {
			return true;
		}
		return false;
	}

	/**
	 * 托管
	 * @param pos
	 */
	@SuppressWarnings("unchecked")
	public void roomTrusteeship(int pos) {

		if(m_bSetEnd || this.m_OpPos != pos) {
			return;
		}

		if(CommTime.nowMS()  - this.set.startMS <= 3000 ){
			return;
		}

		WebSocketRequest request = new WebSocketRequestDelegate();
		PDKRoomPos roomPos = (PDKRoomPos) this.room.getRoomPosMgr().getPosByPosID(pos);

		if(roomPos.getPrivateCards().size() <= 0){
			this.checkEndSet(pos);
			return;
		}

		//最后一手
		if(!isCheck){
			List<PDK_CARD_TYPE> types = Arrays.asList(PDK_CARD_TYPE.PDK_WANFA_SINGLECARD,
					PDK_CARD_TYPE.PDK_CARD_TYPE_DUIZI,
					PDK_CARD_TYPE.PDK_CARD_TYPE_SHUNZI,
					PDK_CARD_TYPE.PDK_CARD_TYPE_3BUDAI,
					PDK_CARD_TYPE.PDK_CARD_TYPE_3DAI1,
					PDK_CARD_TYPE.PDK_CARD_TYPE_3DAI2,
//					PDK_CARD_TYPE.PDK_CARD_TYPE_4DAI1,
//					PDK_CARD_TYPE.PDK_CARD_TYPE_4DAI2,
					PDK_CARD_TYPE.PDK_CARD_TYPE_4DAI3,
					PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN,
					PDK_CARD_TYPE.PDK_CARD_TYPE_FEIJI3,
//					PDK_CARD_TYPE.PDK_CARD_TYPE_FEIJI4,
					PDK_CARD_TYPE.PDK_WANFA_LIANDUI);
			for (PDK_CARD_TYPE type : types) {
				int daiNum = 0;
				Map<Integer, Long> collect = roomPos.getPrivateCards().stream().collect(Collectors.groupingBy(BasePocker::getCardValueEx, Collectors.counting()));
				if(collect.containsValue(4L) && !PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.equals(type)){
					continue;
				}
				if(this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_3AZHA) && collect.entrySet().stream().anyMatch(z->z.getKey()==BasePocker.getCardValue(AVAULE) && z.getValue()==3)){
					continue;
				}
				long count = collect.values().stream().filter(z -> z >= 3).count();
				if(PDK_CARD_TYPE.PDK_CARD_TYPE_3DAI1==type) {
					daiNum = 1;
				}else if(PDK_CARD_TYPE.PDK_CARD_TYPE_3DAI2==type){
					daiNum = 2;
				}else if(PDK_CARD_TYPE.PDK_CARD_TYPE_4DAI1==type){
					daiNum = 1;
				}else if(PDK_CARD_TYPE.PDK_CARD_TYPE_4DAI2==type){
					daiNum = 2;
				}else if(PDK_CARD_TYPE.PDK_CARD_TYPE_4DAI3==type){
					daiNum = 3;
				}else if(PDK_CARD_TYPE.PDK_CARD_TYPE_FEIJI3==type){
					for (int i=2;i<=count;i++){
						daiNum = i*PDKRoomSet.DEFAULTDAINUM ;
						CPDK_OpCard make = CPDK_OpCard.make(this.room.getRoomID(), pos, type.value(), new ArrayList<>(roomPos.getPrivateCards()), daiNum, true);
						make.feiJiNum = i;
						boolean flag =  this.onOpCard(request, make,false);
						if(flag){
							return;
						}
					}
					continue;
				}else if(PDK_CARD_TYPE.PDK_CARD_TYPE_FEIJI4==type){
					daiNum = PDKRoomSet.DEFAULTDAINUM+1;
				}else if(PDK_CARD_TYPE.PDK_WANFA_LIANDUI==type){
					if(roomPos.getPrivateCards().size()%2!=0){
						continue;
					}
				}
				boolean flag = this.onOpCard(request, CPDK_OpCard.make(this.room.getRoomID(), pos, type.value(), new ArrayList<>(roomPos.getPrivateCards()), daiNum,true),false);
				if(flag){
					return;
				}
			}
			isCheck = true;
		}

		ArrayList<Integer> tempCard = (ArrayList<Integer>) roomPos.privateCards.clone();
		tempCard.sort(BasePockerLogic.sorterBigToSmallNotTrump);

		ArrayList<Integer> outList = new ArrayList<Integer>();
		int daiNum = 0;

		int opCardType = m_opCardType;
		m_opCardTypeBackUp = m_opCardType;
		switch (PDK_CARD_TYPE.valueOf(m_opCardType)) {
			case PDK_CARD_TYPE_DUIZI:  			//对子
			{
				outList = this.getSameCard(tempCard, 2, 0, PockerValueType.POCKER_VALUE_TYPE_SUB,false);
			}
			break;
			case PDK_CARD_TYPE_3BUDAI:  		//3不带
			{
				outList = this.getSameCard(tempCard, 3, 0, PockerValueType.POCKER_VALUE_TYPE_THREE,false);
			}
			break;
			case PDK_CARD_TYPE_3DAI1: 			//3带1
			{
				daiNum = 1;
				outList = this.getSameCard(tempCard, 3, 1, PockerValueType.POCKER_VALUE_TYPE_THREE,false);
			}
			break;
			case PDK_CARD_TYPE_3DAI2:  			//3带2
			{
				daiNum = 2;
				outList = this.getSameCard(tempCard, 3, 2, PockerValueType.POCKER_VALUE_TYPE_THREE,false);
			}
			break;
			case PDK_CARD_TYPE_4DAI1:  			//4带1
			{
				daiNum = 1;
				outList = this.getSameCard(tempCard, 4, 1, PockerValueType.POCKER_VALUE_TYPE_BOMB,false);
			}
			break;
			case PDK_CARD_TYPE_4DAI2:  			//4带2
			{
				daiNum = 2;
				outList = this.getSameCard(tempCard, 4, 2, PockerValueType.POCKER_VALUE_TYPE_BOMB,false);
			}
			break;
			case PDK_CARD_TYPE_4DAI3:  			//4带3
			{
				daiNum = 3;
				outList = this.getSameCard(tempCard, 4, 3, PockerValueType.POCKER_VALUE_TYPE_BOMB,false);
			}
			break;
			case 	PDK_CARD_TYPE_ZHADAN:  			//炸弹
			{
				outList = this.getSameCard(tempCard, 4, 0, PockerValueType.POCKER_VALUE_TYPE_BOMB,false);
			}
			break;
			case PDK_CARD_TYPE_SHUNZI:  		//顺子
			{
				outList = this.getShunZi(tempCard);
			}
			break;
			case 	PDK_CARD_TYPE_FEIJI3:  			//飞机
			{
				daiNum = PDKRoomSet.DEFAULTDAINUM;
				outList = this.getLianDui(tempCard, 3, daiNum, PockerValueType.POCKER_VALUE_TYPE_THREE,false);
			}
			break;
			case 	PDK_CARD_TYPE_FEIJI4:  //飞机带翅膀
			{
				daiNum = PDKRoomSet.DEFAULTDAINUM+1;
				outList = this.getLianDui(tempCard, 4, daiNum, PockerValueType.POCKER_VALUE_TYPE_BOMB,false);
			}
			break;
			case 	PDK_WANFA_LIANDUI:  //联队
			{
				outList = this.getLianDui(tempCard, 2, 0, PockerValueType.POCKER_VALUE_TYPE_SUB,false);
			}
			break;
			case 	PDK_WANFA_SINGLECARD:
			{
				outList = this.getSinglecard(tempCard, 1,  m_cardList.size() > 0 ? m_cardList.get(0) : Integer.valueOf((byte) 0),false);
				if (outList.size() > 0 && tempCard.size() > 0 ) {
					if (!this.checkNextIsOneCard(pos, outList.get(0))) {
						tempCard.sort(BasePockerLogic.sorterBigToSmallNotTrump);
						outList.clear();
						if(tempCard.size() > 0) {
							outList.add(tempCard.get(0));
						}
					}
				}
			}
			break;
			case PDK_CARD_TYPE_NOMARL:
			{
				daiNum = this.getNomarlTypeCard(pos,outList,  (ArrayList<Integer>)tempCard.clone());
				if(this.set.isFirstOp() && !this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_HEITAO3BUBI) && !outList.contains(this.set.m_FirstOpCard) && this.set.m_FirstOpCard>0){
					//首出没带首出的牌
					ArrayList<Integer> sameCard = BasePockerLogic.getSameCard(tempCard, this.set.m_FirstOpCard, true);
					if(CollectionUtils.isNotEmpty(sameCard)){
						sameCard.sort((o2,o1)->{
							if(o2==this.set.m_FirstOpCard){
								return -1;
							}
							return 0;
						});
					}
					if(sameCard.size()>=4){
						m_opCardTypeBackUp = PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value();
						outList.clear();
						outList.addAll(sameCard);
					}else if(sameCard.size()>=2){
						m_opCardTypeBackUp = PDK_CARD_TYPE.PDK_CARD_TYPE_DUIZI.value();
						outList.clear();
						outList.addAll(sameCard.subList(0,2));
					}else if(sameCard.size()>=1){
						m_opCardTypeBackUp = PDK_CARD_TYPE.PDK_WANFA_SINGLECARD.value();
						outList.clear();
						outList.addAll(sameCard.subList(0,1));
					}
				}
			}
			break;
			default:
				CommLogD.error("not find opTYpe ="+m_opCardType +","+ PDK_CARD_TYPE.valueOf(m_opCardType));
				break;
		}
		opCardType = m_opCardTypeBackUp;
		if (PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value() != opCardType) {
			if ( null == outList || outList.size() <=0) {

				ArrayList< ArrayList<Integer> > opOutList = new ArrayList< ArrayList<Integer> > ();
				int count = BasePockerLogic.getSameCardByType(opOutList, tempCard, PockerValueType.POCKER_VALUE_TYPE_BOMB);

				if(count > 0){
					opCardType = PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value();
					outList.addAll(opOutList.get(0));
				}
				if( null == outList || outList.size() <=0){
					if (this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_3AZHA)) {
						ArrayList<Integer> tempList = BasePockerLogic.getSameCard(tempCard, 0x0E, true);
						if(null != tempList &&  tempList.size() == 3){
							opCardType = PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value();
							outList = tempList;
						}
					}
				}
			}
		}else{
			boolean is3AZha = PDK_define.PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value() == m_opCardType&&m_cardList.stream().allMatch(n->BasePocker.getCardValue(n)== BasePocker.getCardValue(AVAULE));
			if (is3AZha && !this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_3AZHAMAX)) {
				ArrayList<ArrayList<Integer>> mCardList = this.getSameCardByList(roomPos.privateCards, 4);
				if(CollectionUtils.isNotEmpty(mCardList)){
					opCardType = PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value();
					outList = mCardList.get(0);
				}
			}
		}

		//3A是最大
		if (null == outList || outList.size() <= 0) {
			if (this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_3AZHA) && this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_3AZHAMAX)) {
				ArrayList<Integer> tempList = BasePockerLogic.getSameCard(tempCard, 0x0E, true);
				if(null != tempList &&  tempList.size() == 3){
					opCardType = PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value();
					outList = tempList;
				}
			}else{
				opCardType = PDK_CARD_TYPE.PDK_CARD_TYPE_BUCHU.value();
			}
		}
		//炸弹不可拆3A
		if(this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_3AZHA) && !this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_ZHADANKECHAI)){
			if(opCardType!=PDK_CARD_TYPE.PDK_CARD_TYPE_BUCHU.value() && outList.stream().anyMatch(n->BasePocker.getCardValueEx(n) == BasePocker.getCardValueEx(AVAULE))){
				ArrayList<Integer> tempList = BasePockerLogic.getSameCard(roomPos.getPrivateCards(), 0x0E, true);
				if(null != tempList &&  tempList.size() == 3){
					opCardType = PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value();
					outList = tempList;
				}
			}
		}
		if (null == outList || outList.size() <= 0) {
			opCardType = PDK_CARD_TYPE.PDK_CARD_TYPE_BUCHU.value();
		}

		boolean flag =  this.onOpCard(request, CPDK_OpCard.make(this.room.getRoomID(), pos, opCardType, outList, daiNum,true),true);
		if (!flag) {
			if (CommTime.nowMS()  - this.set.startMS > INTERVAL ) {
				opCardType  = PDK_CARD_TYPE.PDK_CARD_TYPE_BUCHU.value();
				flag = this.onOpCard(request, CPDK_OpCard.make(this.room.getRoomID(), pos, opCardType, outList, daiNum,true),true);
				if (!flag) {
					this.set.endSet();
					CommLogD.error("roomTrusteeship is not robot roomID =  "+ this.room.getRoomID() +",posCardList = " + tempCard.toString() +", m_opCardType"+ m_opCardType + ", m_cardList=" + m_cardList.toString());
				}
				return;
			}
		}
	}

	/**
	 * 检查炸弹可拆
	 * @return
	 */
	private boolean checkZhaDan(CPDK_OpCard opCard,ArrayList<Integer> cardList) {
		final boolean is3AZha = this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_3AZHA);
		if(!this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_ZHADANKECHAI)){
			//默认打牌或者不出不检验
			if(opCard.opCardType!=PDK_CARD_TYPE.PDK_CARD_TYPE_NOMARL.value() && opCard.opCardType!=PDK_CARD_TYPE.PDK_CARD_TYPE_BUCHU.value() && opCard.opCardType!=PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value()){
				//分组
				Map<Integer, Long> cardGroupMap = cardList.stream().collect(
						Collectors.groupingBy(p -> BasePocker.getCardValueEx(p), Collectors.counting()));
				if(opCard.cardList!=null){
					//检测炸弹
					return opCard.cardList.stream().allMatch(n->{
						if(cardGroupMap.get(BasePocker.getCardValueEx(n))!=null){
							boolean isNormalBomb = cardGroupMap.get(BasePocker.getCardValueEx(n))==4;
							boolean is3ABomb = is3AZha?cardGroupMap.get(BasePocker.getCardValueEx(n))==3 && BasePocker.getCardValueEx(n) == BasePocker.getCardValueEx(AVAULE):false;
							return !isNormalBomb && !is3ABomb;
						}
						return false;
					});
				}
				return false;
			}
			return true;
		}
		return true;
	}

	public synchronized boolean onOpCard(WebSocketRequest request, CPDK_OpCard opCard,boolean needNoTify){
		if(m_bSetEnd){
			if(null != request) {
				request.error(ErrorCode.NotAllow, "onOpCard error: m_bSetEnd is ture");
			}
			return false;
		}
		if (m_bTurnEnd) {
			if(null != request) {
				request.error(ErrorCode.NotAllow, "onOpCard error: m_bTurnEnd is ture");
			}
			return false;
		}

		if(opCard.pos != m_OpPos){
			if(null != request) {
				request.error(ErrorCode.NotAllow, "onOpCard error: not current pos op oppos: "+m_OpPos);
			}
			return false;
		}
		// 首出判断
		if (this.set.isFirstOp() && !this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_HEITAO3BUBI) && set.m_FirstOpCard != 0) {
			boolean isConstantFirstCard = opCard.cardList.stream().anyMatch(n -> n == set.m_FirstOpCard);
			if (!isConstantFirstCard) {
				if (null != request) {
					request.error(ErrorCode.NotAllow, "onOpCard error: not current pos op oppos: " + m_OpPos);
				}
				return false;
			}
		}
		PDKRoomPos roomPos = (PDKRoomPos) this.room.getRoomPosMgr().getPosByPosID(opCard.pos);
		//炸弹不可拆限制
		boolean isCanOp = checkZhaDan(opCard,roomPos.getPrivateCards());
		if(!isCanOp){
			if(null != request) request.error(ErrorCode.ZhaDanBuKeChai, "onOpCard error: zha dan bu ke chai: "+opCard.cardList);
			return false;
		}


		if( PDK_CARD_TYPE.PDK_CARD_TYPE_BUCHU.value() == opCard.opCardType ){
			if(!this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_FEIBICHU)){
				if (CommTime.nowMS()  - this.set.startMS >= INTERVAL && roomPos.isRobot()) {

				}else{
					if(this.m_opCardType == PDK_CARD_TYPE.PDK_CARD_TYPE_NOMARL.value()){
						if(null != request) {
							request.error(ErrorCode.NotAllow, "onOpCard error:PDK_WANFA_FEIBICHU");
						}
						return false;
					}
					if(this.m_opCardType != PDK_CARD_TYPE.PDK_CARD_TYPE_NOMARL.value() && this.checkHaveMaxCard(opCard.pos)){
						if(null != request) {
							request.error(ErrorCode.NotAllow, "onOpCard error:your have max card");
						}
						return false;
					}
				}
			}else{
				//非必出是你的回合不能不出
				if(this.m_opCardType == PDK_CARD_TYPE.PDK_CARD_TYPE_NOMARL.value()){
					if(null != request) {
						request.error(ErrorCode.NotAllow, "onOpCard error:PDK_WANFA_FEIBICHU");
					}
					return false;
				}
			}
		}else if(PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value() == opCard.opCardType){

			if(!this.checkIsMyCard(opCard)){
				if(null != request) {
					request.error(ErrorCode.OP_CARD_ERROR, "onOpCard error:card is not myself");
				}
				return false;
			}

			if(this.checkBomb(opCard.opCardType, opCard.cardList)){
				if(!check3AZha(opCard.cardList)){
					if(null != request) request.error(ErrorCode.ZhaDanBuKeChai, "onOpCard error: zha dan errro,current bomb : "+opCard.cardList+" ,last bomb: "+m_cardList);
					return false;
				}
			}else{
				if(null != request && needNoTify) {
					request.error(ErrorCode.NotAllow, "onOpCard error:card checkBomb fail");
				}
				return false;
			}
		}else if (PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value() != opCard.opCardType ) {

			if( PDK_CARD_TYPE.PDK_CARD_TYPE_NOMARL.value() != this.m_opCardType &&  this.m_opCardType != opCard.opCardType){
				if(null != request && needNoTify) {
					request.error(ErrorCode.NotAllow, "onOpCard error:optype do not op,this last opType:"+ this.m_opCardType+",your optype:"+opCard.opCardType);
				}
				return false;
			}

			if(!this.checkIsMyCard(opCard)){
				if(null != request) {
					request.error(ErrorCode.OP_CARD_ERROR, "onOpCard error:card is not myself");
				}
				return false;
			}

			if(!this.checkCardList(opCard)){
				if(null != request && needNoTify) {
					request.error(ErrorCode.NotAllow, "onOpCard error:card check fail");
				}
				return false;
			}

			if (PDK_CARD_TYPE.PDK_WANFA_SINGLECARD.value() == opCard.opCardType && !this.checkNextIsOneCard(opCard.pos, opCard.cardList.get(0)) ) {
				if(null != request) {
					request.error(ErrorCode.NotAllow, "onOpCard error:you must op max card");
				}
				return false;
			}
		}

		if(this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_4DAIFAN)){
			if(PDK_CARD_TYPE.PDK_CARD_TYPE_FEIJI4.value() == opCard.opCardType || PDK_CARD_TYPE.PDK_CARD_TYPE_4DAI1.value() == opCard.opCardType || PDK_CARD_TYPE.PDK_CARD_TYPE_4DAI2.value() == opCard.opCardType || PDK_CARD_TYPE.PDK_CARD_TYPE_4DAI3.value() == opCard.opCardType){
				this.set.addRoomDouble(opCard.pos, PDKRoomSet.FOURDAIFANDOUBLE);
			}
		}


		if(opCard.cardList.size() > 0 && !roomPos.deleteCard(opCard.cardList)){
			if(null != request) {
				request.error(ErrorCode.NotAllow, "card delete error : your cards:"+opCard.cardList.toString() + ", posCard:" + roomPos.getPrivateCards().toString());
			}
			return false;
		}

		if(null != request) {
			request.response();
		}

		this.set.setFirstOp(false);
		roomPos.clearLatelyOutCardTime();
		if(opCard.opCardType == PDK_CARD_TYPE.PDK_CARD_TYPE_BUCHU.value()){
			PDKRoomPosMgr roomPosMgr = (PDKRoomPosMgr) this.room.getRoomPosMgr();
			//更新打牌时间
			if(room.getRoomCfg().getFangJianXianShi() != 0){
				roomPos.setSecTotal(roomPos.getSecTotal() - (CommTime.nowMS() - this.set.startMS));
			}
			this.addOpPos(true);
			long runWaitSec = (CommTime.nowMS() - this.set.startMS)/1000 ;
			int secTotal = 0;
			int dataSecTotal = 0;
			//每个回合开始之前设置时间
			if (room.getRoomCfg().getFangJianXianShi() != 0) {//不是罚分玩法 用过的时间
				//本回合跑了多少秒
				long total = this.set.getTime(this.room.getRoomCfg().getFangJianXianShi());
				//除了本回合，之前的所有回合跑了多少秒
				long useTime2 = (((PDKRoomPos) room.getRoomPosMgr().getPosByPosID(m_OpPos)).getSecTotal() / 1000);
				long useTime1 = total - useTime2;
				//总的跑多少(本回合+之前所有回合跑的时间)，让客户端自己拿剩余多少秒去减
				int zongjie = (int)useTime1 + (int)runWaitSec;
//                    //该玩家使用的时间
				secTotal = zongjie;
				dataSecTotal = (int)total-((int)roomPos.getSecTotal() / 1000);
			}
			for (int i = 0; i < this.room.getPlayerNum(); i++) {
				ArrayList<Integer> privateList = resolveCardList(roomPos.privateCards,i,roomPos.getPosID());
				if (0 == i) {
					this.set.getRoomPlayBack().playBack2Pos(i, SPDK_OpCard.make(opCard.roomID, opCard.pos, opCard.opCardType, m_OpPos, opCard.cardList, m_bTurnEnd, opCard.daiNum, m_bSetEnd,privateList,opCard.isFlash,runWaitSec,secTotal,dataSecTotal,getTrusteeshipList()), roomPosMgr.getAllPlayBackNotify());
				} else {
					this.room.getRoomPosMgr().notify2Pos(i, SPDK_OpCard.make(opCard.roomID, opCard.pos, opCard.opCardType, m_OpPos, opCard.cardList, m_bTurnEnd, opCard.daiNum, m_bSetEnd,privateList,opCard.isFlash,runWaitSec,secTotal,dataSecTotal,getTrusteeshipList()));
				}
			}
			return true;
		}

		if(PDK_CARD_TYPE.PDK_CARD_TYPE_NOMARL.value() == this.m_opCardType){
			this.m_opCardType = opCard.opCardType;
		}
		if(PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value() == opCard.opCardType){
			this.m_opCardType = opCard.opCardType;
			roundBombCount++;
			maxBombPos = opCard.pos;
			addBombByAtOnce(opCard.pos);
		}

		if(!this.set.isRobCloseCalc()){
			//设置抢关门是否成功
			if (this.set.getRobClosePos() >= 0 &&  this.set.getRobClosePos() != opCard.pos && this.set.getRobCloseNum() == PDK_ROBCLOSE_STATUS.PDK_ROBCLOSE_STATUS_SUCCESS.value()) {
				this.set.setRobCloseNum(PDK_ROBCLOSE_STATUS.PDK_ROBCLOSE_STATUS_FAIL.value());
			}

			//设置反关门是否成功
			if (-1 == this.set.getReverseRobClosePos() && this.set.getFirstOpPos() != opCard.pos) {
				this.set.setReverseRobClosePos(opCard.pos);
				this.set.setReverseRobCloseNum(PDK_ROBCLOSE_STATUS.PDK_ROBCLOSE_STATUS_SUCCESS.value());
			}
			else if ( -1 != this.set.getReverseRobClosePos() && this.set.getReverseRobClosePos() != opCard.pos) {
				this.set.setReverseRobCloseNum(PDK_ROBCLOSE_STATUS.PDK_ROBCLOSE_STATUS_FAIL.value());
			}
		}

		this.set.addOpCardList(opCard.cardList,opCard.opCardType,opCard.pos);

		m_lastOpPosBack = this.m_lastOpPos = opCard.pos;
		this.m_cardList = opCard.cardList;

		if (m_cardList.size() <= 0) {
			CommLogD.error("cardList.size<=0");
		}

		if (this.set.getFirstOpPos() != opCard.pos && this.set.getFirstOpNum() == PDK_ROBCLOSE_STATUS.PDK_ROBCLOSE_STATUS_NOMAL.value()) {
			PDKRoomPos firstRoomPos = (PDKRoomPos) this.room.getRoomPosMgr().getPosByPosID(this.set.getFirstOpPos());
			this.set.setFirstOpNum(firstRoomPos.getPrivateCards().size());
		}

		//更新打牌时间
		if(room.getRoomCfg().getFangJianXianShi() != 0){
			roomPos.setSecTotal(roomPos.getSecTotal() - (CommTime.nowMS() - this.set.startMS));
		}
		this.addOpPos(false);
		PDKRoomPosMgr roomPosMgr = (PDKRoomPosMgr) this.room.getRoomPosMgr();

		this.checkEndSet(opCard.pos);

		long runWaitSec = (CommTime.nowMS() - this.set.startMS)/1000 ;
		int secTotal = 0;
		int dataSecTotal = 0;
		//每个回合开始之前设置时间
		if (room.getRoomCfg().getFangJianXianShi() != 0) {//不是罚分玩法 用过的时间
			//本回合跑了多少秒
			long total = this.set.getTime(this.room.getRoomCfg().getFangJianXianShi());
			//除了本回合，之前的所有回合跑了多少秒
			long useTime2 = (((PDKRoomPos) room.getRoomPosMgr().getPosByPosID(m_OpPos)).getSecTotal() / 1000);
			long useTime1 = total - useTime2;
			//总的跑多少(本回合+之前所有回合跑的时间)，让客户端自己拿剩余多少秒去减
			int zongjie = (int)useTime1 + (int)runWaitSec;
//                    //该玩家使用的时间
			secTotal = zongjie;
			dataSecTotal = (int)total-((int)roomPos.getSecTotal() / 1000);
		}

		for (int i = 0; i < this.room.getPlayerNum(); i++) {
			ArrayList<Integer> privateList = resolveCardList(roomPos.privateCards,i,roomPos.getPosID());
			if (0 == i) {
				this.set.getRoomPlayBack().playBack2Pos(i, SPDK_OpCard.make(opCard.roomID, opCard.pos, opCard.opCardType, m_OpPos, opCard.cardList, m_bTurnEnd, opCard.daiNum, m_bSetEnd,privateList,opCard.isFlash,runWaitSec,secTotal,dataSecTotal,getTrusteeshipList()), roomPosMgr.getAllPlayBackNotify());
			} else {
				this.room.getRoomPosMgr().notify2Pos(i, SPDK_OpCard.make(opCard.roomID, opCard.pos, opCard.opCardType, m_OpPos, opCard.cardList, m_bTurnEnd, opCard.daiNum, m_bSetEnd,privateList,opCard.isFlash,runWaitSec,secTotal,dataSecTotal,getTrusteeshipList()));
			}
		}
		return true;
	}

	/**
	 * 处理私有牌显示
	 * @param cardsList 牌
	 * @param currentPos 自己的位置
	 * @param showOps 显示的位置
	 * @return
	 */
	public ArrayList<Integer> resolveCardList(ArrayList<Integer> cardsList,int currentPos,int showOps) {
		ArrayList<Integer> cards = new ArrayList<>();
		for(Integer card:cardsList){
			if(showOps == currentPos){
				cards.add(card);
			}else{
				cards.add(0);
			}
		}
		return cards;
	}

	/**
	 * 检测3A炸弹
	 * @param cardList
	 * @return
	 */
	private boolean check3AZha(ArrayList<Integer> cardList) {
		boolean is3A = cardList.size()==3 && cardList.stream().allMatch(n->BasePocker.getCardValue(n.intValue())== BasePocker.getCardValue(AVAULE));
		//3A炸最小玩法，不能压任何炸弹
		if(m_opCardType == PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value() && !this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_3AZHAMAX)){
			return !is3A;
		}
		//3A炸最大玩法。任何炸弹不能压3A
		boolean lastIs3A = m_cardList.size()==3 && m_cardList.stream().allMatch(n->BasePocker.getCardValue(n.intValue())== BasePocker.getCardValue(AVAULE));
		if(m_opCardType == PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value() && this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_3AZHAMAX)){
			return !lastIs3A;
		}
		return true;
	}

	/**
	 * 检查是否结束
	 * **/
	public void checkEndSet(int pos){
		PDKRoomPos roomPos = (PDKRoomPos) this.room.getRoomPosMgr().getPosByPosID(pos);

		if(roomPos.privateCards.size() <= 0 && !this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_XUEZHANDAODI)){
			//this.set.endSet();
			addBombScore();
			this.m_bSetEnd = true;
		}else if(roomPos.privateCards.size() <= 0 && this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_XUEZHANDAODI)){
			this.set.resultCalcEx();
		}
		if (this.getPlayerPlaying() <=  1) {
			//this.set.endSet();
			this.m_bSetEnd = true;
		}
	}

	/**
	 * 还有多少玩家
	 * **/
	public int getPlayerPlaying(){
		int count = 0;
		for (int i = 0; i < this.room.getPlayerNum(); i++) {
			PDKRoomPos roomPos = (PDKRoomPos) this.room.getRoomPosMgr().getPosByPosID(i);
			if(roomPos.privateCards.size() > 0){
				count++;
			}
		}
		return count;
	}


	/**
	 * 验证是否是下载最后一张单牌
	 * **/
	@SuppressWarnings("unchecked")
	public boolean checkNextIsOneCard(int pos, int card){
		int nextPos = pos;
		PDKRoomPos roomPos = (PDKRoomPos) this.room.getRoomPosMgr().getPosByPosID(nextPos);
		if (roomPos.getPrivateCards().size() == 1) {
			return true;
		}
		for (int i = 0; i < this.room.getPlayerNum(); i++) {
			nextPos = (++nextPos)%this.room.getPlayerNum();
			PDKRoomPos tempRoomPos = (PDKRoomPos) this.room.getRoomPosMgr().getPosByPosID(nextPos);
			int cardNum = tempRoomPos.getPrivateCards().size();
			if(cardNum > 0){
				if(cardNum == 1) {
					ArrayList<Integer> cardList = (ArrayList<Integer>) roomPos.getPrivateCards().clone();
					cardList.sort(BasePockerLogic.sorterBigToSmallNotTrump);
					if (BasePocker.getCardValue( card ) != BasePocker.getCardValue(cardList.get(0))) {
						return false;
					}
				}
				break;
			}
		}

		return true;
	}

	/**
	 * 验证是否是自己的牌
	 * */
	public boolean checkIsMyCard(CPDK_OpCard opCard){
		PDKRoomPos roomPos = (PDKRoomPos) this.room.getRoomPosMgr().getPosByPosID(opCard.pos);
		for (Integer byte1 : opCard.cardList) {
			if (!roomPos.privateCards.contains(byte1)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 是不是最后一手的牌
	 * @return
	 */
	private boolean checkIsLastCard(ArrayList<Integer> cardList,PDKRoomPos roomPos) {
		long count = cardList.stream().distinct().count();
		if (count == cardList.size() && roomPos.getPrivateCards().containsAll(cardList)) {
			return roomPos.getPrivateCards().size() == cardList.size();
		}
		return false;
	}


	/**
	 * 牌验证
	 * **/
	public boolean  checkCardList(CPDK_OpCard opCard) {
		PDKRoomPos roomPos = (PDKRoomPos) this.room.getRoomPosMgr().getPosByPosID(opCard.pos);

		boolean flag = false;
		PDK_CARD_TYPE cardType = PDK_CARD_TYPE.valueOf(opCard.opCardType);
		switch (cardType) {
			case PDK_CARD_TYPE_DUIZI:  			//对子
			{
				flag = this.checkSameCard(opCard.pos, cardType, opCard.cardList, 2, 0, PockerValueType.POCKER_VALUE_TYPE_SUB);
			}
			break;
			case PDK_CARD_TYPE_SHUNZI:  		//顺子
			{
				flag = this.checkShunZi(cardType, opCard.cardList);
			}
			break;
			case PDK_CARD_TYPE_3BUDAI:  		//3不带
			{
				if (m_opCardType != opCard.opCardType &&  !this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_3BUDAI)  && roomPos.getPrivateCards().size() != 3) {
					flag = false;
				} else {
					flag = this.checkSameCard(opCard.pos, cardType, opCard.cardList, 3, 0, PockerValueType.POCKER_VALUE_TYPE_THREE);
				}
			}
			break;
			case PDK_CARD_TYPE_3DAI1: 			//3带1
			{
				if (m_opCardType != opCard.opCardType && !this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_3DAI1)  && roomPos.getPrivateCards().size() != 4 ) {
					flag = false;
				} else {
					flag = this.checkSameCard(opCard.pos, cardType, opCard.cardList, 3, 1, PockerValueType.POCKER_VALUE_TYPE_THREE);
				}
			}
			break;
			case PDK_CARD_TYPE_3DAI2:  			//3带2
			{
				if (!this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_3DAI2)  ) {
					flag = false;
				} else {
					flag = this.checkSameCard(opCard.pos, cardType, opCard.cardList, 3, 2, PockerValueType.POCKER_VALUE_TYPE_THREE);
				}
			}
			break;
			case PDK_CARD_TYPE_4DAI1:  			//4带1
			{
				if (m_opCardType != opCard.opCardType && !(this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_4DAI1)||this.room.isSiDaiByNum(1))  && roomPos.getPrivateCards().size() != 5 ) {
					flag = false;
				} else {
					flag = this.checkSameCard(opCard.pos, cardType, opCard.cardList, 4, 1, PockerValueType.POCKER_VALUE_TYPE_BOMB);
				}
			}
			break;
			case PDK_CARD_TYPE_4DAI2:  			//4带2
			{
				if (m_opCardType != opCard.opCardType && !((this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_4DAI2)||this.room.isSiDaiByNum(2)))  && roomPos.getPrivateCards().size() != 6 ) {
					flag = false;
				} else {
					flag = this.checkSameCard(opCard.pos, cardType, opCard.cardList, 4, 2, PockerValueType.POCKER_VALUE_TYPE_BOMB);
				}
			}
			break;
			case PDK_CARD_TYPE_4DAI3:  			//4带3
			{
				if (!(this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_4DAI3)||this.room.isSiDaiByNum(3))) {
					flag = false;
				} else {
					flag = this.checkSameCard(opCard.pos, cardType, opCard.cardList, 4, 3, PockerValueType.POCKER_VALUE_TYPE_BOMB);
				}
			}
			break;
			case 	PDK_CARD_TYPE_ZHADAN:  			//炸弹
			{
				flag = this.checkSameCard(opCard.pos, cardType, opCard.cardList, opCard.cardList.size(), 0, PockerValueType.POCKER_VALUE_TYPE_BOMB);
			}
			break;
			case 	PDK_CARD_TYPE_FEIJI3:  			//飞机
			{
				flag = this.checkLianDui(cardType, opCard.cardList, 3, opCard.daiNum, PockerValueType.POCKER_VALUE_TYPE_THREE,checkIsLastCard(opCard.cardList,roomPos),opCard.feiJiNum);
			}
			break;
			case 	PDK_CARD_TYPE_FEIJI4:  //飞机带翅膀
			{
				flag = this.checkLianDui(cardType, opCard.cardList, 4, opCard.daiNum, PockerValueType.POCKER_VALUE_TYPE_BOMB,false,0);
			}
			break;
			case 	PDK_WANFA_LIANDUI:  //联队
			{
				flag = this.checkLianDui(cardType, opCard.cardList, 2, 0, PockerValueType.POCKER_VALUE_TYPE_SUB,false,0);
			}
			break;
			case 	PDK_WANFA_SINGLECARD:
			{
				flag = this.checkSingleCard(cardType, opCard.cardList);
			}
			break;
			default:
				break;
		}
		return flag;
	}


	/**
	 * 顺子的牌
	 * @param cardType
	 * @param cardList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean checkShunZi(PDK_CARD_TYPE cardType, ArrayList<Integer> cardList){
		if (cardList.size() < 5) {
			return false;
		}


		boolean isNomarlOpCard = true;
		if(m_cardList.size() > 0){
			isNomarlOpCard = false;
		}

		ArrayList<Integer> tempList = (ArrayList<Integer>) cardList.clone();
		//从大到小
		tempList.sort(BasePockerLogic.sorterBigToSmallNotTrump);

		boolean isLaiZi = this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_LAIZI);

		if (isLaiZi) {
			ArrayList<Integer> List = this.getShunZiByList(tempList);
			if (List == null) {
				return false;
			}
			if(isNomarlOpCard) {
				return true;
			}
			ArrayList<Integer> mCardList = this.getShunZiByList(this.m_cardList);
			if (mCardList == null) {
				CommLogD.error("checkShunZi error: m_cardList do not get shun zi");
				return false;
			}else{
				return this.compareOneCard(List.get(0), mCardList.get(0));
			}
		} else {
			for (int i = 0 ; i < tempList.size() -1; i++) {
				if(Math.abs( BasePocker.getCardValue( tempList.get(i) )  - BasePocker.getCardValue( tempList.get(i+1) ) ) != 1){
					return false;
				}
			}
		}

		this.m_cardList.sort(BasePockerLogic.sorterBigToSmallNotTrump);
		return isNomarlOpCard ? true : this.compareOneCard(tempList.get(0), this.m_cardList.get(0));
	}

	/**
	 * 相同的牌
	 * */
	public boolean  checkSameCard (int pos, PDK_CARD_TYPE cardType, ArrayList<Integer> cardList, int sameNum, int daiNum, PockerValueType pockerType) {
		if(cardList.size()>(sameNum + daiNum)){
			return false;
		}
		if (cardList.size() !=  sameNum + daiNum) {
			PDKRoomPos roomPos = (PDKRoomPos) this.room.getRoomPosMgr().getPosByPosID(pos);
			if(roomPos.getPrivateCards().size() != cardList.size()){
				return false;
			}
		}

		ArrayList< ArrayList<Integer> > opOutList = new ArrayList< ArrayList<Integer> > ();
		int count = BasePockerLogic.getSameCardByType(opOutList, cardList, pockerType);

		boolean isLaiZi = this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_LAIZI);
		boolean isNomarlOpCard = true;

		ArrayList<ArrayList<Integer>> mCardList = this.getSameCardByList(this.m_cardList, sameNum);
		if(m_cardList.size() > 0){
			isNomarlOpCard = false;

			if(mCardList == null ||(null != mCardList && mCardList.size() <= 0)){
				CommLogD.error("checkSameCard error: m_cardList do not get same card sameNum = "+sameNum + ", m_cardList="+ m_cardList.toString());
				return false;
			}
		}




		if(!isLaiZi){
			if(count <= 0) {
				return false;
			}
			return isNomarlOpCard ? true : this.compare(opOutList, mCardList);
		}

		ArrayList<ArrayList<Integer>> outlist = this.getSameCardByList(cardList, sameNum);
		if(outlist == null || outlist.size() <= 0) {
			return false;
		}

		return isNomarlOpCard ? true : this.compare(outlist, mCardList);
	}

	/**
	 * 根据list获取联队list
	 * */
	public ArrayList<ArrayList<Integer>> getLianDuiList(ArrayList<ArrayList<Integer>> list, int size) {
		ArrayList<ArrayList<Integer>> tempList = new ArrayList<ArrayList<Integer>>();
		if (list == null || (null != list &&  list.size() < 2)) {
			return tempList;
		}
		int count  = list.size();
		ArrayList<Integer> opList = new ArrayList<Integer>();;
		for (int i = 0; i < count; i++) {
			opList.add(list.get(i).get(0));
		}

		ArrayList<Integer> tempOpList = this.getShunZiByListEx(opList, size );
		if(tempOpList == null ){
			return tempList;
		}

		ArrayList< ArrayList<Integer> >  lianDuiList = new ArrayList< ArrayList<Integer> >();
		for (Integer byte1 : tempOpList) {
			for (int i = 0; i < count; i++) {
				if (BasePocker.getCardValue(byte1) == BasePocker.getCardValue(list.get(i).get(0))) {
					lianDuiList.add(list.get(i));
					break;
				}
			}
		}
		return lianDuiList;
	}

	/**
	 * 联队
	 * */
	public boolean  checkLianDui (PDK_CARD_TYPE cardType, ArrayList<Integer> cardList, int sameNum, int daiNum, PockerValueType pockerType,boolean isLastCardList,int feiJiNum) {
		ArrayList< ArrayList<Integer> > opOutList = new ArrayList< ArrayList<Integer> > ();
		int count = BasePockerLogic.getSameCardByType(opOutList, cardList, pockerType);
		int size = feiJiNum>0?feiJiNum:(cardList.size() - daiNum)/sameNum ;

		ArrayList<ArrayList<Integer>> mCardList = this.getLianDuiList( this.getSameCardByList(this.m_cardList, sameNum), size);

		boolean isNomarlOpCard = true;
		if(m_cardList.size() > 0){
			isNomarlOpCard = false;

			if(mCardList == null || mCardList.size() < 2){
				CommLogD.error("checkLianDui error: m_cardList do not get same card sameNum = "+sameNum +",m_cardList"+ m_cardList.toString());
				return false;
			}
		}

		if(feiJiNum>0 && feiJiNum*5<cardList.size()){
			return false;
		}

		//飞机最后一手牌不够不能出的bug，剑锋说只改鄱阳的
		if (!isNomarlOpCard && cardList.size() <  m_cardList.size() && !isLastCardList) {
			return false;
		}

		boolean isLaiZi = this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_LAIZI);

		if(!isLaiZi){

			ArrayList< ArrayList<Integer> >  lianDuiList = this.getLianDuiList(opOutList,size);
			if (null == lianDuiList || lianDuiList.size() < 2) {
				return false;
			}
			return isNomarlOpCard ? true : this.compare(lianDuiList, mCardList);
		}

		opOutList.clear();
		opOutList = this.getSameCardByList(cardList, sameNum);
		if(opOutList == null || opOutList.size() < 2) {
			return false;
		}

		count = opOutList.size();

		ArrayList<Integer> opList = new ArrayList<Integer>();;
		for (int i = 0; i < count; i++) {
			opList.add(opOutList.get(i).get(0));
		}

		ArrayList<Integer> tempOpList = this.getShunZiByList(opList);
		if(tempOpList == null ){
			return false;
		}

		return isNomarlOpCard ? true :  this.compare(opOutList, mCardList);
	}

	/**
	 * 单牌
	 * */
	public boolean  checkSingleCard (PDK_CARD_TYPE cardType,  ArrayList<Integer> cardList) {
		if (cardList.size() !=  1 ) {
			return false;
		}
		boolean isNomarlOpCard = true;
		if(m_cardList.size() > 0){
			isNomarlOpCard = false;
		}
		return isNomarlOpCard ? true :  this.compareOneCard(cardList.get(0), m_cardList.get(0));
	}


	/**
	 * 比较牌的大小
	 * */
	public boolean compare(ArrayList< ArrayList<Integer> > leftList, ArrayList< ArrayList<Integer> > RightList) {
		if(leftList.size() != RightList.size()) {
			return false;
		}

		int num =  leftList.size() ;

		ArrayList<Integer> soundList = new ArrayList<Integer>();
		ArrayList<Integer> opList = new ArrayList<Integer>();
		for (int i = 0; i < num; i++) {
			if(RightList.size() > i) {
				soundList.add(RightList.get(i).get(0));
			}
			if(leftList.size() > i) {
				opList.add(leftList.get(i).get(0));
			}
		}

		ArrayList<Integer> tempLeft = this.getShunZiByList(opList);
		ArrayList<Integer> tempRight = this.getShunZiByList(soundList);

		tempLeft.sort(BasePockerLogic.sorterBigToSmallNotTrump);
		tempRight.sort(BasePockerLogic.sorterBigToSmallNotTrump);

		return this.compareOneCard(tempLeft.get(0), tempRight.get(0));
	}


	/**
	 * 比较一张牌的大小
	 * **/
	public boolean compareOneCard(Integer leftCard, Integer rightCard) {
		int cbLeftMaxValue= BasePocker.getCardValue(leftCard);
		int cbRightMaxValue= BasePocker.getCardValue(rightCard);
		return cbLeftMaxValue > cbRightMaxValue;
	}

	/**
	 * 获取带牌
	 * @param card
	 * **/
	public ArrayList<Integer> getSinglecard(ArrayList<Integer> cardList, int daiNum, int card,boolean needPair){
		if(!needPair){//不需要对子
			return getSinglecard(cardList,daiNum,card);
		}
		ArrayList<Integer> temp = new ArrayList<Integer>();
		Map<Integer, List<Integer>> valueMap = cardList.stream()
				.collect(Collectors.groupingBy(p -> BasePocker.getCardValueEx(p)));
		for(Map.Entry<Integer, List<Integer>> n:valueMap.entrySet()){
			if(n.getValue().size()>=4){
				temp = new ArrayList<>(n.getValue().subList(0,4));
				m_opCardTypeBackUp = PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value();
				return temp;
			}
			if(n.getValue().size()>=2){
				if (0 != card && BasePockerLogic.getCardValue(card) < n.getKey())  {
					temp.addAll(new ArrayList<>(n.getValue().subList(0,2)));
				} else if(card == 0){
					temp.addAll(new ArrayList<>(n.getValue().subList(0,2)));
				}
			}
			if(temp.size() >= daiNum)
				return temp;
		}
		return temp;
	}

	/**
	 * 获取单牌
	 * @param card
	 * **/
	public ArrayList<Integer> getSinglecard(ArrayList<Integer> cardList, int daiNum, int card){
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for (int i = PockerValueType.POCKER_VALUE_TYPE_SINGLE.value(); i <= PockerValueType.POCKER_VALUE_TYPE_BOMB.value(); i++) {
			ArrayList< ArrayList<Integer> > opOutList = new ArrayList< ArrayList<Integer> > ();
			int count = BasePockerLogic.getSameCardByTypeEx(opOutList, cardList, PockerValueType.valueOf(i));

			if(count <= 0) {
				continue;
			}
			if(i == PockerValueType.POCKER_VALUE_TYPE_BOMB.value()){
				temp.addAll(opOutList.get(count - 1));
//				if(0 != card ){
				m_opCardTypeBackUp = PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value();
//				}
				return temp;
			}else {
				for (int j = 0; j < count; j++) {
					for (int j2 = 0; j2 < opOutList.get(j).size(); j2++) {
						int tempCard = opOutList.get(j).get(j2);
						if (0 != card && BasePockerLogic.getCardValue(card) < BasePockerLogic.getCardValue(tempCard) || card == 0)  {
							temp.add(tempCard);
						}

						if(temp.size() >= daiNum) {
							return temp;
						}
					}
				}
			}
		}
		return temp;
	}

	/**
	 * 相同的牌
	 * */
	public ArrayList<Integer>   getSameCard (ArrayList<Integer> cardList, int sameNum, int daiNum, PockerValueType pockerType,boolean isPair) {
		if ((cardList.size() <  sameNum + daiNum)&&daiNum==0) {
			return new ArrayList<Integer>();
		}

		cardList.sort(BasePockerLogic.sorterBigToSmallNotTrump);

		ArrayList<ArrayList<Integer>> mCardList = this.getSameCardByList(this.m_cardList, sameNum);
		boolean isNomarlOpCard = true;

		if(m_cardList.size() > 0){
			isNomarlOpCard = false;

			if(mCardList == null || mCardList.size() <= 0){
				CommLogD.info("getSameCard error: m_cardList do not get same card "+sameNum+", m_cardList"+m_cardList.toString());
				return new ArrayList<Integer>();
			}
		}


		for (int i = pockerType.value(); i < PockerValueType.POCKER_VALUE_TYPE_FLUSH.value(); i++) {
			ArrayList<Integer> temp = new ArrayList<Integer>();
			ArrayList< ArrayList<Integer> > opOutList = new ArrayList< ArrayList<Integer> > ();
			int count = BasePockerLogic.getSameCardByTypeEx(opOutList, cardList, PockerValueType.valueOf(i));
			if(count <= 0) {
				continue;
			}

			if (BasePocker.PockerValueType.POCKER_VALUE_TYPE_BOMB.value() == i) {
				if (PDK_define.PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value() != m_opCardType) {
					temp.addAll(opOutList.get(count - 1));
					m_opCardTypeBackUp = PDK_define.PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value();
					return temp;
				} else {
					if (m_cardList.size() > 3) {
						int cardValue = BasePocker.getCardValue(m_cardList.get(0));
						for (ArrayList<Integer> opList : opOutList) {
							int currentCardValue = BasePocker.getCardValue(opList.get(0));
							if (currentCardValue > cardValue) {
								temp.addAll(opList);
								m_opCardTypeBackUp = PDK_define.PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value();
								return temp;
							}
						}
					}
				}

			}

			for (int j = 0; j < count; j++) {
				ArrayList<Integer> list = opOutList.get(j);
				if(isNomarlOpCard || ( !isNomarlOpCard && BasePockerLogic.getCardValue( list.get(0)) > BasePockerLogic.getCardValue(mCardList.get(0).get(0)))){
					for (int k = 0; k < sameNum; k++) {
						if(k < list.size()) {
							temp.add(list.get(k));
						}
					}
					break;
				}
			}
			if(temp.size() <= 0) {
				continue;
			}

			if(temp.size() == sameNum){
				cardList.removeAll(temp);
				if (daiNum > 0) {
					temp.addAll(this.getSinglecard(cardList, daiNum, (byte) 0,isPair));
					if(temp.size() == this.m_cardList.size() || cardList.size()<=daiNum) {
						return temp;
					}
				}else{
					if(temp.size() == sameNum + daiNum) {
						return temp;
					}
				}
			}
			temp.clear();
		}

		return new ArrayList<Integer>();
	}


	/***
	 * 顺子的牌
	 * @param cardList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Integer> getShunZi(ArrayList<Integer> cardList){
		if (cardList.size() < 5) {
			return new ArrayList<Integer>();
		}
		ArrayList<Integer> tempCardList = (ArrayList<Integer>) cardList.clone();
		ArrayList< ArrayList<Integer> > outList = new ArrayList< ArrayList<Integer> > ();
		BasePockerLogic.getShunZiByCount(outList, tempCardList, m_cardList.size());

		boolean isNomarlOpCard = true;
		if(m_cardList.size() > 0){
			isNomarlOpCard = false;

			if(outList.size() <= 0) {
				return new ArrayList<Integer>();
			}
		}

		ArrayList<Integer> mCardList = (ArrayList<Integer>) m_cardList.clone();
		mCardList.sort(BasePockerLogic.sorterBigToSmallNotTrump);
		for(int i = outList.size() - 1 ; i >= 0 ; i--){
			ArrayList<Integer> temp = (ArrayList<Integer>) outList.get(i).clone();
			//判断是否有2
			if(temp.indexOf(new Integer((byte) 0x0F)) >= 0 || temp.indexOf(new Integer((byte) 0x1F))>= 0 || temp.indexOf(new Integer((byte) 0x2F))>= 0 || temp.indexOf(new Integer((byte) 0x3F))>= 0) {
				continue;
			}
			temp.sort(BasePockerLogic.sorterBigToSmallNotTrump);
			if( isNomarlOpCard || ( !isNomarlOpCard && this.compareOneCard(temp.get(0), mCardList.get(0)))){
				if(cardList.containsAll(temp)) {
					return temp;
				}
			}
		}
		return new ArrayList<Integer>();
	}

	/**
	 * 联队
	 * */
	public ArrayList<Integer>   getLianDui (ArrayList<Integer> cardList, int sameNum, int daiNum, PockerValueType pockerType,boolean isPair) {
		if (cardList.size() <  m_cardList.size()&&daiNum==0) {
			return new ArrayList<Integer>();
		}
		ArrayList<ArrayList<Integer>> mCardList = this.getSameCardByList(this.m_cardList, sameNum);
		if(mCardList == null || mCardList.size() < 2){
			CommLogD.error("getLianDui error: m_cardList do not get same card"+sameNum+", m_cardList"+m_cardList.toString());
			return new ArrayList<Integer>();
		}

		ArrayList<Integer> mShunZiList = new ArrayList<Integer>();
		for (int j = 0; j < mCardList.size(); j++) {
			mShunZiList.add(mCardList.get(j).get(0));
		}

		ArrayList<Integer> tempMShunZilist = this.getShunZiByList2(mShunZiList,this.m_cardList.size()/5);
		if(tempMShunZilist == null || tempMShunZilist.size() < 2){
			CommLogD.error("getLianDui error: m_cardList do not get same card "+sameNum+", mShunZiList="+mShunZiList.toString());
			return new ArrayList<Integer>();
		}

		tempMShunZilist.sort(BasePockerLogic.sorterBigToSmallNotTrump);
		int minCard = 0;
		if (tempMShunZilist.size() > 0) {
			minCard = BasePocker.getCardValue(tempMShunZilist.get(tempMShunZilist.size() -1));
		}
		if(daiNum>0){
			//顺子更新
			if(mCardList.size()!=tempMShunZilist.size()){
				mCardList = (ArrayList<ArrayList<Integer>>)mCardList.stream().filter(n->tempMShunZilist.contains(n.get(0))).collect(Collectors.toList());
			}
			//顺子取错
			if(tempMShunZilist.size()*5!=this.m_cardList.size()){
				if(tempMShunZilist.size()>this.m_cardList.size()/5){
					while(tempMShunZilist.size()!=this.m_cardList.size()/5){
						Integer removeCard = BasePockerLogic.getCardValue(tempMShunZilist.remove(tempMShunZilist.size() - 1));
						mCardList = (ArrayList<ArrayList<Integer>>)mCardList.stream().filter(n -> BasePockerLogic.getCardValue(n.get(0)) != removeCard).collect(Collectors.toList());
					}
				}
			}
		}
		ArrayList<Integer> temp = new ArrayList<Integer>();
		cardList.sort(BasePockerLogic.sorterBigToSmallNotTrump);
		for (int i = pockerType.value(); i < PockerValueType.POCKER_VALUE_TYPE_BOMB.value(); i++) {

			ArrayList< ArrayList<Integer> > opOutList = new ArrayList< ArrayList<Integer> > ();
			int count = BasePockerLogic.getSameCardByType(opOutList, cardList, PockerValueType.valueOf(i));

			if(count < 2) {
				continue;
			}
			ArrayList< ArrayList<Integer> >  lianDuiList = this.getLianDuiList(opOutList,mCardList.size());
			if (lianDuiList == null || lianDuiList.size() < 2) {
				continue;
			}

			ArrayList<Integer> opList = new ArrayList<Integer>();
			for (int j = 0; j < lianDuiList.size(); j++) {
				if(BasePocker.getCardValue(lianDuiList.get(j).get(0)) > minCard) {
					opList.add(lianDuiList.get(j).get(0));
				}
			}

			ArrayList<Integer> tempShunZilist = this.getShunZiByList(opList);
			if(null == tempShunZilist ||  (null != tempShunZilist && tempShunZilist.size() < mCardList.size())) {
				continue;
			}
			for (int j = tempShunZilist.size() ; j >=0; j--) {
				if(j >= tempShunZilist.size()) {
					continue;
				}
				if (BasePocker.getCardValue(tempShunZilist.get(j)) > BasePocker.getCardValue(tempMShunZilist.get(tempMShunZilist.size() - 1 ))) {

					ArrayList<Integer> sameCardList = BasePockerLogic.getSameCard(cardList, tempShunZilist.get(j), true);
					while (sameCardList.size() > sameNum) {
						sameCardList.remove(0);
					}
					temp.addAll(sameCardList);
				}

				if(temp.size() == sameNum * mCardList.size()) {
					break;
				}
			}

			if(temp.size() == sameNum * mCardList.size()){
				cardList.removeAll(temp);

				if (daiNum > 0) {
					int num = this.m_cardList.size() - temp.size();
					temp.addAll(this.getSinglecard(cardList, num, (byte) 0,isPair));
					if(temp.size() == this.m_cardList.size() || cardList.size()<=num) {
						return temp;
					}
				}else{
					if(temp.size() == sameNum * mCardList.size()) {
						return temp;
					}
				}
			}
		}

		return new ArrayList<Integer>();
	}

	/**
	 * 判断是不是炸弹
	 * **/
	public boolean checkBomb(int opCardType, ArrayList<Integer> list){
		if(PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value() != opCardType){
			return false;
		}
		//小于3张都不是炸弹
		if(list.size() < 3){
			return false;
		}
		int cardValue = BasePocker.getCardValue(list.get(0));
		//炸弹牌值相等
		for (Integer byte1 : list) {
			if (BasePocker.getCardValue(byte1) != cardValue) {
				return false;
			}
		}
		//3A炸只有在3A炸弹玩法才有
		if(list.size() == 3){
			if(cardValue != AVAULE || !this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_3AZHA)){
				return false;
			}
		}
		if(m_opCardType == opCardType){//上一手是炸弹
			if (m_cardList.size() > 0 && list.size() == m_cardList.size() &&  BasePocker.getCardValue(m_cardList.get(0)) > BasePocker.getCardValue(list.get(0))) {
				return false;
			}
			if(list.size() ==3){//3A压任何牌
				ArrayList<Integer> tempList = BasePockerLogic.getSameCard(list, 0x0E, true);
				if(null != tempList &&  tempList.size() == 3 && this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_3AZHAMAX)){
					return true;
				}
				return false;//3张炸弹只有3A，其他都是错误的牌型
			}
			if(m_cardList.size() ==3){//3A不能被任何牌压
				return !this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_3AZHAMAX);
			}
		}

		return true;
	}

	/**
	 * 操作位改变
	 * */
	public void addOpPos(boolean isCalcEndTurn){
		for (int i = 0; i < this.room.getPlayerNum(); i++) {
			m_OpPos = (++m_OpPos)%this.room.getPlayerNum();
			PDKRoomPos tempRoomPos = (PDKRoomPos) this.room.getRoomPosMgr().getPosByPosID(m_OpPos);
			if(tempRoomPos.getPrivateCards().size() > 0){
				break;
			}
		}
		this.set.startMS = CommTime.nowMS();
		this.isCheck = false;
		this.set.setOpPos(m_OpPos);
		if(!isCalcEndTurn) {
			return;
		}

		int tempLastOpPos = m_lastOpPos;
		PDKRoomPos roomPos = (PDKRoomPos) this.room.getRoomPosMgr().getPosByPosID(m_lastOpPos);
		if(this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_XUEZHANDAODI) && roomPos.privateCards.size() <= 0) {
			for (int i = 0; i < this.room.getPlayerNum(); i++) {
				m_lastOpPos = (++m_lastOpPos)%this.room.getPlayerNum();
				PDKRoomPos tempRoomPos = (PDKRoomPos) this.room.getRoomPosMgr().getPosByPosID(m_lastOpPos);
				if(tempRoomPos.getPrivateCards().size() > 0){
					break;
				}
			}
		}
		if(tempLastOpPos != m_lastOpPos) {
			return;
		}
		if(m_OpPos == m_lastOpPos){
			addBombScore();
			m_bTurnEnd = true;
		}
	}

	/**
	 * 增加炸弹分数
	 */
	private void addBombScore() {
		if(maxBombPos>=0){
			if(PDK_define.BombAlgorithm.GETROUNDALLBOMB.has(room.getRoomCfg().zhadansuanfa)){
				this.set.addRoomDouble(maxBombPos, roundBombCount);
			}else if(PDK_define.BombAlgorithm.WINNER.has(room.getRoomCfg().zhadansuanfa)){
				this.set.addRoomDouble(maxBombPos, 1);
			}
		}
		maxBombPos = -1;
	}

	/**
	 * 有炸就算，炸下去立马算分
	 * @param pos
	 */
	private void addBombByAtOnce(int pos) {
		if(PDK_define.BombAlgorithm.ALWAYS.has(room.getRoomCfg().zhadansuanfa)){
			this.set.addRoomDouble(pos, 1);
		}
	}

	/**
	 * 首出 或新一轮首出
	 * @return 带牌数量
	 * **/
	public int   getNomarlTypeCard (int pos , ArrayList<Integer> outList,ArrayList<Integer> intList){
		int daiNum = 0;
		if(intList.size() <= 0) {
			return daiNum;
		}
//		ArrayList<Integer> list = new ArrayList<Integer>();
		intList.sort(BasePockerLogic.sorterBigToSmallNotTrump);

		int index = Math.max( intList.size() - 1, 0);
		int size = intList.size();
		for(int a = 0; a < size; a++){
			int card = intList.get(index);
			int count = BasePockerLogic.getCardCount(intList, card, true);
			if (1 == count) {
				if (!this.checkNextIsOneCard(pos, card)) {
					index = Math.max(index - 1, 0);
				}else{
					outList.add(card);
					m_opCardTypeBackUp = PDK_CARD_TYPE.PDK_WANFA_SINGLECARD.value();
					break;
				}
			} else if(2 == count){
				outList.add(card);
				outList.add(intList.get(index - 1));
				m_opCardTypeBackUp = PDK_CARD_TYPE.PDK_CARD_TYPE_DUIZI.value();
				break;
			}else if(3 == count){
				if (intList.size() == 3) {
					outList.addAll(intList);
					m_opCardTypeBackUp = PDK_CARD_TYPE.PDK_CARD_TYPE_3BUDAI.value();
				} else {
					outList.add(intList.remove(index));
					outList.add(intList.remove(index - 1));
					outList.add(intList.remove(index - 2));
					if (intList.size() < PDKRoomSet.DEFAULTDAINUM) {
						if(intList.size() == PDKRoomSet.DEFAULTDAINUM-1) {
							m_opCardTypeBackUp = PDK_CARD_TYPE.PDK_CARD_TYPE_3DAI1.value();
//							outList.addAll(intList);
							daiNum = PDKRoomSet.DEFAULTDAINUM-1;
						}else if(intList.size() == 0){
							m_opCardTypeBackUp = PDK_CARD_TYPE.PDK_CARD_TYPE_3BUDAI.value();
						}
						for (int i = 0; i < daiNum; i++) {
							if(intList.size()>0){
								outList.add(intList.remove(0));
							}
						}
					} else {
						ArrayList<Integer> cards = this.getSinglecard(intList, PDKRoomSet.DEFAULTDAINUM, (byte) 0);
						if(m_opCardTypeBackUp!=PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value()){
							List<Integer> tempList = cards.size()>=PDKRoomSet.DEFAULTDAINUM?cards.subList(0,PDKRoomSet.DEFAULTDAINUM):cards;
							outList.addAll(tempList);
							daiNum = PDKRoomSet.DEFAULTDAINUM;
							m_opCardTypeBackUp = PDK_CARD_TYPE.PDK_CARD_TYPE_3DAI2.value();
						}else{
							outList.clear();
							outList.addAll(cards);
							m_opCardTypeBackUp = PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value();
						}
					}
				}
				break;
			}else if(4 ==  count && this.set.isFirstOp()){
				outList.add(intList.remove(index));
				outList.add(intList.remove(index - 1));
				outList.add(intList.remove(index - 2));
				outList.add(intList.remove(index - 3));
				m_opCardTypeBackUp = PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value();
//				if (intList.size() < PDKRoomSet.DEFAULT4DAINUM) {
//					if(intList.size() == PDKRoomSet.DEFAULT4DAINUM-1) {
//						m_opCardTypeBackUp = PDK_CARD_TYPE.PDK_CARD_TYPE_4DAI2.value();
////						outList.addAll(intList);
//						daiNum = PDKRoomSet.DEFAULT4DAINUM-1;
//					} else if(intList.size() == PDKRoomSet.DEFAULT4DAINUM-2) {
//						m_opCardTypeBackUp = PDK_CARD_TYPE.PDK_CARD_TYPE_4DAI1.value();
////						outList.addAll(intList);
//						daiNum = PDKRoomSet.DEFAULT4DAINUM-2;
//					}else if(intList.size() == 0) {
//						m_opCardTypeBackUp = PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value();
//					}
//					for (int i = 0; i < daiNum; i++) {
//						if(intList.size()>0){
//							outList.add(intList.remove(0));
//						}
//					}
//				} else {
//					if (this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_FEIBICHU)) {
//						ArrayList<Integer> cards = this.getSinglecard(intList, PDKRoomSet.DEFAULT4DAINUM, (byte) 0);
//						if(m_opCardTypeBackUp!=PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value()){
//							List<Integer> tempList = cards.size()>=PDKRoomSet.DEFAULT4DAINUM?cards.subList(0,PDKRoomSet.DEFAULT4DAINUM):cards;
//							outList.addAll(tempList);
//							daiNum = PDKRoomSet.DEFAULT4DAINUM;
//							m_opCardTypeBackUp = PDK_CARD_TYPE.PDK_CARD_TYPE_4DAI3.value();
//						}else{
//							outList.clear();
//							outList.addAll(cards);
//							m_opCardTypeBackUp = PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value();
//						}
//					}else{
//						m_opCardTypeBackUp = PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value();
//					}
//				}
				break;
			}else{
				index = Math.max(index - count, 0);
			}

		} ;


		if(outList.size() <= 0 && intList.size() > 0){
			outList.add(intList.get(0));
			daiNum = 0;
			m_opCardTypeBackUp = PDK_CARD_TYPE.PDK_WANFA_SINGLECARD.value();
		}

		return daiNum;
	}

	/**
	 * 获取赖子牌
	 * */
	public ArrayList<Integer> getLaiZiList(ArrayList<Integer> cardList){
		if(!this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_LAIZI)){
			//CommLogD.info("getLaiZiList error: is not laizi wanfa");
			return null;
		}
		ArrayList<Integer> list = new ArrayList<Integer>();
		int razzValue = BasePocker.getCardValue(this.set.getRazz());
		for (Integer byte1 : cardList) {
			if(BasePocker.getCardValue(byte1) == razzValue){
				list.add(byte1);
			}
		}
		return list;
	}

	/**
	 * 在有赖子的情况的 没有考虑没有赖子的
	 * 根据传入的牌返回顺子
	 * **/
	@SuppressWarnings("unchecked")
	public ArrayList<Integer> getShunZiByList(ArrayList<Integer> cardList){
		ArrayList<Integer> list = (ArrayList<Integer>) cardList.clone();
		ArrayList<Integer> laiziList = getLaiZiList(list);
		if(null != laiziList && laiziList.size() > 0 ) {
			list.removeAll(laiziList);
		}
		list.sort(BasePockerLogic.sorterBigToSmallNotTrump);
		for (int i = 0; i < cardList.size() - 1; i++) {
			if( Math.abs( BasePocker.getCardValue( list.get(i) )  - BasePocker.getCardValue( list.get(i+1) ) ) != 1){
				if(laiziList == null || laiziList.size() <= 0){
					return null;
				}
				int card = laiziList.remove(0);
				card = (byte) (BasePocker.getCardColor(list.get(i)) - 1);
				list.add(i+1, card);
			}
		}
		if(list.size() == cardList.size()){
			list.sort(BasePockerLogic.sorterBigToSmallNotTrump);
			return list;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Integer> getShunZiByList2(ArrayList<Integer> cardList,int size){
		ArrayList<Integer> list = (ArrayList<Integer>) cardList.clone();
		ArrayList<Integer> targetList = new ArrayList<>();
		list.sort(BasePockerLogic.sorterBigToSmallNotTrump);
		for (int i = 0; i < cardList.size() - 1; i++) {
			if(Math.abs( BasePocker.getCardValue( list.get(i) )  - BasePocker.getCardValue( list.get(i+1) ) ) == 1){
				if(!targetList.contains(list.get(i))){
					targetList.add(list.get(i));
				}
				if(!targetList.contains(list.get(i+1))){
					targetList.add(list.get(i+1));
				}
				if(i+1>=(cardList.size() - 1)){
					if(targetList.size()>=size){
						targetList.sort(BasePockerLogic.sorterBigToSmallNotTrump);
						return targetList;
					}
				}
			}else{
				if(targetList.size()>=size){
					targetList.sort(BasePockerLogic.sorterBigToSmallNotTrump);
					return targetList;
				}
				targetList = new ArrayList<>();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Integer> getShunZiByListEx(ArrayList<Integer> cardList, int size){
		ArrayList<Integer> list = (ArrayList<Integer>) cardList.clone();
		ArrayList<Integer> laiziList = this.getLaiZiList(list);
		if(null != laiziList && laiziList.size() > 0 ) {
			list.removeAll(laiziList);
		}
		list.sort(BasePockerLogic.sorterBigToSmallNotTrump);
		ArrayList<Integer> tempList = new ArrayList<Integer>();
		tempList.add(list.get(0));
//		for (int j = 0; j < size; j++) {
		for (int i = 0; i < cardList.size() - 1; i++) {
			if( Math.abs( BasePocker.getCardValue( list.get(i) )  - BasePocker.getCardValue( list.get(i+1) ) ) != 1){
				if(laiziList == null || laiziList.size() <= 0){
					tempList.clear();
					tempList.add(list.get(i+1));
					continue;
//						return null;
				}
				int card = laiziList.remove(0);
				card = (BasePocker.getCardColor(list.get(i)) - 1);
				list.add(i+1, card);
			}else{
				tempList.add(list.get(i+1));
			}
			if (tempList.size() == size) {
				tempList.sort(BasePockerLogic.sorterBigToSmallNotTrump);
				return tempList;
			}
		}
//		}

//		if(list.size() == cardList.size()){
//			list.sort(BasePockerLogic.sorterBigToSmallNotTrump);
//			return list;
//		}
		return null;
	}

	/**
	 * 在有赖子的情况的 没有考虑没有赖子的
	 * 根据传入的牌返回顺子
	 * **/
	@SuppressWarnings("unchecked")
	public ArrayList<ArrayList<Integer>> getSameCardByList(ArrayList<Integer> cardList, int sameNum){
		ArrayList<Integer> list = (ArrayList<Integer>) cardList.clone();
		ArrayList<Integer> laiziList = getLaiZiList(list);
		if(laiziList != null && laiziList.size() > 0) {
			list.removeAll(laiziList);
		}
		ArrayList<ArrayList<Integer>> outList = new ArrayList<ArrayList<Integer>>();
		int count = BasePockerLogic.getPockerEqualValue(outList, list);
		for (int i = 0; i < count; ) {
			if (i >= outList.size()) {
				break;
			}
			int size = outList.get(i).size();

			if(size > sameNum){
//				outList.remove(i);
				while (outList.get(i).size() > sameNum) {
					outList.get(i).remove(0);
				}
				i++;
			}
//			else if (sameNum >  size && (null == laiziList || (null != laiziList && laiziList.size() <= 0) )) {
//				outList.remove(i);
//			}
			else {
				for (int j = 0; j < sameNum - size; j++) {
					if(laiziList == null || laiziList.size() <= 0) {
						break;
					}
					laiziList.remove(0);
					outList.get(i).add(BasePocker.getCardValue(outList.get(i).get(0)));
				}

				if (sameNum < outList.get(i).size()) {
					outList.remove(i);
				}else  if(sameNum > outList.get(i).size()) {
					while (outList.get(i).size() > sameNum) {
						outList.get(i).remove(0);
					}
					i++;
				}else{
					i++;
				}
			}
		}
		if (null != laiziList &&  laiziList.size() >= sameNum) {
			int num = laiziList.size() / sameNum;
			for (int i = 0; i < num; i++) {
				ArrayList<Integer> temp = new ArrayList<Integer>();
				for (int j = 0; j < sameNum; j++) {
					temp.add(laiziList.remove(j));
				}
				outList.add(temp);
			}
		}

		for (int i = 0; i < count; ) {
			if(outList.size() > i &&  outList.get(i).size() < sameNum){
				outList.remove(i);
			}else{
				i++;
			}
		}
		return outList;
	}

	/**
	 * @return m_OpPos
	 */
	public int getOpPos() {
		return m_OpPos;
	}

	/**
	 * @return m_lastOpPos
	 */
	public int getLastOpPos() {
		return m_lastOpPosBack;
	}

	/**
	 * @return m_opCardType
	 */
	public int getOpCardType() {
		return m_opCardType;
	}

	/**
	 * @return m_cardList
	 */
	public ArrayList<Integer> getCardList() {
		return m_cardList;
	}

	/**
	 * @return m_bSetEnd
	 */
	public boolean isSetEnd() {
		return m_bSetEnd;
	}


	/**
	 * 判断牌有没有大牌
	 * */
	@SuppressWarnings("unchecked")
	public boolean checkHaveMaxCard(int pos){
		PDKRoomPos roomPos = (PDKRoomPos) this.room.getRoomPosMgr().getPosByPosID(pos);

		if(roomPos.getPrivateCards().size() <= 0){
			return false;
		}

		ArrayList<Integer> tempCard = (ArrayList<Integer>) roomPos.privateCards.clone();
		tempCard.sort(BasePockerLogic.sorterBigToSmallNotTrump);

		ArrayList<Integer> outList = new ArrayList<Integer>();
		int daiNum = 0;
		int opCardType = m_opCardType;
		m_opCardTypeBackUp = m_opCardType;
		switch (PDK_CARD_TYPE.valueOf(m_opCardType)) {
			case PDK_CARD_TYPE_DUIZI:  			//对子
			{
				outList = this.getSameCard(tempCard, 2, 0, PockerValueType.POCKER_VALUE_TYPE_SUB,false);
			}
			break;
			case PDK_CARD_TYPE_3BUDAI:  		//3不带
			{
				outList = this.getSameCard(tempCard, 3, 0, PockerValueType.POCKER_VALUE_TYPE_THREE,false);
			}
			break;
			case PDK_CARD_TYPE_3DAI1: 			//3带1
			{
				daiNum = 1;
				outList = this.getSameCard(tempCard, 3, 1, PockerValueType.POCKER_VALUE_TYPE_THREE,false);
			}
			break;
			case PDK_CARD_TYPE_3DAI2:  			//3带2
			{
				daiNum = 2;
				outList = this.getSameCard(tempCard, 3, 2, PockerValueType.POCKER_VALUE_TYPE_THREE,false);
			}
			break;
			case PDK_CARD_TYPE_4DAI1:  			//4带1
			{
				daiNum = 1;
				outList = this.getSameCard(tempCard, 4, 1, PockerValueType.POCKER_VALUE_TYPE_BOMB,false);
			}
			break;
			case PDK_CARD_TYPE_4DAI2:  			//4带2
			{
				daiNum = 2;
				outList = this.getSameCard(tempCard, 4, 2, PockerValueType.POCKER_VALUE_TYPE_BOMB,false);
			}
			break;
			case PDK_CARD_TYPE_4DAI3:  			//4带3
			{
				daiNum = 3;
				outList = this.getSameCard(tempCard, 4, 3, PockerValueType.POCKER_VALUE_TYPE_BOMB,false);
			}
			break;
			case 	PDK_CARD_TYPE_ZHADAN:  			//炸弹
			{
				outList = this.getSameCard(tempCard, 4, 0, PockerValueType.POCKER_VALUE_TYPE_BOMB,false);
			}
			break;
			case PDK_CARD_TYPE_SHUNZI:  		//顺子
			{
				outList = this.getShunZi(tempCard);
			}
			break;
			case 	PDK_CARD_TYPE_FEIJI3:  			//飞机
			{
				daiNum = PDKRoomSet.DEFAULTDAINUM;
				outList = this.getLianDui(tempCard, 3, daiNum, PockerValueType.POCKER_VALUE_TYPE_THREE,false);
			}
			break;
			case 	PDK_CARD_TYPE_FEIJI4:  //飞机带翅膀
			{
				daiNum = PDKRoomSet.DEFAULTDAINUM+1;
				outList = this.getLianDui(tempCard, 4, daiNum, PockerValueType.POCKER_VALUE_TYPE_BOMB,false);
			}
			break;
			case 	PDK_WANFA_LIANDUI:  //联队
			{
				outList = this.getLianDui(tempCard, 2, 0, PockerValueType.POCKER_VALUE_TYPE_SUB,false);
			}
			break;
			case 	PDK_WANFA_SINGLECARD:
			{
				outList = this.getSinglecard(tempCard, 1,  m_cardList.size() > 0 ? m_cardList.get(0) : Integer.valueOf((byte) 0),false);
				if (null != outList && outList.size() > 0 && tempCard.size() > 0 ) {
					if (!this.checkNextIsOneCard(pos, outList.get(0))) {
						tempCard.sort(BasePockerLogic.sorterBigToSmallNotTrump);
						outList.clear();
						if(tempCard.size() > 0) {
							outList.add(tempCard.get(0));
						}
					}
				}
			}
			break;
			default:
				CommLogD.error("not find opTYpe ="+m_opCardType +","+ PDK_CARD_TYPE.valueOf(m_opCardType));
				break;
		}
		opCardType = m_opCardTypeBackUp;
		if (PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value() != opCardType) {
			if ( null == outList || outList.size() <=0) {

				ArrayList< ArrayList<Integer> > opOutList = new ArrayList< ArrayList<Integer> > ();
				int count = BasePockerLogic.getSameCardByType(opOutList, tempCard, PockerValueType.POCKER_VALUE_TYPE_BOMB);

				if(count > 0){
					opCardType = PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value();
					outList.addAll(opOutList.get(0));
				}

				if (this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_3AZHA)) {
					ArrayList<Integer> tempList = BasePockerLogic.getSameCard(tempCard, 0x0E, true);
					if(null != tempList &&  tempList.size() == 3){
						opCardType = PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value();
						outList = tempList;
					}
				}
			}
		}else{
			boolean is3AZha = PDK_define.PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value() == m_opCardType&&m_cardList.stream().allMatch(n->BasePocker.getCardValue(n)== BasePocker.getCardValue(AVAULE));
			if (is3AZha && !this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_3AZHAMAX)) {
				ArrayList<ArrayList<Integer>> mCardList = this.getSameCardByList(roomPos.privateCards, 4);
				if(CollectionUtils.isNotEmpty(mCardList)){
					opCardType = PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value();
					outList = mCardList.get(0);
				}
			}
		}
		//3A最大
		if (this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_3AZHA) && this.room.isWanFaByType(PDK_WANFA.PDK_WANFA_3AZHAMAX)) {
			ArrayList<Integer> tempList = BasePockerLogic.getSameCard(roomPos.privateCards, 0x0E, true);
			if(null != tempList &&  tempList.size() == 3){
				opCardType = PDK_CARD_TYPE.PDK_CARD_TYPE_ZHADAN.value();
				outList = tempList;
			}
		}

		if (null == outList || outList.size() <= 0) {
			return false;
		}
		return true;
	}

	/**
	 * 获取托管列表
	 * @return
	 */
	public List<Boolean> getTrusteeshipList(){
		return room.getRoomPosMgr().getPlayingPos().stream().map(AbsRoomPos::isTrusteeship).collect(Collectors.toList());
	}
}
