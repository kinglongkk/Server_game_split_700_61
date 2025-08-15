package core.db.entity.clarkGame;

import com.ddm.server.annotation.DataBaseField;
import com.ddm.server.annotation.TableName;
import core.db.entity.BaseEntity;
import core.ioc.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName(value = "cityGive")
@Data
@NoArgsConstructor
public class CityGiveBO extends BaseEntity<CityGiveBO> {

    @DataBaseField(type = "bigint(20)", fieldname = "id", comment = "自增主key", indextype = DataBaseField.IndexType.Unique)
    private long id;
    @DataBaseField(type = "int(11)", fieldname = "cityId", comment = "城市ID")
    private int cityId;
    @DataBaseField(type = "int(2)", fieldname = "state", comment = "0:关闭,1:开启")
    private int state;
    @DataBaseField(type = "int(11)", fieldname = "startTime", comment = "更新时间")
    private int updateTime;






    public static String getSql_TableCreate() {
        String sql = "CREATE TABLE IF NOT EXISTS `cityGive` ("
                + "`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',"
                + "`cityId` int(11) NOT NULL DEFAULT '0' COMMENT '城市id',"
                + "`state` int(2) NOT NULL DEFAULT '0' COMMENT '0:关闭,1:开启',"
                + "`updateTime` int(11) NOT NULL DEFAULT '0' COMMENT '更新时间'," + "PRIMARY KEY (`id`)"
                + ") COMMENT='赠送房卡不限制城市表'  DEFAULT CHARSET=utf8 AUTO_INCREMENT=" + (Constant.InitialID + 1);
        return sql;
    }
}
