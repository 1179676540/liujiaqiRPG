package rpg.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.configure.MsgSize;
import rpg.configure.UserStatus;
import rpg.core.AllOnlineUser;
import rpg.core.ChannelUtil;
import rpg.pojo.Jy;
import rpg.pojo.User;
import rpg.pojo.Userbag;
import rpg.pojo.Yaopin;
import rpg.pojo.Zb;
import rpg.service.jy.JyCache;
import rpg.service.task.TaskManage;
import rpg.util.RpgUtil;
import rpg.util.SendMsg;
import rpg.xmlparse.YaopinXmlParse;
import rpg.xmlparse.ZbXmlParse;

/**
 * 交易逻辑
 * 
 * @author ljq
 *
 */
@Component
public class JyDispatch {

	@Autowired
	private YaopinXmlParse yaopinXmlParse;
	@Autowired
	private TaskManage taskManage;
	@Autowired
	private RpgUtil rpgUtil;
	@Autowired
	private ZbXmlParse zbXmlParse;

	private static final int JY_FLAG = 2;
	private static final String NULL_GOOD = "0";
	private Lock lock = new ReentrantLock();
	private Lock lock1 = new ReentrantLock();

	/**
	 * 取消交易请求
	 * 
	 * @param user
	 * @param ch
	 * @param msgR
	 * @param group
	 */
	public void cancelJyRequest(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_1.getValue()) {
			lock.lock();
			try {
				user.getAndSetjySendFlag(user, 0);
				Jy jy = JyCache.getInstance().getJyCache().get(user.getJyId());
				jy = null;
				JyCache.getInstance().getJyCache().remove(user.getJyId());
				SendMsg.send("交易已取消", ch);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}
	}

	/**
	 * 退出交易
	 * 
	 * @param user
	 * @param ch
	 */
	public void quitJy(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		lock1.lock();
		try {
			if (msg.length == 1) {
				user.getAndSetjyFlag(user, 0);
				Jy jy = JyCache.getInstance().getJyCache().get(user.getJyId());
				User sendUser = jy.getSendUser();
				if (sendUser.equals(user)) {
					sendUser = jy.getAcceptUser();
				}
				sendUser.getAndSetjyFlag(sendUser, 0);
				jy = null;
				JyCache.getInstance().getJyCache().remove(user.getJyId());
				Channel channel = AllOnlineUser.userchMap.get(sendUser);
				ChannelUtil.getSessionData(channel).setStatus(UserStatus.ORDINARY.getValue());
				ChannelUtil.getSessionData(ch).setStatus(UserStatus.ORDINARY.getValue());
				SendMsg.send("对方已取消交易", channel);
				SendMsg.send("交易取消", ch);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock1.unlock();
		}
	}

	/**
	 * 交易中放入物品
	 * 
	 * @param user
	 * @param ch
	 * @param msg
	 */
	public void jyPutGood(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_3.getValue() && user.getJyFlag() == 1) {
			if (!NULL_GOOD.equals(msg[1])) {
				List<Userbag> list = user.getUserbags();
				boolean flag = false;
				for (Userbag userbag : list) {
					if (userbag.getId().equals(msg[1])) {
						Jy jy = JyCache.getInstance().getJyCache().get(user.getJyId());
						if (jy != null) {
							User sendUser = jy.getSendUser();
							if (sendUser.equals(user)) {
								sendUser = jy.getAcceptUser();
							}
							// 存储交易内容
							ConcurrentHashMap<User, Userbag> map = jy.getJycontentMap();
							ConcurrentHashMap<User, Integer> jyMoney = jy.getJyMoney();
							if (map == null) {
								ConcurrentHashMap<User, Userbag> jycontentMap = new ConcurrentHashMap<>(500);
								jycontentMap.put(user, userbag);
								jy.setJycontentMap(jycontentMap);
							} else {
								map.put(user, userbag);
							}
							if (jyMoney == null) {
								ConcurrentHashMap<User, Integer> concurrentHashMap = new ConcurrentHashMap<>(500);
								concurrentHashMap.put(user, Integer.valueOf(msg[2]));
								jy.setJyMoney(concurrentHashMap);
							} else {
								jyMoney.put(user, Integer.valueOf(msg[2]));
							}
							// 改变状态
							user.getAndSetjyFlag(user, 2);
							Channel channel = AllOnlineUser.userchMap.get(sendUser);
							if (userbag.getIsadd() == 0) {
//								Zb zb = IOsession.zbMp.get(userbag.getGid());
								Zb zb = zbXmlParse.getZbById(userbag.getGid());
								SendMsg.send(user.getNickname() + "---装备:" + zb.getName() + "---金币:" + msg[2], channel);
							} else {
//								Yaopin yaopin = IOsession.yaopinMp.get(userbag.getGid());
								Yaopin yaopin = yaopinXmlParse.getYaopinById(userbag.getGid());
								SendMsg.send(user.getNickname() + "---药品:" + yaopin.getName() + "---金币:" + msg[2],
										channel);
							}
						}
						flag = true;
						break;
					}
				}
				if (flag == false) {
					SendMsg.send("物品不存在，请重新放入", ch);
				}
			} else {
				Jy jy = JyCache.getInstance().getJyCache().get(user.getJyId());
				if (jy != null) {
					User sendUser = jy.getSendUser();
					if (sendUser.equals(user)) {
						sendUser = jy.getAcceptUser();
					}
					// 存储交易内容
					ConcurrentHashMap<User, Integer> jyMoney = jy.getJyMoney();
					if (jyMoney == null) {
						ConcurrentHashMap<User, Integer> concurrentHashMap = new ConcurrentHashMap<>(500);
						concurrentHashMap.put(user, Integer.valueOf(msg[2]));
						jy.setJyMoney(concurrentHashMap);
					} else {
						jyMoney.put(user, Integer.valueOf(msg[2]));
					}
					// 改变状态
					user.getAndSetjyFlag(user, 2);
					Channel channel = AllOnlineUser.userchMap.get(sendUser);
					SendMsg.send(user.getNickname() + "---金币:" + msg[2], channel);
				}
			}
		}
	}

	/**
	 * 确认交易
	 * 
	 * @param user
	 * @param ch
	 */
	public void confirmJy(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		lock1.lock();
		try {
			if (msg.length == 1 && user.getJyFlag() == JY_FLAG) {
				Jy jy = JyCache.getInstance().getJyCache().get(user.getJyId());
				int acceptFlag = jy.getAcceptFlag();
				if (acceptFlag == 0) {
					jy.setAcceptFlag(1);
					User sendUser = jy.getSendUser();
					if (sendUser.equals(user)) {
						sendUser = jy.getAcceptUser();
					}
//					IOsession.chStatus.put(ch, UserStatus.JYYES.getValue());
					ChannelUtil.getSessionData(ch).setStatus(UserStatus.JYYES.getValue());
					SendMsg.send("你已确认了", ch);
					Channel channel = AllOnlineUser.userchMap.get(sendUser);
					SendMsg.send("对方已确认", channel);
				} else {
					User acceptUser = jy.getAcceptUser();
					User sendUser = jy.getSendUser();
					exchange(jy, acceptUser, sendUser);
					exchange(jy, sendUser, acceptUser);
					acceptUser.getAndSetjyFlag(acceptUser, 0);
					sendUser.getAndSetjyFlag(sendUser, 0);
					jy = null;
					JyCache.getInstance().getJyCache().remove(user.getJyId());
					Channel channel = AllOnlineUser.userchMap.get(sendUser);
//					IOsession.chStatus.put(channel, UserStatus.ORDINARY.getValue());
					ChannelUtil.getSessionData(channel).setStatus(UserStatus.ORDINARY.getValue());
					SendMsg.send("交易成功", channel);
					Channel channel2 = AllOnlineUser.userchMap.get(acceptUser);
//					IOsession.chStatus.put(channel2, UserStatus.ORDINARY.getValue());
					ChannelUtil.getSessionData(channel2).setStatus(UserStatus.ORDINARY.getValue());
					SendMsg.send("交易成功", channel2);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock1.unlock();
		}
	}

	/**
	 * 交换
	 * 
	 * @param jy
	 * @param acceptUser
	 * @param sendUser
	 */
	public void exchange(Jy jy, User acceptUser, User sendUser) {
		ConcurrentHashMap<User, Userbag> jycontentMap = jy.getJycontentMap();
		ConcurrentHashMap<User, Integer> jyMoney = jy.getJyMoney();
		List<Userbag> acceptUseList = acceptUser.getUserbags();
		List<Userbag> sendUserList = sendUser.getUserbags();
		if (jycontentMap != null) {
			Userbag userbag = jycontentMap.get(acceptUser);
			if (userbag != null) {
				if (userbag.getIsadd() == 0) {
//					Zb zb = IOsession.zbMp.get(userbag.getGid());
					Zb zb = zbXmlParse.getZbById(userbag.getGid());
					Integer njd = userbag.getNjd();
					Integer enhance = userbag.getEnhance();
					// 移除
					acceptUseList.remove(userbag);
					// 入包
					rpgUtil.putZbWithNJD(sendUser, zb, njd, enhance);
				} else {
//					Yaopin yaopin = IOsession.yaopinMp.get(userbag.getGid());
					Yaopin yaopin = yaopinXmlParse.getYaopinById(userbag.getGid());
					if (userbag.getNumber() == 1) {
						acceptUseList.remove(userbag);
					} else {
						userbag.setNumber(userbag.getNumber() - 1);
					}
					rpgUtil.putYaopin(sendUser, yaopin);
				}
			}
		}
		Integer integer = jyMoney.get(acceptUser);
		Integer integer2 = jyMoney.get(sendUser);
		acceptUser.getAndSetMoney(acceptUser, acceptUser.getMoney() - integer + integer2);
	}

	/**
	 * 接受交易
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msg
	 */
	public void acceptJy(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_2.getValue()) {
			Jy jy = JyCache.getInstance().getJyCache().get(msg[1]);
			if (jy != null) {
				lock.lock();
				try {
					User user2 = jy.getSendUser();
					if (user2.getJyFlag() == 0) {
						user2.getAndSetjyFlag(user2, 1);
						user2.getAndSetjySendFlag(user2, 0);
						user.getAndSetjySendFlag(user, 0);
						user.getAndSetjyFlag(user, 1);
						user.setJyId(jy.getId());
						jy.setAcceptUser(user);
//						IOsession.chStatus.put(ch, 4);
						ChannelUtil.getSessionData(ch).setStatus(4);
						SendMsg.send("进入交易状态", ch);
						Channel channel = AllOnlineUser.userchMap.get(user2);
//						IOsession.chStatus.put(channel, 4);
						ChannelUtil.getSessionData(channel).setStatus(4);
						SendMsg.send("和" + user.getNickname() + "开始交易", channel);
						taskManage.checkTaskCompleteBytaskid(user, 9);
					} else if (user2.getJySendFlag() == 0) {
						SendMsg.send("交易已过期", ch);
					} else {
						SendMsg.send("对方正在交易中", ch);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			} else {
				SendMsg.send("交易单号不存在", ch);
			}
		} else {
			SendMsg.send("指令错误", ch);
		}
	}

	/**
	 * 发送交易请求
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msg
	 */
	public void sendJy(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_2.getValue()) {
			if (user.getJySendFlag() == 0) {
				if (AllOnlineUser.onlineUserMap != null) {
					for (User user2 : AllOnlineUser.onlineUserMap.values()) {
						if (msg[1].equals(user2.getNickname())) {
							if (user2.getJyFlag() == 1) {
								SendMsg.send("对方正在交易中,请稍后再试", ch);
							} else {
								user.getAndSetjySendFlag(user, 1);
								String jyId = UUID.randomUUID().toString();
								user.setJyId(jyId);
								Jy jy = new Jy();
								jy.setId(jyId);
								jy.setStartTime(System.currentTimeMillis());
								jy.setSendUser(user);
								JyCache.getInstance().getJyCache().put(jyId, jy);
								SendMsg.send("向" + user2.getNickname() + "-交易请求已发送", ch);
								Channel channel = AllOnlineUser.userchMap.get(user2);
								SendMsg.send(jyId + "--" + user.getNickname() + "请求跟你交易", channel);
								taskManage.checkTaskCompleteBytaskid(user, 9);
							}
						}
					}
				}
			} else {
				SendMsg.send("操作频繁，请稍后再试", ch);
			}
		}
	}
}
