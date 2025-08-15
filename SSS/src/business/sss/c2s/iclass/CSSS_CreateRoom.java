package business.sss.c2s.iclass;

import com.ddm.server.common.CommLogD;
import jsproto.c2s.cclass.room.BaseCreateRoom;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 自由扑克 接收客户端数据 创建房间
 *
 * @author Huaxing
 *
 */
public class CSSS_CreateRoom extends BaseCreateRoom implements Cloneable, Serializable {

	public int zhuangjiaguize=-1;
	public List<Integer> huase = new ArrayList<Integer>();// 加色列表
	public int difen = 0;
	public int daqiang = 0;
	public int guize;
	public boolean isKeptOutAfterStartGame;
	public int paixingfenshu=0;

	@Override
	public CSSS_CreateRoom clone() {
		return (CSSS_CreateRoom) super.clone();
	}

	/**
	 * 实现对象间的深度克隆【从外形到内在细胞，完完全全深度copy】
	 *
	 * @return
	 */
	public CSSS_CreateRoom deepClone() {
		// Anything 都是可以用字节流进行表示，记住是任何！
		CSSS_CreateRoom cookBook = null;
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
			cookBook = (CSSS_CreateRoom) ois.readObject();

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