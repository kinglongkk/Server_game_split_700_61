package business.shareplayer;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xsj
 * @date 2020/9/12 14:32
 * @description 节点在线人数
 */
@Data
public class ShareNodePlayerSize implements Serializable{
    //节点名称
    private String name;
    //节点地址
    private String vipAddress;
    //节点ip
    private String ip;
    //节点端口
    private Integer port;
    //节点玩家数量
    private Long playerSize;


}
