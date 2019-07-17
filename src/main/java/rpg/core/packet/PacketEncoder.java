package rpg.core.packet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.google.protobuf.MessageLite;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * protobuf编码器
 * @author ljq
 *
 */
public class PacketEncoder extends MessageToByteEncoder<MessageLite> {

	/**
	 * 这个HEAD_LENGTH是我们用于表示头长度的字节数， 由于我们传的是一个int类型的值，所以这里HEAD_LENGTH的值为4.
	 */
	private static final int HEAD_LENGTH = 4;

	@Override
	protected void encode(ChannelHandlerContext channelHandlerContext, MessageLite messageLite, ByteBuf byteBuf)
			throws Exception {
		encode(messageLite, byteBuf);
	}

	protected void encode(MessageLite messageLite, ByteBuf byteBuf) throws IOException {
		try {
			// 获取整个包的长度(不包括自身)
			int protoLength = HEAD_LENGTH + messageLite.getSerializedSize();
			// 先获取消息对应的枚举编号,传进来的是messageLite无法获得协议编号,根据类型获取协议编号
			int protoIndex = ProtoBufEnum.protoIndexOfMessage(messageLite);
			if (protoIndex == -1) {
				throw new UnsupportedEncodingException(
						"UnsupportedEncodingProtoBuf " + messageLite.getClass().getSimpleName());
			}
			// 写入长度
			byteBuf.writeInt(protoLength);
			// 写入协议头
			byteBuf.writeInt(protoIndex);
			// 写入数据
			byteBuf.writeBytes(messageLite.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
