package rpg.service.email;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import rpg.pojo.EmailRpg;

/**
 * 邮件缓存
 * 
 * @author ljq
 *
 */
@Component
public class EmailCache {

	private HashMap<String, ArrayList<EmailRpg>> alluserEmailCache;

	private static EmailCache self;

	public static EmailCache getInstance() {
		return self;
	}

	@PostConstruct
	private void init() {
		self = this;
	}

	public HashMap<String, ArrayList<EmailRpg>> getAlluserEmailCache() {
		return alluserEmailCache;
	}

	public void setAlluserEmailCache(HashMap<String, ArrayList<EmailRpg>> alluserEmailCache) {
		this.alluserEmailCache = alluserEmailCache;
	}

}
