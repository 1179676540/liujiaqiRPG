package rpg.core.packet;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;

/**
 * 枚举客户端和服务端信息包
 * @author ljq
 *
 */
public enum ProtoBufEnum {

	/**
	 * 服务端心跳响应包
	 */
	SERVER_HEART_RESP(1000, "ServerRespPacket", "HeartResp"),

	/**
	 * 正常响应包
	 */
	SERVER_RESP(1001, "ServerRespPacket", "Resp"),

	/**
	 * 登陆响应包
	 */
	SERVER_LOGINRESP(1002, "ServerRespPacket", "LoginResp"),
	
	/**
	 * 用户Buff响应包
	 */
	SERVER_USERBUFRESP(1003, "ServerRespPacket", "UserBufResp"),
	
	/**
	 * 怪物攻击响应包
	 */
	SERVER_MONSTERACKRESP(1004, "ServerRespPacket", "MonsterAckResp"),
	
	/**
	 * 怪物buff响应包
	 */
	SERVER_MONSTERBUFRESP(1005, "ServerRespPacket", "MonsterBufResp"),
	
	/**
	 * 客户端的心跳请求包
	 */
	CLIENT_HEART_REQ(1011, "ClientReqPacket", "HeartReq"),

	/**
	 * 客户端请求包
	 */
	CLIENT_REQ(1012, "ClientReqPacket", "Req");

	private int iValue;

	private String innerClass;

	private String outClass;

	public String getInnerClass() {
		return innerClass;
	}

	public void setInnerClass(String innerClass) {
		this.innerClass = innerClass;
	}

	public String getOutClass() {
		return outClass;
	}

	public void setOutClass(String outClass) {
		this.outClass = outClass;
	}

	ProtoBufEnum(int iValue, String outClass, String innerClass) {
		this.iValue = iValue;
		this.outClass = outClass;
		this.innerClass = innerClass;
	}

	public int getiValue() {
		return iValue;
	}

	public void setiValue(int iValue) {
		this.iValue = iValue;
	}

	private static ProtoBufEnum[] values = ProtoBufEnum.values();

	/**
	 * 采用的懒汉式管理，使用threadLocal
	 */
	private static final ThreadLocal<ProtoParser> threadLocalParser = new ThreadLocal<ProtoParser>() {
		@Override
		protected ProtoParser initialValue() {
			return new ProtoParser();
		}
	};

	/**
	 * 通过protoIndex获得它的消息解析器
	 *
	 * @param protoIndex
	 * @return 若protoIndex无对应的parser，则返回null
	 */
	public static Parser parserOfProtoIndex(final int protoIndex) {
		return threadLocalParser.get().getParser(protoIndex);
	}

	/**
	 * 通过消息获取它的索引
	 *
	 * @param messageLite
	 * @return 若不存在对应的索引，返回 -1，存在对应的索引，则返回0-n
	 */
	public static int protoIndexOfMessage(final MessageLite messageLite) throws UnsupportedOperationException {
		return threadLocalParser.get().getProtoIndex(messageLite);
	}

	private static class ProtoParser {

		private final Map<Integer, Parser> parserMap = Maps.newHashMap();

		private final Map<Class, ProtoBufEnum> messageLiteToEnumMap = Maps.newHashMap();

		private final Map<Integer, ProtoBufEnum> protoBufEnumMap = Maps.newHashMap();
		/**
		 * protoBuf文件导出的java包路径
		 */
		private final String protoBufPackagePath;

		private ProtoParser() {
//          统一packet
			protoBufPackagePath = "rpg.core.packet";
		}

		/**
		 * 根据编号拿到枚举类型
		 */
		private ProtoBufEnum getProtoBufEnumByIvalue(final int iValue) {
			if (protoBufEnumMap.containsKey(iValue)) {
				return protoBufEnumMap.get(iValue);
			}
			for (ProtoBufEnum protoBufEnum : values) {
				if (protoBufEnum.getiValue() == iValue) {
					protoBufEnumMap.put(protoBufEnum.getiValue(), protoBufEnum);
					return protoBufEnum;
				}
			}
			return null;
		}

		/**
		 * 协议解析器
		 *
		 * @param protoIndex
		 * @return
		 */
		private Parser getParser(final int protoIndex) {
			try {
				if (parserMap.containsKey(protoIndex)) {
					return parserMap.get(protoIndex);
				}
				ProtoBufEnum protoBufEnum = getProtoBufEnumByIvalue(protoIndex);
//              内外部类名拿到解析器
				String innerClassName = protoBufEnum.getInnerClass();
				String outerClassName = protoBufEnum.getOutClass();
//              拼接解析器路径
				String className = protoBufPackagePath + "." + outerClassName + "$" + innerClassName;
				Class messageClass = Class.forName(className);

				Parser parser = (Parser) messageClass.getField("PARSER").get(null);
				parserMap.put(protoIndex, parser);
				return parser;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * 通过消息 获得它的索引
		 *
		 * @param messageLite
		 * @return 若不存在对应的索引，返回 -1，存在对应的索引，则返回 0-n
		 */
		private int getProtoIndex(final MessageLite messageLite) throws UnsupportedOperationException {
			if (messageLiteToEnumMap.containsKey(messageLite.getClass())) {
				return messageLiteToEnumMap.get(messageLite.getClass()).getiValue();
			}
//          通过外部类名，拿到枚举名s
			String enumName = messageLite.getClass().getSimpleName();
			for (ProtoBufEnum protoBufEnum : values) {
				if (enumName.equals(protoBufEnum.getInnerClass())) {
					messageLiteToEnumMap.put(messageLite.getClass(), protoBufEnum);
					return protoBufEnum.getiValue();
				}
			}
			return -1;
		}

	}
}
