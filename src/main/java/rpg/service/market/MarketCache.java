package rpg.service.market;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.dao.ConcurrencyFailureException;

import rpg.pojo.MarketItem;
import rpg.pojo.User;

/**
 * 交易行缓存
 * @author ljq
 *
 */
public class MarketCache {

	/**
	 * 用户交易行商品缓存
	 */
	public static HashMap<User, List<String>> userMarketItemMp = new HashMap<User, List<String>>();
	/**
	 * 交易行商品缓存
	 */
	public static ConcurrentHashMap<String, MarketItem> MarketItemMp = new ConcurrentHashMap<String, MarketItem>();

}
