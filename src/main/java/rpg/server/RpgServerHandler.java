package rpg.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.protobuf.MessageLite;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import rpg.client.ClientMain;
import rpg.core.ChannelUtil;
import rpg.core.packet.ClientReqPacket;
import rpg.core.packet.ProtoBufEnum;
import rpg.server.handle.MsgHandle;
import rpg.service.offine.OffineDispatch;
import rpg.util.SendMsg;

/**
 * 服务端业务逻辑处理
 * 
 * @author ljq
 *
 */
@Slf4j
@Sharable
@Component("rpgServerHandler")
public class RpgServerHandler extends ChannelHandlerAdapter {

	@Autowired
	private OffineDispatch offineDispatch;
	@Autowired
	private MsgHandle msgHandle;

	/**
	 * 客户端超时次数
	 */
	private Map<ChannelHandlerContext, Integer> clientOvertimeMap = new ConcurrentHashMap<>();
	/**
	 * 超时次数超过该值则注销连接
	 */
	private final int MAX_OVERTIME = 3;

	/**
	 * 存储连接进来的玩家
	 */
	public static final ChannelGroup USER_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	/**
	 * 客户端和服务端建立连接，并且告诉每个客户端
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		ChannelUtil.setSessionData(channel, null, 0, null);
		USER_GROUP.add(channel);
	}

	/**
	 * 客户端断开连接
	 */
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		USER_GROUP.remove(channel);
	}

	/**
	 * 连接处于活跃状态
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {

	}

	/**
	 * 客户端断开连接
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Channel ch = ctx.channel();
		offineDispatch.groupOffine(ch);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("[" + ctx.channel().remoteAddress() + "]" + "exit the room");
		cause.printStackTrace();
		ctx.close().sync();
	}

	/**
	 * 服务端处理客户端请求消息
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (ProtoBufEnum.protoIndexOfMessage((MessageLite) msg) == ProtoBufEnum.CLIENT_REQ.getiValue()) {
			System.out.println(ProtoBufEnum.protoIndexOfMessage((MessageLite) msg));
			ClientReqPacket.Req req = (ClientReqPacket.Req) msg;
			msgHandle.msgHandle(ctx, req.getData());
		}
		resetReconnectTimes();
		// 只要接受到数据包，则清空超时次数
		clientOvertimeMap.remove(ctx);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		// 心跳包检测读超时
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			Channel channel = ctx.channel();
			if (e.state() == IdleState.READER_IDLE) {
				int overtimeTimes = clientOvertimeMap.getOrDefault(ctx, 0);
				if (overtimeTimes < MAX_OVERTIME) {
					log.info("服务端发送心跳----------");
					SendMsg.sendServerHeartResp(channel);
					addUserOvertime(ctx);
				} else {
					System.err.println("客户端读超时");
					ServerManager.ungisterUserContext(ctx);
				}
			}
		}
	}

	private void addUserOvertime(ChannelHandlerContext ctx) {
		int oldTimes = 0;
		if (clientOvertimeMap.containsKey(ctx)) {
			oldTimes = clientOvertimeMap.get(ctx);
		}
		clientOvertimeMap.put(ctx, (int) (oldTimes + 1));
	}

	public void resetReconnectTimes() {
		if (ClientMain.reconnectTimes > 0) {
			ClientMain.reconnectTimes = 0;
			System.err.println("断线重连成功");
		}
	}

}
