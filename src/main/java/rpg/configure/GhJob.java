package rpg.configure;

/**
 * 工会职位
 * 
 * @author ljq
 *
 */
public enum GhJob {
	/**
	 * 会长
	 */
	PRESIDENT(1),
	/**
	 * 副会长
	 */
	VICE_PRESIDENT(2),
	/**
	 * 精英
	 */
	ELITE(3),
	/**
	 * 成员
	 */
	MEMBER(4);

	private final int value;

	private GhJob(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
