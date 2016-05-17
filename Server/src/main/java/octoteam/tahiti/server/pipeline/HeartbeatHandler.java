package octoteam.tahiti.server.pipeline;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.shared.netty.ExtendedContext;
import octoteam.tahiti.shared.netty.MessageHandler;

/**
 * 该模块会在收到 IdleStateEvent 时发送 HEARTBEAT_PUSH 上行消息。
 * 需要配合 IdleStateHandler 所提供的 IdleStateEvent 来实现心跳功能。
 */
@ChannelHandler.Sharable
public class HeartbeatHandler extends MessageHandler {

    public HeartbeatHandler(ExtendedContext extendedContext) {
        super(extendedContext);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            Message.Builder heartbeat = Message
                    .newBuilder()
                    .setDirection(Message.DirectionCode.PUSH)
                    .setService(Message.ServiceCode.HEARTBEAT_PUSH);
            ctx
                    .writeAndFlush(heartbeat.build())
                    .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }
        ctx.fireUserEventTriggered(evt);
    }

}
