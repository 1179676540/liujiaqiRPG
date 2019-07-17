package rpg.server.handle;

import java.lang.reflect.Method;
import java.net.SocketAddress;

import io.netty.channel.Channel;
import rpg.pojo.OrderMethod;

/**
 * 指令处理非登陆状态
 * 
 * @author ljq
 *
 */
public class OrderHandleNoLogin implements Runnable {

	private Class<?> c;
	private OrderMethod method;
	private Object bean;
	private Channel ch;
	private String meesage;
	private SocketAddress address;
	

	public OrderHandleNoLogin(Class<?> c, OrderMethod method, Object bean, Channel ch, String meesage,
			SocketAddress address) {
		super();
		this.c = c;
		this.method = method;
		this.bean = bean;
		this.ch = ch;
		this.meesage = meesage;
		this.address = address;
	}


	@Override
	public void run() {
		try {
			Method method2 = c.getMethod(method.getMethodName(), Channel.class, String.class, SocketAddress.class);
			method2.invoke(bean, ch, meesage, address);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
