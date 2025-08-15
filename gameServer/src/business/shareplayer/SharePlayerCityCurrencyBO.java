package business.shareplayer;

import lombok.Data;

/**
 * @author xsj
 * @date 2020/8/28 11:20
 * @description 共享城市钻石对象
 */
@Data
public class SharePlayerCityCurrencyBO {
    //自增主key
    private long id;
    //所属玩家ID
    private long pid;
    //城市id
    private int cityId;
    //圈卡
    private int value;
    //时间戳
    private int time;
}
