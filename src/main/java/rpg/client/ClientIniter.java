package rpg.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import rpg.core.packet.PacketDecoder;
import rpg.core.packet.PacketEncoder;

/**
 * 配置netty客户端
 * 
 * @author ljq
 *
 */
public class ClientIniter extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel arg0) throws Exception {
		ChannelPipeline pipeline = arg0.pipeline();
		pipeline.addLast(new PacketEncoder());
		pipeline.addLast(new PacketDecoder());
		//每隔一段时间向服务端发送心跳
		pipeline.addLast(new IdleStateHandler(1, 1, 0));
		pipeline.addLast("chat", new RpgClientHandler());
	}

}
