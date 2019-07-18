package rpg.service.login;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rpg.data.dao.AccountMapper;
import rpg.data.dao.MarketItemMapper;
import rpg.data.dao.UserMapper;
import rpg.data.dao.UserbagMapper;
import rpg.data.dao.UserfinishtaskMapper;
import rpg.data.dao.UseritemMapper;
import rpg.data.dao.UserlevelMapper;
import rpg.data.dao.UsertaskprocessMapper;
import rpg.data.dao.UserzbMapper;
import rpg.pojo.Account;
import rpg.pojo.AccountExample;
import rpg.pojo.AccountExample.Criteria;
import rpg.pojo.MarketItem;
import rpg.pojo.MarketItemExample;
import rpg.pojo.User;
import rpg.pojo.UserAttribute;
import rpg.pojo.Userbag;
import rpg.pojo.UserbagExample;
import rpg.pojo.Userfinishtask;
import rpg.pojo.UserfinishtaskExample;
import rpg.pojo.Useritem;
import rpg.pojo.UseritemExample;
import rpg.pojo.Userlevel;
import rpg.pojo.UserlevelExample;
import rpg.pojo.Usertaskprocess;
import rpg.pojo.UsertaskprocessExample;
import rpg.pojo.Userzb;
import rpg.pojo.UserzbExample;
import rpg.service.group.GroupCache;
import rpg.service.market.MarketCache;

/**
 * 登陆功能
 * 
 * @author ljq
 *
 */
@Component
public class Login {
	@Autowired
	private AccountMapper accountMapper;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserbagMapper userbagMapper;
	@Autowired
	private UserzbMapper userzbMapper;
	@Autowired
	private UserlevelMapper userlevelMapper;
	@Autowired
	private MarketItemMapper marketItemMapper;
	@Autowired
	private UseritemMapper useritemMapper;
	@Autowired
	private UsertaskprocessMapper usertaskprocessMapper;
	@Autowired
	private UserfinishtaskMapper userfinishtaskMapper;

	public User login(String username, String psw) {
		// 验证账户和密码
		AccountExample example = new AccountExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<Account> list = accountMapper.selectByExample(example);
		if (list == null || list.size() == 0) {
			return null;
		}
		Account account = list.get(0);
		if (!account.getPsw().equals(psw)) {
			return null;
		}
		Integer id = account.getId();
		User user = userMapper.selectByPrimaryKey(id);
		return user;
	}

	public void loadData(User user) {
		// 加载用户背包
		String nickname = user.getNickname();
		UserbagExample example = new UserbagExample();
		rpg.pojo.UserbagExample.Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(nickname);
		List<Userbag> userBagList = userbagMapper.selectByExample(example);
		user.setUserbags(userBagList);
		// 加载用户装备
		UserzbExample example1 = new UserzbExample();
		rpg.pojo.UserzbExample.Criteria criteria1 = example1.createCriteria();
		criteria1.andUsernameEqualTo(nickname);
		List<Userzb> userZbList = userzbMapper.selectByExample(example1);
		user.setUserzbs(userZbList);
		// 加载用户等级
		UserlevelExample userlevelExample = new UserlevelExample();
		rpg.pojo.UserlevelExample.Criteria createCriteria = userlevelExample.createCriteria();
		createCriteria.andUsernameEqualTo(nickname);
		List<Userlevel> userlevels = userlevelMapper.selectByExample(userlevelExample);
		Userlevel userlevel = userlevels.get(0);
		user.setLevel(userlevel.getLevel());
		user.setExp(userlevel.getExp());
		UserAttribute attribute = new UserAttribute();
		attribute.setAck(userlevel.getAck());
		attribute.setDef(userlevel.getDef());
		user.setUserAttribute(attribute);
		// 初始化组队资源
		ArrayList<String> applyList = new ArrayList<String>();
		GroupCache.getInstance().getUserApplyCache().put(nickname, applyList);
		// 加载交易行信息
		MarketItemExample marketItemExample = new MarketItemExample();
		List<MarketItem> marketItemList = marketItemMapper.selectByExample(marketItemExample);
		for (MarketItem marketItem : marketItemList) {
			MarketCache.MarketItemMp.put(marketItem.getId(), marketItem);
		}
		UseritemExample useritemExample = new UseritemExample();
		rpg.pojo.UseritemExample.Criteria createCriteria2 = useritemExample.createCriteria();
		createCriteria2.andUsernameEqualTo(nickname);
		List<Useritem> useritemList = useritemMapper.selectByExample(useritemExample);
		ArrayList<String> list = new ArrayList<String>();
		for (Useritem useritem : useritemList) {
			list.add(useritem.getItemid());
		}
		MarketCache.userMarketItemMp.put(user, list);
		// 加载任务
		UsertaskprocessExample usertaskprocessExample = new UsertaskprocessExample();
		rpg.pojo.UsertaskprocessExample.Criteria createCriteria3 = usertaskprocessExample.createCriteria();
		createCriteria3.andUsernameEqualTo(nickname);
		List<Usertaskprocess> usertaskprocesseList = usertaskprocessMapper.selectByExample(usertaskprocessExample);
		Map<Integer, Usertaskprocess> doingTask = new ConcurrentHashMap<Integer, Usertaskprocess>(500);
		for (Usertaskprocess usertaskprocess : usertaskprocesseList) {
			doingTask.put(usertaskprocess.getTaskid(), usertaskprocess);
		}
		user.setDoingTask(doingTask);
		UserfinishtaskExample userfinishtaskExample = new UserfinishtaskExample();
		rpg.pojo.UserfinishtaskExample.Criteria createCriteria4 = userfinishtaskExample.createCriteria();
		createCriteria4.andUsernameEqualTo(nickname);
		List<Userfinishtask> userfinishtaskList = userfinishtaskMapper.selectByExample(userfinishtaskExample);
		Map<Integer, Userfinishtask> finishTask = new ConcurrentHashMap<Integer, Userfinishtask>(500);
		for (Userfinishtask userfinishtask : userfinishtaskList) {
			finishTask.put(userfinishtask.getTaskid(), userfinishtask);
		}
		user.setFinishTask(finishTask);
		user.setDrawNum(0);
		user.setWinNum(0);
	}
}