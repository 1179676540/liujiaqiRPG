package rpg.configure;

/**指令类型
 * @author ljq
 *
 */
public enum InstructionsType {
	/**
	 * 登陆
	 */
	LOGIN("login"),
	/**
	 * 改变攻击目标
	 */
	CHANGE_ACK_TARGET("a"),
	/**
	 * 退出
	 */
	ESC("esc"),
	/**
	 * 攻击
	 */
	ACK("ack"),
	/**
	 * 技能按钮1
	 */
	SKILL_KEY_1("1"),
	/**
	 * 技能按钮3
	 */
	SKILL_KEY_3("3"),
	/**
	 * 添加好友
	 */
	ADD("add"),
	/**
	 * 展示申请
	 */
	SHOWSQ("showsq"),
	/**
	 * 展示
	 */
	SHOW("show"),
	/**
	 * 接受
	 */
	ACCEPT("accept"),
	/**
	 * 创建
	 */
	CREAT("creat"),
	/**
	 * 加入
	 */
	JOIN("join"),
	/**
	 * 展示用户
	 */
	SHOWUSER("showuser"),
	/**
	 * 解散
	 */
	JS("js"),
	/**
	 * 提升
	 */
	RAISE("raise"),
	/**
	 * 降级
	 */
	DOWN("down"),
	/**
	 * 放入
	 */
	PUT("put"),
	/**
	 * 展示商店
	 */
	SHOWSTORE("showstore"),
	/**
	 * 取出
	 */
	TAKE("take"),
	/**
	 * 踢出
	 */
	T("t"),
	/**
	 * 接受
	 */
	YES("yes"),
	/**
	 * 取消
	 */
	NO("no"),
	/**
	 * 移除
	 */
	DIV("div"),
	/**
	 * 交易时确认
	 */
	Y("y"),
	/**
	 * 买
	 */
	BUY("buy"),
	/**
	 * 卖
	 */
	SELL("sell"),
	/**
	 * 展示正在做的任务
	 */
	SHOWD("showd"),
	/**
	 * 展示已完成任务
	 */
	SHOWF("showf"),
	/**
	 * 一口价
	 */
	APRICE("1"),
	/**
	 * 竞拍
	 */
	AUCTION("2");
	
	private String value;

	private InstructionsType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
}
