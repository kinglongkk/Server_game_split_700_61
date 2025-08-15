package business.player;

import cenum.RoomTypeEnum;
import com.ddm.server.common.Config;
import com.ddm.server.common.utils.EncryptUtils;
import com.ddm.server.mq.factory.MqConsumerTopicFactory;
import com.ddm.server.websocket.def.SubscribeEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 玩家房间的信息
 */
@Data
public class PlayerRoomInfo {
    /**
     * 房间Id
     */
    private long roomId = 0L;
    /**
     * 亲友圈Id
     */
    private long clubId;
    /**
     * 联赛Id
     */
    private long unionId;
    /**
     * 消耗卡
     */
    private int consumeCard = 0;
    /**
     * 配置Id
     */
    private long configId;

    /**
     * 城市id
     */
    private int cityId;

    /**
     * 房间类型
     */
    private RoomTypeEnum roomTypeEnum;
    /**
     * 密码
     */
    private String password;
    /**
     * 发布的主题
     */
    private String subjectTopic;
    public void clear() {
        this.setRoomId(0L);
        this.setConsumeCard(0);
        this.setCityId(0);
    }

    public boolean checkClubOrUnion() {
        return clubId > 0L || unionId > 0L;
    }

    public void setConsumeCard(int consumeCard, int cityId) {
        this.consumeCard = consumeCard;
        this.cityId = cityId;
    }

//    public void setPassword(String password) {
//        if (StringUtils.isNotEmpty(password)) {
//            this.password = EncryptUtils.decryptDES(password);
//        }
//    }

    public void setPasswordDES(String password) {
        if (StringUtils.isNotEmpty(password)) {
            this.password = EncryptUtils.decryptDES(password);
        }
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
        if (this.roomId > 0) {
            this.subjectTopic = Config.getLocalServerTopic();
        } else {
            this.subjectTopic = "";
        }
    }


}
