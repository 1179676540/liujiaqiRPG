package rpg.util;

import com.google.protobuf.MessageLite;

import io.netty.channel.Channel;
import rpg.core.packet.ClientReqPacket;
import rpg.core.packet.ServerRespPacket;
import rpg.core.packet.ServerRespPacket.Resp.Builder;

/**
 * 消息发送管理
 * 
 * @author ljq
 *
 */
public class SendMsg {
	
	/**
	 * 发送普通响应包
	 * @param s
	 * @param ch
	 */
	public static void send(String s, Channel ch) {
		Builder builder = ServerRespPacket.Resp.newBuilder();
		builder.setData(s);
		SendMsg.send(builder.build(), ch);
	}
	
	/**
	 * 发送服务端心跳响应包
	 * @param s
	 * @param ch
	 */
	public static void sendServerHeartResp(Channel ch) {
		ServerRespPacket.HeartResp.Builder builder = ServerRespPacket.HeartResp.newBuilder();
		SendMsg.send(builder.build(), ch);
	}
	
	/**
	 * 发送客户端请求包
	 * @param s
	 * @param ch
	 */
	public static void sendClientPacket(String s, Channel ch) {
		ClientReqPacket.Req.Builder builder = ClientReqPacket.Req.newBuilder();
		builder.setData(s);
		SendMsg.send(builder.build(), ch);
	}
	
	/**
	 * 发送客户端心跳包
	 * @param s
	 * @param ch
	 */
	public static void sendClientHeartReq(Channel ch) {
		ClientReqPacket.HeartReq.Builder builder = ClientReqPacket.HeartReq.newBuilder();
		SendMsg.send(builder.build(), ch);
	}
	
	/**
	 * 写和刷新出去
	 * @param messageLite
	 * @param channel
	 */
	public static void send(MessageLite messageLite, Channel channel) {
		channel.writeAndFlush(messageLite);
	}
	
	/**
	 * 发送怪物buff响应包
	 * @param s
	 * @param ch
	 */
	public static void sendMonsterbufMsg(String s, Channel ch) {
		ServerRespPacket.MonsterBufResp.Builder builder = ServerRespPacket.MonsterBufResp.newBuilder();
		builder.setData(s);
		SendMsg.send(builder.build(), ch);
	}
	
	/**
	 * 发送怪物攻击响应包
	 * @param s
	 * @param ch
	 */
	public static void sendMonsterAckMsg(String s, Channel ch) {
		ServerRespPacket.MonsterAckResp.Builder builder = ServerRespPacket.MonsterAckResp.newBuilder();
		builder.setData(s);
		SendMsg.send(builder.build(), ch);
	}
}