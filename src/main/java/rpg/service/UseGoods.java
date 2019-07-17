package rpg.service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.data.dao.UserbagMapper;
import rpg.pojo.Buff;
import rpg.pojo.User;
import rpg.pojo.Userbag;
import rpg.pojo.Yaopin;
import rpg.util.SendMsg;
import rpg.xmlparse.BuffXmlParse;
import rpg.xmlparse.YaopinXmlParse;

/**
 * 使用物品逻辑
 * 
 * @author ljq
 *
 */
@Component
public class UseGoods {

	@Autowired
	private UserbagMapper userbagMapper;
	@Autowired
	private YaopinXmlParse yaopinXmlParse;
	@Autowired
	private BuffXmlParse buffXmlParse;

	public void use(User user, Channel ch, ChannelGroup group, String msgR) {
			String[] msg = msgR.split("\\s+");
			String nickname = user.getNickname();
			List<Userbag> list = user.getUserbags();
			for (Userbag userbag : list) {
				Yaopin yaopin = yaopinXmlParse.getYaopinById(userbag.getGid());
				if (yaopin != null) {
					if (yaopin.getName().equals(msg[1])) {
						// 找到药品所产生的Buff
						Buff buff = buffXmlParse.getBuffByid(yaopin.getBuff());
						// 存储上次使用buff时间
						long currentTimeMillis = System.currentTimeMillis();
						if (user.getBuffStartTime() == null) {
							ConcurrentHashMap<Integer, Long> buffMap = new ConcurrentHashMap<Integer, Long>(500);
							buffMap.put(buff.getId(), currentTimeMillis);
							user.setBuffStartTime(buffMap);
						} else {
							ConcurrentHashMap<Integer, Long> buffMap = user.getBuffStartTime();
							buffMap.put(buff.getId(), currentTimeMillis);
						}
						userbag.setNumber(userbag.getNumber() - 1);
						SendMsg.send(yaopin.getName() + "使用成功，剩余" + userbag.getNumber(), ch);
						userbagMapper.updateByPrimaryKeySelective(userbag);
					}
				}
			}
	}
}