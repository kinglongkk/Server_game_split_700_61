package business.global.pk.sss;

import com.ddm.server.common.utils.CommMath;

import java.util.ArrayList;
import java.util.List;

public class SSSRandomPos {
	public final static int SaiziCnt = 3; // 筛子数量
	// 随机筛子
	public static int randomSaizi(int playerNum) {
		List<Integer> Saizi = new ArrayList<>();
		int totalPoint = 0;
		for (int i = 0; i < SaiziCnt; i++) {
			int tmp = CommMath.randomInt(1, 6);
			Saizi.add(tmp);
			totalPoint += tmp;
		}
		return (totalPoint - 1) % playerNum;
	}


	
}
