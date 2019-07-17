package rpg.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.configure.InstructionsType;
import rpg.configure.MsgSize;
import rpg.core.AllOnlineUser;
import rpg.data.dao.MarketItemMapper;
import rpg.data.dao.UserMapper;
import rpg.data.dao.UserbagMapper;
import rpg.data.dao.UseritemMapper;
import rpg.pojo.MarketItem;
import rpg.pojo.User;
import rpg.pojo.Userbag;
import rpg.pojo.Useritem;
import rpg.pojo.UseritemExample;
import rpg.pojo.UseritemExample.Criteria;
import rpg.pojo.Zb;
import rpg.service.market.MarketCache;
import rpg.service.user.UserResources;
import rpg.util.RpgUtil;
import rpg.util.SendMsg;
import rpg.xmlparse.ZbXmlParse;

/**
 * 交易行处理逻辑
 * 
 * @author ljq
 *
 */
@Component
public class MarketDispatch {

	@Autowired
	private RpgUtil rpgUtil;
	@Autowired
	private ZbXmlParse zbXmlParse;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private MarketItemMapper marketItemMapper;
	@Autowired
	private UserbagMapper userbagMapper;
	@Autowired
	private UseritemMapper useritemMapper;

	/**
	 * 购买一口价商品锁
	 */
	private ReentrantLock buyLock = new ReentrantLock();

	/**
	 * 展示用户上架商品
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void showUserItem(User user, Channel ch, ChannelGroup group, String msgR) {
		List<String> list = MarketCache.userMarketItemMp.get(user);
		String aprice = "-----------一口价商品-----------\n";
		String auction = "-----------竞拍商品-----------\n";
		for (String string : list) {
			MarketItem item = MarketCache.MarketItemMp.get(string);
			if (item != null) {
				if (item.getType() == Integer.valueOf(InstructionsType.APRICE.getValue())) {
					aprice += "id-" + item.getId() + "-商品名-" + item.getName() + "-价格-" + item.getNewprice() + "-耐久度-"
							+ item.getNjd() + "-强化等级-" + item.getEnhance() + "\n";
				} else {
					auction = "id-" + item.getId() + "-商品名-" + item.getName() + "-价格-" + item.getNewprice() + "-耐久度-"
							+ item.getNjd() + "-强化等级-" + item.getEnhance() + "-竞拍剩余时间-"
							+ (30000 - System.currentTimeMillis() + item.getTime()) + "\n";
				}
			}
		}
		SendMsg.send(aprice + auction, ch);
	}

	/**
	 * 展示竞拍商品
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void showAuctionItem(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		String word = "";
		if (msg.length == MsgSize.MAX_MSG_SIZE_1.getValue()) {
			if (MarketCache.MarketItemMp != null) {
				for (MarketItem item : MarketCache.MarketItemMp.values()) {
					if (item.getType() == 2) {
						word += "id-" + item.getId() + "-商品名-" + item.getName() + "-价格-" + item.getNewprice() + "-耐久度-"
								+ item.getNjd() + "-强化等级-" + item.getEnhance() + "-竞拍剩余时间-"
								+ (30000 - System.currentTimeMillis() + item.getTime()) + "\n";
					}
				}
				SendMsg.send(word, ch);
			}
		} else {
			SendMsg.send("指令错误", ch);
		}
	}

	/**
	 * 展示一口价商品
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void showApriceItem(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		String word = "";
		if (msg.length == MsgSize.MAX_MSG_SIZE_1.getValue()) {
			if (MarketCache.MarketItemMp != null) {
				for (MarketItem item : MarketCache.MarketItemMp.values()) {
					if (item.getType() == 1) {
						word += "id-" + item.getId() + "-商品名-" + item.getName() + "-价格-" + item.getNewprice() + "-耐久度-"
								+ item.getNjd() + "-强化等级-" + item.getEnhance() + "\n";
					}
				}
				SendMsg.send(word, ch);
			}
		} else {
			SendMsg.send("指令错误", ch);
		}
	}

	/**
	 * 购买竞拍商品
	 * 
	 * @param user
	 * @param ch
	 * @param msg
	 */
	public void buyAuctionItem(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_3.getValue()) {
			MarketItem marketItem = MarketCache.MarketItemMp.get(msg[MsgSize.MSG_INDEX_1.getValue()]);
			if (marketItem != null) {
				int type = marketItem.getType();
				int money = Integer.valueOf(msg[MsgSize.MSG_INDEX_2.getValue()]);
				if (money > marketItem.getNewprice()) {
					if (user.getMoney() >= money) {
						// 竞拍商品
						if (type == Integer.valueOf(InstructionsType.AUCTION.getValue())) {
							user.getAndSetMoney(user, user.getMoney() - money);
							marketItem.setNewprice(money);
							marketItem.setAuctioner(user.getNickname());
							SendMsg.send("竞价成功", ch);
						} else {
							SendMsg.send("找不到指定商品", ch);
						}
					} else {
						SendMsg.send("金币不足，请充值.......", ch);
					}
				} else {
					SendMsg.send("请大于当前竞拍价", ch);
				}
			} else {
				SendMsg.send("物品不存在", ch);
			}
		}
	}

	/**
	 * 购买一口价商品
	 * 
	 * @param user
	 * @param ch
	 * @param msg
	 */
	public void buyAPriceItem(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_2.getValue()) {
			MarketItem marketItem = MarketCache.MarketItemMp.get(msg[MsgSize.MSG_INDEX_1.getValue()]);
			try {
				buyLock.lock();
				if (marketItem != null) {
					int type = marketItem.getType();
					if (user.getMoney() >= marketItem.getNewprice()) {
						// 一口价商品
						if (type == Integer.valueOf(InstructionsType.APRICE.getValue())) {
							user.getAndSetMoney(user, user.getMoney() - marketItem.getNewprice());
							// 清除用户的商品
							User user2 = UserResources.nameMap.get(marketItem.getOwnername());
							user2.getAndSetMoney(user2, user2.getMoney() + marketItem.getNewprice());
							userMapper.updateByPrimaryKeySelective(user);
							userMapper.updateByPrimaryKeySelective(user2);
							List<String> list = MarketCache.userMarketItemMp.get(user2);
							Channel channel = AllOnlineUser.userchMap.get(user2);
							SendMsg.send(marketItem.getName() + "已被卖出...", channel);
							for (String string : list) {
								if (msg[MsgSize.MSG_INDEX_1.getValue()].equals(string)) {
									UseritemExample example = new UseritemExample();
									Criteria createCriteria = example.createCriteria();
									createCriteria.andItemidEqualTo(string);
									useritemMapper.deleteByExample(example);
									list.remove(string);
									break;
								}
							}
							// 清除交易行商品
							MarketCache.MarketItemMp.remove(msg[MsgSize.MSG_INDEX_1.getValue()]);
							// 放入用户背包
							Zb zb = zbXmlParse.getZbById(marketItem.getGid());
							rpgUtil.putZbWithNJD(user, zb, marketItem.getNjd(), marketItem.getEnhance());
							SendMsg.send("商品购买成功", ch);
						} else {
							SendMsg.send("找不到指定商品", ch);
						}
					} else {
						SendMsg.send("金币不足，请充值.......", ch);
					}
				} else {
					SendMsg.send("物品不存在", ch);
				}
			} finally {
				buyLock.unlock();
			}
		}
	}

	/**
	 * 上架竞拍商品
	 * 
	 * @param user
	 * @param ch
	 * @param msg
	 */
	public void addAuctionItem(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_3.getValue()) {
			List<Userbag> list = user.getUserbags();
			for (Userbag userbag : list) {
				if (msg[MsgSize.MSG_INDEX_1.getValue()].equals(userbag.getId())) {
					// 放入用户
					String id = UUID.randomUUID().toString();
					ArrayList<String> list2 = new ArrayList<>();
					list2.add(id);
					MarketCache.userMarketItemMp.put(user, list2);
					// 放入交易行
					MarketItem marketItem = new MarketItem();
					marketItem.setId(id);
					marketItem.setOwnername(user.getNickname());
					marketItem.setName(zbXmlParse.getZbById(userbag.getGid()).getName());
					marketItem.setType(Integer.valueOf(InstructionsType.AUCTION.getValue()));
					marketItem.setOldprice(Integer.valueOf(msg[MsgSize.MSG_INDEX_2.getValue()]));
					marketItem.setNewprice(Integer.valueOf(msg[MsgSize.MSG_INDEX_2.getValue()]));
					marketItem.setGid(userbag.getGid());
					marketItem.setNjd(userbag.getNjd());
					marketItem.setEnhance(userbag.getEnhance());
					marketItem.setTime(System.currentTimeMillis());
					MarketCache.MarketItemMp.put(id, marketItem);
					list.remove(userbag);
					SendMsg.send("上架竞价物品成功", ch);
					break;
				}
			}
		}
	}

	/**
	 * 上架一口价商品
	 * 
	 * @param user
	 * @param ch
	 * @param msg
	 */
	public void addApriceItem(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_3.getValue()) {
			List<Userbag> list = user.getUserbags();
			for (Userbag userbag : list) {
				if (msg[MsgSize.MSG_INDEX_1.getValue()].equals(userbag.getId())) {
					// 放入用户
					String id = UUID.randomUUID().toString();
					ArrayList<String> list2 = new ArrayList<>();
					list2.add(id);
					MarketCache.userMarketItemMp.put(user, list2);
					Useritem useritem = new Useritem();
					useritem.setUsername(user.getNickname());
					useritem.setItemid(id);
					useritemMapper.insert(useritem);
					// 放入交易行
					MarketItem marketItem = new MarketItem();
					marketItem.setId(id);
					marketItem.setOwnername(user.getNickname());
					marketItem.setName(zbXmlParse.getZbById(userbag.getGid()).getName());
					marketItem.setType(Integer.valueOf(InstructionsType.APRICE.getValue()));
					marketItem.setOldprice(Integer.valueOf(msg[MsgSize.MSG_INDEX_2.getValue()]));
					marketItem.setNewprice(Integer.valueOf(msg[MsgSize.MSG_INDEX_2.getValue()]));
					marketItem.setGid(userbag.getGid());
					marketItem.setNjd(userbag.getNjd());
					marketItem.setEnhance(userbag.getEnhance());
					marketItem.setTime(System.currentTimeMillis());
					MarketCache.MarketItemMp.put(id, marketItem);
					marketItemMapper.insert(marketItem);
					userbagMapper.deleteByPrimaryKey(userbag.getId());
					list.remove(userbag);
					SendMsg.send("上架一口价物品成功", ch);
					break;
				}
			}
		}
	}
}
