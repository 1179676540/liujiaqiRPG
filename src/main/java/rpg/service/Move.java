package rpg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import rpg.data.dao.UserMapper;
import rpg.data.dao.function.UserDao;
import rpg.pojo.User;
import rpg.service.area.Area;
import rpg.util.SendMsg;

/**
 * 移动
 * 
 * @author ljq
 *
 */
@Component("move")
public class Move {

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserDao userDao;

	public void move(Channel ch, String msg, User user) {
		if (Area.checkArea(msg, user.getAreaid()) > 0) {
			Integer id = Area.mp1.get(msg);
			SendMsg.send("您已经进入" + Area.sceneList.get(id-1).getName(), ch);
			user.setAreaid(id);
			//异步存储数据库
			userDao.updateByPrimaryKey(user);
		} else {
			SendMsg.send("你不能跨场景，请重新输入指令", ch);
		}
	}
}
