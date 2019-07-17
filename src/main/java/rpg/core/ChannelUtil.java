package rpg.core;

import java.util.List;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import rpg.pojo.Monster;
import rpg.pojo.User;

/**
 * 对channel信息进行操作
 * @author ljq
 *
 */
public class ChannelUtil {
	
	public static final AttributeKey<SessionData> NETTY_CHANNEL_KEY = AttributeKey.valueOf("netty.channel");
	
	/**
	 * 获取channel的信息
	 * @param channel
	 * @return
	 */
	public static SessionData getSessionData(Channel channel){
		Attribute<SessionData> attr = channel.attr(NETTY_CHANNEL_KEY);
		SessionData sessionData = attr.get();
		return sessionData;
	}
	
	/**
	 * 设置channel信息
	 * @param channel
	 * @param user
	 * @param status
	 * @param monsterMp
	 */
	public static void setSessionData(Channel channel,User user,int status,List<Monster> monsterMp) {
		SessionData sessionData = new SessionData(user,status,monsterMp);
		Attribute<SessionData> attr = channel.attr(NETTY_CHANNEL_KEY);
		attr.setIfAbsent(sessionData);
	}
	
}
