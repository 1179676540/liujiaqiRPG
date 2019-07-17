package rpg.configure;

/**
 * buff类型
 * @author ljq
 *
 */
public enum BuffType {
	/**
	 * 回血
	 */
	ADDHP(7),
	/**
	 * 宠物攻击
	 */
	PETACK(5);
	
	private final int value;

	private BuffType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
