package octoteam.tahiti.client.pipeline;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.shared.netty.ExtendedContext;
import octoteam.tahiti.shared.netty.MessageHandler;

/**
 * 该模块处理下行消息中的聊天消息 (CHAT_BROADCAST_PUSH).
 * 收到聊天消息时, 会产生 ChatMessageEvent 事件
 */
@ChannelHandler.Sharable
public class BroadcastPushHandler extends MessageHandler {

    public BroadcastPushHandler(ExtendedContext extendedContext) {
        super(extendedContext);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message msg) {
        if (msg.getService() == Message.ServiceCode.CHAT_BROADCAST_PUSH) {
            /*
            ctx.fireUserEventTriggered(new ChatMessageEvent(
                    msg.getChatBroadcastPush().getPayload(),
                    msg.getChatBroadcastPush().getSenderUsername(),
                    msg.getChatBroadcastPush().getTimestamp()
            ));*/
            // TODO: send sync
        }
        ctx.fireChannelRead(msg);
    }

}
