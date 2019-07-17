package rpg.service.login;

import java.net.SocketAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import rpg.configure.MsgResp;
import rpg.core.packet.ServerRespPacket;
import rpg.core.packet.ServerRespPacket.Resp.Builder;
import rpg.util.SendMsg;

/**
 * 注册处理器
 * 
 * @author ljq
 *
 */
@Component
public class RegistDispatch {

	private static final int MIN_ROLE_ID = 0;
	private static final int MAX_ROLE_ID = 5;
	private static final int MAX_MSG_LENGTH = 5;
	@Autowired
	private Regist regist;

	public void registDispatch(Channel ch, String arg1, SocketAddress address) {
		String[] msg = arg1.split("\\s+");
		Integer roleid = Integer.valueOf(msg[4]);
		if (msg.length <= MAX_MSG_LENGTH) {
			Builder builder = ServerRespPacket.Resp.newBuilder();
			builder.setData(MsgResp.ORDER_ERR);
			SendMsg.send(builder.build(), ch);
			return;
		}
		
		if (roleid >= MAX_ROLE_ID || roleid <= MIN_ROLE_ID) {
			Builder builder = ServerRespPacket.Resp.newBuilder();
			builder.setData(MsgResp.ROLECHECK_ERR);
			SendMsg.send(builder.build(), ch);
			return;
		}

		RegistStaus status = regist.regist(msg[1], msg[2], msg[3], roleid, msg[5]);
		if (status.equals(RegistStaus.PASSWORDERROR)) {
			Builder builder = ServerRespPacket.Resp.newBuilder();
			builder.setData(MsgResp.PSW_ERR);
			SendMsg.send(builder.build(), ch);
			return;
		}
		
		if (status.equals(RegistStaus.SUCCESS)) {
			Builder builder = ServerRespPacket.Resp.newBuilder();
			builder.setData(MsgResp.REG_SUCCESS);
			SendMsg.send(builder.build(), ch);
			return;
		}

		if (status.equals(RegistStaus.ACCOUNTERROR)) {
			Builder builder = ServerRespPacket.Resp.newBuilder();
			builder.setData(MsgResp.ACCOUNT_ERR);
			SendMsg.send(builder.build(), ch);
			return;
		}

		if (status.equals(RegistStaus.USERNAMEERROR)) {
			Builder builder = ServerRespPacket.Resp.newBuilder();
			builder.setData(MsgResp.USERNAME_ERR);
			SendMsg.send(builder.build(), ch);
			return;
		}
	}
}
