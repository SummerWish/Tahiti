package octoteam.tahiti.client.pipeline;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import octoteam.tahiti.client.event.BroadcastPushEvent;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.shared.netty.ExtendedContext;
import octoteam.tahiti.shared.netty.MessageHandler;

/**
 * 该模块处理下行消息中的新聊天消息事件 (CHAT_BROADCAST_PUSH).
 * 收到新聊天消息推送时, 进行消息同步
 */
@ChannelHandler.Sharable
public class ChatBroadcastPushHandler extends MessageHandler {

    public ChatBroadcastPushHandler(ExtendedContext extendedContext) {
        super(extendedContext);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message msg) {
        if (msg.getService() == Message.ServiceCode.CHAT_BROADCAST_PUSH) {
            ctx.fireUserEventTriggered(new BroadcastPushEvent(
                    msg.getBroadcastPush().getGroupId()
            ));
        }
        ctx.fireChannelRead(msg);
    }

}
