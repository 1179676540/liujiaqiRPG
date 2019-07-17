package rpg.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import rpg.core.ChannelUtil;
import rpg.util.SendMsg;

/**o
 * 客户端处理类
 * @author ljq
 *
 */
public class ClientMain {
	private String host;
	private int port;
	private boolean stop = false;
	public static Jm jm1;
	public final  int RECONNECT_MAX_TIMES=5;

	/** 当前重接次数 */
	public static int reconnectTimes = 0;

	public ClientMain(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public static void main(String[] args) throws IOException {
		new ClientMain("127.0.0.1", 8080).run();
	}

	public void run() throws IOException {
		EventLoopGroup worker = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(worker);
		bootstrap.channel(NioSocketChannel.class);
//		bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(2048));
		bootstrap.option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.handler(new ClientIniter());

		try {
			Channel channel = bootstrap.connect(host, port).sync().channel();
			System.out.println("请选择指令");
			System.out.println("login、登陆 regist、注册");
			System.out.println("格式：login username psw");
			System.out.println("格式：regist username psw psw");
			jm1 = new Jm(channel);
			ChannelUtil.setSessionData(channel, null, 0, null);
			while (true) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				String input = reader.readLine();
				if (input != null) {
					if ("quit".equals(input)) {
						System.exit(1);
					}
					SendMsg.send(input, channel);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			// 设置最大重连次数，防止服务端正常关闭导致的空循环
			if (reconnectTimes < RECONNECT_MAX_TIMES) {
				reConnectServer();
			}
		}
	}

	/**
	 * 断线重连
	 */
	public void reConnectServer() {
		try {
			Thread.sleep(5000);
			System.err.println("客户端进行断线重连");
			run();
			reconnectTimes++;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 重置重连次数
	 */
	public static void resetReconnectTimes() {
		if (reconnectTimes > 0) {
			reconnectTimes = 0;
			System.err.println("断线重连成功");
		}
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

}
