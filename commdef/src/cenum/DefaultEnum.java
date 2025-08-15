package cenum;
/**
 * 默认参数值
 * @author Administrator
 *
 */
public enum DefaultEnum {
	FAMILY_ID(10001),	//默认公会ID
	;

	private int value;

	DefaultEnum(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}
};