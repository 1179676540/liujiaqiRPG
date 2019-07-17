package rpg.service.friend;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/**
 * 好友缓存
 * @author ljq
 *
 */
@Component
public class FriendCache {

	private HashMap<String, List<String>> friendMpCache = new HashMap<String, List<String>>();
	
	private static FriendCache self;

	public static FriendCache getInstance() {
		return self;
	}

	@PostConstruct
	private void init() {
		self = this;
	}

	public HashMap<String, List<String>> getFriendMp() {
		return friendMpCache;
	}
	
}
