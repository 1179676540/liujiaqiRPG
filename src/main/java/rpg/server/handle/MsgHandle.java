package rpg.server.handle;

import java.net.SocketAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import rpg.core.AllOnlineUser;
import rpg.core.ChannelUtil;
import rpg.core.SessionData;
import rpg.pojo.OrderMethod;
import rpg.pojo.User;
import rpg.server.RpgServerHandler;
import rpg.util.SendMsg;
import rpg.util.SpringContextUtil;
import rpg.xmlparse.OrderMethodXmlParse;

/**
 * 服务端接受指令处理逻辑
 * 
 * @author ljq
 *
 */
@Component
public class MsgHandle {

	@Autowired
	private OrderMethodXmlParse orderMethodXmlParse;
	@Autowired
	private OrderQueue orderQueue;

	public void msgHandle(ChannelHandlerContext chContext, String meesage) throws Exception {
		ChannelGroup userGroup = RpgServerHandler.USER_GROUP;
		Channel ch = chContext.channel();
		SocketAddress address = ch.remoteAddress();
		SessionData sessionData = ChannelUtil.getSessionData(ch);
		int chStatus = sessionData.getStatus();
		String[] msg = meesage.split("\\s+");
		OrderMethod method = orderMethodXmlParse.getById(msg[0]);
		if (method != null) {
			if (method.getStatus().contains(chStatus)) {
				Object bean = SpringContextUtil.getBean(method.getClassName());
				Class<?> c = bean.getClass();
				User user = AllOnlineUser.onlineUserMap.get(ch.remoteAddress());
				// 将指令放入队列去消费
				if (user == null) {
					OrderHandleNoLogin orderHandle = new OrderHandleNoLogin(c, method, bean, ch, meesage, address);
					orderQueue.addOrder(orderHandle, address, meesage);
				} else {
					OrderHandleOnline orderHandle = new OrderHandleOnline(c, method, bean, ch, meesage, user,
							userGroup);
					orderQueue.addOrder(orderHandle, address, meesage, user);
				}
			} else {
				SendMsg.send("该场景下不能做此操作", ch);
			}
		} else {
			SendMsg.send("指令错误", ch);
		}
	}
}
