package rpg.core;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/**
 * 所有用户数据
 * 
 * @author ljq
 *
 */
@Component
public class AllUserData {

	private static AllUserData self;

	private HashMap<String, String> accountToNameMap;
	
	public static AllUserData getInstance() {
		return self;
	}

	@PostConstruct
	private void init() {
		self = this;
	}

	public HashMap<String, String> getAccountToNameMap() {
		return accountToNameMap;
	}

	public void setAccountToNameMap(HashMap<String, String> accountToNameMap) {
		this.accountToNameMap = accountToNameMap;
	}
}
