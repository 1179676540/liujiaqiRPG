package rpg.a;

import io.netty.channel.ChannelHandlerContext;

public class ServerManager {
	public static void ungisterUserContext(ChannelHandlerContext context ){
		if(context  != null){
			context.close();
		}
	}
}
