package rpg.service.area;

import io.netty.channel.Channel;
import rpg.core.AllOnlineUser;
import rpg.pojo.BossScene;
import rpg.pojo.User;
import rpg.service.skill.SkillList;
import rpg.service.user.UserResources;

/**
 * 离线处理线程
 * 
 * @author ljq
 *
 */
public class OffineFlagRefresh implements Runnable {

	private User user;
	private Channel ch;
	private BossScene bossScene;

	public OffineFlagRefresh(User user, Channel ch, BossScene bossScene) {
		super();
		this.user = user;
		this.ch = ch;
		this.bossScene = bossScene;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(2000);
				AllOnlineUser.onlineUserMap.remove(ch.remoteAddress());
				AllOnlineUser.userchMap.remove(user);
				SkillList.getInstance().getCdMpCache().remove(user);
				UserResources.nameMap.remove(user.getNickname());
				user = null;
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
