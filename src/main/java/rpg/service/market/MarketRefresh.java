package rpg.service.market;

import java.util.List;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import rpg.configure.MsgResp;
import rpg.core.AllOnlineUser;
import rpg.core.packet.ServerRespPacket;
import rpg.core.packet.ServerRespPacket.Resp.Builder;
import rpg.pojo.MarketItem;
import rpg.pojo.User;
import rpg.pojo.Zb;
import rpg.service.user.UserResources;
import rpg.util.RpgUtil;
import rpg.util.SendMsg;
import rpg.xmlparse.ZbXmlParse;

/**
 * 拍卖线程
 * 
 * @author ljq
 *
 */
@Slf4j
public class MarketRefresh implements Runnable {

	private RpgUtil rpgUtil;
	private ZbXmlParse zbXmlParse;

	public MarketRefresh(RpgUtil rpgUtil, ZbXmlParse zbXmlParse) {
		this.rpgUtil = rpgUtil;
		this.zbXmlParse = zbXmlParse;
		log.info("拍卖线程启动......");
	}

	@Override
	public void run() {
		try {
			if (MarketCache.MarketItemMp != null) {
				for (MarketItem item : MarketCache.MarketItemMp.values()) {
					if (item.getType() == 2) {
						if (System.currentTimeMillis() - item.getTime() > 30000) {
							User owner = UserResources.nameMap.get(item.getOwnername());
							Channel channel2 = AllOnlineUser.userchMap.get(owner);
							Zb zb = zbXmlParse.getZbById(item.getGid());
							if (item.getAuctioner() != null) {
								User auctioner = UserResources.nameMap.get(item.getAuctioner());
								// 放入用户背包
								rpgUtil.putZbWithNJD(auctioner, zb, item.getNjd(), item.getEnhance());
								owner.getAndAddMoney(owner, item.getNewprice());
								Channel channel = AllOnlineUser.userchMap.get(auctioner);

								Builder builder = ServerRespPacket.Resp.newBuilder();
								builder.setData("成功竞拍到---" + zb.getName());
								SendMsg.send(builder.build(), channel);

								Builder builder1 = ServerRespPacket.Resp.newBuilder();
								builder1.setData("物品已被拍卖成功,获得金币---" + item.getNewprice());
								SendMsg.send(builder1.build(), channel2);
							} else {
								rpgUtil.putZbWithNJD(owner, zb, item.getNjd(), item.getEnhance());
								Builder builder = ServerRespPacket.Resp.newBuilder();
								builder.setData(MsgResp.MARKET_ERR);
								SendMsg.send(builder.build(), channel2);
							}
							// 移除物品
							MarketCache.MarketItemMp.remove(item.getId());
							List<String> list = MarketCache.userMarketItemMp.get(owner);
							list.remove(item.getId());
							log.info("商品完成。。。");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
