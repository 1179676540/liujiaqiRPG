package rpg.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.configure.MsgSize;
import rpg.data.dao.UserbagMapper;
import rpg.data.dao.UserlevelMapper;
import rpg.data.dao.UserzbMapper;
import rpg.pojo.User;
import rpg.pojo.UserAttribute;
import rpg.pojo.Userbag;
import rpg.pojo.Userlevel;
import rpg.pojo.UserlevelExample;
import rpg.pojo.UserlevelExample.Criteria;
import rpg.pojo.Userzb;
import rpg.pojo.Yaopin;
import rpg.pojo.Zb;
import rpg.service.task.TaskManage;
import rpg.util.SendMsg;
import rpg.xmlparse.YaopinXmlParse;
import rpg.xmlparse.ZbXmlParse;

/**
 * 背包处理逻辑
 * 
 * @author ljq
 *
 */
@Component
public class BagDispatch {

	@Autowired
	private YaopinXmlParse yaopinXmlParse;
	@Autowired
	private TaskManage taskManage;
	@Autowired
	private ZbXmlParse zbXmlParse;
	@Autowired
	private UserzbMapper userzbMapper;
	@Autowired
	private UserbagMapper userbagMapper;
	@Autowired
	private UserlevelMapper userlevelMapper;

	/**
	 * 展示背包
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param messgage
	 */
	public void showBag(User user, Channel ch, ChannelGroup group, String messgage) {
		List<Userbag> list = user.getUserbags();
		String yaopinWord = "";
		String zbWord = "";
		for (Userbag userbag : list) {
			Yaopin yaopin = yaopinXmlParse.getYaopinById(userbag.getGid());
			Zb zb = zbXmlParse.getZbById(userbag.getGid());
			if (yaopin != null)
				yaopinWord += "格子id:" + userbag.getId() + "---" + yaopin.getName() + "---" + userbag.getNumber() + "\n";
			else {
				if (zb.getType() == 1) {
					zbWord += "格子id:" + userbag.getId() + "---" + zb.getName() + "-耐久度：" + userbag.getNjd() + "-攻击力"
							+ zb.getAck() + "-星级:" + zb.getLevel() + "强化等级--" + userbag.getEnhance() + "\n";
				} else if (zb.getType() == 2) {
					zbWord += "格子id:" + userbag.getId() + "---" + zb.getName() + "-耐久度：" + userbag.getNjd() + "-防御力"
							+ zb.getAck() + "-星级:" + zb.getLevel() + "强化等级--" + userbag.getEnhance() + "\n";
				}
			}
		}
		UserAttribute attribute = user.getUserAttribute();
		SendMsg.send("-------背包-------\n" + "用户等级" + user.getLevel() + "---经验" + user.getExp() + "\n" + "用户金币："
				+ user.getMoney() + "\n" + "用户血量：" + user.getHp() + "\n" + "用户攻击力：" + attribute.getAck() + "\n"
				+ "用户防御力：" + attribute.getDef() + "\n" + yaopinWord + zbWord, ch);
	}

	/**
	 * 展示装备
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param messgage
	 */
	public void showZb(User user, Channel ch, ChannelGroup group, String messgage) {
		List<Userzb> list = user.getUserzbs();
		String msg = "----------装备----------\n";
		for (Userzb userzb : list) {
			Zb zb = zbXmlParse.getZbById(userzb.getZbid());
			int njd = userzb.getNjd() > 0 ? userzb.getNjd() : 0;
			if (zb.getType() == 1) {
				msg += "id:" + userzb.getId() + zb.getName() + "-耐久度：" + njd + "-攻击力:" + zb.getAck() + "-星级:"
						+ zb.getLevel() + "强化等级--" + userzb.getEnhance() + "\n";
			} else if (zb.getType() == 2) {
				msg += "id:" + userzb.getId() + zb.getName() + "-耐久度：" + njd + "-防御力:" + zb.getAck() + "-星级:"
						+ zb.getLevel() + "强化等级--" + userzb.getEnhance() + "\n";
			}
		}
		SendMsg.send(msg, ch);
	}

	/**
	 * 脱装备
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void tkffZb(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		String nickname = user.getNickname();
		List<Userzb> list = user.getUserzbs();
		List<Userbag> list2 = user.getUserbags();
		if (msg.length == MsgSize.MAX_MSG_SIZE_2.getValue()) {
			for (Userzb userzb : list) {
				if (userzb.getId().equals(msg[1])) {
					String id = userzb.getId();
					// 脱下装备
					Userlevel userlevel = new Userlevel();
					Zb zb = zbXmlParse.getZbById(userzb.getZbid());
					UserAttribute attribute = user.getUserAttribute();
					if (zb.getType() == 1) {
						attribute.setAck(attribute.getAck()
								- userzb.getEnhance() * userzb.getEnhance() * zb.getAck() * userzb.getIsuse());
						userlevel.setAck(attribute.getAck());
						SendMsg.send("脱下装备成功" + "-攻击下降："
								+ userzb.getEnhance() * userzb.getEnhance() * zb.getAck() * userzb.getIsuse() + "现在攻击力"
								+ attribute.getAck(), ch);
					} else if (zb.getType() == 2) {
						attribute.setDef(attribute.getDef()
								- userzb.getEnhance() * userzb.getEnhance() * zb.getAck() * userzb.getIsuse());
						userlevel.setDef(attribute.getDef());
						SendMsg.send("脱下装备成功" + "-防御下降："
								+ userzb.getEnhance() * userzb.getEnhance() * zb.getAck() * userzb.getIsuse() + "现在防御力"
								+ attribute.getDef(), ch);
					}
					list.remove(userzb);
					// 放入背包
					Userbag userbag = new Userbag();
					String userbagId = UUID.randomUUID().toString();
					userbag.setId(userbagId);
					userbag.setUsername(nickname);
					userbag.setGid(zb.getId());
					userbag.setNumber(1);
					userbag.setNjd(userzb.getNjd());
					userbag.setIsadd(0);
					userbag.setEnhance(userzb.getEnhance());
					list2.add(userbag);
					userzbMapper.deleteByPrimaryKey(id);
					userbagMapper.insert(userbag);
					UserlevelExample userlevelExample = new UserlevelExample();
					Criteria createCriteria = userlevelExample.createCriteria();
					createCriteria.andUsernameEqualTo(nickname);
					userlevelMapper.updateByExampleSelective(userlevel, userlevelExample);
					break;
				}
			}
		} else {
			SendMsg.send("没有此物品", ch);
		}
	}

	/**
	 * 穿装备
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void wearzb(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		String nickname = user.getNickname();
		List<Userbag> list = user.getUserbags();
		List<Userzb> list2 = user.getUserzbs();
		if (msg.length == MsgSize.MAX_MSG_SIZE_2.getValue()) {
			for (Userbag userbag : list) {
				if (userbag.getId().equals(msg[1])) {
					if (list2.size() >= 3) {
						SendMsg.send("请先脱下装备", ch);
						break;
					} else {
						// 从背包移除装备
						Userlevel userlevel = new Userlevel();
						String bagId = userbag.getId();
						Zb zb = zbXmlParse.getZbById(userbag.getGid());
						list.remove(userbag);
						// 放入装备栏
						Userzb userzb = new Userzb();
						String id = UUID.randomUUID().toString();
						userzb.setId(id);
						userzb.setUsername(nickname);
						userzb.setZbid(userbag.getGid());
						userzb.setNjd(userbag.getNjd());
						userzb.setEnhance(userbag.getEnhance());
						if (userbag.getNjd() <= 0) {
							userzb.setIsuse(0);
						} else {
							userzb.setIsuse(1);
						}
						UserAttribute attribute = user.getUserAttribute();
						if (zb.getType() == 1) {
							attribute.setAck(attribute.getAck()
									+ userzb.getEnhance() * userzb.getEnhance() * zb.getAck() * userzb.getIsuse());
							list2.add(userzb);
							userlevel.setAck(attribute.getAck());
							SendMsg.send("穿戴装备成功" + "-攻击力上升："
									+ userzb.getEnhance() * userzb.getEnhance() * zb.getAck() * userzb.getIsuse()
									+ "现在攻击力" + attribute.getAck(), ch);
							taskManage.checkTaskCompleteByTaskidWithZbList(user, 5, list2);
						} else if (zb.getType() == 2) {
							attribute.setDef(attribute.getDef()
									+ userzb.getEnhance() * userzb.getEnhance() * zb.getAck() * userzb.getIsuse());
							list2.add(userzb);
							userlevel.setDef(attribute.getDef());
							SendMsg.send("穿戴装备成功" + "-防御力上升："
									+ userzb.getEnhance() * userzb.getEnhance() * zb.getAck() * userzb.getIsuse()
									+ "现在防御力" + attribute.getDef(), ch);
							taskManage.checkTaskCompleteByTaskidWithZbList(user, 5, list2);
						}
						userzbMapper.insert(userzb);
						userbagMapper.deleteByPrimaryKey(bagId);
						UserlevelExample userlevelExample = new UserlevelExample();
						Criteria createCriteria = userlevelExample.createCriteria();
						createCriteria.andUsernameEqualTo(nickname);
						userlevelMapper.updateByExampleSelective(userlevel, userlevelExample);
						break;
					}
				}
			}
		} else {
			SendMsg.send("没有此物品", ch);
		}
	}

	/**
	 * 修理装备
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void fix(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		String nickname = user.getNickname();
		List<Userzb> list = user.getUserzbs();
		if (msg.length == MsgSize.MAX_MSG_SIZE_2.getValue()) {
			for (Userzb userzb : list) {
				Zb zb = zbXmlParse.getZbById(userzb.getZbid());
				if (zb != null) {
					if (userzb.getId().equals(msg[1])) {
						if (userzb.getIsuse() == 0) {
							Userlevel userlevel = new Userlevel();
							userzb.setNjd(zb.getNjd());
							UserAttribute attribute = user.getUserAttribute();
							attribute.setAck(
									attribute.getAck() + userzb.getEnhance() * userzb.getEnhance() * zb.getAck());
							userzb.setIsuse(1);
							userlevel.setAck(attribute.getAck());
							SendMsg.send("装备修理成功", ch);
							userzbMapper.updateByPrimaryKeySelective(userzb);
							UserlevelExample userlevelExample = new UserlevelExample();
							Criteria createCriteria = userlevelExample.createCriteria();
							createCriteria.andUsernameEqualTo(nickname);
							userlevelMapper.updateByExampleSelective(userlevel, userlevelExample);
						}
					}
				}
			}
		} else {
			SendMsg.send("没有此物品", ch);
		}
	}
}