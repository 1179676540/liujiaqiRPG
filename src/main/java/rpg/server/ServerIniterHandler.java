package rpg.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import rpg.core.packet.PacketDecoder;
import rpg.core.packet.PacketEncoder;

/**服务端配置
 * @author ljq
 *
 */
@Component("serverIniterHandler")
public class ServerIniterHandler extends ChannelInitializer<SocketChannel> {

	@Autowired
	private RpgServerHandler rpgServerHandler;

	@Override
	protected void initChannel(SocketChannel arg0) throws Exception {
		ChannelPipeline pipeline = arg0.pipeline();
		//先解码
		pipeline.addLast(new PacketDecoder());
		//然后编码
		pipeline.addLast(new PacketEncoder());
		//进行心跳包的统计，每隔一段时间判断
		pipeline.addLast("idleStateHandler", new IdleStateHandler(2, 0, 0));
		//任务派发给用户
		pipeline.addLast("chat", rpgServerHandler);
	}

}
