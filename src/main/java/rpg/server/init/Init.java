package rpg.server.init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import rpg.core.AllUserData;
import rpg.data.dao.AccountMapper;
import rpg.data.dao.UserMapper;
import rpg.pojo.Account;
import rpg.pojo.AccountExample;
import rpg.pojo.AccountExample.Criteria;
import rpg.pojo.EmailRpg;
import rpg.pojo.User;
import rpg.pojo.UserExample;
import rpg.service.area.Refresh;
import rpg.service.email.EmailCache;
import rpg.service.market.MarketRefresh;
import rpg.util.RpgUtil;
import rpg.xmlparse.BuffXmlParse;
import rpg.xmlparse.ZbXmlParse;

/**
 * 初始化
 * 
 * @author ljq
 *
 */
@Component
public class Init {

	@Autowired
	private BuffXmlParse buffXmlParse;
	@Autowired
	private RpgUtil rpgUtil;
	@Autowired
	private ZbXmlParse zbXmlParse;
	@Autowired
	private AccountMapper accountMapper;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private EmailCache emailCache;

	@PostConstruct
	public void initThread() {
		ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("线程").build();
		ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(1, namedThreadFactory);
		scheduled.scheduleAtFixedRate(new Refresh(buffXmlParse), 0, 1000, TimeUnit.MILLISECONDS);
		ThreadFactory namedThreadFactory1 = new ThreadFactoryBuilder().setNameFormat("拍卖线程").build();
		ScheduledThreadPoolExecutor scheduled1 = new ScheduledThreadPoolExecutor(1, namedThreadFactory1);
		scheduled1.scheduleAtFixedRate(new MarketRefresh(rpgUtil, zbXmlParse), 0, 100, TimeUnit.MILLISECONDS);
	}

	@PostConstruct
	public void initData() {
		HashMap<String,String> map = new HashMap<>(100);
		AllUserData.getInstance().setAccountToNameMap(map);
		HashMap<String, ArrayList<EmailRpg>> alluserEmailCache = new HashMap<>(100);
		emailCache.setAlluserEmailCache(alluserEmailCache);
		AccountExample accountExample = new AccountExample();
		List<Account> accountList = accountMapper.selectByExample(accountExample);
		for (Account account : accountList) {
			UserExample userExample = new UserExample();
			rpg.pojo.UserExample.Criteria createCriteria = userExample.createCriteria();
			createCriteria.andIdEqualTo(account.getId());
			List<User> userList = userMapper.selectByExample(userExample);
			User user = userList.get(0);
			ArrayList<EmailRpg> arrayList = new ArrayList<EmailRpg>();
			alluserEmailCache.put(user.getNickname(), arrayList);
			map.put(account.getUsername(), user.getNickname());
		}
	}
}