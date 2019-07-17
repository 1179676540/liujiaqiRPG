package rpg.configure;

/**角色类型
 * @author ljq
 *
 */
public enum RoleType {
	/**
	 * 战士
	 */
	ZHANSHI(1),
	/**
	 * 牧师
	 */
	MUSHI(2),
	/**
	 * 法师
	 */
	FASHI(3),
	/**
	 * 召唤师
	 */
	ZHAOHUANSHI(4);
	private final int value;

	private RoleType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
}
