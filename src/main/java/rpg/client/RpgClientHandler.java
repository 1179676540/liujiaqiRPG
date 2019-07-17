package rpg.client;

import com.google.protobuf.MessageLite;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import rpg.core.packet.ProtoBufEnum;
import rpg.core.packet.ServerRespPacket;
import rpg.util.SendMsg;

/**
 * 客户端处理业务逻辑
 * 
 * @author ljq
 *
 */
@Slf4j
public class RpgClientHandler extends ChannelHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg3) throws Exception {
		int respType = ProtoBufEnum.protoIndexOfMessage((MessageLite) msg3);

		if (respType == ProtoBufEnum.SERVER_RESP.getiValue()) {
			ServerRespPacket.Resp resp = (ServerRespPacket.Resp) msg3;
			if (resp.getData() != "") {
				Jm.printMsg(resp.getData(), ClientMain.jm1.jTextArea);
				System.out.println(resp.getData());
			}
		}

		if (respType == ProtoBufEnum.SERVER_LOGINRESP.getiValue()) {
			ServerRespPacket.LoginResp resp = (ServerRespPacket.LoginResp) msg3;
			ClientMain.jm1.setTitle(resp.getData());
		}

		if (respType == ProtoBufEnum.SERVER_USERBUFRESP.getiValue()) {
			ServerRespPacket.UserBufResp resp = (ServerRespPacket.UserBufResp) msg3;
			if (resp.getData() != "") {
				Jm.printMsg(resp.getData(), ClientMain.jm1.jTextArea2);
				System.out.println(resp.getData());
			}
		}

		if (respType == ProtoBufEnum.SERVER_MONSTERACKRESP.getiValue()) {
			ServerRespPacket.MonsterAckResp resp = (ServerRespPacket.MonsterAckResp) msg3;
			if (resp.getData() != "") {
				Jm.printMsg(resp.getData(), ClientMain.jm1.jTextArea3);
				System.out.println(resp.getData());
			}
		}

		if (respType == ProtoBufEnum.SERVER_MONSTERBUFRESP.getiValue()) {
			ServerRespPacket.MonsterBufResp resp = (ServerRespPacket.MonsterBufResp) msg3;
			if (resp.getData() != "") {
				Jm.printMsg(resp.getData(), ClientMain.jm1.jTextArea4);
				System.out.println(resp.getData());
			}
		}
	}
	
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // 向服务器发送心跳包
        	log.info("客户端发送心跳----------");
        	SendMsg.sendClientHeartReq(ctx.channel());
        }
    }
    
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.err.println("客户端关闭3");
		Channel channel = ctx.channel();
		cause.printStackTrace();
		if (channel.isActive()) {
			System.err.println("simpleclient" + channel.remoteAddress() + "异常");
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		new ClientMain("127.0.0.1", 8080).reConnectServer();
	}
}
