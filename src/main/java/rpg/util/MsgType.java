package rpg.util;

/**
 * 消息类型枚举
 * 
 * @author ljq
 *
 */
public enum MsgType {
	/**
	 * 心跳
	 */
	HEART_BEAT("心跳"),
	/**
	 * 玩家Buff
	 */
	USER_BUFF_MSG("001"),
	/**
	 * 怪物攻击
	 */
	MONSTER_ACK_MSG("002"),
	/**
	 * 怪物Buff
	 */
	MONSTER_BUFF_MSG("003"),
	/**
	 * 登陆成功
	 */
	LOGIN_SUCCES_MSG("000");
	private String value;

	private MsgType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
