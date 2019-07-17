package rpg.service.login;

import java.net.SocketAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import rpg.configure.MsgResp;
import rpg.configure.UserStatus;
import rpg.core.AllOnlineUser;
import rpg.core.ChannelUtil;
import rpg.core.packet.ServerRespPacket;
import rpg.core.packet.ServerRespPacket.Resp.Builder;
import rpg.pojo.User;
import rpg.service.user.UserResources;
import rpg.util.SendMsg;
import rpg.xmlparse.RoleXmlParse;

/**
 * 登陆处理器
 * 
 * @author ljq
 *
 */
@Component
@Slf4j
public class LoginDispatch {

	@Autowired
	private Login login;
	@Autowired
	private RoleXmlParse roleXmlParse;
	private static final int MSG_MAX_LENGTH = 2;

	public void loginDispatch(Channel ch, String message, SocketAddress address) {
		String[] msg = message.split("\\s+");

		if (msg.length <= MSG_MAX_LENGTH) {
			Builder builder = ServerRespPacket.Resp.newBuilder();
			builder.setData(MsgResp.ORDER_ERR);
			SendMsg.send(builder.build(), ch);
			return;
		}

		User user = login.login(msg[1], msg[2]);
		if (user == null) {
			Builder builder = ServerRespPacket.Resp.newBuilder();
			builder.setData(MsgResp.LOGIN_ERR);
			SendMsg.send(builder.build(), ch);
			return;
		}
		
//			 登陆有多个线程跑，通过hash一致性算法，所有相同的账户名的用户都会到一个线程执行，无须加锁。
//			 lock.lock();
		User user2 = UserResources.nameMap.get(user.getNickname());
//			 账号已登陆，顶号，转移状态
		if (user2 != null) {
			Channel channel = AllOnlineUser.userchMap.get(user2);
			Builder builder = ServerRespPacket.Resp.newBuilder();
			builder.setData(MsgResp.DINGHAO_ERR);
			SendMsg.send(builder.build(), channel);
			AllOnlineUser.onlineUserMap.put(ch.remoteAddress(), user2);
			AllOnlineUser.onlineUserMap.remove(channel.remoteAddress());
			AllOnlineUser.userchMap.put(user2, ch);
			ChannelUtil.getSessionData(ch).setStatus(ChannelUtil.getSessionData(channel).getStatus());
			ChannelUtil.getSessionData(ch).setUser(user2);
			ChannelUtil.getSessionData(channel).setStatus(0);
			AllOnlineUser.monsterMp.put(ch.remoteAddress(), AllOnlineUser.monsterMp.get(channel.remoteAddress()));
		} else {
//			 正常登陆
			ChannelUtil.getSessionData(ch).setStatus(UserStatus.ORDINARY.getValue());
			ChannelUtil.getSessionData(ch).setUser(user);
			AllOnlineUser.onlineUserMap.put(address, user);
			AllOnlineUser.userchMap.put(user, ch);
			UserResources.nameMap.put(user.getNickname(), user);
			login.loadData(user);
		}

		String roleName = roleXmlParse.getNameById(user.getRoletype());
		ServerRespPacket.LoginResp.Builder builder2 = ServerRespPacket.LoginResp.newBuilder();
		builder2.setData("角色名-" + user.getNickname() + "-职业-" + roleName);
		SendMsg.send(builder2.build(), ch);
		Builder builder = ServerRespPacket.Resp.newBuilder();
		builder.setData("登陆成功，欢迎" + user.getNickname() + "进入游戏");
		SendMsg.send(builder.build(), ch);
		log.info("登陆游戏");
	}
}
