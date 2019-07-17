package rpg.service.login;

public enum RegistStaus {
	
	/**
	 * 成功
	 */
	SUCCESS(1),
	/**
	 * 密码不匹配
	 */
	PASSWORDERROR(2),
	/**
	 * 账户已存在
	 */
	ACCOUNTERROR(3),
	/**
	 * 用户名已存在
	 */
	USERNAMEERROR(4);
	
	private final int value;

	private RegistStaus(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
