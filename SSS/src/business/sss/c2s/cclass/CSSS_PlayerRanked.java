package business.sss.c2s.cclass;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家牌的顺序
 * @author Huaxing
 *
 */
public class CSSS_PlayerRanked {
	public List<String> first = new ArrayList<>();
	public List<String> second = new ArrayList<>();
	public List<String> third = new ArrayList<>();
	
	public CSSS_PlayerRanked() {
		super();
	}

	public CSSS_PlayerRanked(List<String> first, List<String> second,
			List<String> third) {
		super();
		this.first = first;
		this.second = second;
		this.third = third;
	}

	@Override
	public String toString() {
		return "CSSS_PlayerRanked [first=" + first + ", second=" + second
				+ ", third=" + third + "]";
	}

}
