package rpg.service.group;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import rpg.pojo.Group;

/**
 * 队伍缓存
 * 
 * @author ljq
 *
 */
@Component
public class GroupCache {
	private static GroupCache self;

	/**
	 * 通过队伍id将队伍缓存起来
	 */
	private HashMap<String, Group> userGroupMpCache = new HashMap<String, Group>();
	/**
	 * 用户申请列表
	 */
	private HashMap<String, List<String>> userApplyCache = new HashMap<String, List<String>>();

	public static GroupCache getInstance() {
		return self;
	}

	@PostConstruct
	private void init() {
		self = this;
	}

	public HashMap<String, Group> getUserGroupMpCache() {
		return userGroupMpCache;
	}

	public HashMap<String, List<String>> getUserApplyCache() {
		return userApplyCache;
	}
}
