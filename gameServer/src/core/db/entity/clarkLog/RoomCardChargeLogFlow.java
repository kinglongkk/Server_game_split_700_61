package core.db.entity.clarkLog;

import com.ddm.server.annotation.DataBaseField;
import com.ddm.server.annotation.TableName;
import com.ddm.server.common.utils.CommTime;
import core.db.entity.BaseClarkLogEntity;
import core.ioc.Constant;
import lombok.Data;

@TableName(value = "RoomCardChargeLog")
@Data
public class RoomCardChargeLogFlow extends BaseClarkLogEntity<RoomCardChargeLogFlow> {

	@DataBaseField(type = "bigint(20)", fieldname = "pid", comment = "玩家pid")
	private long pid = 0; // 玩家pid
	@DataBaseField(type = "int(11)", fieldname = "reason", comment = "产生原因类型")
	private int reason = 0; // 产生原因类型
	@DataBaseField(type = "int(11)", fieldname = "num", comment = "数量")
	private int num = 0; // 数量
	@DataBaseField(type = "int(11)", fieldname = "cur_remainder", comment = "当前剩余")
	private int cur_remainder = 0; // 当前剩余
	@DataBaseField(type = "int(11)", fieldname = "pre_value", comment = "前值")
	private int pre_value = 0; // 前值
	@DataBaseField(type = "int(11)", fieldname = "type", comment = "类型(1:获得,2:消耗)")
	private int type = 0; // 类型(1:获得,2:消耗)
	@DataBaseField(type = "bigint(20)", fieldname = "familyID", comment = "工会ID")
	private long familyID;
	@DataBaseField(type = "int(11)", fieldname = "gameType", comment = "游戏类型")
	private int gameType;
	@DataBaseField(type = "int(11)", fieldname = "rechargeType", comment = "类型(1:直充,2:平台)")
	private int rechargeType = 0; // 类型(1:直充,2:平台)
    @DataBaseField(type = "int(11)", fieldname = "cityId", comment = "城市ID")
    private int cityId;
	
	public RoomCardChargeLogFlow() {
	}

	public RoomCardChargeLogFlow(long pid, int reason, int num, int cur_remainder, int pre_value, int type,
			long familyID, int gameType, int rechargeType,int cityId) {
		this.pid = pid;
		this.reason = reason;
		this.num = num;
		this.cur_remainder = cur_remainder;
		this.pre_value = pre_value;
		this.type = type;
		this.familyID = familyID;
		this.gameType = gameType;
		this.rechargeType = rechargeType;
		this.cityId = cityId;
	}

	@Override
	public String getInsertSql() {
		return "INSERT INTO RoomCardChargeLog"
				+ "(`server_id`, `timestamp`, `date_time`, `pid`, `reason`, `num`, `cur_remainder`, `pre_value`, `type`,`familyID`,`gameType`,`rechargeType`,`cityId`)"
				+ "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";
	}

	public static String getCreateTableSQL() {
		String sql = "CREATE TABLE IF NOT EXISTS `RoomCardChargeLog` ("
				+ "`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',"
				+ "`server_id` int(11) NOT NULL DEFAULT '0' COMMENT '服务器ID',"
				+ "`timestamp` int(11) NOT NULL DEFAULT '0' COMMENT '日志时间(时间戳)',"
				+ "`date_time` varchar(20) NOT NULL DEFAULT '20160801' COMMENT '日志时间(yyyymmdd)',"
				+ "`pid` bigint(20) NOT NULL DEFAULT '0' COMMENT '玩家pid',"
				+ "`reason` int(11) NOT NULL DEFAULT '0' COMMENT '产生原因类型',"
				+ "`num` int(11) NOT NULL DEFAULT '0' COMMENT '数量',"
				+ "`cur_remainder` int(11) NOT NULL DEFAULT '0' COMMENT '当前剩余',"
				+ "`pre_value` int(11) NOT NULL DEFAULT '0' COMMENT '前值',"
				+ "`type` int(11) NOT NULL DEFAULT '0' COMMENT '类型(1:获得,2:消耗)',"
				+ "`familyID` bigint(20) NOT NULL DEFAULT '0' COMMENT '工会ID',"
				+ "`gameType` int(11) NOT NULL DEFAULT '-1' COMMENT '游戏类型',"
				+ "`rechargeType` int(11) NOT NULL DEFAULT '0' COMMENT '类型(1:直充,2:平台)'," + "PRIMARY KEY (`id`),"
                + "`cityId` int(11) NOT NULL DEFAULT '0' COMMENT '城市ID',"
				+ "KEY `PRD` (`pid`,`rechargeType`,`date_time`(20)) USING BTREE,"
				+ "KEY `DTR` (`date_time`(20),`type`,`rechargeType`) USING BTREE"
				+ ") COMMENT='Room Card 房卡消费日志' DEFAULT CHARSET=utf8";
		return sql;
	}

	@Override
	public Object[] addToBatch() {
		Object[] params = new Object[13];
		params[0] = Constant.serverIid;
		params[1] = CommTime.nowSecond();
		params[2] = CommTime.getNowTimeStringYMD();
		params[3] = pid;
		params[4] = reason;
		params[5] = num;
		params[6] = cur_remainder;
		params[7] = pre_value;
		params[8] = type;
		params[9] = familyID;
		params[10] = gameType;
		params[11] = rechargeType;
		params[12] = cityId;
		return params;
	}
}
