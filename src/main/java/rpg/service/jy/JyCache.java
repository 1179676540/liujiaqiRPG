package rpg.service.jy;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import rpg.pojo.Jy;

/**
 * 交易缓存
 * 
 * @author ljq
 *
 */
@Component
public class JyCache {

	private ConcurrentHashMap<String, Jy> jyMapCache = new ConcurrentHashMap<String, Jy>();

	private static JyCache self;

	public static JyCache getInstance() {
		return self;
	}

	@PostConstruct
	private void init() {
		self = this;
	}

	public ConcurrentHashMap<String, Jy> getJyCache() {
		return jyMapCache;
	}

}
