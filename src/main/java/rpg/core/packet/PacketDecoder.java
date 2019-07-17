package rpg.core.packet;

import java.util.List;

import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * protobuf解码器
 * @author ljq
 *
 */
public class PacketDecoder extends ByteToMessageDecoder {

	/**
	 * 这个HEAD_LENGTH是我们用于表示头长度的字节数， 由于我们传的是一个int类型的值，所以这里HEAD_LENGTH的值为4.
	 */
	private static final int HEAD_LENGTH = 4;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
		try {
			if (byteBuf.readableBytes() < HEAD_LENGTH) {
				return;
			}
//          标记当前的readIndex的位置,便于之后的记录
			byteBuf.markReaderIndex();
//          读取传送过来的消息的长度。ByteBuf 的readInt()方法会让他的readIndex增加4
			int length = byteBuf.readInt();
//          读到的消息体长度如果小于我们传送过来的消息长度，则resetReaderIndex. 这个配合markReaderIndex使用的。把readIndex重置到mark的地方
			if (byteBuf.readableBytes() < length) {
				byteBuf.resetReaderIndex();
				return;
			}
//          获取协议头
			int protoIndex = byteBuf.readInt();
//          核心包体，数据都在此
			byte[] data = new byte[length - HEAD_LENGTH];
			byteBuf.readBytes(data);

//      根据协议头获得协议解析器
			Parser parser = ProtoBufEnum.parserOfProtoIndex(protoIndex);
//      根据协议头处理协议体
			MessageLite messageLite = null;
			messageLite = (MessageLite) parser.parsePartialFrom(data);
			out.add(messageLite);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
