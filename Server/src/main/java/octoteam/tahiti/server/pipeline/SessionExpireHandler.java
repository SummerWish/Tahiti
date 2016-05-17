package octoteam.tahiti.server.pipeline;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.protocol.SocketMessageProtos.SessionExpiredPushBody;
import octoteam.tahiti.server.event.RateLimitExceededEvent;
import octoteam.tahiti.server.session.PipelineHelper;
import octoteam.tahiti.shared.netty.ExtendedContext;
import octoteam.tahiti.shared.netty.MessageHandler;

/**
 * 传入一个RateLimitExceededEvent类型的event，
 * 如果event.getName()为NAME_PER_SESSION，给客户端发送session过期的消息。
 */
@ChannelHandler.Sharable
public class SessionExpireHandler extends MessageHandler {

    public SessionExpireHandler(ExtendedContext extendedContext) {
        super(extendedContext);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof RateLimitExceededEvent) {
            RateLimitExceededEvent event = (RateLimitExceededEvent) evt;
            if (event.getName().equals(RateLimitExceededEvent.NAME_PER_SESSION)) {
                Message.Builder resp = Message.newBuilder()
                        .setDirection(Message.DirectionCode.PUSH)
                        .setService(Message.ServiceCode.SESSION_EXPIRED_PUSH)
                        .setSessionExpiredPush(SessionExpiredPushBody.newBuilder()
                                .setReason(SessionExpiredPushBody.Reason.EXPIRED)
                        );
                ctx.channel().writeAndFlush(resp.build());
                PipelineHelper.clearSession(ctx);
            }
        }
        ctx.fireUserEventTriggered(evt);
    }

}
