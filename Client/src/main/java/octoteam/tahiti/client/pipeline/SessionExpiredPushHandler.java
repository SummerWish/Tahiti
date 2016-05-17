package octoteam.tahiti.client.pipeline;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import octoteam.tahiti.client.event.SessionExpiredEvent;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.shared.netty.ExtendedContext;
import octoteam.tahiti.shared.netty.MessageHandler;

/**
 * 该模块处理下行消息中的会话过期推送 (SESSION_EXPIRED_PUSH)
 * 收到该消息时, 会产生 SessionExpiredEvent
 */
@ChannelHandler.Sharable
public class SessionExpiredPushHandler extends MessageHandler {

    public SessionExpiredPushHandler(ExtendedContext extendedContext) {
        super(extendedContext);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message msg) {
        if (msg.getService() == Message.ServiceCode.SESSION_EXPIRED_PUSH) {
            ctx.fireUserEventTriggered(new SessionExpiredEvent(msg.getSessionExpiredPush().getReason()));
        }
        ctx.fireChannelRead(msg);
    }

}
