package rpg.server;

import io.netty.channel.ChannelHandlerContext;

/**服务管理
 * @author ljq
 *
 */
public class ServerManager {
	public static void ungisterUserContext(ChannelHandlerContext context ){
		if(context  != null){
			context.close();
		}
	}
}
