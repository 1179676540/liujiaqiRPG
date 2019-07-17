package rpg.service;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.configure.MsgSize;
import rpg.core.ThreadResource;
import rpg.data.dao.UserMapper;
import rpg.data.dao.UserbagMapper;
import rpg.pojo.Enhance;
import rpg.pojo.User;
import rpg.pojo.Userbag;
import rpg.util.SendMsg;
import rpg.xmlparse.EnhanceXmlParse;

/**
 * 强化装备逻辑
 * @author ljq
 *
 */
@Component
public class EnhanceDispatch {
	
	@Autowired
	private EnhanceXmlParse enhanceXmlParse;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserbagMapper userbagMapper;

	/**
	 * 强化装备
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void enhance(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if(MsgSize.MAX_MSG_SIZE_2.getValue()==msg.length) {
			List<Userbag> list = user.getUserbags();
			for (Userbag userbag : list) {
				if(userbag.getId().equals(msg[MsgSize.MSG_INDEX_1.getValue()])) {
					Integer enhanceLevel = userbag.getEnhance();
					Enhance enhance = enhanceXmlParse.getEnhanceById(enhanceLevel+1);
					int enhanceMoney = enhance.getMoney();
					if(user.getMoney()>=enhanceMoney) {
						user.getAndSetMoney(user, user.getMoney()-enhanceMoney);
						userMapper.updateByPrimaryKeySelective(user);
						Random random = new Random();
						int nextInt = random.nextInt(10000)+1;
						if(nextInt<enhance.getSuccesRate()) {
							userbag.setEnhance(enhanceLevel+1);
							userbagMapper.updateByPrimaryKeySelective(userbag);
							SendMsg.send("强化<<<+"+userbag.getEnhance()+">>>成功", ch);
						} else {
							SendMsg.send("强化失败", ch);
						}
					} else {
						SendMsg.send("金币不足", ch);
					}
				}
			}
		} else {
			SendMsg.send("指令错误", ch);
		}
	}
}
