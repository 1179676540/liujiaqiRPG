package rpg.service;

import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.pojo.User;
import rpg.pojo.UserAttribute;
import rpg.util.SendMsg;

/**
 * 进行游戏调试
 * @author ljq
 *
 */
@Component
public class GameDebug {
	
	public void debug(User user, Channel ch, ChannelGroup group, String messgage) {
		user.setHp(500000);
		user.getAndSetMoney(user, 1000000);
		UserAttribute userAttribute = user.getUserAttribute();
		userAttribute.setAck(7000);
		userAttribute.setDef(1000);
		SendMsg.send("超级赛亚人变身...", ch);
	}
}
