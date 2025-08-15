package business.dzpk.c2s.iclass;

import com.ddm.server.common.CommLogD;
import jsproto.c2s.cclass.room.BaseCreateRoom;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.*;

/**
 * 创建房间
 *
 * @author Administrator
 */
@SuppressWarnings("serial")
@Setter
@Getter
public class CDZPK_CreateRoom extends BaseCreateRoom implements Cloneable, Serializable {
    private int daiFen;//带入记分牌
    private int qianzhu;//ante前注：
    private int daxiaomangjibie;//大小盲
    private int santiaoshunzi;//三条顺子：
    private int moshi;//模/s/s/s/s/s/s式：


    /**
     * 对象之间的浅克隆【只负责copy对象本身，不负责深度copy其内嵌的成员对象】
     *
     * @return
     */
    @Override
    public CDZPK_CreateRoom clone() {
        return (CDZPK_CreateRoom) super.clone();
    }

    /**
     * 实现对象间的深度克隆【从外形到内在细胞，完完全全深度copy】
     *
     * @return
     */
    public CDZPK_CreateRoom deepClone() {
        // Anything 都是可以用字节流进行表示，记住是任何！	
        CDZPK_CreateRoom cookBook = null;
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
            cookBook = (CDZPK_CreateRoom) ois.readObject();

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
