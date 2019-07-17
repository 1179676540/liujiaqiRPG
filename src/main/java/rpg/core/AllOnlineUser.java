package rpg.core;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;

import io.netty.channel.Channel;
import rpg.pojo.Monster;
import rpg.pojo.User;

/**
 * 所有在线玩家
 * 
 * @author ljq
 *
 */
public class AllOnlineUser {
	/**
	 * 所有在线玩家
	 */
	public static HashMap<SocketAddress, User> onlineUserMap = new HashMap<SocketAddress, User>();
	/**
	 * 在线玩家的channel
	 */
	public static HashMap<User, Channel> userchMap = new HashMap<User, Channel>();
	/**
	 * 在线玩家正在攻击的怪物
	 */
	public static HashMap<SocketAddress, List<Monster>> monsterMp = new HashMap<SocketAddress, List<Monster>>();
}
