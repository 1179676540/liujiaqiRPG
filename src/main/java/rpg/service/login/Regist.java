package rpg.service.login;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rpg.configure.RoleType;
import rpg.core.AllUserData;
import rpg.data.dao.AccountMapper;
import rpg.data.dao.UserMapper;
import rpg.data.dao.UserbagMapper;
import rpg.data.dao.UserbuffMapper;
import rpg.data.dao.UserkeyMapper;
import rpg.data.dao.UserlevelMapper;
import rpg.data.dao.UserskillMapper;
import rpg.data.dao.UsertaskprocessMapper;
import rpg.data.dao.UserzbMapper;
import rpg.pojo.Account;
import rpg.pojo.Task;
import rpg.pojo.User;
import rpg.pojo.Userbag;
import rpg.pojo.Userbuff;
import rpg.pojo.Userkey;
import rpg.pojo.Userlevel;
import rpg.pojo.Userskill;
import rpg.pojo.Usertaskprocess;
import rpg.pojo.Userzb;
import rpg.xmlparse.TaskXmlParse;

/**
 * 注册功能
 * 
 * @author ljq
 *
 */
@Component("regist")
public class Regist {
	@Autowired
	private AccountMapper accountMapper;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserbuffMapper userbuffMapper;
	@Autowired
	private UserskillMapper userskillMapper;
	@Autowired
	private UserzbMapper userzbMapper;
	@Autowired
	private UserlevelMapper userlevelMapper;
	@Autowired
	private UserbagMapper userbagMapper;
	@Autowired
	private UsertaskprocessMapper usertaskprocessMapper;
	@Autowired
	private TaskXmlParse taskXmlParse;
	@Autowired
	private UserkeyMapper userkeyMapper;
	
	private static final int TASK_START_NUM = 2;
	private static final int TASK_NUM = 12;

	public RegistStaus regist(String accountname, String psw, String psw1, Integer roleid,String username) {
		HashMap<String,String> accountToNameMap = AllUserData.getInstance().getAccountToNameMap();
		if(accountToNameMap.containsKey(accountname)) {
			return RegistStaus.ACCOUNTERROR;
		}
		
		if(accountToNameMap.containsValue(username)) {
			return RegistStaus.USERNAMEERROR;
		}
		
		if (psw.equals(psw1)) {
			Account account = new Account();
			account.setUsername(accountname);
			account.setPsw(psw);
			accountMapper.insertSelective(account);
			Integer id = account.getId();
			User user = new User();
			user.setId(id);
			user.setNickname(username);
			user.setAreaid(1);
			user.setHp(10000);
			user.setMp(100);
			user.setMoney(8000);
			user.setGhId(0);
			user.setRoletype(roleid);
			// 设置日期格式
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			user.setUpdatetime(df.format(new Date()));
			userMapper.insert(user);
			accountToNameMap.put(accountname, username);
			Userbuff userbuff = new Userbuff();
			userbuff.setUsername(username);
			userbuff.setBuff("1");
			userbuffMapper.insert(userbuff);
			Userbuff userbuff2 = new Userbuff();
			userbuff2.setUsername(username);
			userbuff2.setBuff("2");
			userbuffMapper.insert(userbuff2);
			Userskill userskill = new Userskill();
			userskill.setUsername(username);
			Userskill userskill2 = new Userskill();
			userskill2.setSkill(9);
			userskill2.setUsername(username);
			if (roleid == RoleType.ZHANSHI.getValue()) {
				userskill.setSkill(8);
			} else if (roleid == RoleType.MUSHI.getValue()) {
				userskill.setSkill(5);
			} else if (roleid == RoleType.FASHI.getValue()) {
				userskill.setSkill(6);
			} else if (roleid == RoleType.ZHAOHUANSHI.getValue()) {
				userskill.setSkill(7);
			}
			Userskill userskill3 = new Userskill();
			userskill3.setUsername(username);
			userskill3.setSkill(3);
			Userskill userskill4 = new Userskill();
			userskill4.setUsername(username);
			userskill4.setSkill(1);
			userskillMapper.insert(userskill3);
			userskillMapper.insert(userskill);
			userskillMapper.insert(userskill2);
			userskillMapper.insert(userskill4);
			Userkey userkey = new Userkey();
			userkey.setUsername(username);
			userkey.setSkillid(userskill.getSkill());
			userkeyMapper.insert(userkey);
			Userbag userbag = new Userbag();
			userbag.setId(UUID.randomUUID().toString());
			userbag.setUsername(username);
			userbag.setGid(1);
			userbag.setNumber(10);
			userbag.setNjd(0);
			userbag.setIsadd(1);
			userbag.setEnhance(0);
			userbagMapper.insert(userbag);
			Userzb userzb = new Userzb();
			userzb.setId(UUID.randomUUID().toString());
			userzb.setUsername(username);
			userzb.setZbid(101);
			userzb.setNjd(10);
			userzb.setIsuse(1);
			userzb.setEnhance(1);
			userzbMapper.insert(userzb);
			Userlevel userlevel = new Userlevel();
			userlevel.setUsername(username);
			userlevel.setLevel(1);
			userlevel.setExp(0);
			userlevel.setAck(105);
			userlevel.setDef(50);
			if(roleid == RoleType.ZHANSHI.getValue()) {
				userlevel.setDef(200);
			}
			userlevelMapper.insert(userlevel);
			for (int i = TASK_START_NUM; i <= TASK_NUM; i++) {
				Task task = taskXmlParse.getTaskById(i);
				Usertaskprocess usertaskprocess = new Usertaskprocess();
				usertaskprocess.setUsername(username);
				usertaskprocess.setTaskid(task.getId());
				usertaskprocess.setName(task.getName());
				usertaskprocess.setReqid(task.getReqid());
				usertaskprocess.setNum(0);
				if (i == 2) {
					usertaskprocess.setNum(1);
				}
				usertaskprocessMapper.insert(usertaskprocess);
			}
			return RegistStaus.SUCCESS;
		} else {
			return RegistStaus.PASSWORDERROR;
		}
	}
}
