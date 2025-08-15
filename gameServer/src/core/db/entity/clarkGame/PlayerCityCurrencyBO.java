package core.db.entity.clarkGame;

import com.ddm.server.annotation.DataBaseField;
import com.ddm.server.annotation.TableName;
import com.ddm.server.common.utils.CommTime;
import com.google.common.collect.Maps;
import core.db.entity.BaseEntity;
import core.db.other.AsyncInfo;
import core.ioc.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;

@TableName(value = "playerCityCurrency" )
@Data
@NoArgsConstructor
public class PlayerCityCurrencyBO extends BaseEntity<PlayerCityCurrencyBO> {

    @DataBaseField(type = "bigint(20)", fieldname = "id", comment = "自增主key",indextype = DataBaseField.IndexType.Unique)
    private long id;
    @DataBaseField(type = "bigint(20)", fieldname = "pid", comment = "所属玩家ID")
    private long pid;
	@DataBaseField(type = "int(11)", fieldname = "cityId", comment = "城市id")
    private int cityId;
	@DataBaseField(type = "int(11)", fieldname = "value", comment = "圈卡")
    private int value;
    @DataBaseField(type = "int(11)", fieldname = "time", comment = "时间戳")
    private int time;

    public PlayerCityCurrencyBO(long pid, int cityId) {
        this.pid = pid;
        this.cityId = cityId;
    }

    public void savePid(long pid) {
        if(pid==this.pid) {
            return;
        }
        this.pid = pid;
        getBaseService().update("pid", pid,id,new AsyncInfo(id));
    }



	public void saveValue(int value) {
		if (this.value == value) {
            return;
        }
        HashMap<String,Object> map = Maps.newHashMapWithExpectedSize(2);
        this.value = value;
        map.put("value",value);

		this.time = CommTime.nowSecond();
        map.put("time",time);
        getBaseService().update(map,id,new AsyncInfo(id));
    }

    public static String getSql_TableCreate() {
        String sql = "CREATE TABLE IF NOT EXISTS `playerCityCurrency` ("
                + "`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',"
                + "`pid` bigint(20) NOT NULL DEFAULT '0' COMMENT '所属玩家ID',"
                + "`cityId` int(11) NOT NULL DEFAULT '0' COMMENT '城市id',"
                + "`value` int(11) NOT NULL DEFAULT '0' COMMENT '值',"
                + "`time` int(11) NOT NULL DEFAULT '0' COMMENT '时间戳',"
                + "PRIMARY KEY (`id`),"
                + "KEY `pid_cityId` (`pid`,`cityId`)"
                + ") COMMENT='玩家城市钻石'  DEFAULT CHARSET=utf8 AUTO_INCREMENT=" + (Constant.InitialID + 1);
        return sql;
    }
}
