package rpg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.configure.UserStatus;
import rpg.core.ChannelUtil;
import rpg.pojo.User;

/**
 * 攻擊方法選擇
 * @author ljq
 *
 */
@Component
public class AckMethodDispatch {

	@Autowired
	private AckDispatch ackDispatch;
	@Autowired
	private AckBossDispatch ackBossDispatch;

	/**
	 * 根据状态进入打boss逻辑或打小怪
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void ackMethodDispatch(User user, Channel ch, ChannelGroup group, String msgR) {
//		Integer chStatus = IOsession.chStatus.get(ch);
		int chStatus = ChannelUtil.getSessionData(ch).getStatus();
		if (chStatus == UserStatus.ACK_MONSTER.getValue()) {
			ackDispatch.ack(user, ch, group, msgR);
		} else {
			ackBossDispatch.ack(user, ch, group, msgR);
		}
	}
}
