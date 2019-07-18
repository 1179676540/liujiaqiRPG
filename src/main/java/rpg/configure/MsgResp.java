package rpg.configure;

/**
 * 服务器发送响应消息
 * @author ljq
 *
 */
public class MsgResp {
	
	public static String ORDER_ERR = "指令错误";
	public static String LOGIN_ERR = "账户或密码错误，请重新登陆:";
	public static String DINGHAO_ERR = "你已在别处登陆，请重新登陆";
	public static String ROLECHECK_ERR = "角色类型选择错误";
	public static String PSW_ERR = "两次密码不一致";
	public static String REG_SUCCESS = "恭喜注册成功";
	public static String ACCOUNT_ERR = "账户名已存在";
	public static String USERNAME_ERR = "用户昵称名已存在";
	public static String MONEY_ERR = "金币不足，请充值";
	
	public static String BOSSSCENE_SUCCESS = "boss已全被消灭，退出副本";
	public static String BOSSSCENE_DEAD = "你已被打死，副本挑战失败，你已被传送出副本";
	public static String BOSSSCENE_TIME_OVER = "已达挑战时间上限，副本挑战失败，自动退出副本";
	public static String MARKET_ERR = "物品流拍了........";
}