package rpg.service.gh;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/**
 * 工会缓存
 * @author ljq
 *
 */
@Component
public class GhCache {

	private HashMap<Integer, HashMap<String, String>> ghsqMpCache = new HashMap<Integer, HashMap<String, String>>();
	
	private static GhCache self;

	public static GhCache getInstance() {
		return self;
	}

	@PostConstruct
	private void init() {
		self = this;
	}

	public HashMap<Integer, HashMap<String, String>> getGhsqMp() {
		return ghsqMpCache;
	}
}
