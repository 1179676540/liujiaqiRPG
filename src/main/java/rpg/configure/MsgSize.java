package rpg.configure;

/**消息大小
 * @author ljq
 *
 */
public enum MsgSize {
	/**
	 * 消息最大大小1
	 */
	MAX_MSG_SIZE_1(1),
	/**
	 * 消息最大大小2
	 */
	MAX_MSG_SIZE_2(2), 
	/**
	 * 消息最大大小3
	 */
	MAX_MSG_SIZE_3(3),
	/**
	 * 消息最大大小4
	 */
	MAX_MSG_SIZE_4(4), 
	/**
	 * 消息最大大小5
	 */
	MAX_MSG_SIZE_5(5), 
	/**
	 * 消息指针1
	 */
	MSG_INDEX_1(1),
	/**
	 * 消息指针2
	 */
	MSG_INDEX_2(2), 
	/**
	 * 消息指针3
	 */
	MSG_INDEX_3(3),
	/**
	 * 消息指针4
	 */
	MSG_INDEX_4(4), 
	/**
	 * 消息指针5
	 */
	MSG_INDEX_5(5);
	
	private final int value;

	private MsgSize(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
