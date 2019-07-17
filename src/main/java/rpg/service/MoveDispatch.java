package rpg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.pojo.User;
import rpg.util.SendMsg;

/**
 * 移动处理器
 * @author ljq
 *
 */
@Component
public class MoveDispatch {
	
	@Autowired
	private Move move;
	
	public void dispatch(User user, Channel ch, ChannelGroup userGroup, String messgage) {
		String[] msg = messgage.split("\\s+");
		if(msg.length>1) {
			move.move(ch, msg[1], user);
		} else {
			SendMsg.send("无效指令",ch);
		}
	}
}
