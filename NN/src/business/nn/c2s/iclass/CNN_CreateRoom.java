package business.nn.c2s.iclass;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.ddm.server.common.CommLogD;
import jsproto.c2s.cclass.room.BaseCreateRoom;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 牛牛
 * 接收客户端数据
 * 创建房间
 *
 * @author zaf
 */

public class CNN_CreateRoom extends BaseCreateRoom implements Cloneable, Serializable {
    public int difen;                    //底分 1/2 2/4 4/8
    public int fanbeiguize = 0;            //翻倍规则 0：牛牛*4,牛九*3,牛八*2,牛七*2 1:牛牛*3,牛九*2,牛八*2
    public List<Integer> teshupaixing;        //特殊牌型 0：五花牛，1：炸弹牛2：五小牛
    public int isXianJiaTuiZhu;            //闲家推注0:不推，1: 5倍 2：10倍
    public int shangzhuangfenshu;                        //上庄分数 0:无 1:100 2:150 3:200
    public int zuidaqiangzhuang;                        //最大抢庄 0:1倍 1:2倍 2:3倍 3:4倍
    public List<Integer> gaojixuanxiang = new ArrayList<>();//游戏开始后禁止加入,禁止搓牌

    /**
     * 对象之间的浅克隆【只负责copy对象本身，不负责深度copy其内嵌的成员对象】
     *
     * @return
     */
    @Override
    public CNN_CreateRoom clone() {
        return (CNN_CreateRoom) super.clone();
    }

    /**
     * 实现对象间的深度克隆【从外形到内在细胞，完完全全深度copy】
     *
     * @return
     */
    public CNN_CreateRoom deepClone() {
        // Anything 都是可以用字节流进行表示，记住是任何！
        CNN_CreateRoom cookBook = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // 将当前的对象写入baos【输出流 -- 字节数组】里
            oos.writeObject(this);

            // 从输出字节数组缓存区中拿到字节流
            byte[] bytes = baos.toByteArray();

            // 创建一个输入字节数组缓冲区
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            // 创建一个对象输入流
            ObjectInputStream ois = new ObjectInputStream(bais);
            // 下面将反序列化字节流 == 重新开辟一块空间存放反序列化后的对象
            cookBook = (CNN_CreateRoom) ois.readObject();

        } catch (Exception e) {
            CommLogD.error(e.getClass() + ":" + e.getMessage());
        }
        return cookBook;
    }


    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
