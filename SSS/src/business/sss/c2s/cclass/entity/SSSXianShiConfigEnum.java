package business.sss.c2s.cclass.entity;

/**
 * 显示操作配置
 * 不限制,3分钟出牌,5分钟出牌
 * @author Administrator
 *
 */
public enum SSSXianShiConfigEnum {
	// 45秒
	NOT_LIMIT(46000),
	// 80秒
	Min3(81000),
	// 2分钟
	Min2(121000),
	// 5分钟
	Min5(301000),
	// 1分钟
	Min1(61000),
	;
	private int value;
	SSSXianShiConfigEnum(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	
	public static SSSXianShiConfigEnum valueOf(int value) {
		for (SSSXianShiConfigEnum flow : SSSXianShiConfigEnum.values()) {
			if (flow.ordinal() == value) {
				return flow;
			}
		}
		return SSSXianShiConfigEnum.NOT_LIMIT;
	}
	
}
