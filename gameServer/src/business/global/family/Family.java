package business.global.family;

import BaseCommon.CommLog;
import business.global.sharefamily.ShareFamilyCurrencyBOMgr;
import business.player.Player;
import business.player.PlayerMgr;
import business.player.feature.PlayerCityCurrency;
import business.player.feature.PlayerClub;
import business.player.feature.PlayerFamily;
import business.shareplayer.SharePlayerCurrencyBOMgr;
import cenum.ConstEnum;
import cenum.ConstEnum.RechargeType;
import cenum.ConstEnum.ResOpType;
import cenum.ItemFlow;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.Config;
import com.ddm.server.common.redis.DistributedRedisLock;
import com.ddm.server.common.utils.CommTime;
import com.ddm.server.websocket.def.ErrorCode;
import com.google.common.collect.Maps;
import core.db.entity.clarkGame.FamilyBO;
import core.db.entity.clarkGame.FamilyCityCurrencyBO;
import core.db.entity.clarkGame.PlayerCityCurrencyBO;
import core.db.entity.clarkGame.ZleRechargeBO;
import core.db.other.Restrictions;
import core.db.service.clarkGame.ClubMemberRelationBOService;
import core.db.service.clarkGame.FamilyCityCurrencyBOService;
import core.ioc.ContainerMgr;
import core.logger.flow.FlowLogger;
import core.network.http.proto.SData_Result;
import jsproto.c2s.cclass.family.FamilyCityCurrencyItem;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class Family {
	public static final long DefaultFamilyID = 10001L;

	private FamilyBO familyBO;

	private Map<Integer, FamilyCityCurrencyBO> familyCityCurrencyBOMap = Maps.newConcurrentMap();


	public Family(FamilyBO familyBO) {
		this.familyBO = familyBO;
	}

	public void setFamilyBO(FamilyBO familyBO) {
		this.familyBO = familyBO;
	}

	public FamilyBO getFamilyBO() {
		return this.familyBO;
	}

	/**
	 * 获取游戏列表
	 * @return
	 */
	public String getHaveYouXi () {
		return this.familyBO.getHaveYouxi();
	}

	/**
	 * 获取工会ID
	 * @return
	 */
	public long getFamilyID () {
		return this.familyBO.getFamilyID();
	}

	/**
	 * 获取名称
	 * @return
	 */
	public String getName () {
		return this.familyBO.getName();
	}

	/**
	 * 会长ID
	 * @return
	 */
	public long getOwnerID() {
		return this.familyBO.getOwnerID();
	}

	/**
	 * 获取RMB
	 * @return
	 */
	public long getTotalRMB() {
		return this.familyBO.getTotalRMB();
	}

	/**
	 * 计算人民币
	 * @param price
	 */
	public void addTotalRMB (int price) {
		long totalRMB = this.familyBO.getTotalRMB();
		this.familyBO.saveTotalRMB_Sync(totalRMB + price);
	}

	/**
	 * 亲友圈计算人民币
	 * @param price
	 */
	public void addClubTotalRMB (int price) {
		long totalRMB = this.familyBO.getClubTotalRMB();
		this.familyBO.saveClubTotalRMB_Sync(totalRMB + price);
	}


	/**
	 * 添加房卡
	 * */
	public void addRoomCard(int count){
		int num = this.familyBO.getRoomcardNum() + count;
		this.familyBO.saveRoomcardNum_Sync(num);
	}
	/**
	 * 获取房卡
	 * */
	public int getRoomCard(){
		return this.familyBO.getRoomcardNum();
	}

	/**
	 * 设置房卡
	 * @param roomCard
	 */
	public void setRoomCard(int roomCard) {
		int roomcardNum = this.familyBO.getRoomcardNum() + roomCard;
		if(roomcardNum <= 0) {
			this.familyBO.saveRoomcardNum_Sync(0);
		} else {
			this.familyBO.saveRoomcardNum_Sync(roomcardNum);
		}
	}

	/**
	 * 操作代理圈卡
	 * @param clubCard 圈卡数
	 * @param type 1:充值,2:撤回
	 * @return
	 */
	public boolean onClubCard(int clubCard,int type) {
		if (type == 1) {
			// 获得圈卡
			return gainRoomCard(this.getFamilyID(), 0L, clubCard, type, 1,this.getFamilyBO().getCityId());
		} else if (type == 2) {
			// 消耗圈卡
			return consumeRoomCard(this.getFamilyID(), 0L, clubCard, type, 1,this.getFamilyBO().getCityId());
		} else {
			return false;
		}
	}

	/**
	 * 代理对玩家操作圈卡
	 * @param player 玩家
	 * @param clubCard 圈卡数
	 * @param type 1:给玩家拨卡,2:将玩家的卡收回
	 * @return
	 */
	public boolean onFamilyClubCardToPlayer(Player player,int clubCard,int type) {
		// 检查玩家
		if(null == player){
			return false;
		}
		if (type == 1) {
			// 给玩家拨卡
			// 检查代理圈卡 < 操作的圈卡
			if (this.getFamilyBO().getClubCardNum() < clubCard ) {
				return false;
			}
			// 消耗代理圈卡
			this.consumeRoomCard(this.getFamilyID(),player.getPid(), clubCard, ResOpType.Lose.ordinal(), 2,this.getFamilyBO().getCityId());
			// 玩家获取圈卡
			player.getFeature(PlayerClub.class).onAdminClubCard(clubCard, this.getFamilyID(), ResOpType.Gain.ordinal(),RechargeType.Family,ItemFlow.FamilyClubCardToPlayer,this.getFamilyBO().getCityId());
			return true;
		} else if (type == 2) {
			// 将玩家的卡收回
			int playerClubCard =player.getFeature(PlayerClub.class).getPlayerClubRoomCard(this.getFamilyID(), 3);
			// 检查代理圈卡 < 操作的圈卡
			if(playerClubCard < clubCard) {
				return false;
			}
			// 代理获得圈卡
			this.gainRoomCard(this.getFamilyID(), player.getPid(), clubCard,ResOpType.Gain.ordinal(),2,this.getFamilyBO().getCityId());
			// 玩家消耗圈卡
			player.getFeature(PlayerClub.class).onAdminClubCard(clubCard, this.getFamilyID(), ResOpType.Lose.ordinal(),RechargeType.Family,ItemFlow.FamilyClubCardToPlayer,this.getFamilyBO().getCityId());
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 代理对玩家操作圈卡
	 * @param player 玩家
	 * @param value 钻石
	 * @param cityId 城市id
	 * @return
	 */
	public SData_Result onFamilyCardToPlayer(Player player, int value, int cityId,String beizhu) {
		// 检查玩家
		if(Objects.isNull(player)){
			return SData_Result.make(ErrorCode.Player_PidError,"Player_PidError");
		}
		SData_Result result = player.getFeature(PlayerFamily.class).getFamilyRecommend();
		if (ErrorCode.Success.equals(result.getCode())) {
			if (getFamilyID() != result.getCustom()) {
				return SData_Result.make(ErrorCode.Not_Family_Member, "getFamilyRecommend this.player.getPid({%d}) != family.getOwnerID({%d})", player.getPid(), getOwnerID());
			}
		} else if(player.getFamiliID() != getFamilyID()) {
			return SData_Result.make(ErrorCode.Not_Family_Member,"Not_Family_Member");
		}
		// 会长
		Player playerFamily = PlayerMgr.getInstance().getPlayer(this.getFamilyBO().getOwnerID());
		if(Objects.isNull(playerFamily)) {
			return SData_Result.make(ErrorCode.Player_PidError,"playerFamily error pid:{%d}",this.getFamilyBO().getOwnerID());
		}
		if (playerFamily.getFeature(PlayerCityCurrency.class).checkAndConsumeItemFlow(value,ItemFlow.FamilyCardToPlayer,cityId)) {
			player.getFeature(PlayerCityCurrency.class).gainItemFlow(value,ItemFlow.FamilyCardToPlayer,cityId);
			ZleRechargeBO zleRechargeBO = new ZleRechargeBO();
			zleRechargeBO.setToId(player.getPid());
			zleRechargeBO.setRoomCard(value);
			zleRechargeBO.setType(1);
			zleRechargeBO.setCreateTime(CommTime.nowSecond());
			zleRechargeBO.setCityId(cityId);
			zleRechargeBO.setKeyId(getOwnerID());
			zleRechargeBO.setGive_roomcard_num(0);
			zleRechargeBO.setBeizhu(beizhu);
			zleRechargeBO.getBaseService().save(zleRechargeBO);
			return SData_Result.make(ErrorCode.Success);
		} else {
			return SData_Result.make(ErrorCode.NotEnough_Currency,"playerFamily pid:{%d},cityId:{%d},value:{%d},cardValue:{%d}",playerFamily.getPid(),cityId,playerFamily.getFeature(PlayerCityCurrency.class).getPlayerCityCurrencyValue(cityId),value);
		}
	}


	/**
	 * 获得圈卡
	 * @param familyID 代理ID
	 * @param pid 玩家ID
	 * @param value 值
	 * @param type (1:获得,2:消耗)
	 * @param sourceType 来源类型(1:后台,2:玩家)
	 * @return
	 */
	private boolean gainRoomCard(long familyID,long pid, int value, int type,int sourceType,int cityId) {
		int before = 0;
		int finalValue = 0;
		before = this.familyBO.getClubCardNum();
		finalValue = Math.min(1999999999, before + value);
		// 获得奖励房卡
		this.familyBO.saveClubCardNum_Sync(finalValue);
		// 日志:获得房卡
		FlowLogger.familyCardChargeLog(familyID, pid, value, finalValue, before, type, sourceType,cityId);
		return true;
	}

	/**
	 * 消耗圈卡
	 * @param familyID 代理ID
	 * @param pid 玩家ID
	 * @param value 值
	 * @param type (1:获得,2:消耗)
	 * @param sourceType 来源类型(1:后台,2:玩家)
	 * @return
	 */
	private boolean consumeRoomCard(long familyID,long pid, int value, int type,int sourceType,int cityId) {
		int before = 0;
		int finalValue = 0;
		before = this.familyBO.getClubCardNum();
		finalValue = Math.max(0, before - value);
		// 消耗平台房卡
		this.familyBO.saveClubCardNum_Sync(finalValue);
		// 日志:消耗房卡
		FlowLogger.familyCardChargeLog(familyID, pid, -value, finalValue, before, type, sourceType,cityId);
		return true;
	}


	/**
	 * 获取指定公会等级信息
	 *
	 * @param cityId 城市Id
	 * @return
	 */
	public int getFamilyCityCurrencyValue(int cityId) {
		FamilyCityCurrencyBO familyCityCurrencyBO = this.newFamilyCityCurrencyBO(cityId);
		return Objects.isNull(familyCityCurrencyBO) ? -1:familyCityCurrencyBO.getValue();
	}

	/**
	 * 获取指定公会等级信息
	 *
	 * @param cityId 城市Id
	 * @return
	 */
	public FamilyCityCurrencyBO newFamilyCityCurrencyBO(int cityId) {
		if (cityId <= 0) {
			return null;
		}
		FamilyCityCurrencyBO currencyBO = null;
		if(Config.isShare()){
			currencyBO = ShareFamilyCurrencyBOMgr.getInstance().get(getFamilyID(), cityId);
		} else {
			currencyBO = this.getFamilyCityCurrencyBOMap().get(cityId);
		}
		if (Objects.isNull(currencyBO)) {
			// 新创建指定城市
			currencyBO = new FamilyCityCurrencyBO(getFamilyID(), cityId,-1);
			currencyBO.getBaseService().saveIgnoreOrUpDate(currencyBO);
			this.getFamilyCityCurrencyBOMap().put(currencyBO.getCityId(), currencyBO);
			if(Config.isShare()){
				ShareFamilyCurrencyBOMgr.getInstance().add(currencyBO);
			}
		}
		return currencyBO;
	}

	/**
	 * 设置公会指定信息
	 * @param value     值
	 * @param cityId    城市id
	 * @return
	 */
	public boolean familyCityCurrency(int value, int cityId) {
		if (value < 0) {
			return false;
		}
		String uuid= UUID.randomUUID().toString();
		try {
			//redis分布式锁
			DistributedRedisLock.acquire("familyCityValue" + getFamilyID(), uuid);
			FamilyCityCurrencyBO currencyBO = this.newFamilyCityCurrencyBO(cityId);
			if (Objects.isNull(currencyBO)) {
				CommLog.error("familyCityCurrency pid:{},cityId:{}", getFamilyID(), cityId);
				return false;
			}
			currencyBO.saveValue(value);
			if(Config.isShare()){
				ShareFamilyCurrencyBOMgr.getInstance().add(currencyBO);
			}
			CommLog.info("familyCityCurrency cityId:{},Value:{}",cityId,value);
			return true;
		} finally {
			DistributedRedisLock.release("familyCityValue" + getFamilyID(), uuid);
		}
	}


	public boolean deleteFamilyCityCurrency(int cityId) {
		String uuid= UUID.randomUUID().toString();
		try {
			//redis分布式锁
			DistributedRedisLock.acquire("familyCityValue" + getFamilyID(), uuid);
			FamilyCityCurrencyBO cityCurrencyBO = null;
			if(Config.isShare()){
				cityCurrencyBO = ShareFamilyCurrencyBOMgr.getInstance().get(getFamilyID(), cityId);
			} else {
				cityCurrencyBO = this.getFamilyCityCurrencyBOMap().get(cityId);
			}
			if (Objects.nonNull(cityCurrencyBO)) {
				cityCurrencyBO.getBaseService().delete(cityCurrencyBO.getId());
				this.getFamilyCityCurrencyBOMap().remove(cityId);
				ShareFamilyCurrencyBOMgr.getInstance().remove(cityCurrencyBO);
				return true;
			}
			return false;
		} finally {
			DistributedRedisLock.release("familyCityValue" + getFamilyID(), uuid);
		}
	}


	/**
	 * 获取公会城市列表
	 *
	 * @return
	 */
	public List<FamilyCityCurrencyItem> getFamilyCityCurrencyList() {
		List<FamilyCityCurrencyItem> familyCityCurrencyItemList = ContainerMgr.get().getComponent(FamilyCityCurrencyBOService.class).findAllE(Restrictions.eq("familyId", getFamilyID()), FamilyCityCurrencyItem.class, FamilyCityCurrencyItem.getItemsName());
		if (CollectionUtils.isEmpty(familyCityCurrencyItemList)) {
			return Collections.emptyList();
		}
		return familyCityCurrencyItemList.stream().map(k -> {
			FamilyCityCurrencyBO familyCityCurrencyBO = getFamilyCityCurrencyBOMap().get(k.getCityId());
			if (Objects.nonNull(familyCityCurrencyBO)) {
				return new FamilyCityCurrencyItem(familyCityCurrencyBO.getCityId(), familyCityCurrencyBO.getValue());
			} else {
				return new FamilyCityCurrencyItem(k.getCityId(), k.getValue());
			}
		}).collect(Collectors.toList());
	}

}
