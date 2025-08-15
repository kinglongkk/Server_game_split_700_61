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

import java.util.HashMap;

@TableName(value = "familyCityCurrency" )
@Data
@NoArgsConstructor
public class FamilyCityCurrencyBO extends BaseEntity<FamilyCityCurrencyBO> {

    @DataBaseField(type = "bigint(20)", fieldname = "id", comment = "自增主key",indextype = DataBaseField.IndexType.Unique)
    private long id;
    @DataBaseField(type = "bigint(20)", fieldname = "familyId", comment = "所属公会ID")
    private long familyId;
	@DataBaseField(type = "int(11)", fieldname = "cityId", comment = "城市id")
    private int cityId;
	@DataBaseField(type = "int(11)", fieldname = "value", comment = "值")
    private int value;
    @DataBaseField(type = "int(11)", fieldname = "time", comment = "时间戳")
    private int time;

    public FamilyCityCurrencyBO(long familyId, int cityId) {
        this.familyId = familyId;
        this.cityId = cityId;
    }

    public FamilyCityCurrencyBO(long familyId, int cityId,int value) {
        this.familyId = familyId;
        this.cityId = cityId;
        this.value = value;
    }

    public void saveFamilyId(long familyId) {
        if(familyId==this.familyId) {
            return;
        }
        this.familyId = familyId;
        getBaseService().update("familyId", familyId,id,new AsyncInfo(id));
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
        String sql = "CREATE TABLE IF NOT EXISTS `familyCityCurrency` ("
                + "`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',"
                + "`familyId` bigint(20) NOT NULL DEFAULT '0' COMMENT '所属公会ID',"
                + "`cityId` int(11) NOT NULL DEFAULT '0' COMMENT '城市id',"
                + "`value` int(11) NOT NULL DEFAULT '0' COMMENT '值',"
                + "`time` int(11) NOT NULL DEFAULT '0' COMMENT '时间戳',"
                + "PRIMARY KEY (`id`),"
                + "KEY `familyId_cityId` (`familyId`,`cityId`)"
                + ") COMMENT='公会城市等级'  DEFAULT CHARSET=utf8 AUTO_INCREMENT=" + (Constant.InitialID + 1);
        return sql;
    }
}
