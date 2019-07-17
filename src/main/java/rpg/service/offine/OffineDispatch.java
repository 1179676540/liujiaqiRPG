package rpg.service.offine;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import rpg.core.AllOnlineUser;
import rpg.core.ThreadResource;
import rpg.core.packet.ServerRespPacket;
import rpg.core.packet.ServerRespPacket.Resp.Builder;
import rpg.data.dao.UserMapper;
import rpg.pojo.BossScene;
import rpg.pojo.Group;
import rpg.pojo.Level;
import rpg.pojo.Monster;
import rpg.pojo.User;
import rpg.service.area.OffineFlagRefresh;
import rpg.service.bossscene.BossSceneCache;
import rpg.service.group.GroupCache;
import rpg.service.skill.SkillList;
import rpg.service.user.UserResources;
import rpg.util.SendMsg;
import rpg.xmlparse.LevelXmlParse;

/**
 * 离线处理逻辑
 * 
 * @author ljq
 *
 */
@Component("offineDispatch")
public class OffineDispatch {
	
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private LevelXmlParse levelXmlParse;

	public void groupOffine(Channel ch) {
		User user = AllOnlineUser.onlineUserMap.get(ch.remoteAddress());
		if (user != null) {
			user.setMp(100);
			Level level = levelXmlParse.getLevelById(user.getLevel());
			user.setHp(10000+level.getHp());
			userMapper.updateByPrimaryKeySelective(user);
			user.setLiveFlag(1);
			Group group = GroupCache.getInstance().getUserGroupMpCache().get(user.getGroupId());
			if (group != null) {
				// 如果是队长
				if (group.getUser().getNickname().equals(user.getNickname())) {
					List<User> list = group.getList();
					if (list.size() == 1) {
						System.out.println("移除前" + list.size());
						group.setUser(null);
						list.remove(user);
						System.out.println("移除后" + list.size());
					} else {
						for (User user2 : list) {
							if (user2 != user) {
								group.setUser(user2);
								list.remove(user);
								for (User user3 : list) {
									Channel channel = AllOnlineUser.userchMap.get(user3);
									Builder builder = ServerRespPacket.Resp.newBuilder();
									builder.setData(user2.getNickname() + "成为队长");
									SendMsg.send(builder.build(), channel);
								}
								System.out.println("执行");
								break;
							}
						}
					}
				} else {
					// 不是队长
					List<User> list = group.getList();
					list.remove(user);
					for (User user3 : list) {
						if (user3 != user) {
							Channel channel = AllOnlineUser.userchMap.get(user3);
							Builder builder = ServerRespPacket.Resp.newBuilder();
							builder.setData(user.getNickname() + "离开队伍");
							SendMsg.send(builder.build(), channel);
						}
					}
				}
				// 移除怪物攻击目标
				// 获取地图中的怪物
				BossScene bossScene = BossSceneCache.getInstance().getUserBossCache().get(user.getGroupId());
				if (bossScene != null) {
					ArrayList<Monster> monsterList = bossScene.getMonsterList();
					// 找到场景内怪物
					for (int i = 0; i < monsterList.size(); i++) {
						Monster monster = monsterList.get(i);
						if (monster != null) {
							List<User> userList = monster.getUserList();
							userList.remove(user);
						}
					}
					ThreadResource.monsterThreadPool.execute(new OffineFlagRefresh(user, ch, bossScene));
				} else {
					recoveryMp(ch, user);
				}
			} else {
				recoveryMp(ch, user);
			}
		}
	}

	/**
	 * 回收所有的map
	 * 
	 * @param ch
	 * @param user
	 */
	public void recoveryMp(Channel ch, User user) {
		AllOnlineUser.onlineUserMap.remove(ch.remoteAddress());
		AllOnlineUser.userchMap.remove(user);
		SkillList.getInstance().getCdMpCache().remove(user);
		UserResources.nameMap.remove(user.getNickname());
		user = null;
	}
}