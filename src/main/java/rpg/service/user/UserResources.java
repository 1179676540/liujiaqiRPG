package rpg.service.user;

import java.util.concurrent.ConcurrentHashMap;

import rpg.pojo.User;

/**
 * 用户资源
 * @author ljq
 *
 */
public class UserResources {

	/**
	 * 用户名找到用户
	 */
	public static ConcurrentHashMap<String, User> nameMap = new ConcurrentHashMap<String, User>();

}
