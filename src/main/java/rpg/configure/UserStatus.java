package rpg.configure;

/**
 * 用户状态
 * @author ljq
 *
 */
public enum UserStatus {
	/**
	 * 普通状态
	 */
	ORDINARY(1),
	/**
	 * 攻击小怪状态
	 */
	ACK_MONSTER(2), 
	/**
	 * 攻击boss状态
	 */
	ACK_BOSS(3),
	/**
	 * 交易中状态
	 */
	JYFLAGING(4),
	
	/**
	 * 交易确认状态
	 */
	JYYES(5);
	
	private final int value;

	private UserStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
