package rpg.service.bossscene;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import rpg.pojo.BossScene;

/**
 * boss场景缓存
 * @author ljq
 *
 */
@Component
public class BossSceneCache {

	private  HashMap<String, BossScene> userBossSceneCache = new HashMap<String, BossScene>();
	
	private static BossSceneCache self;
	
	public static BossSceneCache getInstance() {
		return self;
	}

	@PostConstruct
	private void init() {
		self = this;
	}

	public HashMap<String, BossScene> getUserBossCache() {
		return userBossSceneCache;
	}

}
