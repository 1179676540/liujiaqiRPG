package rpg.server.handle;

import java.lang.reflect.Method;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.pojo.OrderMethod;
import rpg.pojo.User;

/**
 * 指令处理在线状态
 * @author ljq
 *
 */
public class OrderHandleOnline implements Runnable {

	private Class<?> c;
	private OrderMethod method;
	private Object bean;
	private Channel ch;
	private String meesage;
	private User user;
	private ChannelGroup userGroup;

	public OrderHandleOnline(Class<?> c, OrderMethod method, Object bean, Channel ch, String meesage, User user,
			ChannelGroup userGroup) {
		super();
		this.c = c;
		this.method = method;
		this.bean = bean;
		this.ch = ch;
		this.meesage = meesage;
		this.user = user;
		this.userGroup = userGroup;
	}

	@Override
	public void run() {
		try {
			Method method2 = c.getMethod(method.getMethodName(), User.class, Channel.class, ChannelGroup.class,
					String.class);
			method2.invoke(bean, user, ch, userGroup, meesage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
